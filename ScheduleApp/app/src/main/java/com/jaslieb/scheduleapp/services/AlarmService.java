package com.jaslieb.scheduleapp.services;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jaslieb.scheduleapp.actors.ChildActor;
import com.jaslieb.scheduleapp.jobs.NotificationJob;
import com.jaslieb.scheduleapp.models.tasks.Reminder;
import com.jaslieb.scheduleapp.models.tasks.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.states.ChildState;
import com.jaslieb.scheduleapp.utils.DateUtil;

import java.util.List;

public class AlarmService extends Service {
    public static boolean isRunning = false;
    private static int NotificationId = 0;

    private JobScheduler jobScheduler;
    private ChildActor childActor;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmService.isRunning = true;
        Context context = getApplicationContext();

        jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();

        childActor = ChildActor.getInstance();
        ChildState childState = childActor.childStateBehavior.getValue();

        setAlarmForTasks(context, childState.tasks);
        return Service.START_REDELIVER_INTENT;
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

    private void setAlarmForTasks(Context context, List<Task> tasks) {
        tasks.sort(
            (sA, sB) -> Long.compare(sB.begin, sA.begin)
        );

        Task nextTask = Task.makeDefault();
        NotificationId = 0;
        for(Task task : tasks) {
            setAlarmForTask(context, task);
            if ( task.reminder != null && !task.reminder.isTriggered ) {
                setAlarmForReminder(context, task, nextTask.begin);
            }

            nextTask = task;
        }
    }

    private void setAlarm(Context context, Task task, long triggerTime) {
        if (triggerTime > 0 ) {
            Log.d("SERVICE", "ADD ALARM FOR " + task.name);
            Log.d("SERVICE", "RING ALARM AT " + DateUtil.formatToDateString(triggerTime));
            Log.d("SERVICE", "CURRENT TIME " + DateUtil.formatToDateString(System.currentTimeMillis()));
            PersistableBundle extras = new PersistableBundle();
            extras.putString("task_name", task.name);
            extras.putString("child_name", task.childName);

            jobScheduler.schedule(
                new JobInfo.Builder(NotificationId, new ComponentName(context, NotificationJob.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setExtras(extras)
                    .setMinimumLatency( triggerTime - System.currentTimeMillis() - 5000)
                    .setOverrideDeadline(triggerTime)
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .build()
            );
            NotificationId++;
        }
    }

    private void setAlarmForTask(Context context, Task task) {
        long triggerTime = makeTriggerTime(task);

        if(triggerTime == -2)  {
            Log.d("SERVICE", "NO REMINDER AND TIME PASS FOR " + task.name);
            childActor.warnParentForTask(task);
        } else {
            setAlarm(context, task, triggerTime);
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

        if(!isBeforeTrigger && task.reminder == null) {
            Log.d("SERVICE", "NO REMINDER AND TIME PASS FOR " + task.name);
            return -2;
        }

        return trigger;
    }

    private void setAlarmForReminder(Context context, Task task, long beginNextTask) {
        Log.d("SERVICE", "REMINDER PRESENT IN " + task.name);
        long begin = task.begin,
            duration = task.duration,
            baseTrigger = begin + duration,
            trigger = begin + duration,
            currentTime = System.currentTimeMillis();
        Reminder reminder = task.reminder;

        if (reminder.isBeforeTask) {
            for(int i = reminder.count - 1; i >= 0; i--) {
                trigger = begin - (reminder.count - i) * reminder.duration;
                if(trigger > currentTime) {
                    Log.d("SERVICE", "BEFORE : " +  i + " : TRIGGER AT " + DateUtil.formatToDateString(trigger));
                    Log.d("SERVICE", "WARNING BEFORE BEGIN " + task.name);
                    setAlarm(context, task, trigger);
                }
            }
        } else {
            for (int i = reminder.count -1 ; i >= 0; i--) {
                trigger = trigger + (reminder.count - i)  * reminder.duration;
                Log.d("SERVICE", "AFTER : " +  i + " : TRIGGER AT " + DateUtil.formatToDateString(trigger));
                if (currentTime < trigger) {
                    Log.d("SERVICE", "AFTER : " +  i + " : TRIGGER NEXT TASK AT " + DateUtil.formatToDateString(beginNextTask));
                    Log.d("SERVICE", "AFTER : " +  i + " : TRIGGER NEXT TASK AT " + DateUtil.formatToDateString(beginNextTask - currentTime));
                    if(
                        beginNextTask > 0
                        && beginNextTask - trigger < TimeUnitEnum.MINUTES.toMilliseconds(5)
                        && System.currentTimeMillis() > baseTrigger
                    ) {
                        Log.d("SERVICE", "NO TIME ANYMORE FOR " + task.name);
                        childActor.warnParentForTask(task);
                        childActor.removeReminderFor(task);
                        break;
                    }
                    Log.d("SERVICE", "REMINDER AFTER AT " + DateUtil.formatToDateString(trigger));
                    setAlarm(context, task, trigger);
                }
            }
        }
    }
}
