package com.jaslieb.scheduleapp.jobs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.receivers.AlarmActionReceiver;

public class NotificationJob extends JobService {
    private static int counter = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = getApplicationContext();
        String channelId ="ScheduleAppNotification";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                new NotificationChannel(
                    channelId,
                    "alarm notification",
                    NotificationManager.IMPORTANCE_HIGH
                );

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        String taskName = params.getExtras().getString("task_name");
        String childName = params.getExtras().getString("child_name");
        Log.d("ALARM NOTIFICATION", "TASK NAME : " + taskName);
        assert taskName != null;

        notificationManager.notify(
            counter,
            makeNotification(
                context,
                channelId,
                childName,
                taskName
            )
        );
        counter++;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private Notification makeNotification(
            Context context,
            String channelId,
            String childName,
            String taskName
    ) {
        Intent alarmReceiverIntent = new Intent(context, AlarmActionReceiver.class);
        PendingIntent piAlarmReceiver = PendingIntent.getBroadcast(context, counter, alarmReceiverIntent, 0);

        NotificationCompat.Builder build =
            new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle(taskName + " is finished ?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(piAlarmReceiver)
                .addAction(
                    0,
                    "Done",
                    PendingIntent
                        .getBroadcast(
                            context,
                            counter,
                            intentWithAction(alarmReceiverIntent, counter, childName, taskName, true),
                            PendingIntent.FLAG_ONE_SHOT
                        )
                )
                .addAction(
                    0,
                    "Not finished yet",
                    PendingIntent.getBroadcast(
                        context,
                        counter,
                        intentWithAction(alarmReceiverIntent, counter, childName, taskName),
                        PendingIntent.FLAG_ONE_SHOT
                    )
                );

        return build.build();
    }

    private Intent intentWithAction(Intent intent, int notificationId, String childName, String taskName ) {
        return intentWithAction(intent, notificationId, childName, taskName, false);
    }

    private Intent intentWithAction(Intent intent, int notificationId, String childName, String taskName, boolean isDone) {
        intent.setAction(
            isDone
                ? "done"
                : "not_yet"
        );

        intent.putExtra("task_name", taskName);
        intent.putExtra("child_name", childName);
        intent.putExtra("notification_id", notificationId);
        return intent;
    }
}
