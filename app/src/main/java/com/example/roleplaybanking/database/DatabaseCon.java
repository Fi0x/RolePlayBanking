
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
    public final ArrayList<Account> accounts = new ArrayList<>();
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
        NutzerClass newuser = new NutzerClass(Name, Id, UserPW);
        database.collection("Nutzer").document(User).set(newuser);
        DocMenge.update("nutzer", (long) Id + 1);
        user = newuser;
    }

    public void registerAccount(CreateNewActivity act, String Game, Number Geld, String Kontonamen, boolean isnewGame, double dbalance) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            if (isnewGame)
                registerGame(Game, (Number) m.get("kontos"), dbalance);

            registerAccount(Game, Geld, (long) m.get("kontos"), Kontonamen, user.getNutzerID());
            if (act != null)
                act.closeActivityWhenDone();
        });
    }

    public void registerAccount(String Game, Number Geld, Number KontoID, String Kontonamen, Number NutzerID) {
        Account newAcc = new Account(Game, Kontonamen, Double.parseDouble(Geld.toString()), KontoID);
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
    }

    public void registerTransaction(Number Betrag, Number Empfaenger, String Notiz, Number Nutzerkonto, Timestamp time, Account fromAcc) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerTransaction(Betrag, Empfaenger, (long) m.get("historys"), Notiz, user.getNutzerID(), Nutzerkonto, time, fromAcc);
        });
    }

    public void registerTransaction(Number Betrag, Number Empfaenger, Number HistoryID, String Notiz, Number Nutzer, Number Nutzerkonto, Timestamp time, Account fromAcc) {
        int e;
        for (e = 0; e < recipients.size(); e++) {
            if (recipients.get(e).KontoID.equals(Empfaenger))
                break;
        }
        int n;
        for (n = 0; n < recipients.size(); n++) {
            if (recipients.get(n).KontoID.equals(Nutzerkonto))
                break;
        }
        Transaction newTran = new Transaction(recipients.get(n).Name, recipients.get(e).Name, (double) Betrag, time, Notiz, HistoryID, fromAcc.gameName);
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
    }

    public void registerGame(String name, Number Admin, double dbalance) {
        DocMenge.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerGame(name, Admin, (long) m.get("games"), dbalance);
        });
    }

    public void registerGame(String name, Number Admin, Number GameID, double dbalance) {
        Map<String, Object> m = new HashMap<>();
        m.put("Admin", Admin);
        m.put("GameID", GameID);
        m.put("Name", name);
        m.put("DefaultBalance", dbalance);

        database.collection("Game").document(GameID.toString()).set(m);
        DocMenge.update("games", (long) GameID + 1);
        Game a = new Game(name, Admin, dbalance);
        games.add(a);
    }

    public void connect(AccountSelectionActivity activity, String Name, Number Id, String UserPW) {
        user = new NutzerClass(Name, Id, UserPW);
        if (!(user.getNutzerPW().equals(PW)))
            user = null;

        this.connectGames(activity);
    }

    public void connectUser(AccountSelectionActivity activity, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        String currentUserID = getFromDeviceStorage("UserID");
        String currentUserPW = getFromDeviceStorage("UserPW");

        DocumentReference DocUser = ColUser.document(currentUserID);
        PW = currentUserPW;
        DocUser.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            if (m == null)
                registerUser(currentUserID, currentUserID, currentUserPW);
            else
                connect(activity, m.get("name").toString(), (long) m.get("nutzerID"), m.get("nutzerPW").toString());
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
                        connectTransactions((Number) m.get("KontoID"), m.get("Game").toString());
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

    public void connectTransactions(Number KontoID, String Gamename) {
        ColHistory.whereEqualTo("Nutzerkonto", KontoID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addTransaction((Number) m.get("Nutzerkonto"), (Number) m.get("Empfaenger"), Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString(), (Number) m.get("HistoryID"), Gamename);
                }
            }
        });
        ColHistory.whereEqualTo("Empfaenger", KontoID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addTransaction((Number) m.get("Nutzerkonto"), (Number) m.get("Empfaenger"), Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString(), (Number) m.get("HistoryID"), Gamename);
                }
                orderTransactions();
            }
        });
    }

    public void addTransaction(Number sender, Number recipient, double amount, Timestamp Time, String notiz, Number HistoryID, String Gamename) {
        for (int t = 0; t < transactions.size(); t++) {
            if (transactions.get(t).TransactioID.equals(HistoryID))
                return;
        }

        String Empf = "DELETED";
        for (int e = 0; e < recipients.size(); e++) {
            if (recipients.get(e).KontoID.equals(recipient)) {
                Empf = recipients.get(e).Name;
                break;
            }
        }

        String senders = "DELETED";
        for (int n = 0; n < recipients.size(); n++) {
            if (recipients.get(n).KontoID.equals(sender)) {
                senders = recipients.get(n).Name;
                break;
            }
        }

        Transaction newTran = new Transaction(senders, Empf, amount, Time, notiz, HistoryID, Gamename);
        transactions.add(newTran);
    }

    public void connectRecipients(AccountSelectionActivity activity) {
        ColAccount.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    for (int i = 0; i < games.size(); i++) {
                        if (games.get(i).name.contentEquals(m.get("Game").toString()))
                            addRecipient(m.get("Kontoname").toString(), m.get("Game").toString(), (Number) m.get("KontoID"));
                    }
                }
                connectAccounts(activity);
            }
        });
    }

    public void addRecipient(String Empfaenger, String GameName, Number KontoID) {
        Recipients R = new Recipients(Empfaenger, GameName, KontoID);
        recipients.add(R);
    }

    public void connectGames(AccountSelectionActivity activity) {
        database.collection("Game").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addGame(m.get("Name").toString(), (Number) m.get("Admin"), Double.parseDouble(m.get("DefaultBalance").toString()));
                }
                connectRecipients(activity);
            }
        });
    }

    public void addGame(String name, Number AID, double dbalance) {
        Game a = new Game(name, AID, dbalance);
        games.add(a);
    }

    public void transferMoney(double Betrag, Number KontoId, Number senderKontoID, boolean FromAdmin) {
        ColAccount.whereEqualTo("KontoID", KontoId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    transferMoneyOnComplete(Betrag, (Number) m.get("KontoID"), senderKontoID, FromAdmin);
                }
            }
        });
    }

    public void transferMoneyOnComplete(double Betrag, Number KontoIDempfaenger, Number senderKontoID, boolean FromAdmin) {
        ColAccount.document(KontoIDempfaenger.toString()).update("Geld", FieldValue.increment(Betrag));

        if (!FromAdmin)
            ColAccount.document(senderKontoID.toString()).update("Geld", FieldValue.increment(-1 * Betrag));
    }

    public String getName() {
        return user.getName();
    }

    public Number getAdminName(String gameName) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).name.equals(gameName))
                return games.get(i).adminID;
        }

        return -1;
    }

    public Account getAccount(Integer i) {
        if (i > accounts.size() - 1)
            return null;

        return accounts.get(i);
    }

    public ArrayList<Account> getAllAccounts() {
        return accounts;
    }

    @Nullable
    public Transaction getTransaction(Integer i) {
        if (i > transactions.size() - 1)
            return null;

        return transactions.get(i);
    }

    public void orderTransactions() {
        int i, j;
        for (i = 0; this.getAccount(i) != null; i++) {
            accounts.get(i).AccountHistory.clear();
            for (j = 0; this.getTransaction(j) != null; j++) {
                boolean isSenderOrRecipient = transactions.get(j).sender.contentEquals(accounts.get(i).name) || transactions.get(j).recipient.contentEquals(accounts.get(i).name);
                if ((isSenderOrRecipient) && transactions.get(j).Game.equals(accounts.get(i).gameName))
                    accounts.get(i).AccountHistory.add(transactions.get(j));
            }
        }
    }

    public Game getGame(Integer i) {
        if (i > games.size() - 1)
            return null;

        return games.get(i);
    }

    public Recipients getEmpfaenger(Integer i) {
        if (i > recipients.size() - 1)
            return null;

        return recipients.get(i);
    }

    private String getFromDeviceStorage(String variableName) {
        String generated;
        String id = sharedPreferences.getString(variableName, "0123456789");

        if (id.equals("0123456789")) {
            generated = new StringGenerator().nextString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(variableName, generated);
            editor.apply();
        } else
            generated = id;

        return generated;
    }

    public void deleteAccount(String gameName, Number accountId) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).AccountID.equals(accountId)) {
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
        ColAccount.whereEqualTo("Game", gameName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    database.collection("Game").whereEqualTo("Name", gameName).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                Number d = (Number) document.get("GameID");
                                database.collection("Game").document(d.toString()).delete();
                            }
                        }
                    });
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
