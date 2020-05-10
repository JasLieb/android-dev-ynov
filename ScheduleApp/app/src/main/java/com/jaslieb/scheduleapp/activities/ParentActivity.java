package com.jaslieb.scheduleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaslieb.scheduleapp.R;
import com.jaslieb.scheduleapp.actors.FamilyActor;
import com.jaslieb.scheduleapp.adapters.children.ChildrenAdapter;
import com.jaslieb.scheduleapp.models.tasks.Task;
import com.jaslieb.scheduleapp.services.AlarmService;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ParentActivity extends AppCompatActivity {

    private ChildrenAdapter tasksAdapter;
    private CompositeDisposable disposable = new CompositeDisposable();

    private DisposableObserver<Map<String, List<Task>>> childrenTasksObserver =
        new DisposableObserver<Map<String, List<Task>>>() {
            @Override
            public void onNext(@NonNull Map<String, List<Task>> childrenTaskMap) {
                tasksAdapter.setListItem(childrenTaskMap);
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
        setContentView(R.layout.activity_parent);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView rvChildren = findViewById(R.id.rvChildren);
        tasksAdapter = new ChildrenAdapter(this);
        rvChildren.setLayoutManager(layoutManager);
        rvChildren.setAdapter(tasksAdapter);

        Intent intent = getIntent();
        String parentName = intent.getStringExtra("name");
        TextView tvParentName = findViewById(R.id.tvParentName);
        tvParentName.setText("Hello " + parentName + " !");

        String lastName = intent.getStringExtra("lastName");

        FamilyActor familyActor = FamilyActor.getInstance();
        familyActor.getChildrenTasks(lastName);
        familyActor.childrenMappedTasksBehavior.subscribe(childrenTasksObserver);

        disposable.add(childrenTasksObserver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
