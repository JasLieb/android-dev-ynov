package com.jaslieb.scheduleapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 1;
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
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            if (
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.SEND_SMS
                )
            ) {
              // Close app
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.SEND_SMS
                    },
                    PERMISSIONS_REQUEST_SEND_SMS
                );
            }
        } else {
            childActor.warmParentForTask(name);
        }
    }

    public void updateTaskAsDone(String name) {
        childActor.updateTaskAsDone(name);
    }
}
