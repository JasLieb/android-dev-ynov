package com.jaslieb.scheduleapp.adapters.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.models.Reminder;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

class TaskViewHolder extends RecyclerView.ViewHolder {
    private static final String BaseRecurrence = "This task will recurrence every ";
    private LinearLayout llContainer;
    private LinearLayout llTaskSmall;
    private MaterialCardView llTaskBig;
    TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        llContainer = (LinearLayout) itemView;
    }

    void bindTo(Task task) {
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
            calcFirstReminder(task.begin, reminder)
        );
    }

    private String calcFirstReminder(long begin, Reminder reminder) {
        if (reminder.isBeforeTask) {
            return
                DateUtil.formatToDateString(
                    begin - reminder.count * reminder.duration)
                ;
        } else {
            return
                DateUtil.formatToDateString(
                    begin - reminder.count * reminder.duration)
                ;
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

public class ChildTasksAdapter extends ListAdapter<Task, TaskViewHolder> {
    private List<Task> tasks;

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(
                @NonNull Task oldTask,
                @NonNull Task newTask
            ) {
                return false;
            }
            @Override
            public boolean areContentsTheSame(
                @NonNull Task oldTask,
                @NonNull Task newTask
            ) {
                return false;
            }
        };

    public ChildTasksAdapter() {
        super(DIFF_CALLBACK);
        tasks = new ArrayList<>();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
            LayoutInflater
                .from(parent.getContext())
                .inflate(
                    R.layout.list_task_row, parent,
                    false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (getItemCount()  > 0) {
            holder.bindTo(
                getItem(position)
            );
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setListItem(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        submitList(null);
        submitList(tasks);
        notifyDataSetChanged();
    }
}
