package com.jaslieb.scheduleapp.actors;

import android.telephony.SmsManager;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaslieb.scheduleapp.models.Reminder;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.states.ChildState;
import com.jaslieb.scheduleapp.utils.DateUtil;

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
                            long begin = (long) taskDoc.get("begin");
                            Log.d("CHILD ACTOR", "BEGIN AT " + DateUtil.formatToDateString(begin) + " RECURRENCE : " + TimeUnitEnum.find(recurrenceString).name());
                            Log.d("CHILD ACTOR", "CURRENT AT " + DateUtil.formatToDateString(System.currentTimeMillis()));
                            if(System.currentTimeMillis() > begin) {
                                begin += TimeUnitEnum.find(recurrenceString).toMilliseconds(1);
                                Log.d("CHILD ACTOR", "NEW BEGIN AT " + DateUtil.formatToDateString(begin));
                                ref.update( "begin", begin);
                            }
                        } else {
                            ref.delete();
                        }
                    });
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
