package com.jaslieb.scheduleapp.actors;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaslieb.scheduleapp.models.family.Child;
import com.jaslieb.scheduleapp.models.family.Family;
import com.jaslieb.scheduleapp.models.family.Parent;
import com.jaslieb.scheduleapp.models.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FamilyActor {

    private static FamilyActor actor = null;
    public static FamilyActor getInstance() {
        if(actor == null) actor = new FamilyActor();
        return actor;
    }

    public BehaviorSubject<String> resultBehavior;
    public BehaviorSubject<Map<String, List<Task>>> childrenMappedTasksBehavior;
    public BehaviorSubject<List<String>> childrenNamesBehavior;


    private FirebaseFirestore database;
    private CollectionReference families;

    private FamilyActor() {
        resultBehavior = BehaviorSubject.createDefault("not connected");
        childrenMappedTasksBehavior = BehaviorSubject.createDefault( new HashMap<>() );
        childrenNamesBehavior = BehaviorSubject.createDefault(new ArrayList<>());
        database = FirebaseFirestore.getInstance();
        families = database.collection("families");
    }

    public void checkPassword(String firstName, String password) {
        families.get().addOnCompleteListener(task -> {
            List<DocumentSnapshot> docs = Objects.requireNonNull(task.getResult()).getDocuments();
            for (DocumentSnapshot doc: docs) {
                Family family = doc.toObject(Family.class);
                if(family != null && family.name.equals(firstName)) {
                    for (Parent parent: family.parents) {
                        if(parent.password.equals(password)){
                            this.resultBehavior.onNext("parent " + parent.name);
                            return ;
                        }
                    }

                    for (Child child: family.children) {
                        if(child.password.equals(password)){
                            this.resultBehavior.onNext("child " + child.name);
                            return ;
                        }
                    }
                    this.resultBehavior.onNext("notConnected");
                }
            }
        });
    }

    public void getChildrenTasks(String lastName) {
        families.get().addOnCompleteListener(task -> {
            List<DocumentSnapshot> docs = Objects.requireNonNull(task.getResult()).getDocuments();
            Map<String, List<Task>> map = new HashMap<>();
            for (DocumentSnapshot doc : docs) {
                Family family = doc.toObject(Family.class);
                if(family != null && family.name.equals(lastName)) {
                    for (Child child: family.children) {
                        database.collection("tasks")
                            .whereEqualTo("childrenId", child.name)
                            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                                List<Task> taskList = Objects.requireNonNull(queryDocumentSnapshots).toObjects(Task.class);
                                map.put(child.name, taskList);
                                this.childrenMappedTasksBehavior.onNext(map);
                            });
                    }
                    this.childrenNamesBehavior.onNext(
                        family.children.stream()
                            .map(child -> child.name)
                            .collect(Collectors.toList()
                        )
                    );
                    break;
                }
            }
        });
    }
}
