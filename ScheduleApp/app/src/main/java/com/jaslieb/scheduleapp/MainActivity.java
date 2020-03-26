package com.jaslieb.scheduleapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.jaslieb.scheduleapp.activities.ChildActivity;
import com.jaslieb.scheduleapp.activities.ParentActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonChildPanel;
    private Button buttonParentPanel;

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
}
