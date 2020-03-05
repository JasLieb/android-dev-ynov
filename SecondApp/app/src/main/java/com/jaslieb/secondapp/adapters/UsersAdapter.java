package com.jaslieb.secondapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jaslieb.secondapp.MainActivity;
import com.jaslieb.secondapp.R;
import com.jaslieb.secondapp.fragments.UserProfileFragment;
import com.jaslieb.secondapp.models.User;

public class UsersAdapter extends ArrayAdapter<User> {
    private static final String FRAGMENT_TAG = "USER_PROFILE_FRAGMENT";

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
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserProfile(getItem(position));
            }
        });
        tvName.setText(user.name);

        Switch swIsSelected = convertView.findViewById(R.id.tvIsSelected);
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

    private void showUserProfile(User user) {
        Fragment userProfile = new UserProfileFragment();
        FragmentManager manager = ((MainActivity) getContext()).getSupportFragmentManager();

        if(manager.findFragmentByTag(FRAGMENT_TAG) == null) {
            FragmentTransaction transaction = manager.beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putString("name", user.name);
            userProfile.setArguments(bundle);

            transaction.add(R.id.userProfileFragmentContainer, userProfile, FRAGMENT_TAG);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
