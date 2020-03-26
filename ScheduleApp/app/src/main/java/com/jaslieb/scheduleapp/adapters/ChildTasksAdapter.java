package com.jaslieb.scheduleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.utils.DateUtil;

public class ChildTasksAdapter extends ArrayAdapter<Task> {
    public ChildTasksAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Task task = getItem(position);
        if (convertView == null) {
            convertView =
                LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.list_task_row, parent, false);
        }
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvTaskDuration = convertView.findViewById(R.id.tvTaskDuration);
        TextView tvDateBegin = convertView.findViewById(R.id.tvTaskBegin);

        tvTitle.setText(task.name);
        tvTaskDuration.setText(TimeUnitEnum.fromMilliseconds(task.duration));
        tvDateBegin.setText(DateUtil.formatToDateString(task.begin));

        return convertView;
    }
}
