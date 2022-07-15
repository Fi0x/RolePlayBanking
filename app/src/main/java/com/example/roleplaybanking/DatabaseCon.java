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

import java.util.Map;

public class DatabaseCon {

    private NutzerClass user;
    private String PW;
    private boolean Sucess = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ColUser = db.collection("Nutzer");
    private CollectionReference ColKont = db.collection("Konten");
    private CollectionReference ColHist = db.collection("History");
    private DocumentReference DocMenge = db.collection("Menge").document("Menge");;


    public void ReqisterUser(String Name, String User, String UserPW){
        DocMenge.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> m = documentSnapshot.getData();
                Register(Name, (long)m.get("nutzer"), User, UserPW);
            }
        });
    }

    public void Register(String Name, Number Id, String User, String UserPW){
        NutzerClass newuser = new NutzerClass(Name, Id, User, UserPW);
        db.collection("Nutzer").document(User).set(newuser);
        DocMenge.update("nutzer", (long)Id+1);
        user = newuser;
        Sucess = true;
    }

    public void Connect(String Name, Number Id, String User, String UserPW){
        user = new NutzerClass(Name, Id, User, UserPW);
        if(!(user.getNutzerPW().equals(PW))){
            user = null;
            Sucess = false;
        } else {
            Sucess = true;
        }

    }

    public void ConnectUser(String User, String UserPW){
        DocumentReference DocUser = ColUser.document(User);
        PW = UserPW;
        DocUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> m = documentSnapshot.getData();
                Log.d("ConnectUser", documentSnapshot.getData().toString());
                Connect(m.get("Name").toString(), (long)m.get("NutzerID"), m.get("NutzerName").toString(), m.get("NutzerPW").toString());
            }
        });
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

    public boolean getSucess(){
        return Sucess;
    }

}
