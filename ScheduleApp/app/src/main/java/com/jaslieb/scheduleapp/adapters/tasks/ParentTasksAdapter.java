package com.jaslieb.scheduleapp.adapters.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.activities.ParentActivity;
import com.jaslieb.scheduleapp.adapters.tasks.viewHolders.ParentTaskViewHolder;
import com.jaslieb.scheduleapp.models.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ParentTasksAdapter extends ListAdapter<Task, ParentTaskViewHolder> {
    private ParentActivity parentActivity;
    private List<Task> tasks;
    private int lastPosition = -1;

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldTask,@NonNull Task newTask) {
                return false;
            }
            @Override
            public boolean areContentsTheSame(@NonNull Task oldTask,@NonNull Task newTask) {
                return false;
            }
        };

    public ParentTasksAdapter(ParentActivity parentActivity) {
        super(DIFF_CALLBACK);
        this.parentActivity = parentActivity;
        tasks = new ArrayList<>();
    }

    @NonNull
    @Override
    public ParentTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentTaskViewHolder(
            LayoutInflater
                .from(parent.getContext())
                .inflate(
                    R.layout.list_parent_task_row, parent,
                    false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ParentTaskViewHolder holder, int position) {
        if (getItemCount() >  0) {
            holder.bindTo(
                getItem(position)
            );
            setAnimationFadeIn(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ParentTaskViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        setAnimationFadeOut(holder.itemView);
        holder.clearAnimation();
    }

    private void setAnimationFadeIn(View viewToAnimate, int position)
    {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(parentActivity, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void setAnimationFadeOut(View viewToAnimate)
    {
        Animation animation = AnimationUtils.loadAnimation(parentActivity, android.R.anim.fade_out);
        viewToAnimate.startAnimation(animation);
    }

    public void setListItem(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        submitList(null);
        submitList(tasks);
        notifyDataSetChanged();
    }
}
