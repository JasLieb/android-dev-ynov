package com.jaslieb.backendapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int SHORT_DURATION_ANIMATION = 500;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int RESULT_PICK_CONTACT = 1;

    private String weatherString;
    private String phoneNo;
    private EditText townNameEdt;
    private FrameLayout resultFrame;
    private TextView weatherResultTxt;
    private ImageView weatherIcon;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        townNameEdt = findViewById(R.id.townNameEdt);
        resultFrame = findViewById(R.id.resultFrame);
        resultFrame.setVisibility(View.GONE);

        weatherResultTxt = findViewById(R.id.weatherResultTxt);
        weatherIcon = findViewById(R.id.weatherIconImg);

        queue = Volley.newRequestQueue(this);

        Button townWeatherBtn = findViewById(R.id.weatherTownBtn);
        townWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String townName = townNameEdt.getText().toString();

                if(townName.length() > 0) {
                    hideKeyboard();
                    getWeatherFor(townName);
                } else {
                    Toast.makeText(getApplicationContext(), "Please give a town name", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button sendWeatherBtn = findViewById(R.id.sendWeatherBtn);
        sendWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent =
                    new Intent(
                            Intent.ACTION_PICK,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    );
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    try {
                        Cursor cursor = null;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(checkSendSMSPermission()){
                            sendSMS();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (
                        grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    sendSMS();
                }
                return;
            }

        }
    }


    private boolean checkSendSMSPermission() {
        if (
            ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
           return true;
        }

        if (
                ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.SEND_SMS
                )
        ) {
            ActivityCompat.requestPermissions(
                    this,
                        new String[]{
                            Manifest.permission.SEND_SMS
                    },
                    PERMISSIONS_REQUEST_SEND_SMS
            );
        } else {
            // ShutDown App here or go to preferences > this app > permissions
        }
        return false;
    }

    private void sendSMS() {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(phoneNo, null, weatherString, null, null);
    }

    private void hideKeyboard() {
        try {

            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWeatherFor(final String town) {
        String url ="https://www.prevision-meteo.ch/services/json/" + town.toLowerCase();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject
                                jsonResponse = new JSONObject(response),
                                currentCondition = jsonResponse.getJSONObject("current_condition");

                        String
                                weather = currentCondition.getString("condition"),
                                temperature = currentCondition.getString("tmp"),
                                icon = currentCondition.getString("icon");

                        Picasso.get().load(icon).into(weatherIcon);

                        weatherString = String.format("%s : %s - %s °C", town.toUpperCase(), weather, temperature);
                        weatherResultTxt.setText(weatherString);

                        crossFadeResult();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    weatherResultTxt.setText("500 - Internal Server Error !");
                }
            }
        );

        queue.add(stringRequest);
    }

    private void crossFadeResult() {
        resultFrame.setAlpha(0f);
        resultFrame.setVisibility(View.VISIBLE);

        resultFrame.animate()
                .alpha(1f)
                .setDuration(SHORT_DURATION_ANIMATION);
    }
}
