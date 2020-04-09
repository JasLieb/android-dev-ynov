package com.jaslieb.scheduleapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.jaslieb.scheduleapp.actors.ChildActor;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.recievers.AlarmActionReceiver;
import com.jaslieb.scheduleapp.recievers.AlarmNotificationReceiver;
import com.jaslieb.scheduleapp.states.ChildState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class AlarmService extends Service {
    public static boolean isRunning = false;

    private CompositeDisposable disposable = new CompositeDisposable();
    private DisposableObserver<ChildState> childStateObserver =
        new DisposableObserver<ChildState>() {
            @Override
            public void onNext(@NonNull ChildState childState) {
                assert alarmMgr != null;
                int notificationId = 0;
                for(Task task : childState.tasks) {
                    Context context = getApplicationContext();
                    Intent alarmReceiverIntent = new Intent(context, AlarmNotificationReceiver.class);
                    alarmReceiverIntent.putExtra("task_name", task.name);

                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, notificationId, alarmReceiverIntent, 0);
                    alarmMgr.cancel(alarmIntent);

                    long triggerTime = task.begin + task.duration;

                    if (task.reminder != null) {
                        triggerTime =
                            task.reminder.isBeforeTask
                                ? task.begin - task.reminder.duration
                                : task.begin + task.reminder.duration;
                    }

                    alarmMgr.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        alarmIntent
                    );

                    notificationId++;
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

        ChildActor childActor = new ChildActor();
        childActor.childStateBehavior.subscribe(childStateObserver);
        disposable.add(childStateObserver);

        ComponentName receiverTrigger = new ComponentName(context, AlarmNotificationReceiver.class);
        ComponentName receiverAction = new ComponentName(context, AlarmActionReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(
            receiverTrigger,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        );

        pm.setComponentEnabledSetting(
            receiverAction,
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
}
