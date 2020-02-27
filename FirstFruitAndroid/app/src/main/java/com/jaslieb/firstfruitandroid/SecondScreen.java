package com.jaslieb.firstfruitandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.maybeDangerous))
                .setTitle(R.string.warning)
                .show();

        final Button openURLBtn = findViewById(R.id.openURL);
        openURLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.urlWritten);
                openURL(editText.getEditableText().toString());
            }
        });
    }

    private void openURL(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        if(browserIntent.resolveActivity(getPackageManager()) != null && isURL(url)) {
            startActivity(browserIntent);
        } else {
            Toast.makeText(this, R.string.notAURL, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isURL(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
