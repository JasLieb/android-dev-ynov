package com.jaslieb.scheduleapp.activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.adapters.ChildTasksAdapter;
import com.jaslieb.scheduleapp.services.ChildService;
import com.jaslieb.scheduleapp.states.ChildState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ChildActivity extends AppCompatActivity {
    private ListView taskList;
    private ChildTasksAdapter tasksAdapter;

    private ChildService service;
    private CompositeDisposable disposable = new CompositeDisposable();

    private DisposableObserver<ChildState> childStateObserver =
            new DisposableObserver<ChildState>() {
                @Override
                public void onNext(@NonNull ChildState childState) {
                    tasksAdapter.addAll(childState.tasks);
                    taskList.setAdapter(tasksAdapter);
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        taskList = findViewById(R.id.lvTasks);
        tasksAdapter = new ChildTasksAdapter(this);

        /// Maybe useful
        // list.setOnItemClickListener();
        // list.setOnItemSelectedListener();

        service = new ChildService();
        service.childStateBehavior.subscribe(childStateObserver);
        disposable.add(childStateObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
