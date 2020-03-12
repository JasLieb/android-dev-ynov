package com.jaslieb.backendapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WeatherHistoryActivity extends AppCompatActivity {

    private DBHelper db;

    private ListView weatherHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_history);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.weather_list_row, R.id.weatherHistoryTxt);
        db = new DBHelper(this);

        Cursor cursor = db.readAllData();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String weather =
                String.format(
                        "%s : %s°C",
                        cursor.getString(cursor.getColumnIndex("col_town")),
                        cursor.getString(cursor.getColumnIndex("col_weather"))
                );
            adapter.add(weather);
            cursor.moveToNext();
        }
        cursor.close();

        weatherHistory = findViewById(R.id.weatherHistoryList);
        weatherHistory.setAdapter(adapter);
    }
}
