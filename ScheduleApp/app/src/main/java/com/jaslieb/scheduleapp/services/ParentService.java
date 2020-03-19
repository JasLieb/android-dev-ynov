package com.jaslieb.scheduleapp.services;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.states.ParentState;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ParentService {
    private CollectionReference tasks;

    public BehaviorSubject<ParentState> parentStateBehavior;

    public  ParentService() {
        parentStateBehavior = BehaviorSubject.createDefault(ParentState.Default);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        tasks = database.collection("tasks");
        tasks.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                assert value != null;
                parentStateBehavior.onNext(new ParentState(value.toObjects(Task.class)));
            }
        });
    }

    public void addTask(String name, long beginTime, long duration, TaskTypeEnum type) {
        tasks.add(
            new Task(
                name,
                "JohnId",
                beginTime,
                duration,
                type
            )
        );
    }
}
