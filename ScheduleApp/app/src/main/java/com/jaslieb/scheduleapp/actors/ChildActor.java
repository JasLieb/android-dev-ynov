package com.jaslieb.scheduleapp.actors;

import android.telephony.SmsManager;

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

    public void warnParentForTask(String name) {
        sendSMS(name);
        updateParentWarned(name);
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
