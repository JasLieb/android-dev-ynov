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

public class ChildActor{

    private static ChildActor actor = null;
    public static ChildActor getInstance() {
        if(actor == null) actor = new ChildActor();
        return actor;
    }

    private CollectionReference tasks;
    public BehaviorSubject<ChildState> childStateBehavior;

    private ChildActor() {
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

    public void updateReminderDisplayedCount(Task task) {
        tasks.whereEqualTo("name", task.name)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    doc.getReference().update("reminder.displayedCount",  (task.reminder.displayedCount + 1));
                }
            });
    }

    public void removeReminderFor(Task task) {
        tasks.whereEqualTo("name", task.name)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    doc.getReference().update("reminder",  null);
                }
            });
    }

    public void warnParentForTask(Task task, boolean setParentWarned) {
        sendSMS(task.name);
        updateParentWarned(task.name, setParentWarned);
    }

    public void warnParentForTask(String name) {
        sendSMS(name);
        updateParentWarned(name, false);
    }

    private void sendSMS(String name) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(
            "0609580401",
            null,
            name + " will not be finished in time",
            null,
            null
        );
    }

    private void updateParentWarned(String name, boolean value) {
        tasks.whereEqualTo("name", name)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    doc.getReference().update("parentWarned",  value);
                }
            });
    }
}
