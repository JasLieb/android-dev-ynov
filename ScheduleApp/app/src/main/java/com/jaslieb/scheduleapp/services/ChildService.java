package com.jaslieb.scheduleapp.services;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.states.ChildState;

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
}
