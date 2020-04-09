package com.jaslieb.scheduleapp.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.actors.ChildActor;
import com.jaslieb.scheduleapp.adapters.tasks.ChildTasksAdapter;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.services.AlarmService;
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

        NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert nMgr != null;
        nMgr.cancelAll();
    }

    @Override
    protected void onStop() {
        if(!AlarmService.isRunning) {
            Intent intent = new Intent(this, AlarmService.class);
            startService(intent);
        } else {
            AlarmService.makeNotificationStream.onNext(0);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void warmParentForTask(Task task) {
        childActor.warnParentForTask(task, false);
    }

    public void updateTaskAsDone(String name) {
        childActor.updateTaskAsDone(name);
    }
}
