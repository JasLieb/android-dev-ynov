package com.jaslieb.backendapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

    private EditText townNameEdt;
    private TextView weatherResultTxt;
    private ImageView weatherIcon;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        townNameEdt = findViewById(R.id.townNameEdt);
        weatherResultTxt = findViewById(R.id.weatherResultTxt);
        weatherIcon = findViewById(R.id.weatherIconImg);

        queue = Volley.newRequestQueue(this);

        Button townWeatherBtn = findViewById(R.id.weatherTownBtn);
        townWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                getWeatherFor(townNameEdt.getText().toString());
            }
        });
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
}
