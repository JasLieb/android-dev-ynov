package com.jaslieb.scheduleapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.services.ParentService;
import com.jaslieb.scheduleapp.states.ParentState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ParentActivity extends AppCompatActivity {

    private Button btAddTasks;
    private EditText etTaskName;

    private ParentService service;
    private CompositeDisposable disposable = new CompositeDisposable();

    private DisposableObserver<ParentState> childStateObserver =
            new DisposableObserver<ParentState>() {
                @Override
                public void onNext(@NonNull ParentState parentState) {

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
}
