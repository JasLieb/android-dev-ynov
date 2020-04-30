package com.jaslieb.scheduleapp.adapters.tasks.viewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.actors.ParentActor;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.utils.DateUtil;

public class ParentTaskViewHolder extends RecyclerView.ViewHolder {
    private ParentActor parentActor;
    private LinearLayout llContainer;

    private ImageView ivIsLate;

    private Button btRemoveRecurrence;
    private Button btRemoveReminder;

    public ParentTaskViewHolder( @NonNull View itemView) {
        super(itemView);
        llContainer = (LinearLayout) itemView;
        this.parentActor = ParentActor.getInstance();
    }

    public void bindTo(Task task) {
        ivIsLate = llContainer.findViewById(R.id.ivIsLate);
        TextView tvTaskTitleBig = llContainer.findViewById(R.id.tvTaskTitle);
        TextView tvTaskDurationBig = llContainer.findViewById(R.id.tvTaskDuration);
        TextView tvTaskDateBeginBig = llContainer.findViewById(R.id.tvTaskDateBegin);

        tvTaskTitleBig.setText(task.name);

        tvTaskDateBeginBig.setText(DateUtil.formatToDateString(task.begin));
        tvTaskDurationBig.setText(TimeUnitEnum.fromMilliseconds(task.duration));

        LinearLayout llTaskActions = llContainer.findViewById(R.id.llTaskActions);

        Button btShowActions = llContainer.findViewById(R.id.btShowActions);
        btShowActions.setOnClickListener(
                v -> llTaskActions.setVisibility(llTaskActions.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE)
        );

        btRemoveRecurrence = llContainer.findViewById(R.id.btRemoveRecurrence);
        btRemoveRecurrence.setOnClickListener(
                v -> parentActor.removeRecurrence(task)
        );

        btRemoveReminder = llContainer.findViewById(R.id.btRemoveReminder);
        btRemoveReminder.setOnClickListener(
                v -> parentActor.removeReminder(task)
        );

        Button btRemoveTask = llContainer.findViewById(R.id.btRemoveTask);
        btRemoveTask.setOnClickListener(
                v -> parentActor.removeTask(task)
        );

        setButtonVisibility(task);
    }

    public void clearAnimation() {
        llContainer.clearAnimation();
    }

    private void setButtonVisibility(Task task) {
        ivIsLate.setVisibility(task.parentWarned ? View.VISIBLE : View.GONE);
        btRemoveRecurrence.setVisibility(task.recurrence != null ? View.VISIBLE : View.GONE);
        btRemoveReminder.setVisibility(task.reminder != null ? View.VISIBLE : View.GONE);
    }
}