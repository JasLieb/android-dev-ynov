package com.jaslieb.scheduleapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jaslieb.scheduleapp.activities.ChildActivity;
import com.jaslieb.scheduleapp.activities.ParentActivity;
import com.jaslieb.scheduleapp.actors.FamilyActor;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 1;

    private FamilyActor familyActor;

    private CompositeDisposable disposable = new CompositeDisposable();

    private DisposableObserver<String> resultObserver =
        new DisposableObserver<String>() {
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull String result) {
                String[] resultSplinted = result.split(" ");
                Intent intent;
                switch (resultSplinted[0]) {
                    case "notConnected":
                        Toast.makeText(getApplicationContext(), "Not connected :(", Toast.LENGTH_LONG).show();
                        break;
                    case "parent":
                        intent = new Intent( getApplicationContext(), ParentActivity.class);
                        intent.putExtra("name", resultSplinted[1]);
                        intent.putExtra("lastName", etLastName.getText().toString());
                        startActivity(intent);
                        break;
                    case "child":
                        intent = new Intent( getApplicationContext(), ChildActivity.class);
                        intent.putExtra("name", resultSplinted[1]);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {}

            @Override
            public void onComplete() {}
        };


    private LinearLayout llPermissions;

    private EditText etLastName;
    private EditText etUserPassword;
    private Button btLogSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        familyActor = FamilyActor.getInstance();

        familyActor.resultBehavior.skip(1).subscribe(resultObserver);
        disposable.add(resultObserver);

        llPermissions = findViewById(R.id.llPermissions);
        Button btPermissions = findViewById(R.id.btPermissions);
        btPermissions.setOnClickListener(v -> openAppSettings());

        etLastName = findViewById(R.id.etFirstName);
        etUserPassword = findViewById(R.id.etUserPassword);

        btLogSubmit = findViewById(R.id.btLogSubmit);

        setButtonsEnabled(
            checkIfPermissionsAllowed()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.disposable.dispose();
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
        btLogSubmit.setEnabled(value);

        llPermissions.setVisibility(
            !value
                ? View.VISIBLE
                : View.GONE
        );

        if (value) {
            LinearLayout llAppBase = findViewById(R.id.llAppBase);
            llAppBase.setOnClickListener(v -> {
                etLastName.clearFocus();
                etUserPassword.clearFocus();
            });

            btLogSubmit.setOnClickListener(v -> {
                String firstName = etLastName.getText().toString();
                String password = etUserPassword.getText().toString();
                familyActor.checkPassword(firstName, password);
                etLastName.clearFocus();
                etUserPassword.clearFocus();
            });
        }
    }
}
