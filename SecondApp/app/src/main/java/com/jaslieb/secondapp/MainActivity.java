package com.jaslieb.secondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.jaslieb.secondapp.adapters.UsersAdapter;
import com.jaslieb.secondapp.models.User;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    ArrayList<User> arrayOfUsers = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button alphaOrderBtn = findViewById(R.id.alphaOrderBtn);
        alphaOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderPreference(true);
            }
        });

        Button invertOrderBtn = findViewById(R.id.invertOrderBtn);
        invertOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderPreference(false);
            }
        });

        if(arrayOfUsers.size() == 0) {
            arrayOfUsers.add(new User("toto", true));
            arrayOfUsers.add(new User("tata", false));
            arrayOfUsers.add(new User("titi", false));
            arrayOfUsers.add(new User("tutu", false));
            arrayOfUsers.add(new User("tete", false));
            arrayOfUsers.add(new User("teto", false));
            arrayOfUsers.add(new User("tato", false));
            arrayOfUsers.add(new User("tati", false));
            arrayOfUsers.add(new User("tito", false));
            arrayOfUsers.add(new User("tote", false));
            arrayOfUsers.add(new User("tota", false));
            arrayOfUsers.add(new User("tota", false));
            arrayOfUsers.add(new User("tati", false));
            arrayOfUsers.add(new User("tito", false));
            arrayOfUsers.add(new User("tote", false));
            arrayOfUsers.add(new User("tota", false));
            arrayOfUsers.add(new User("tota", false));
            arrayOfUsers.add(new User("tota", false));
            arrayOfUsers.add(new User("tota", false));
            arrayOfUsers.add(new User("tota", false));
            arrayOfUsers.add(new User("tota", false));
        }

        buildUserList();
    }

    private void buildUserList() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        boolean isAlphaOrder = prefs.getBoolean("AlphabeticalOrderUsers", false);
        if(isAlphaOrder)
            arrayOfUsers.sort(new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o1.name.compareTo(o2.name);
                }
            });
        else
            arrayOfUsers.sort(new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return o2.name.compareTo(o1.name);
                }
            });

        UsersAdapter adapter = new UsersAdapter(this);
        adapter.addAll(arrayOfUsers);

        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);
    }

    private void setOrderPreference(boolean isAlphabeticalOrder) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("AlphabeticalOrderUsers", isAlphabeticalOrder);
        editor.commit();
        buildUserList();
    }
}
