package com.jaslieb.secondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jaslieb.secondapp.adapters.UsersAdapter;
import com.jaslieb.secondapp.models.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<User> arrayOfUsers = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list = findViewById(R.id.listView);
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

        UsersAdapter adapter = new UsersAdapter(this);
        adapter.addAll(arrayOfUsers);
        list.setAdapter(adapter);
    }
}
