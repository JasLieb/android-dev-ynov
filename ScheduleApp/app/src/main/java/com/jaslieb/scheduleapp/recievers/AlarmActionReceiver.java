package com.jaslieb.scheduleapp.recievers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jaslieb.scheduleapp.actors.ChildActor;
import com.jaslieb.scheduleapp.models.Task;

public class AlarmActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("task_name");
        boolean isDone = "done".equals(intent.getAction());
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.cancel(intent.getIntExtra("notification_id", -1));
        if(!isDone) {
            ChildActor childActor = new ChildActor();
            childActor.warmParentForTask(taskName);
        }
    }
}
