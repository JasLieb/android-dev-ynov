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
import com.jaslieb.scheduleapp.receivers.AlarmActionReceiver;
import com.jaslieb.scheduleapp.receivers.AlarmNotificationReceiver;
import com.jaslieb.scheduleapp.states.ChildState;
import com.jaslieb.scheduleapp.utils.DateUtil;

import java.util.List;

public class AlarmService extends Service {
    public static boolean isRunning = false;
    private static int NotificationId = 0;

    private AlarmManager alarmMgr;
    private ChildActor childActor;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmService.isRunning = true;
        Context context = getApplicationContext();

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        childActor = ChildActor.getInstance();

        ChildState childState = childActor.childStateBehavior.getValue();
        setAlarmForTasks(context, childState.tasks);

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

    private void setAlarmForTasks(Context context, List<Task> tasks) {
        tasks.sort(
            (sA, sB) -> Long.compare(sB.begin, sA.begin)
        );

        Task nextTask = Task.makeDefault();
        NotificationId = 0;
        for(Task task : tasks) {
            setAlarmForTask(context, task);
            if ( task.reminder != null ) {
                setAlarmForReminder(context, task, nextTask.begin);
            }

            if ( task.recurrence != null ) {
                //setAlarmForRecurrence(task);
            }
            nextTask = task;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        AlarmService.isRunning = false;
        super.onDestroy();
    }

    private void setAlarm(Context context, String taskName, long triggerTime) {
        Intent alarmReceiverIntent = new Intent(context, AlarmNotificationReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, NotificationId, alarmReceiverIntent, 0);
        context.sendBroadcast(alarmReceiverIntent.putExtra("task_name", taskName));
        if (triggerTime > 0 ) {
            Log.d("SERVICE", "ADD ALARM FOR " + taskName);
            Log.d("SERVICE", "RING ALARM AT " + DateUtil.formatToDateString(triggerTime));
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent);
            NotificationId++;
        }
    }

    private void setAlarmForTask(Context context, Task task) {
        long triggerTime = makeTriggerTime(task);

        if(triggerTime == -2)  {
            childActor.warnParentForTask(task.name);
        } else {
            setAlarm(context, task.name, triggerTime);
        }

        // TODO
        // triggerTime == -1 ==>  Parent already warned, so say that to the child

    }

    private long makeTriggerTime(Task task) {
        if(task.parentWarned) {
            return -1;
        }

        long trigger = task.begin + task.duration, currentTime = System.currentTimeMillis();
        boolean isBeforeTrigger = currentTime < trigger;

        if(!isBeforeTrigger) {
            return -2;
        }

        return trigger;
    }

    private void setAlarmForReminder(Context context, Task task, long beginNextTask) {
        Log.d("SERVICE", "REMINDER PRESENT IN " + task.name);
        long begin = task.begin,
            duration = task.duration,
            trigger = begin + duration,
            currentTime = System.currentTimeMillis();
        Reminder reminder = task.reminder;

        if (reminder.isBeforeTask) {
            for(int i = reminder.count - 1; i >= 0; i--) {
                trigger = begin - (reminder.count - i) * reminder.duration;
                if(trigger > currentTime) {
                    Log.d("SERVICE", "BEFORE : " +  i + " : TRIGGER AT " + DateUtil.formatToDateString(trigger));
                    Log.d("SERVICE", "WARNING BEFORE BEGIN " + task.name);
                    setAlarm(context, task.name, trigger);
                }
            }
        } else {
            for (int i = 0; i < reminder.count; i++) {
                trigger = trigger + (reminder.count - i)  * reminder.duration;
                Log.d("SERVICE", "AFTER : " +  i + " : TRIGGER AT " + DateUtil.formatToDateString(trigger));
                if (currentTime < trigger) {
                    Log.d("SERVICE", "AFTER : " +  i + " : TRIGGER NEXT TASK AT " + DateUtil.formatToDateString(beginNextTask));
                    Log.d("SERVICE", "AFTER : " +  i + " : TRIGGER NEXT TASK AT " + DateUtil.formatToDateString(beginNextTask - currentTime));
                    if(
                        beginNextTask > 0 &&
                        beginNextTask - trigger < TimeUnitEnum.MINUTES.toMilliseconds(5)
                    ) {
                        Log.d("SERVICE", "NO TIME ANYMORE FOR " + task.name);
                        childActor.warnParentForTask(task.name);
                        childActor.removeReminderFor(task);
                        break;
                    }
                    Log.d("SERVICE", "REMINDER AFTER AT " + DateUtil.formatToDateString(trigger));
                    setAlarm(context, task.name, trigger);
                }
            }
        }
    }
}
