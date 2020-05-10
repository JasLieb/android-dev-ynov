package com.jaslieb.scheduleapp.adapters.children;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.activities.ParentActivity;
import com.jaslieb.scheduleapp.actors.FamilyActor;
import com.jaslieb.scheduleapp.models.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildrenAdapter extends ListAdapter<Task, ChildrenViewHolder> {

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

    private Map<String, List<Task>> childrenTaskMap;
    private ParentActivity parentActivity;

    public ChildrenAdapter(ParentActivity parentActivity) {
        super(DIFF_CALLBACK);
        this.parentActivity = parentActivity;
        this.childrenTaskMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildrenViewHolder(
            this.parentActivity,
            LayoutInflater
                .from(parent.getContext())
                .inflate(
                    R.layout.list_parent_children_row,
                    parent,
                    false
                ),
                "toto"
        );
    }

    @Override
    public int getItemCount() {
        return childrenTaskMap.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ChildrenViewHolder holder, int position) {
        Log.d("CHILDREN APDAPTER", "CHECK COUNT SUP TO 0");
        if (getItemCount() >  0) {
            String childName = FamilyActor.getInstance().childrenNamesBehavior.getValue().get(position);
            Log.d("CHILDREN APDAPTER", "BEGIN BIND TO");
            holder.bindTo(
                childName,
                childrenTaskMap.get(childName)
            );
        }
    }

    public void setListItem(Map<String, List<Task>> map) {
        Log.d("CHILDREN APDAPTER", "Update LIST ITEMS");
        this.childrenTaskMap = map;
        submitList(null);
        submitList(new ArrayList<>());
        notifyDataSetChanged();
    }
}
