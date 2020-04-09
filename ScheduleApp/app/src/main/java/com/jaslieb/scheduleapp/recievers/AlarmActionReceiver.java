package com.jaslieb.scheduleapp.recievers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jaslieb.scheduleapp.MainActivity;
import com.jaslieb.scheduleapp.activities.ChildActivity;
import com.jaslieb.scheduleapp.actors.ChildActor;

public class AlarmActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String resultAct = intent.getAction();
        boolean isDone = "done".equals(resultAct);
        boolean isLate = "not_yet".equals(resultAct);

        if(isDone || isLate) {
            handleResultAction(context, intent, isDone);
        } else {
            startMainActivity(context);
        }
    }

    private void handleResultAction(Context context, Intent intent, boolean isDone) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        String taskName = intent.getStringExtra("task_name");
        notificationManager.cancel(intent.getIntExtra("notification_id", -1));
        ChildActor childActor = ChildActor.getInstance();

        if (isDone) {
            childActor.updateTaskAsDone(taskName);
        }
        else {
            childActor.warnParentForTask(taskName);
        }
    }

    private void startMainActivity(Context context) {
        Intent i = new Intent(context, ChildActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
