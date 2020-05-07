package com.jaslieb.scheduleapp.actors;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaslieb.scheduleapp.models.tasks.Reminder;
import com.jaslieb.scheduleapp.models.tasks.Task;
import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.states.ParentState;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ParentActor {
    private static ParentActor actor = null;
    public static ParentActor getInstance() {
        if(actor == null) actor = new ParentActor();
        return actor;
    }

    private CollectionReference tasks;

    public BehaviorSubject<ParentState> parentStateBehavior;

    private ParentActor() {
        parentStateBehavior = BehaviorSubject.createDefault(ParentState.Default);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        tasks = database.collection("tasks");
        tasks.addSnapshotListener((value, e) -> {
            if (e != null) {
                return;
            }

            assert value != null;
            parentStateBehavior.onNext(new ParentState(value.toObjects(Task.class)));
        });
    }

    public void addTask(
            String name,
            long beginTime,
            long duration,
            TaskTypeEnum type,
            TimeUnitEnum recurrence,
            Reminder reminder
    ) {
        tasks.add(
            new Task(
                name,
                "JohnId",
                beginTime,
                duration,
                type,
                recurrence,
                reminder,
                false
            )
        );
    }

    public void removeRecurrence(Task task) {
        removeAttribute(task.name, "recurrence");
    }

    public void removeReminder(Task task) {
        removeAttribute(task.name, "reminder");
    }

    public void removeTask(Task task) {
        tasks.whereEqualTo("name", task.name)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    assert queryDocumentSnapshots != null;
                    for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }

    private void removeAttribute(String taskName, String toRemove) {
        tasks.whereEqualTo("name", taskName)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    DocumentReference ref = doc.getReference();
                    ref.update(toRemove, null);
                }
            });
    }
}
