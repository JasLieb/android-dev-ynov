package com.jaslieb.scheduleapp.activities;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.models.Reminder;
import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.actors.ParentActor;
import com.jaslieb.scheduleapp.services.AlarmService;
import com.jaslieb.scheduleapp.states.ParentState;
import com.jaslieb.scheduleapp.utils.DateUtil;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

import static java.lang.String.*;

public class ParentActivity extends AppCompatActivity {

    private ParentState state;
    private ParentActor service;
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

    private Button btShowAddTask;
    private LinearLayout llAddTask;

    private EditText etTaskName;

    private EditText etTimeValue;
    private TextWatcher taskTimeTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkEtValueAsTime(
                etTimeValue,
                TimeUnitEnum.find(
                    spTaskDurationTU.getSelectedItem().toString()
                )
            );
        }

        public void afterTextChanged(Editable s) {}
    };

    private Spinner spTaskType;

    private Spinner spTaskDurationTU;

    private TimePicker tpTaskTimeBegin;
    private DatePicker dpDateBegin;

    private CheckBox cbHaveRecurrence;
    private LinearLayout llTaskRecurrence;
    private Spinner spTaskRecurrenceTU;

    private CheckBox cbHaveReminder;
    private LinearLayout llTaskReminder;
    private EditText etTaskCountReminder;
    private EditText etTaskReminderValue;
    private TextWatcher taskReminderTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkEtValueAsTime(
                etTaskReminderValue,
                TimeUnitEnum.find(
                    spTaskReminderTU.getSelectedItem().toString()
                )
            );
        }

        public void afterTextChanged(Editable s) {}
    };

    private Spinner spTaskReminderTU;
    private Spinner spTaskReminderBeAf;

    private View.OnClickListener onClickAddTaskListener = v -> {
        String taskName = etTaskName.getText().toString();
        TaskTypeEnum type = TaskTypeEnum.find(spTaskType.getSelectedItemId());

        long beginTime = getTaskBeginTime();
        long duration =
            TimeUnitEnum.find(
                spTaskDurationTU
                    .getSelectedItem()
                    .toString()
            )
            .toMilliseconds(
                etTimeValue.getText().toString()
            );

        boolean hasRecurrence = cbHaveRecurrence.isChecked();
        TimeUnitEnum recurrence = null;
        if(hasRecurrence) {
            recurrence =
                TimeUnitEnum.find(
                    spTaskRecurrenceTU
                        .getSelectedItem()
                        .toString()
                        .split(" ")[0]
                );
        }

        boolean hasReminder = cbHaveReminder.isChecked();
        Reminder reminder = null;
        if(hasReminder) {
             reminder = new Reminder(
                 false,
                 spTaskReminderBeAf.getSelectedItem().toString().equals("Before"),
                 Integer.parseInt(etTaskCountReminder.getText().toString()),
                 TimeUnitEnum.find(
                     spTaskReminderTU
                         .getSelectedItem()
                        .toString()
                 )
                 .toMilliseconds(
                    etTaskReminderValue.getText().toString()
                 )
            );
        }

        if(taskName.length() > 0) {
            service.addTask(taskName, beginTime, duration, type, recurrence, reminder);
        }

        lostFocusOnEditText();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(AlarmService.isRunning) {
            Intent intent = new Intent(this, AlarmService.class);
            stopService(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        btShowAddTask = findViewById(R.id.btShowAddTask);;
        btShowAddTask.setOnClickListener(v -> {
            llAddTask.setVisibility(
                    llAddTask.getVisibility() == View.VISIBLE
                    ? View.GONE
                    : View.VISIBLE
            );
        });
        llAddTask = findViewById(R.id.llAddTask);

        etTaskName = findViewById(R.id.etTaskName);

        etTimeValue = findViewById(R.id.etTimeValue);
        etTimeValue.addTextChangedListener(taskTimeTextWatcher);

        spTaskType = findViewById(R.id.spTaskType);
        spTaskType.setAdapter(
            getTaskTypeAdapter()
        );

        spTaskDurationTU = findViewById(R.id.spTimeUnit);
        spTaskDurationTU.setAdapter(
            getTimeUnitAdapter()
        );

        spTaskDurationTU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkEtValueAsTime(etTimeValue, TimeUnitEnum.find(position));
                lostFocusOnEditText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        tpTaskTimeBegin = findViewById(R.id.tpTaskTimeBegin);
        tpTaskTimeBegin.setOnTimeChangedListener(
            (view, hourOfDay, minute) -> {
                updateSpTaskTU(spTaskRecurrenceTU, 2, getTaskBeginTime());
                lostFocusOnEditText();
            }
        );

        dpDateBegin = findViewById(R.id.dpDateBegin);
        dpDateBegin.setMinDate(System.currentTimeMillis());
        dpDateBegin.setOnDateChangedListener(
            (view, year, monthOfYear, dayOfMonth) -> {
                updateSpTaskTU(spTaskRecurrenceTU, 2, getTaskBeginTime());
                lostFocusOnEditText();
            }
        );

        llTaskRecurrence = findViewById(R.id.llTaskRecurrence);

        cbHaveRecurrence = findViewById(R.id.cbHaveRecurrence);
        cbHaveRecurrence.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                llTaskRecurrence.setVisibility(
                    isChecked ? View.VISIBLE : View.GONE
                );
                lostFocusOnEditText();
            }
        );

        spTaskRecurrenceTU = findViewById(R.id.spTaskRepeatTU);
        spTaskRecurrenceTU.setAdapter(
            getTimeUnitAdapter(1, getTaskBeginTime())
        );

        llTaskReminder = findViewById(R.id.llTaskReminder);
        cbHaveReminder = findViewById(R.id.cbHaveReminder);
        cbHaveReminder.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                llTaskReminder.setVisibility( isChecked ? View.VISIBLE : View.GONE);
                lostFocusOnEditText();
            }
        );

        etTaskCountReminder = findViewById(R.id.etTaskCountReminder);
        etTaskReminderValue = findViewById(R.id.etTaskReminderValue);

        etTaskReminderValue.addTextChangedListener(taskReminderTextWatcher);
        spTaskReminderTU = findViewById(R.id.spTaskReminderTU);
        spTaskReminderTU.setAdapter(
            getTimeUnitAdapter(1)
        );

        spTaskReminderTU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkEtValueAsTime(etTaskReminderValue, TimeUnitEnum.find(position + 1));
                lostFocusOnEditText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spTaskReminderBeAf = findViewById(R.id.spTaskReminderBeAf);
        spTaskReminderBeAf.setAdapter(
            new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                new String[]{"Before", "After"}
            )
        );

        Button btAddTasks = findViewById(R.id.btAddTasks);
        btAddTasks.setOnClickListener(onClickAddTaskListener);

        service = new ParentActor();
        service.parentStateBehavior.subscribe(childStateObserver);
        disposable.add(childStateObserver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    private void lostFocusOnEditText() {
        etTaskName.clearFocus();
        etTimeValue.clearFocus();
        etTaskReminderValue.clearFocus();
        etTaskCountReminder.clearFocus();
    }

    private void updateSpTaskTU(Spinner sp, int minPosition, long  taskBegin) {
        sp.setAdapter(
            getTimeUnitAdapter(minPosition, taskBegin)
        );
    }

    private long getTaskBeginTime() {
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
            getTimeUnits(0, 0)
        );
    }

    private ArrayAdapter<String> getTimeUnitAdapter(int minPosition) {
        return getSpinnerAdapter(
            getTimeUnits(minPosition, 0)
        );
    }

    private ArrayAdapter<String> getTimeUnitAdapter(int minPosition, long taskBegin) {
        return getSpinnerAdapter(
                getTimeUnits(minPosition, taskBegin)
        );
    }

    private List<String> getTimeUnits(int minPosition, long taskBegin) {
        ArrayList<String> timeUnits = new ArrayList<>();
        for(TimeUnitEnum unit: TimeUnitEnum.values()) {
            if(unit.position >= minPosition) {
                timeUnits.add(
                    taskBegin == 0
                        ? unit.toString()
                        : format(
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

    private void checkEtValueAsTime(EditText et, TimeUnitEnum timeUnit) {
        String value = et.getText().toString();
        if(
            !value.isEmpty()
            && Integer.parseInt(value) > timeUnit.max
        ) {
            et.setError(
                format("Maximum allowed : %d", timeUnit.max)
            );
        }

        et.setHint(
            formatMaxValue(timeUnit)
        );
    }

    private String formatMaxValue(TimeUnitEnum timeUnit) {
        return format("Max : %d", timeUnit. max);
    }
}
