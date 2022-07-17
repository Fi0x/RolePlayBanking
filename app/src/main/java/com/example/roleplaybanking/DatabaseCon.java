
package com.example.roleplaybanking;

import android.os.Bundle;
import android.util.Log;

import com.example.roleplaybanking.controllers.AccountSelectionActivity;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firestore.v1.StructuredQuery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseCon {

    private NutzerClass user;
    private String PW;
    private boolean Sucess = false;
    private ArrayList<Account> Konten = new ArrayList<>();
    private ArrayList<Transaction> Trans = new ArrayList<>();
    private ArrayList<String> Empfaengernamen = new ArrayList<>();
    private ArrayList<String> Gamenames = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ColUser = db.collection("Nutzer");
    private CollectionReference ColKont = db.collection("Konten");
    private CollectionReference ColHist = db.collection("History");
    private DocumentReference DocMenge = db.collection("Menge").document("Menge");

    public void ReqisterUser(String Name, String User, String UserPW) {
        DocMenge.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> m = documentSnapshot.getData();
                RegisterU(Name, (long) m.get("nutzer"), User, UserPW);
            }
        });
    }

    public void RegisterU(String Name, Number Id, String User, String UserPW) {
        NutzerClass newuser = new NutzerClass(Name, Id, User, UserPW);
        db.collection("Nutzer").document(User).set(newuser);
        DocMenge.update("nutzer", (long) Id + 1);
        user = newuser;
        Sucess = true;
    }

    public void RegisterKonto(String Game, Number Geld, String Kontonamen) {
        DocMenge.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> m = documentSnapshot.getData();
                RegisterK(Game, Geld, (long) m.get("kontos"), Kontonamen, user.getNutzerID());
            }
        });
    }

    public void RegisterK(String Game, Number Geld, Number KontoID, String Kontonamen, Number NutzerID) {
        Account newAcc = new Account(Game, Kontonamen, (long) Geld, KontoID);
        Map<String, Object> m = new HashMap<>();
        m.put("Game", Game);
        m.put("Geld", Geld);
        m.put("KontoID", KontoID);
        m.put("Kontoname", Kontonamen);
        m.put("Nutzer", NutzerID);
        db.collection("Konten").document(KontoID.toString()).set(m);
        DocMenge.update("kontos", (long) KontoID + 1);
        Konten.add(newAcc);
        Empfaengernamen.add(Kontonamen);
        Sucess = true;
    }

    public void RegisterTran(Number Betrag, String Empfaenger, String Notiz, String Nutzerkonto, Timestamp time, Account fromAcc) {
        DocMenge.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> m = documentSnapshot.getData();
                RegisterT(Betrag, Empfaenger, (long) m.get("historys"), Notiz, user.getNutzerID(), Nutzerkonto, time, fromAcc);
            }
        });
    }

    public void RegisterT(Number Betrag, String Empfaenger, Number HistoryID, String Notiz, Number Nutzer, String Nutzerkonto, Timestamp time, Account fromAcc) {
        Transaction newTran = new Transaction(Nutzerkonto, Empfaenger, (double) Betrag, time, Notiz);
        Map<String, Object> m = new HashMap<>();
        m.put("Betrag", Betrag);
        m.put("Empfaenger", Empfaenger);
        m.put("HistoryID", HistoryID);
        m.put("Notiz", Notiz);
        m.put("Nutzer", Nutzer);
        m.put("Nutzerkonto", Nutzerkonto);
        m.put("SendeZeit", time);
        db.collection("History").document(HistoryID.toString()).set(m);
        DocMenge.update("historys", (long) HistoryID + 1);
        Trans.add(newTran);
        fromAcc.AccountHistory.add(newTran);
        Sucess = true;
    }

    public void RegisterGame(String name, Number Admin){
        DocMenge.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> m = documentSnapshot.getData();
                RegisterG(name, Admin, (long) m.get("games"));
            }
        });
    }

    public void RegisterG(String name, Number Admin, Number GameID){
        Map<String, Object> m = new HashMap<>();
        m.put("Admin", Admin);
        m.put("GameID", GameID);
        m.put("Name", name);
        db.collection("Game").document(GameID.toString()).set(m);
        DocMenge.update("games", (long) GameID + 1);
        Gamenames.add(name);
    }

    public void Connect(AccountSelectionActivity activity, String Name, Number Id, String User, String UserPW) {
        user = new NutzerClass(Name, Id, User, UserPW);
        if (!(user.getNutzerPW().equals(PW))) {
            user = null;
            Sucess = false;
        } else {
            Sucess = true;
        }
        this.ConnectKontos(activity);
        this.ConnectTrans();
        this.ConnectEmpfaenger();
        this.ConnectGames();
    }

    public void ConnectUser(AccountSelectionActivity activity, String User, String UserPW) {
        DocumentReference DocUser = ColUser.document(User);
        PW = UserPW;
        DocUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> m = documentSnapshot.getData();
                //Log.d("ConnectUser", documentSnapshot.getData().toString());
                Connect(activity, m.get("Name").toString(), (long) m.get("NutzerID"), m.get("NutzerName").toString(), m.get("NutzerPW").toString());
            }
        });
    }

    public void ConnectKontos(AccountSelectionActivity activity) {
        //Log.d("ConnectKonto", "hey execute");
        if(user == null) {
            //Log.d("ConnectKonto", "hey User = null");
            return;
        }
        //Log.d("ConnectKonto", user.getNutzerID().toString());
        ColKont.whereEqualTo("Nutzer", user.getNutzerID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    final AccountSelectionActivity act = activity;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("ConnectKontos", document.getData().toString());
                                Map<String, Object> m = document.getData();
                                addKonto(m.get("Game").toString(), m.get("Kontoname").toString(), (Long) m.get("Geld"), (Number) m.get("KontoID"));
                            }
                            if (act != null)
                                act.notifyDBConnectionEstablished();
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addKonto(String GName, String KontoName, Long balance, Number AccountID) {
        Account newAcc = new Account(GName, KontoName, balance, AccountID);
        Konten.add(newAcc);
    }

    public void ConnectTrans() {
        ColHist.whereEqualTo("Nutzer", user.getNutzerID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("ConnectTrans", document.getData().toString());
                                Map<String, Object> m = document.getData();
                                addTrans(m.get("Nutzerkonto").toString(), m.get("Empfaenger").toString(), (Long) m.get("Betrag"), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString());
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        ColHist.whereEqualTo("Empfaenger", user.getNutzerName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ConnectTrans", document.getData().toString());
                                Map<String, Object> m = document.getData();
                                addTrans(m.get("Nutzerkonto").toString(), m.get("Empfaenger").toString(), (Long) m.get("Betrag"), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString());
                            }
                            OrderTransactions();
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addTrans(String sender, String recipient, Long amount, Timestamp Time, String notiz) {
        Transaction newTran = new Transaction(sender, recipient, amount, Time, notiz);
        Trans.add(newTran);
    }

    public void ConnectEmpfaenger(){
        ColKont.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("ConnectEmpfaenger", document.getData().toString());
                                Map<String, Object> m = document.getData();
                                addEmpfaenger(m.get("Kontoname").toString());
                            }

                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addEmpfaenger(String Empfaenger){
        Empfaengernamen.add(Empfaenger);
    }

    public void ConnectGames(){
        db.collection("Game").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("ConnectEmpfaenger", document.getData().toString());
                                Map<String, Object> m = document.getData();
                                addGame(m.get("Name").toString());
                            }

                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addGame(String name){
        Gamenames.add(name);
    }

    public void TransferMoney(double Betrag, String Kontonamen, Number senderKontoID) {
        ColKont.whereEqualTo("Kontoname", Kontonamen)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TransferMoney", document.getData().toString());
                                Map<String, Object> m = document.getData();
                                TransferM(Betrag, (Number) m.get("KontoID"), senderKontoID);
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void TransferM(double Betrag, Number KontoIDempfaenger, Number senderKontoID) {
        ColKont.document(KontoIDempfaenger.toString()).update("Geld", FieldValue.increment(Betrag));
        ColKont.document(senderKontoID.toString()).update("Geld", FieldValue.increment(-1 * Betrag));
    }

    public String getName() {
        return user.getName();
    }

    public String getNutzerName() {
        return user.getNutzerName();
    }

    public String getNutzerPw() {
        return user.getNutzerPW();
    }

    public Number getNutzerID() {
        return user.getNutzerID();
    }

    public boolean getSucess() {
        return Sucess;
    }

    public void setSucess(boolean b) {
        Sucess = b;
    }

    public Account getAccount(Integer i) {
        if (i > Konten.size() - 1) {
            return null;
        }
        return Konten.get(i);
    }

    @Nullable
    public Transaction getTrans(Integer i) {
        if (i > Trans.size() - 1) {
            return null;
        }
        return Trans.get(i);
    }

    public void OrderTransactions(){
        int i, j;
        for (i = 0; this.getAccount(i) != null; i++) {
            for (j = 0; this.getTrans(j) != null; j++) {
               if(Trans.get(j).sender.contentEquals(Konten.get(i).name) || Trans.get(j).recipient.contentEquals(Konten.get(i).name)){
                   Konten.get(i).AccountHistory.add(Trans.get(j));
               }
            }
        }
    }
    public String getGame(Integer i) {
        if (i > Gamenames.size() - 1) {
            return null;
        }
        return Gamenames.get(i);
    }


    public NutzerClass getUser(){
        return user;
    }

}
