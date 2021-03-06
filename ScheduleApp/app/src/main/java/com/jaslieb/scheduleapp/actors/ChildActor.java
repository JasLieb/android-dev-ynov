package com.jaslieb.scheduleapp.actors;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaslieb.scheduleapp.models.tasks.Task;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.states.ChildState;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ChildActor{
    private FamilyActor familyActor;

    private static ChildActor actor = null;
    public static ChildActor getInstance() {
        if(actor == null) actor = new ChildActor();
        return actor;
    }

    private CollectionReference tasks;
    public BehaviorSubject<ChildState> childStateBehavior;

    private ChildActor() {
        familyActor = FamilyActor.getInstance();
        childStateBehavior = BehaviorSubject.createDefault(ChildState.Default);
    }

    public void setChildName(String name) {
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
                        .filter(task -> name.equals(task.childName))
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
                    DocumentReference ref = doc.getReference();
                    ref.get().addOnSuccessListener(taskDoc -> {
                        String recurrenceString = taskDoc.getString("recurrence");
                        if(recurrenceString != null && !recurrenceString.equals("null")) {
                            updateBeginFollowRecurrence(
                                ref,
                                taskDoc,
                                TimeUnitEnum.find(recurrenceString).toMilliseconds(1)
                            );
                        } else {
                            ref.delete();
                        }
                    });
                }
            });
    }

    private void updateBeginFollowRecurrence(DocumentReference ref, DocumentSnapshot taskDoc, long recurrence) {
        long begin = (long) taskDoc.get("begin");
        long newBegin = begin + recurrence;
        long currentTime= System.currentTimeMillis();

        while( currentTime > newBegin ) {
            newBegin += recurrence;
        }

        if(currentTime > begin && currentTime < newBegin ) {
            ref.update( "begin", newBegin);
            ref.update( "parentWarned", false);
            if(taskDoc.get("reminder") != null ) {
                ref.update( "reminder.isTriggered", false);
            }
        }
    }

    public void removeReminderFor(Task task) {
        tasks.whereEqualTo("name", task.name)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    doc.getReference().update("reminder.isTriggered",  true);
                }
            });
    }

    public void warnParentForTask(Task task) {
        familyActor.warnParents(task.childName, task.name);
        updateParentWarned(task.name);
    }

    public void warnParentForTask(String childName, String taskName) {
        familyActor.warnParents(childName, taskName);
        updateParentWarned(taskName);
    }

    private void updateParentWarned(String name) {
        tasks.whereEqualTo("name", name)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    doc.getReference().update("parentWarned",  true);
                }
            });
    }
}
