package com.jaslieb.scheduleapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jaslieb.scheduleapp.activities.ChildActivity;
import com.jaslieb.scheduleapp.activities.ParentActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 1;

    private LinearLayout llPermissions;
    private Button btChildPanel;
    private Button btParentPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llPermissions = findViewById(R.id.llPermissions);
        Button btPermissions = findViewById(R.id.btPermissions);
        btPermissions.setOnClickListener(v -> {
            openAppSettings();
        });

        btChildPanel = findViewById(R.id.btChildPanel);
        btParentPanel = findViewById(R.id.btParentPanel);

        setButtonsEnabled(
            checkIfPermissionsAllowed()
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setButtonsEnabled(true);
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                // Display why
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(
            Uri.fromParts("package", getPackageName(), null)
        );
        startActivity(intent);
    }

    private boolean checkIfPermissionsAllowed() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.SEND_SMS
                },
                PERMISSIONS_REQUEST_SEND_SMS
            );
            return false;
        }
        return true;
    }

    private void setButtonsEnabled(boolean value) {
        btChildPanel.setEnabled(value);
        btParentPanel.setEnabled(value);

        llPermissions.setVisibility(
            !value
                ? View.VISIBLE
                : View.GONE
        );

        if (value) {
            btChildPanel.setOnClickListener(v -> startActivity(
                new Intent(getBaseContext(), ChildActivity.class)
            ));

            btParentPanel.setOnClickListener(v -> startActivity(
                new Intent(getBaseContext(), ParentActivity.class)
            ));
        }
    }
}
