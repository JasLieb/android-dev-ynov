package com.jaslieb.scheduleapp.services;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.TaskType;
import com.jaslieb.scheduleapp.states.ChildState;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ChildService {

    private CollectionReference tasks;
    public BehaviorSubject<ChildState> childStateBehavior;

    public ChildService() {
        childStateBehavior = BehaviorSubject.createDefault(ChildState.Default);

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
                childStateBehavior.onNext(new ChildState(value.toObjects(Task.class)));
            }
        });
    }

    /// TODO : MOVE TO PARENT SERVICE
    private void addTaskToDB() {

        tasks.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                }
            }
        });

        Date today = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(2020, 3, 20, 12, 0);

        tasks.add(
                new Task("Faire les tests",
                        "AAA",
                        gc.getTimeInMillis(),
                        TimeUnit.MINUTES.toMillis(30),
                        TaskType.EVERYDAY_LIFE
                )
        );
    }
}
