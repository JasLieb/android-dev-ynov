package com.jaslieb.scheduleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.actors.ParentActor;
import com.jaslieb.scheduleapp.adapters.tasks.ParentTasksAdapter;
import com.jaslieb.scheduleapp.services.AlarmService;
import com.jaslieb.scheduleapp.states.ParentState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ParentActivity extends AppCompatActivity {

    private ParentTasksAdapter tasksAdapter;
    private CompositeDisposable disposable = new CompositeDisposable();

    private DisposableObserver<ParentState> childStateObserver =
        new DisposableObserver<ParentState>() {
            @Override
            public void onNext(@NonNull ParentState parentState) {
                tasksAdapter.setListItem(parentState.tasks);
            }

            @Override
            public void onError(@NonNull Throwable e) {}

            @Override
            public void onComplete() {}
        };

    private LinearLayout llAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(AlarmService.isRunning) {
            Intent intent = new Intent(this, AlarmService.class);
            stopService(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        llAddTask = findViewById(R.id.llAddTask);

        tasksAdapter = new ParentTasksAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView taskList = findViewById(R.id.lvTasks);

        taskList.setLayoutManager(layoutManager);
        taskList.setAdapter(tasksAdapter);

        Button btShowAddTask = findViewById(R.id.btShowAddTask);
        btShowAddTask.setOnClickListener(v -> {
            llAddTask.setVisibility(
                    llAddTask.getVisibility() == View.VISIBLE
                    ? View.GONE
                    : View.VISIBLE
            );
        });

        Button btShowTaskChild = findViewById(R.id.btShowTaskChild);
        btShowTaskChild.setOnClickListener(v -> {
            taskList.setVisibility(
                taskList.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });

        ParentActor service = ParentActor.getInstance();
        service
            .parentStateBehavior
            .subscribe(childStateObserver);
        disposable.add(childStateObserver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
