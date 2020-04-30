package com.jaslieb.scheduleapp.adapters.tasks.viewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.activities.ChildActivity;
import com.jaslieb.scheduleapp.models.Reminder;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.utils.DateUtil;

public class ChildTaskViewHolder extends RecyclerView.ViewHolder {
    private static final String BaseRecurrence = "This task will recurrence every ";

    private ChildActivity childActor;

    private LinearLayout llContainer;
    private LinearLayout llTaskSmall;
    private LinearLayout llTaskState;

    private MaterialCardView llTaskBig;

    public ChildTaskViewHolder(ChildActivity childActivity, @NonNull View itemView) {
        super(itemView);
        llContainer = (LinearLayout) itemView;
        this.childActor = childActivity;
    }

    public void bindTo(Task task) {
        TextView tvTaskTitle = llContainer.findViewById(R.id.tvTaskTitle);
        TextView tvTaskTitleBig = llContainer.findViewById(R.id.tvTaskTitleBig);
        TextView tvTaskDateBegin = llContainer.findViewById(R.id.tvTaskDateBegin);
        TextView tvTaskDuration = llContainer.findViewById(R.id.tvTaskDuration);
        TextView tvTaskDurationBig = llContainer.findViewById(R.id.tvTaskDurationBig);
        TextView tvTaskDateBeginBig = llContainer.findViewById(R.id.tvTaskDateBeginBig);

        TextView tvTaskRecurrenceReminder = llContainer.findViewById(R.id.tvTaskRecurrenceReminder);

        llTaskSmall = llContainer.findViewById(R.id.llTaskSmall);
        llTaskBig = llContainer.findViewById(R.id.llTaskBig);

        tvTaskTitle.setText(task.name);
        tvTaskTitleBig.setText(task.name);

        tvTaskDuration.setText(TimeUnitEnum.fromMilliseconds(task.duration));
        tvTaskDateBegin.setText(DateUtil.formatToDateString(task.begin));

        tvTaskDateBeginBig.setText(DateUtil.formatToDateString(task.begin));
        tvTaskDurationBig.setText(TimeUnitEnum.fromMilliseconds(task.duration));

        LinearLayout llTaskRecurrenceReminder = llContainer.findViewById(R.id.llTaskRecurrenceReminder);
        String reminder = getTextRecurrenceReminder(task);
        tvTaskRecurrenceReminder.setText(reminder);
        if(reminder.length() == 0) llTaskRecurrenceReminder.setVisibility(View.GONE);

        Button btExpandTaskDetails = llContainer.findViewById(R.id.btExpandTaskDetails);
        btExpandTaskDetails.setOnClickListener(
                v -> toggleTaskDetailsVisibility()
        );

        llTaskState = llContainer.findViewById(R.id.llTaskState);
        setTaskButtonVisibility(task);


        Button btTaskDone = llContainer.findViewById(R.id.btTaskDone);
        btTaskDone.setOnClickListener(
                v -> childActor.updateTaskAsDone(task.name)
        );

        Button btTaskNotDone = llContainer.findViewById(R.id.btTaskNotDone);
        btTaskNotDone.setOnClickListener(
                v -> childActor.warmParentForTask(task)
        );
    }

    public void clearAnimation() {
        llContainer.clearAnimation();
    }

    private void setTaskButtonVisibility(Task task) {
        long beginShowButton = (long) (task.begin + task.duration * 0.5);
        llTaskState.setVisibility(
                System.currentTimeMillis() > beginShowButton
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private String getTextRecurrenceReminder(Task task) {
        return
                String.format(
                        "%s\n\n%s",
                        getTextRecurrence(task.recurrence),
                        getTextReminder(task)
                )
                        .trim();
    }

    private String getTextRecurrence(TimeUnitEnum unit) {
        if (unit == null) return "";
        return String.format("%s %s", BaseRecurrence, unit);
    }

    private String getTextReminder(Task task) {
        Reminder reminder = task.reminder;

        if (reminder == null) return "";
        return  String.format(
                "You'll be reminded %s time%s every %s %s the task\n\nYou will receive first notification at %s",
                reminder.count,
                reminder.count > 1 ? "s" : "",
                TimeUnitEnum.fromMilliseconds(reminder.duration),
                reminder.isBeforeTask ? "before" : "after",
                calcFirstReminder(task)
        );
    }

    private String calcFirstReminder(Task task) {
        if (task.reminder.isBeforeTask) {
            return
                    DateUtil.formatToDateString(
                            task.begin - task.reminder.count * task.reminder.duration
                    );
        } else {
            return
                    DateUtil.formatToDateString(
                            task.begin + task.duration + task.reminder.duration
                    );
        }
    }

    private void toggleTaskDetailsVisibility() {
        if(llTaskBig.getVisibility() == View.VISIBLE) {
            llTaskSmall.setVisibility(View.VISIBLE);
            llTaskBig.setVisibility(View.GONE);
        } else {
            llTaskSmall.setVisibility(View.GONE);
            llTaskBig.setVisibility(View.VISIBLE);
        }
    }
}
