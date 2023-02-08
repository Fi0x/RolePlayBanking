
package com.example.roleplaybanking.database;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.roleplaybanking.controllers.AccountSelectionActivity;
import com.example.roleplaybanking.controllers.CreateNewActivity;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Transaction;
import com.example.roleplaybanking.structures.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class DatabaseCon {

    private NutzerClass user;
    private String PW;
    private boolean success = false;
    private final ArrayList<Account> accounts = new ArrayList<>();
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private final ArrayList<String> recipients = new ArrayList<>();
    private final ArrayList<Game> games = new ArrayList<>();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final CollectionReference ColUser = database.collection("Nutzer");
    private final CollectionReference ColAccount = database.collection("Konten");
    private final CollectionReference ColHistory = database.collection("History");
    private final DocumentReference DocMenge = database.collection("Menge").document("Menge");
    private SharedPreferences sharedPreferences;

    public void registerUser(String Name, String User, String UserPW) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerUser(Name, (long) m.get("nutzer"), User, UserPW);
        });
    }

    public void registerUser(String Name, Number Id, String User, String UserPW) {
        NutzerClass newuser = new NutzerClass(Name, Id, User, UserPW);
        database.collection("Nutzer").document(User).set(newuser);
        DocMenge.update("nutzer", (long) Id + 1);
        user = newuser;
        success = true;
    }

    public void registerAccount(CreateNewActivity act, String Game, Number Geld, String Kontonamen) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerAccount(Game, Geld, (long) m.get("kontos"), Kontonamen, user.getNutzerID());
            if (act != null)
                act.closeActivityWhenDone();
        });
    }

    public void registerAccount(String Game, Number Geld, Number KontoID, String Kontonamen, Number NutzerID) {
        Account newAcc = new Account(Game, Kontonamen,  Double.parseDouble(Geld.toString()), KontoID);
        Map<String, Object> m = new HashMap<>();
        m.put("Game", Game);
        m.put("Geld", Geld);
        m.put("KontoID", KontoID);
        m.put("Kontoname", Kontonamen);
        m.put("Nutzer", NutzerID);
        database.collection("Konten").document(KontoID.toString()).set(m);
        DocMenge.update("kontos", (long) KontoID + 1);
        accounts.add(newAcc);
        recipients.add(Kontonamen);
        success = true;
    }

    public void registerTransaction(Number Betrag, String Empfaenger, String Notiz, String Nutzerkonto, Timestamp time, Account fromAcc) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerTransaction(Betrag, Empfaenger, (long) m.get("historys"), Notiz, user.getNutzerID(), Nutzerkonto, time, fromAcc);
        });
    }

    public void registerTransaction(Number Betrag, String Empfaenger, Number HistoryID, String Notiz, Number Nutzer, String Nutzerkonto, Timestamp time, Account fromAcc) {
        Transaction newTran = new Transaction(Nutzerkonto, Empfaenger, (double) Betrag, time, Notiz);
        Map<String, Object> m = new HashMap<>();
        m.put("Betrag", Betrag);
        m.put("Empfaenger", Empfaenger);
        m.put("HistoryID", HistoryID);
        m.put("Notiz", Notiz);
        m.put("Nutzer", Nutzer);
        m.put("Nutzerkonto", Nutzerkonto);
        m.put("SendeZeit", time);
        database.collection("History").document(HistoryID.toString()).set(m);
        DocMenge.update("historys", (long) HistoryID + 1);
        transactions.add(newTran);
        fromAcc.AccountHistory.add(newTran);
        success = true;
    }

    public void registerGame(String name, Number Admin) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerGame(name, Admin, (long) m.get("games"));
        });
    }

    public void registerGame(String name, Number Admin, Number GameID) {
        Map<String, Object> m = new HashMap<>();
        m.put("Admin", Admin);
        m.put("GameID", GameID);
        m.put("Name", name);
        database.collection("Game").document(GameID.toString()).set(m);
        DocMenge.update("games", (long) GameID + 1);
        Game a = new Game(name, Admin);
        games.add(a);
    }

    public void connect(AccountSelectionActivity activity, String Name, Number Id, String User, String UserPW) {
        user = new NutzerClass(Name, Id, User, UserPW);
        if (!(user.getNutzerPW().equals(PW))) {
            user = null;
            success = false;
        } else {
            success = true;
        }
        this.connectAccounts(activity);
        this.connectTransactions();
        this.connectRecipients();
        this.connectGames();
    }

    public void connectUser(AccountSelectionActivity activity, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        String currentUserID = getFromDeviceStorage("UserID", "0123456789");
        String currentUserPW = getFromDeviceStorage("UserPW", "0123456789");

        DocumentReference DocUser = ColUser.document(currentUserID);
        PW = currentUserPW;
        DocUser.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            if (m == null) {
                registerUser(currentUserID, currentUserID, currentUserPW);
            } else
                connect(activity, m.get("name").toString(), (long) m.get("nutzerID"), m.get("nutzerName").toString(), m.get("nutzerPW").toString());
        });
    }

    public void connectAccounts(AccountSelectionActivity activity) {
        if (user == null)
            return;

        ColAccount.whereEqualTo("Nutzer", user.getNutzerID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            final AccountSelectionActivity act = activity;

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> m = document.getData();
                        addAccount(m.get("Game").toString(), m.get("Kontoname").toString(), Double.parseDouble(m.get("Geld").toString()), (Number) m.get("KontoID"));
                    }
                    if (act != null)
                        act.notifyDBConnectionEstablished();
                }
            }
        });
    }

    public void addAccount(String GName, String KontoName, double balance, Number AccountID) {
        Account newAcc = new Account(GName, KontoName, balance, AccountID);
        accounts.add(newAcc);
    }

    public void connectTransactions() {
        ColHistory.whereEqualTo("Nutzer", user.getNutzerID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addTransaction(m.get("Nutzerkonto").toString(), m.get("Empfaenger").toString(),  Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString());
                }
            }
        });
        ColHistory.whereEqualTo("Empfaenger", user.getNutzerName()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("ConnectTrans", document.getData().toString());
                    Map<String, Object> m = document.getData();
                    addTransaction(m.get("Nutzerkonto").toString(), m.get("Empfaenger").toString(), Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString());
                }
                orderTransactions();
            }
        });
    }

    public void addTransaction(String sender, String recipient, double amount, Timestamp Time, String notiz) {
        Transaction newTran = new Transaction(sender, recipient, amount, Time, notiz);
        transactions.add(newTran);
    }

    public void connectRecipients() {
        ColAccount.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addRecipient(m.get("Kontoname").toString());
                }

            }
        });
    }

    public void addRecipient(String Empfaenger) {
        recipients.add(Empfaenger);
    }

    public void connectGames() {
        database.collection("Game").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addGame(m.get("Name").toString(), (Number)m.get("Admin"));
                }
            }
        });
    }

    public void addGame(String name, Number AID) {
        Game a = new Game(name, AID);
        games.add(a);
    }

    public void transferMoney(double Betrag, String Kontonamen, Number senderKontoID, boolean FromAdmin) {
        ColAccount.whereEqualTo("Kontoname", Kontonamen).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("TransferMoney", document.getData().toString());
                    Map<String, Object> m = document.getData();
                    transferMoney(Betrag, (Number) m.get("KontoID"), senderKontoID, FromAdmin);
                }
            }
        });
    }

    public void transferMoney(double Betrag, Number KontoIDempfaenger, Number senderKontoID, boolean FromAdmin) {
        ColAccount.document(KontoIDempfaenger.toString()).update("Geld", FieldValue.increment(Betrag));
        if(!FromAdmin){
            ColAccount.document(senderKontoID.toString()).update("Geld", FieldValue.increment(-1 * Betrag));
        }
    }

    public String getName() {
        return user.getName();
    }

    public Number getAdminName(String gameName) {
        int i = 0;
        while(i > games.size() - 1){
            if(games.get(i).name == gameName){
                return games.get(i).adminID;
            }
        }
        return null;
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
        return success;
    }

    public void setSucess(boolean b) {
        success = b;
    }

    public Account getAccount(Integer i) {
        if (i > accounts.size() - 1) {
            return null;
        }
        return accounts.get(i);
    }

    @Nullable
    public Transaction getTransaction(Integer i) {
        if (i > transactions.size() - 1) {
            return null;
        }
        return transactions.get(i);
    }

    public void orderTransactions() {
        int i, j;
        for (i = 0; this.getAccount(i) != null; i++) {
            accounts.get(i).AccountHistory.clear();
            for (j = 0; this.getTransaction(j) != null; j++) {
                if (transactions.get(j).sender.contentEquals(accounts.get(i).name) || transactions.get(j).recipient.contentEquals(accounts.get(i).name)) {
                    accounts.get(i).AccountHistory.add(transactions.get(j));
                }
            }
        }
    }

    public String getGame(Integer i) {
        if (i > games.size() - 1)
            return null;

        return games.get(i).name;
    }

    public NutzerClass getUser() {
        return user;
    }

    public String getEmpfaenger(Integer i){
        if (i > recipients.size() - 1) {
            return null;
        }
        return recipients.get(i);
    }

    private String getFromDeviceStorage(String variableName, String defaultValue) {
        String generated;
        String id = sharedPreferences.getString(variableName, defaultValue);
        if (id.equals(defaultValue)) {
            generated = new StringGenerator().nextString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(variableName, generated);
            editor.apply();
        } else
            generated = id;

        return generated;
    }

    private static class StringGenerator {
        public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String lower = upper.toLowerCase();
        public static final String digits = "0123456789";
        public static final String alphanum = upper + lower + digits;

        private final Random random;
        private final char[] symbols;
        private final char[] buf;

        public StringGenerator(int length, Random random, String symbols) {
            if (length < 1) throw new IllegalArgumentException();
            if (symbols.length() < 2) throw new IllegalArgumentException();
            this.random = Objects.requireNonNull(random);
            this.symbols = symbols.toCharArray();
            this.buf = new char[length];
        }

        public StringGenerator() {
            this(32, new SecureRandom(), alphanum);
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }
}
