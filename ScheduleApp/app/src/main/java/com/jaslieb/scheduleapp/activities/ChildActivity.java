package com.jaslieb.scheduleapp.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.actors.ChildActor;
import com.jaslieb.scheduleapp.adapters.tasks.ChildTasksAdapter;
import com.jaslieb.scheduleapp.models.tasks.Task;
import com.jaslieb.scheduleapp.services.AlarmService;
import com.jaslieb.scheduleapp.states.ChildState;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ChildActivity extends AppCompatActivity {

    private String childName;

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

        if(AlarmService.isRunning) {
            Intent intent = new Intent(this, AlarmService.class);
            stopService(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        childName = getIntent().getStringExtra("name");
        TextView tvChildName = findViewById(R.id.tvChildName);
        tvChildName.setText("Hello " + childName + " !\nYou have some tasks to do");

        childActor = ChildActor.getInstance();
        childActor.setChildName(childName);

        tasksAdapter = new ChildTasksAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView taskList = findViewById(R.id.lvTasks);

        childActor.childStateBehavior.distinctUntilChanged().skip(1).subscribe(childStateObserver);
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
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        outState.putString("name", childName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.disposable.dispose();
    }

    public void warmParentForTask(Task task) {
        if(!task.parentWarned) {
            childActor.warnParentForTask(task);
        }
    }

    public void updateTaskAsDone(String name) {
        childActor.updateTaskAsDone(name);
    }
}
