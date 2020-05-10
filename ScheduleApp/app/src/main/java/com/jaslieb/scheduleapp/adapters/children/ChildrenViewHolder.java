package com.jaslieb.scheduleapp.adapters.children;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.activities.ParentActivity;
import com.jaslieb.scheduleapp.adapters.tasks.ParentTasksAdapter;
import com.jaslieb.scheduleapp.fragments.AddTaskFormFragment;
import com.jaslieb.scheduleapp.models.tasks.Task;

import java.util.List;

public class ChildrenViewHolder extends RecyclerView.ViewHolder {

    private ParentActivity parentActivity;
    private LinearLayout llAddTask;
    private View view;

    public ChildrenViewHolder(ParentActivity parentActivity, @NonNull View itemView, String childName) {
        super(itemView);
        this.parentActivity = parentActivity;
        this.view = itemView;
    }

    public void bindTo(String childName, List<Task> tasks) {
        TextView tvAboutChild = view.findViewById(R.id.tvAboutChild);

        if(childName != null) {
            tvAboutChild.setText("About " + childName);
        }

        llAddTask = view.findViewById(R.id.llAddTask);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        RecyclerView taskList = view.findViewById(R.id.lvTasks);

        ParentTasksAdapter tasksAdapter = new ParentTasksAdapter(parentActivity);
        taskList.setLayoutManager(layoutManager);
        taskList.setAdapter(tasksAdapter);
        tasksAdapter.setListItem(tasks);

        Button btShowAddTask = view.findViewById(R.id.btShowAddTask);
        btShowAddTask.setOnClickListener(v -> {
            llAddTask.setVisibility(
                    llAddTask.getVisibility() == View.VISIBLE
                            ? View.GONE
                            : View.VISIBLE
            );

            if(llAddTask.getVisibility() == View.VISIBLE) {
                AddTaskFormFragment fragment = new AddTaskFormFragment();
                FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("childName", childName);
                fragment.setArguments(args);
                transaction.replace(R.id.llAddTask, fragment);
                transaction.commit();
            }
        });

        Button btShowTaskChild = view.findViewById(R.id.btShowTaskChild);
        btShowTaskChild.setOnClickListener(v -> {
            taskList.setVisibility(
                    taskList.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });
    }
}
