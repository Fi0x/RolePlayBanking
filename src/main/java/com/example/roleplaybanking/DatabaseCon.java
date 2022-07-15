package com.example.roleplaybanking;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DatabaseCon {

    private NutzerClass user;
    private Menge g;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ColUser = db.collection("Nutzer");
    private CollectionReference ColKont = db.collection("Konten");
    private CollectionReference ColHist = db.collection("History");
    private DocumentReference DocMenge = db.collection("Menge").document("Menge");

    public boolean ReqisterUser(String Name, String User, String UserPW){
        DocMenge.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                g = documentSnapshot.toObject(Menge.class);
            }
        });
        NutzerClass newuser = new NutzerClass(Name, g.getNutzer(), User, UserPW);
        db.collection("Nutzer").document(User).set(newuser);
        DocMenge.update("Nutzer", (int)g.getNutzer()+1);
        user = newuser;
        return true;
    }

    public Boolean ConnectUser(String User, String UserPW){
        DocumentReference DocUser = ColUser.document(User);
        DocUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(NutzerClass.class);
            }
        });
        if(user.getNutzerPW().equals(UserPW)){
            return true;
        }

        user = null;
        return false;
    }

    public String getName(){
        return user.getName();
    }

    public String getNutzerName(){
        return user.getNutzerName();
    }

    public String getNutzerPw(){
        return user.getNutzerPW();
    }

    public Number getNutzerID(){
        return user.getNutzerID();
    }

}
