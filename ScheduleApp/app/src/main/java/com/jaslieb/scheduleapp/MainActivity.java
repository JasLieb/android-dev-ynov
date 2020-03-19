package com.jaslieb.scheduleapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaslieb.scheduleapp.activities.ChildActivity;
import com.jaslieb.scheduleapp.activities.ParentActivity;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.TaskType;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button buttonChildPanel;
    private Button buttonParentPanel;
    private CollectionReference tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChildPanel = findViewById(R.id.buttonChildPanel);
        buttonChildPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(
                    new Intent(getBaseContext(), ChildActivity.class)
                );
            }
        });

        buttonParentPanel = findViewById(R.id.buttonParentPanel);
        buttonParentPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(
                    new Intent(getBaseContext(), ParentActivity.class)
                );
            }
        });
    }

    private void startNewActivity(Intent intent) {
        startActivity(intent);
    }

    // TODO : MOVE INTO SERVICE
    // Firstfruit with FireStore DB
    private void addTaskToDB() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        tasks = database.collection("tasks");

        tasks.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("name") != null) {
                        //cities.add(doc.getString("name"));
                    }
                }
            }
        });

        Date today = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(2020, 3, 20, 12, 0);

        tasks.add(
                new Task("Faire les tests",
                        "AAA",
                        gc.getTimeInMillis(),
                        TimeUnit.MINUTES.toMillis(30),
                        TaskType.EVERYDAY_LIFE
                )
        );
    }
}
