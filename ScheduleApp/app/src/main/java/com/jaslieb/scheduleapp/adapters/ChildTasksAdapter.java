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

import java.text.SimpleDateFormat;

public class ChildTasksAdapter extends ArrayAdapter<Task> {

    private SimpleDateFormat fmt;
    public ChildTasksAdapter(@NonNull Context context) {
        super(context, 0);
        fmt = new SimpleDateFormat("dd MMMM yyyy");
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
        TextView tvDateBegin = convertView.findViewById(R.id.tvDateBegin);

        tvTitle.setText(task.name);
        tvDateBegin.setText(format(task.begin));

        return convertView;
    }

    private String format(long begin) {
        return fmt.format(begin);
    }
}
