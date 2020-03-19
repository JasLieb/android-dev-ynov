package com.jaslieb.scheduleapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.services.ParentService;
import com.jaslieb.scheduleapp.states.ParentState;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ParentActivity extends AppCompatActivity {

    private Button btAddTasks;
    private EditText etTaskName;
    private EditText etTimeValue;
    private Spinner spTimeUnit;
    private NumberPicker npTimeValue;

    /// TODO move to state
    private int max;
    private ParentState state;
    private ParentService service;
    private CompositeDisposable disposable = new CompositeDisposable();

    private DisposableObserver<ParentState> childStateObserver =
            new DisposableObserver<ParentState>() {
                @Override
                public void onNext(@NonNull ParentState parentState) {
                    state = parentState;
                }

                @Override
                public void onError(@NonNull Throwable e) {}

                @Override
                public void onComplete() {}
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        etTaskName = findViewById(R.id.etTaskName);

        etTimeValue = findViewById(R.id.etTimeValue);
        etTimeValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                checkTimeValue();
            }
        });

        spTimeUnit = findViewById(R.id.spTimeUnit);
        spTimeUnit.setAdapter(
            getTimeUnitAdapter()
        );

        spTimeUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(TimeUnitEnum unit: TimeUnitEnum.values()) {
                    if(unit.position == position){
                        max = unit.max;
                        etTimeValue.setHint(formatMaxValue(max));
                        checkTimeValue();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btAddTasks = findViewById(R.id.btAddTasks);
        btAddTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = etTaskName.getText().toString();
                if(taskName.length() > 0) {
                    service.addTask(taskName);
                }
            }
        });

        service = new ParentService();
        service.parentStateBehavior.subscribe(childStateObserver);
        disposable.add(childStateObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private ArrayAdapter<String> getTimeUnitAdapter() {
        ArrayList<String> timeUnits = new ArrayList<>();
        for(TimeUnitEnum unit: TimeUnitEnum.values()) {
            timeUnits.add(unit.toString());
        }

        return new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, timeUnits);
    }

    private void checkTimeValue() {
        String value = etTimeValue.getText().toString();
        if(
            !value.isEmpty() &&
            Integer.parseInt(value) > max
        ) {
            etTimeValue.setError(
                String.format("Maximum allowed : %d", max)
            );
        }
    }

    private String formatMaxValue(int max) {
        return String.format("Max : %d", max);
    }
}
