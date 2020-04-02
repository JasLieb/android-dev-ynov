package com.jaslieb.scheduleapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.actors.ChildActor;
import com.jaslieb.scheduleapp.adapters.tasks.ChildTasksAdapter;
import com.jaslieb.scheduleapp.states.ChildState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ChildActivity extends AppCompatActivity {

    private ChildTasksAdapter tasksAdapter;

    private ChildActor childActor;
    private CompositeDisposable disposable = new CompositeDisposable();

    private DisposableObserver<ChildState> childStateObserver =
        new DisposableObserver<ChildState>() {
            @Override
            public void onNext(@NonNull ChildState childState) {
                tasksAdapter.setListItem(childState.tasks);
            }

            @Override
            public void onError(@NonNull Throwable e) {}

            @Override
            public void onComplete() {}
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        childActor = new ChildActor();

        tasksAdapter = new ChildTasksAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView taskList = findViewById(R.id.lvTasks);

        childActor.childStateBehavior.subscribe(childStateObserver);
        disposable.add(childStateObserver);

        taskList.setLayoutManager(layoutManager);
        taskList.setAdapter(tasksAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    public void warmParentForTask(String name) {
        childActor.warmParentForTask(name);
    }

    public void updateTaskAsDone(String name) {
        childActor.updateTaskAsDone(name);
    }
}
