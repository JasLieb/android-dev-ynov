package com.jaslieb.scheduleapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.services.ParentService;
import com.jaslieb.scheduleapp.states.ParentState;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ParentActivity extends AppCompatActivity {

    private Button btAddTasks;
    private EditText etTaskName;
    private EditText etTimeValue;

    private Spinner spTaskType;
    private Spinner spTimeUnit;

    private TimePicker tpTaskTimeBegin;
    private DatePicker dpDateBegin;

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
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTimeValue(TimeUnitEnum.find(spTimeUnit.getSelectedItem().toString()));
            }

            public void afterTextChanged(Editable s) {}
        });

        spTaskType = findViewById(R.id.spTaskType);
        spTaskType.setAdapter(
            getTaskTypeAdapter()
        );

        spTimeUnit = findViewById(R.id.spTimeUnit);
        spTimeUnit.setAdapter(
            getTimeUnitAdapter()
        );

        spTimeUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimeUnitEnum timeUnit = TimeUnitEnum.find(position);
                checkTimeValue(timeUnit);
                etTimeValue.setHint(formatMaxValue(timeUnit));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        tpTaskTimeBegin = findViewById(R.id.tpTaskTimeBegin);

        dpDateBegin = findViewById(R.id.dpDateBegin);
        dpDateBegin.setMinDate(System.currentTimeMillis());

        btAddTasks = findViewById(R.id.btAddTasks);
        btAddTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = etTaskName.getText().toString();
                long beginTime =
                    new GregorianCalendar(
                        dpDateBegin.getYear(),
                        dpDateBegin.getMonth(),
                        dpDateBegin.getDayOfMonth(),
                        tpTaskTimeBegin.getHour(),
                        tpTaskTimeBegin.getMinute()
                    )
                    .getTimeInMillis();

                long duration =
                    TimeUnitEnum.find(
                        spTimeUnit
                            .getSelectedItem()
                            .toString()
                    )
                    .toMilliseconds(
                        etTimeValue.getText().toString()
                    );

                TaskTypeEnum type = TaskTypeEnum.find(spTaskType.getSelectedItemId());

                if(taskName.length() > 0) {
                    service.addTask(taskName, beginTime, duration, type);
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

    private ArrayAdapter<String> getTaskTypeAdapter() {
        ArrayList<String> taskTypes = new ArrayList<>();
        for(TaskTypeEnum type: TaskTypeEnum.values()) {
            taskTypes.add(type.toString());
        }

        return new ArrayAdapter<>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            taskTypes
        );
    }

    private ArrayAdapter<String> getTimeUnitAdapter() {
        ArrayList<String> timeUnits = new ArrayList<>();
        for(TimeUnitEnum unit: TimeUnitEnum.values()) {
            timeUnits.add(unit.toString());
        }

        return new ArrayAdapter<>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            timeUnits
        );
    }

    private void checkTimeValue(TimeUnitEnum timeUnit) {
        String value = etTimeValue.getText().toString();
        if(
            !value.isEmpty() &&
            Integer.parseInt(value) > timeUnit.max
        ) {
            etTimeValue.setError(
                String.format("Maximum allowed : %d", timeUnit.max)
            );
        }
    }

    private String formatMaxValue(TimeUnitEnum timeUnit) {
        return String.format("Max : %d", timeUnit.max);
    }
}
