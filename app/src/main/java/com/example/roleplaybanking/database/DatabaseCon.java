
package com.example.roleplaybanking.database;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.roleplaybanking.controllers.AccountSelectionActivity;
import com.example.roleplaybanking.controllers.CreateNewActivity;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Recipients;
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
    private final ArrayList<Recipients> recipients = new ArrayList<>();
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

    public void registerAccount(CreateNewActivity act, String Game, Number Geld, String Kontonamen, boolean isnewGame) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            if(isnewGame){
                registerGame(Game, (Number)m.get("kontos"));
            }
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
        Recipients R = new Recipients(Kontonamen, Game, KontoID);
        recipients.add(R);
        success = true;
    }

    public void registerTransaction(Number Betrag, Number Empfaenger, String Notiz, Number Nutzerkonto, Timestamp time, Account fromAcc) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerTransaction(Betrag, Empfaenger, (long) m.get("historys"), Notiz, user.getNutzerID(), Nutzerkonto, time, fromAcc);
        });
    }

    public void registerTransaction(Number Betrag, Number Empfaenger, Number HistoryID, String Notiz, Number Nutzer, Number Nutzerkonto, Timestamp time, Account fromAcc) {
        int e;
        for(e=0; e < recipients.size(); e++){
            if(recipients.get(e).KontoID.equals(Empfaenger)){
                break;
            }
        }
        int n;
        for(n=0; n < recipients.size(); n++){
            if(recipients.get(n).KontoID.equals(Nutzerkonto)){
                break;
            }
        }
        Transaction newTran = new Transaction(recipients.get(n).Name, recipients.get(e).Name, (double) Betrag, time, Notiz, HistoryID);
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
        this.connectGames();
        this.connectRecipients();
        this.connectAccounts(activity);
        int i;
        for(i=0; i < accounts.size(); i++){
            connectTransactions(accounts.get(i).AccountID);
        }
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
                        connectTransactions((Number)m.get("KontoID"));
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

    public void connectTransactions(Number KontoID) {
        ColHistory.whereEqualTo("Nutzerkonto", KontoID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addTransaction((Number)m.get("Nutzerkonto"), (Number)m.get("Empfaenger"),  Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString(), (Number)m.get("HistoryID"));
                }
            }
        });
        ColHistory.whereEqualTo("Empfaenger", KontoID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("ConnectTrans", document.getData().toString());
                    Map<String, Object> m = document.getData();
                    addTransaction((Number)m.get("Nutzerkonto"), (Number)m.get("Empfaenger"), Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString(), (Number)m.get("HistoryID"));
                }
                orderTransactions();
            }
        });
    }

    public void addTransaction(Number sender, Number recipient, double amount, Timestamp Time, String notiz, Number HistoryID) {
        int t;
        for(t=0; t < transactions.size(); t++){
            if(transactions.get(t).TransactioID.equals(HistoryID)){
                return;
            }
        }
        int e;
        for(e=0; e < recipients.size(); e++){
            if(recipients.get(e).KontoID.equals(recipient)){
                break;
            }
        }
        int n;
        for(n=0; n < recipients.size(); n++){
            if(recipients.get(n).KontoID.equals(sender)){
                break;
            }
        }
        Transaction newTran = new Transaction(recipients.get(n).Name, recipients.get(e).Name, amount, Time, notiz, HistoryID);
        transactions.add(newTran);
    }

    public void connectRecipients() {
        ColAccount.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    for(int i = 0; i < games.size(); i++){
                        if(games.get(i).name.contentEquals(m.get("Game").toString())){
                            addRecipient(m.get("Kontoname").toString(), m.get("Game").toString(), (Number)m.get("KontoID"));
                        }
                    }
                }
            }
        });
    }

    public void addRecipient(String Empfaenger, String GameName, Number KontoID) {
        Recipients R = new Recipients(Empfaenger, GameName, KontoID);
        recipients.add(R);
    }

    public void connectGames() {
        database.collection("Game").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addGame(m.get("Name").toString(), (Number)m.get("Admin"));
                }
                connectRecipients();
            }
        });
    }

    public void addGame(String name, Number AID) {
        Game a = new Game(name, AID);
        games.add(a);
    }

    public void transferMoney(double Betrag, Number KontoId, Number senderKontoID, boolean FromAdmin) {
        ColAccount.whereEqualTo("KontoID", KontoId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("TransferMoney", document.getData().toString());
                    Map<String, Object> m = document.getData();
                    transferMoneyOnComplete(Betrag, (Number) m.get("KontoID"), senderKontoID, FromAdmin);
                }
            }
        });
    }

    public void transferMoneyOnComplete(double Betrag, Number KontoIDempfaenger, Number senderKontoID, boolean FromAdmin) {
        ColAccount.document(KontoIDempfaenger.toString()).update("Geld", FieldValue.increment(Betrag));
        if(!FromAdmin){
            ColAccount.document(senderKontoID.toString()).update("Geld", FieldValue.increment(-1 * Betrag));
        }
    }

    public String getName() {
        return user.getName();
    }

    public Number getAdminName(String gameName) {

        for(int i = 0; i < games.size(); i++){
            if(games.get(i).name.equals(gameName)){
                return games.get(i).adminID;
            }
        }
        return -1;
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
    public ArrayList<Account> getAllAccounts()
    {
        return accounts;
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

    public Recipients getEmpfaenger(Integer i){
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

    public void deleteAccount(String gameName, Number accountId)
    {
        int i= 0;
        for( i = 0; i < accounts.size(); i++){
            if(accounts.get(i).AccountID.equals(accountId)){
                Map<String, Object> m = new HashMap<>();
                m.put("Game", accounts.get(i).gameName);
                m.put("Geld", accounts.get(i).balance);
                m.put("KontoID", accounts.get(i).AccountID);
                m.put("Kontoname", accounts.get(i).name);
                m.put("Nutzer", user.getNutzerID());
                database.collection("deletedKonten").document(accounts.get(i).AccountID.toString()).set(m);
                accounts.remove(i);
                break;
            }
        }
        ColAccount.document(accountId.toString()).delete();
        ColAccount.whereEqualTo("Game", gameName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().isEmpty()){
                       database.collection("Game").whereEqualTo("Name", gameName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if (task.isSuccessful()) {
                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                       Number d = (Number)document.get("GameID");
                                       database.collection("Game").document(d.toString()).delete();
                                   }
                               }
                           }
                       });
                    }
                }
            }
        });
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
