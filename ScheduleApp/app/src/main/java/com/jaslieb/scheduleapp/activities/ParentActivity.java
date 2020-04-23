package com.jaslieb.scheduleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.actors.ParentActor;
import com.jaslieb.scheduleapp.services.AlarmService;
import com.jaslieb.scheduleapp.states.ParentState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

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

        service = new ParentActor();
        service.parentStateBehavior.subscribe(childStateObserver);
        disposable.add(childStateObserver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
