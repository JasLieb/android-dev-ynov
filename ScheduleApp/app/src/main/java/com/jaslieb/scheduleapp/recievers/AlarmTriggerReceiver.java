package com.jaslieb.scheduleapp.recievers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.jaslieb.scheduleapp.R;

public class AlarmTriggerReceiver extends BroadcastReceiver{

    private static int counter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // set alarm here
        }

        String channelId ="ScheduleAppNotification";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                new NotificationChannel(
                    channelId,
                    "alarm notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                );

            channel.enableLights(true);
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }

        Intent alarmReceiverIntent = new Intent(context, AlarmActionReceiver.class);
        PendingIntent piAlarmReceiver = PendingIntent.getBroadcast(context, counter, alarmReceiverIntent, 0);

        NotificationCompat.Builder build =
            new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle(intent.getStringExtra("taskName") + " done")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(piAlarmReceiver)
                .addAction(
                    0,
                    "Done",
                    PendingIntent
                        .getBroadcast(
                            context,
                            counter,
                            intentWithAction(alarmReceiverIntent, counter, true),
                            PendingIntent.FLAG_ONE_SHOT
                        )
                )
                .addAction(
                    0,
                    "Not finished yet",
                    PendingIntent.getBroadcast(
                        context,
                        counter,
                        intentWithAction(alarmReceiverIntent, counter),
                        PendingIntent.FLAG_ONE_SHOT
                    )
                );

        notificationManager.notify(counter, build.build());
        counter++;
    }

    private Intent intentWithAction(Intent intent, int notificationId) {
        return intentWithAction(intent, notificationId, false);
    }

    private Intent intentWithAction(Intent intent, int notificationId, boolean isDone) {
        intent.setAction(
            isDone
            ? "done"
            : "not_yet"
        );

        intent.putExtra("notification_id", notificationId);
        return intent;
    }
}
