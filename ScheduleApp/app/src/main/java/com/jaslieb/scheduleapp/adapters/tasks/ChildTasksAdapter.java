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

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

class TaskViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout ll;
    private LinearLayout llTaskDetails;
    TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        ll = (LinearLayout) itemView;
    }

    void bindTo(Task task) {
        TextView tvTitle = ll.findViewById(R.id.tvTitle);
        TextView tvTaskDuration = ll.findViewById(R.id.tvTaskDuration);
        TextView tvDateBegin = ll.findViewById(R.id.tvTaskBegin);

        llTaskDetails = ll.findViewById(R.id.llTaskDetails);

        tvTitle.setText(task.name);
        tvTaskDuration.setText(TimeUnitEnum.fromMilliseconds(task.duration));
        tvDateBegin.setText(DateUtil.formatToDateStringPrefixed("Start on ", task.begin));

        Button btExpandTaskDetails = ll.findViewById(R.id.btExpandTaskDetails);
        btExpandTaskDetails.setOnClickListener(
            v -> toggleTaskDetailsVisibility()
        );
    }

    private void toggleTaskDetailsVisibility() {
        llTaskDetails.setVisibility(
            View.VISIBLE
        );
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
                @NonNull Task newTask)
            {
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
        LinearLayout llListRow =
            (LinearLayout) LayoutInflater
                .from(parent.getContext())
                .inflate(
                    R.layout.list_task_row, parent,
                   false
                );
        return new TaskViewHolder(llListRow);
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
