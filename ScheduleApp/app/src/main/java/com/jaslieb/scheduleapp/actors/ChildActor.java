package com.jaslieb.scheduleapp.actors;

import android.telephony.SmsManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.states.ChildState;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ChildActor {

    private CollectionReference tasks;
    public BehaviorSubject<ChildState> childStateBehavior;

    public ChildActor() {
        childStateBehavior = BehaviorSubject.createDefault(ChildState.Default);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        tasks = database.collection("tasks");
        tasks.addSnapshotListener((value, e) -> {
            if (e != null) {
                return;
            }

            assert value != null;
            List<Task> taskList = value.toObjects(Task.class);

            childStateBehavior.onNext(
                new ChildState(
                    taskList
                        .stream()
                        .filter(task -> "JohnId".equals(task.childrenId))
                        .collect(Collectors.toList())
                )
            );
        });
    }

    public void updateTaskAsDone(String name) {
        tasks.whereEqualTo("name", name)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    doc.getReference().delete();
                }
            });
    }

    public void warmParentForTask(String name) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage("0609580401", null, name + "will not be finished in time", null, null);
    }
}
