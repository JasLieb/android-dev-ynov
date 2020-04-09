package com.jaslieb.scheduleapp.actors;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaslieb.scheduleapp.models.Reminder;
import com.jaslieb.scheduleapp.models.Task;
import com.jaslieb.scheduleapp.models.enums.TaskTypeEnum;
import com.jaslieb.scheduleapp.models.enums.TimeUnitEnum;
import com.jaslieb.scheduleapp.states.ParentState;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ParentActor {
    private CollectionReference tasks;

    public BehaviorSubject<ParentState> parentStateBehavior;

    public ParentActor() {
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
}
