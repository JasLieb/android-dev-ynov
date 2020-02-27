package com.jaslieb.firstfruitandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.reflect.Array.getInt;

public class MainActivity extends AppCompatActivity {

    private static final String
            TAG = "[MainActivity]",
            COUNTERKEY = "counter",
            ONCREATEMESSAGE = "OnCreate ()",
            ONSAVEINSTANCEMESSAGE = "OnSaveInstance ()",
            ONRESUMEMESSAGE = "OnResume ()",
            ONSTOPMESSAGE = "OnStop ()",
            ONSTARTMESSAGE = "OnStart()",
            ONDESTROYMESSAGE = "OnDestroy ()";

    private int clickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, ONCREATEMESSAGE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            clickCount = savedInstanceState.getInt(COUNTERKEY, 0);
        } else {
            clickCount = 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, ONSAVEINSTANCEMESSAGE);
        outState.putInt(COUNTERKEY, clickCount);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, ONRESUMEMESSAGE);
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, ONSTOPMESSAGE);
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, ONSTARTMESSAGE);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, ONDESTROYMESSAGE);
        super.onDestroy();
    }

    public void onClickCounter(View v) {
        Log.d(TAG, "Counter button clicked : +1" );
        incrementCounter();
        TextView textView = findViewById(R.id.helloText);
        textView.setText(getCounterString());
    }

    private void incrementCounter() {
        clickCount++;
    }

    private String getCounterString() {
        return String.format("You have clicked %d times", clickCount);
    }
}
