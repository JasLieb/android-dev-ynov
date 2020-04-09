package com.jaslieb.scheduleapp.recievers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.jaslieb.scheduleapp.R;

public class AlarmNotificationReceiver extends BroadcastReceiver{

    private static int counter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
//            Intent serviceIntent = new Intent(context, AlarmService.class);
//            context.startService(serviceIntent);
        }

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

            notificationManager.createNotificationChannel(channel);
        }

        String taskName = intent.getStringExtra("task_name");
        assert taskName != null;

        notificationManager.notify(
            counter,
            makeNotification(
                context,
                channelId,
                taskName
            )
        );
        counter++;
    }

    private Notification makeNotification(
            Context context,
            String channelId,
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
                            intentWithAction(alarmReceiverIntent, counter, taskName, true),
                            PendingIntent.FLAG_ONE_SHOT
                        )
                )
                .addAction(
                    0,
                    "Not finished yet",
                    PendingIntent.getBroadcast(
                        context,
                        counter,
                        intentWithAction(alarmReceiverIntent, counter, taskName),
                        PendingIntent.FLAG_ONE_SHOT
                    )
                );

        return build.build();
    }

    private Intent intentWithAction(Intent intent, int notificationId, String taskName ) {
        return intentWithAction(intent, notificationId, taskName, false);
    }

    private Intent intentWithAction(Intent intent, int notificationId, String taskName, boolean isDone) {
        intent.setAction(
            isDone
            ? "done"
            : "not_yet"
        );

        intent.putExtra("task_name", taskName);
        intent.putExtra("notification_id", notificationId);
        return intent;
    }
}
