package com.jaslieb.firstfruitandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();

        if(intent.hasExtra(MainActivity.USER_NAME)) {
            String userName = intent.getStringExtra(MainActivity.USER_NAME);
            EditText userNameEditText = findViewById(R.id.userNameLogin);
            userNameEditText.setText(userName);
        }

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        EditText userNameEditText = findViewById(R.id.userNameLogin);
        EditText userPasswordEditText = findViewById(R.id.userPasswordLogin);

        String
            name = userNameEditText.getText().toString(),
            password = userPasswordEditText.getText().toString();

        Intent intent = new Intent();
        if(name.equals("toto") && password.equals("azerty")) {
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_FIRST_USER, intent);
        }

        finish();
    }
}
