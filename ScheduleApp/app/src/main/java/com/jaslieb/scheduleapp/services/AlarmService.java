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
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.recievers.AlarmActionReceiver;
import com.jaslieb.scheduleapp.recievers.AlarmNotificationReceiver;
import com.jaslieb.scheduleapp.states.ChildState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class AlarmService extends Service {
    public static boolean isRunning = false;
    public static PublishSubject<Integer> makeNotificationStream = PublishSubject.create();

    private ChildActor childActor;
    private CompositeDisposable disposable = new CompositeDisposable();
    private DisposableObserver<ChildState> childStateObserver =
        new DisposableObserver<ChildState>() {
            @Override
            public void onNext(@NonNull ChildState childState) {
                assert alarmMgr != null;
                int notificationId = 0;
                childState.tasks.sort(
                    (sA, sB) -> Long.compare(sB.begin, sA.begin)
                );
                Task nextTask = Task.makeDefault();
                for(Task task : childState.tasks) {
                    Context context = getApplicationContext();
                    Intent alarmReceiverIntent = new Intent(context, AlarmNotificationReceiver.class);
                    alarmReceiverIntent.putExtra("task_name", task.name);

                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, notificationId, alarmReceiverIntent, 0);
                    alarmMgr.cancel(alarmIntent);

                    long triggerTime = makeTriggerTime(task, nextTask.begin);

                    if(triggerTime == -1 ) {
                        //alarmIntent.send(); no more warning for this task

                    } else if(triggerTime > System.currentTimeMillis()) {
                        alarmMgr.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            alarmIntent
                        );
                    }

                    notificationId++;
                    nextTask = task;
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {}

            @Override
            public void onComplete() {}
        };

    private AlarmManager alarmMgr;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmService.isRunning = true;
        Context context = getApplicationContext();
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        childActor = new ChildActor();

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
        long trigger = task.begin + task.duration;

        if (task.reminder != null) {
            Log.d("SERVICE", task.reminder.displayedCount  + " / " +  task.reminder.count);
            if(task.reminder.displayedCount < task.reminder.count) {
                trigger =
                    task.reminder.isBeforeTask
                        ? task.begin - (task.reminder.count - task.reminder.displayedCount) * task.reminder.duration
                        : task.begin + (task.reminder.count - task.reminder.displayedCount) * task.reminder.duration;

                childActor.updateReminderDisplayedCount(task);
                return trigger;
            }
        }

        if (
            beginNextTask - trigger > TimeUnitEnum.MINUTES.toMilliseconds(5)
            && !task.parentWarned
        ) {
            childActor.warnParentForTask(task.name, true);
            childActor.removeReminderFor(task);
            return -1;
        }
        return trigger;
    }
}
