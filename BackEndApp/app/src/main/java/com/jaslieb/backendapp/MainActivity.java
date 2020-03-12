package com.jaslieb.backendapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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

    private void getWeatherFor(String town) {
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
                        weatherResultTxt.setText(String.format("%s - %s °C", weather, temperature));
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
