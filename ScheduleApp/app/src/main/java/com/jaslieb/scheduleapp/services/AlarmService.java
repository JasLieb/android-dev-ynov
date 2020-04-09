package com.jaslieb.scheduleapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jaslieb.scheduleapp.actors.ChildActor;
import com.jaslieb.scheduleapp.models.Reminder;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.recievers.AlarmActionReceiver;
import com.jaslieb.scheduleapp.recievers.AlarmNotificationReceiver;
import com.jaslieb.scheduleapp.states.ChildState;
import com.jaslieb.scheduleapp.utils.DateUtil;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class AlarmService extends Service {
    public static boolean isRunning = false;
    public static PublishSubject<Integer> makeNotificationStream = PublishSubject.create();

    private AlarmManager alarmMgr;
    private ChildActor childActor;

    private CompositeDisposable disposable = new CompositeDisposable();
    private DisposableObserver<ChildState> childStateObserver =
        new DisposableObserver<ChildState>() {
            @Override
            public void onNext(@NonNull ChildState childState) {
                childState.tasks.sort(
                    (sA, sB) -> Long.compare(sB.begin, sA.begin)
                );

                Task nextTask = Task.makeDefault();
                int notificationId = 0;

                for(Task task : childState.tasks) {
                    Context context = getApplicationContext();
                    Intent alarmReceiverIntent = new Intent(context, AlarmNotificationReceiver.class);
                    alarmReceiverIntent.putExtra("task_name", task.name);

                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, notificationId, alarmReceiverIntent, 0);

                    long triggerTime = makeTriggerTime(task, nextTask.begin);

                    if (triggerTime > 0 ) {
                        Log.d("SERVICE", "ADD ALARM FOR " + task.name);
                        Log.d("SERVICE", "RING ALARM AT " + DateUtil.formatToDateString(triggerTime));
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent);
                    }

                    if(triggerTime == -3)  {
                        childActor.warnParentForTask(task, true);
                    }

                    // TODO
                    // triggerTime == -1 ==>  alarmIntent.send(); no more warning for this task
                    // triggerTime == -2 ==>  Parent already warned, so say that to the child

                    notificationId++;
                    nextTask = task;
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {}

            @Override
            public void onComplete() {}
        };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmService.isRunning = true;
        Context context = getApplicationContext();

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        childActor = ChildActor.getInstance();

        Observable.combineLatest(
            AlarmService.makeNotificationStream,
            childActor.childStateBehavior,
            (__, childState) -> childState
        )
        .distinctUntilChanged()
        .subscribe(childStateObserver);

        disposable.add(childStateObserver);

        AlarmService.makeNotificationStream.onNext(0);

        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(
            new ComponentName(context, AlarmNotificationReceiver.class),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        );

        pm.setComponentEnabledSetting(
            new ComponentName(context, AlarmActionReceiver.class),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        );

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        AlarmService.isRunning = false;
        disposable.dispose();
        super.onDestroy();
    }

    private long makeTriggerTime(Task task, long beginNextTask) {
        if(task.parentWarned) {
            return -2; // -2 == Parent already warned, so skip
        }

        long begin = task.begin, duration = task.duration, currentTime = System.currentTimeMillis();
        long trigger = begin + duration;
        boolean isBeforeTrigger = currentTime < trigger;
        Reminder reminder = task.reminder;

        if ( reminder != null ) {
           if (reminder.isBeforeTask && isBeforeTrigger) {
               return begin - (reminder.count - reminder.displayedCount) * reminder.duration;
           } else if (!reminder.isBeforeTask && !isBeforeTrigger) {
               for (int i = 0; i < reminder.count; i++) {
                   if (currentTime < trigger + i * reminder.duration) {
                       if(
                           beginNextTask > 0 &&
                           beginNextTask - currentTime < TimeUnitEnum.MINUTES.toMilliseconds(5)
                       ) {
                           Log.d("SERVICE", "NO TIME ANYMORE FOR " + task.name);
                           childActor.warnParentForTask(task, true);
                           childActor.removeReminderFor(task);
                           return -1; // -1 == No time anymore, next task will begin
                       }
                       Log.d("SERVICE", "REMINDER AFTER AT " + DateUtil.formatToDateString(trigger + i * reminder.duration));
                       return trigger + i * reminder.duration;
                   }
               }
           }
        }

        if(!isBeforeTrigger) {
            return -3; // After task's end but parent aren't warned
        }

        return trigger; // Else return task end
    }
}
