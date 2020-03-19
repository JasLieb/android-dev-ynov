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

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChildTasksAdapter extends ArrayAdapter<Task> {

    private SimpleDateFormat fmt;
    public ChildTasksAdapter(@NonNull Context context) {
        super(context, 0);
        fmt = new SimpleDateFormat("dd MMMM yyyy hh:mm");
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
        tvDateBegin.setText(format(task.begin));

        return convertView;
    }

    private String format(long begin) {
        return fmt.format(new Date(begin));
    }
}
