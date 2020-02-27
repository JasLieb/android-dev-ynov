package com.jaslieb.firstfruitandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.reflect.Array.getInt;

public class MainActivity extends AppCompatActivity {

    public static final int LOGIN_ACTIVITY_RESULT_CODE = 0;
    public static final String LOGIN_ACTIVITY_RESULT_KEY_INTENT = "isLogged";
    public static final String USER_NAME = "userName";

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

        clickCount = getSavedCountFrom(savedInstanceState);

        Button nextActivityButton = findViewById(R.id.goToNextActivity);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSecondActivity();
            }
        });

        final Button goToLoginBtn = findViewById(R.id.goToLoginActivity);
        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText =  findViewById(R.id.userName);
                startLoginActivity(editText.getText().toString());
            }
        });

        Button incrementCounterBtn = findViewById(R.id.incrementCounter);
        incrementCounterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementCounter();
                updateTextView();
            }
        });
    }

    private void startLoginActivity(String userName) {
        Intent loginIndent = new Intent(this, LoginActivity.class);

        if(!userName.isEmpty()) {
            loginIndent.putExtra(USER_NAME, userName);
        }

        startActivityForResult(loginIndent, LOGIN_ACTIVITY_RESULT_CODE);
    }

    private void startSecondActivity() {
        Intent intent = new Intent(this, SecondScreen.class);
        startActivity(intent);
    }

    private int getSavedCountFrom(Bundle instance) {
        return instance != null
                ? instance.getInt(COUNTERKEY)
                : 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == LOGIN_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.isLogged, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.isNotLogged, Toast.LENGTH_LONG).show();
            }
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

    private void updateTextView() {
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
