package com.jaslieb.secondapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jaslieb.secondapp.R;
import com.jaslieb.secondapp.models.User;

public class UsersAdapter extends ArrayAdapter<User> {
    UsersAdapter ref= this;
    public UsersAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final User user = getItem(position);
        if (convertView == null) {
            convertView =
                    LayoutInflater
                            .from(getContext())
                            .inflate(R.layout.user_list_row, parent, false);
        }


        TextView tvName = convertView.findViewById(R.id.tvName);
        Switch swIsSelected = convertView.findViewById(R.id.tvIsSelected);

        tvName.setText(user.name);

        swIsSelected.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateUserSelected(position, isChecked);
            }
        });
        swIsSelected.setChecked(user.isSelected);

        return convertView;
    }

    private void updateUserSelected(int position, boolean isSelected) {
        getItem(position).setIsSelected(isSelected);
        this.notifyDataSetChanged();
    }
}
