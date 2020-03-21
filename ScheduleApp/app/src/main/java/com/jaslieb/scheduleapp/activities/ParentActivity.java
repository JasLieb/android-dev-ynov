package com.jaslieb.scheduleapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.services.ParentService;
import com.jaslieb.scheduleapp.states.ParentState;
import com.jaslieb.scheduleapp.utils.DateUtil;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ParentActivity extends AppCompatActivity {

    private EditText etTaskName;
    private EditText etTimeValue;

    private Spinner spTaskType;
    private Spinner spTimeUnit;
    private Spinner spRepeatUnit;

    private TimePicker tpTaskTimeBegin;
    private DatePicker dpDateBegin;

    private CheckBox cbHaveRecurrence;
    private LinearLayout llTaskRecurrence;
    private Button btAddTasks;

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
        tpTaskTimeBegin.setOnTimeChangedListener(
            (view, hourOfDay, minute) -> updateRepeatUnit()
        );

        dpDateBegin = findViewById(R.id.dpDateBegin);
        dpDateBegin.setMinDate(System.currentTimeMillis());
        dpDateBegin.setOnDateChangedListener(
            (view, year, monthOfYear, dayOfMonth) -> updateRepeatUnit()
        );

        llTaskRecurrence = findViewById(R.id.includeRecurrenceLayout);
        cbHaveRecurrence = findViewById(R.id.cbHaveRecurrence);

        cbHaveRecurrence.setOnCheckedChangeListener(
            (buttonView, isChecked) ->
                llTaskRecurrence.setVisibility(
                    isChecked ? View.VISIBLE : View.GONE
                )
        );

        spRepeatUnit = llTaskRecurrence.findViewById(R.id.spRepeatUnit);
        updateRepeatUnit();

        btAddTasks = findViewById(R.id.btAddTasks);
        btAddTasks.setOnClickListener(v -> {
            String taskName = etTaskName.getText().toString();
            long beginTime = getBeginTime();

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
        });

        service = new ParentService();
        service.parentStateBehavior.subscribe(childStateObserver);
        disposable.add(childStateObserver);
    }

    private void updateRepeatUnit() {
        spRepeatUnit.setAdapter(
            getTimeUnitAdapter(2)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private long getBeginTime() {
        return
            new GregorianCalendar(
                dpDateBegin.getYear(),
                dpDateBegin.getMonth(),
                dpDateBegin.getDayOfMonth(),
                tpTaskTimeBegin.getHour(),
                tpTaskTimeBegin.getMinute()
            )
            .getTimeInMillis();
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
        return getSpinnerAdapter(
            getTimeUnits()
        );
    }

    private ArrayAdapter<String> getTimeUnitAdapter(int minPosition) {
        return getSpinnerAdapter(
            getRecurrenceTimeUnits(minPosition, getBeginTime())
        );
    }

    private List<String> getTimeUnits() {
        ArrayList<String> timeUnits = new ArrayList<>();
        for(TimeUnitEnum unit: TimeUnitEnum.values()) {
            timeUnits.add(unit.toString());
        }
        return timeUnits;
    }

    private List<String> getRecurrenceTimeUnits(int minPosition, long taskBegin) {
        ArrayList<String> timeUnits = new ArrayList<>();
        for(TimeUnitEnum unit: TimeUnitEnum.values()) {
            if(unit.position >= minPosition) {
                timeUnits.add(
                    String.format(
                        "%s (next on %s)",
                        unit.toString(),
                        DateUtil.formatToDateString(
                            taskBegin + unit.toMilliseconds(1)
                        )
                    )
                );
            }
        }

        return timeUnits;
    }

    private ArrayAdapter<String> getSpinnerAdapter(List<String> strings) {
        return new ArrayAdapter<>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            strings
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
