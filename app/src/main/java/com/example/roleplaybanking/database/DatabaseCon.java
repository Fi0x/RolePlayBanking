
package com.example.roleplaybanking.database;

import android.content.SharedPreferences;

import com.example.roleplaybanking.controllers.AccountSelectionActivity;
import com.example.roleplaybanking.controllers.CreateNewActivity;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Recipient;
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

    private User user;
    private String password;
    public final ArrayList<Account> accounts = new ArrayList<>();
    private final ArrayList<Transaction> TRANSACTIONS = new ArrayList<>();
    private final ArrayList<Recipient> RECIPIENTS = new ArrayList<>();
    private final ArrayList<Game> GAMES = new ArrayList<>();
    private final FirebaseFirestore DATABASE = FirebaseFirestore.getInstance();
    private final CollectionReference COL_USER = DATABASE.collection("Nutzer");
    private final CollectionReference COL_ACCOUNT = DATABASE.collection("Konten");
    private final CollectionReference COL_HISTORY = DATABASE.collection("History");
    private final DocumentReference DOC_AMOUNT = DATABASE.collection("Menge").document("Menge");
    private SharedPreferences sharedPreferences;

    public void registerUser(String name, String user, String userPW) {
        DOC_AMOUNT.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerUser(name, (long) m.get("nutzer"), user, userPW);
        });
    }

    public void registerUser(String name, Number id, String user, String userPW) {
        com.example.roleplaybanking.database.User newUser = new User(name, id, userPW);
        DATABASE.collection("Nutzer").document(user).set(newUser);
        DOC_AMOUNT.update("nutzer", (long) id + 1);
        this.user = newUser;
    }

    public void registerAccount(CreateNewActivity act, String game, Number money, String accountName, boolean isNewGame, double defaultBalance) {
        DOC_AMOUNT.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            if (isNewGame)
                registerGame(game, (Number) m.get("kontos"), defaultBalance);

            registerAccount(game, money, (long) m.get("kontos"), accountName, user.getUserID());
            if (act != null)
                act.closeActivityWhenDone();
        });
    }

    public void registerAccount(String game, Number money, Number accountID, String accountName, Number userID) {
        Account newAcc = new Account(game, accountName, Double.parseDouble(money.toString()), accountID);
        Map<String, Object> m = new HashMap<>();
        m.put("Game", game);
        m.put("Geld", money);
        m.put("KontoID", accountID);
        m.put("Kontoname", accountName);
        m.put("Nutzer", userID);

        DATABASE.collection("Konten").document(accountID.toString()).set(m);
        DOC_AMOUNT.update("kontos", (long) accountID + 1);
        accounts.add(newAcc);

        RECIPIENTS.add(new Recipient(accountName, game, accountID));
    }

    public void registerTransaction(Number amount, Number recipient, String note, Number userAccount, Timestamp time, Account fromAcc) {
        DOC_AMOUNT.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerTransaction(amount, recipient, (long) m.get("historys"), note, user.getUserID(), userAccount, time, fromAcc);
        });
    }

    public void registerTransaction(Number amount, Number recipient, Number historyID, String note, Number user, Number userAccount, Timestamp time, Account fromAcc) {
        int e;
        for (e = 0; e < RECIPIENTS.size(); e++) {
            if (RECIPIENTS.get(e).accountID.equals(recipient))
                break;
        }
        int n;
        for (n = 0; n < RECIPIENTS.size(); n++) {
            if (RECIPIENTS.get(n).accountID.equals(userAccount))
                break;
        }
        Transaction newTran = new Transaction(RECIPIENTS.get(n).name, RECIPIENTS.get(e).name, (double) amount, time, note, historyID, fromAcc.gameName);
        Map<String, Object> m = new HashMap<>();
        m.put("Betrag", amount);
        m.put("Empfaenger", recipient);
        m.put("HistoryID", historyID);
        m.put("Notiz", note);
        m.put("Nutzer", user);
        m.put("Nutzerkonto", userAccount);
        m.put("SendeZeit", time);

        DATABASE.collection("History").document(historyID.toString()).set(m);
        DOC_AMOUNT.update("historys", (long) historyID + 1);

        TRANSACTIONS.add(newTran);
        fromAcc.accountHistory.add(newTran);
    }

    public void registerGame(String name, Number admin, double defaultBalance) {
        DOC_AMOUNT.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> m = documentSnapshot.getData();
            registerGame(name, admin, (long) m.get("games"), defaultBalance);
        });
    }

    public void registerGame(String name, Number admin, Number gameID, double defaultBalance) {
        Map<String, Object> m = new HashMap<>();
        m.put("Admin", admin);
        m.put("GameID", gameID);
        m.put("Name", name);
        m.put("DefaultBalance", defaultBalance);

        DATABASE.collection("Game").document(gameID.toString()).set(m);
        DOC_AMOUNT.update("games", (long) gameID + 1);

        GAMES.add(new Game(name, admin, defaultBalance));
    }

    public void connect(AccountSelectionActivity activity, String name, Number id, String userPW) {
        user = new User(name, id, userPW);
        if (!(user.getUserPW().equals(password)))
            user = null;

        this.connectGames(activity);
    }

    public void connectUser(AccountSelectionActivity activity, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        String currentUserID = getFromDeviceStorage("UserID");
        String currentUserPW = getFromDeviceStorage("UserPW");

        DocumentReference DocUser = COL_USER.document(currentUserID);
        password = currentUserPW;
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

        COL_ACCOUNT.whereEqualTo("Nutzer", user.getUserID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public void addAccount(String gameName, String accountName, double balance, Number accountID) {
        Account newAcc = new Account(gameName, accountName, balance, accountID);
        accounts.add(newAcc);
    }

    public void connectTransactions(Number accountID, String gameName) {
        COL_HISTORY.whereEqualTo("Nutzerkonto", accountID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addTransaction((Number) m.get("Nutzerkonto"), (Number) m.get("Empfaenger"), Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString(), (Number) m.get("HistoryID"), gameName);
                }
            }
        });
        COL_HISTORY.whereEqualTo("Empfaenger", accountID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addTransaction((Number) m.get("Nutzerkonto"), (Number) m.get("Empfaenger"), Double.parseDouble(m.get("Betrag").toString()), (Timestamp) m.get("SendeZeit"), m.get("Notiz").toString(), (Number) m.get("HistoryID"), gameName);
                }
                orderTransactions();
            }
        });
    }

    public void addTransaction(Number sender, Number recipientID, double amount, Timestamp time, String note, Number historyID, String gameName) {
        for (int t = 0; t < TRANSACTIONS.size(); t++) {
            if (TRANSACTIONS.get(t).transactionID.equals(historyID))
                return;
        }

        String recipientName = "DELETED";
        for (int e = 0; e < RECIPIENTS.size(); e++) {
            if (RECIPIENTS.get(e).accountID.equals(recipientID)) {
                recipientName = RECIPIENTS.get(e).name;
                break;
            }
        }

        String senders = "DELETED";
        for (int n = 0; n < RECIPIENTS.size(); n++) {
            if (RECIPIENTS.get(n).accountID.equals(sender)) {
                senders = RECIPIENTS.get(n).name;
                break;
            }
        }

        Transaction newTran = new Transaction(senders, recipientName, amount, time, note, historyID, gameName);
        TRANSACTIONS.add(newTran);
    }

    public void connectRecipients(AccountSelectionActivity activity) {
        COL_ACCOUNT.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    for (int i = 0; i < GAMES.size(); i++) {
                        if (GAMES.get(i).name.contentEquals(m.get("Game").toString()))
                            addRecipient(m.get("Kontoname").toString(), m.get("Game").toString(), (Number) m.get("KontoID"));
                    }
                }
                connectAccounts(activity);
            }
        });
    }

    public void addRecipient(String recipient, String gameName, Number accountID) {
        RECIPIENTS.add(new Recipient(recipient, gameName, accountID));
    }

    public void connectGames(AccountSelectionActivity activity) {
        DATABASE.collection("Game").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    addGame(m.get("Name").toString(), (Number) m.get("Admin"), Double.parseDouble(m.get("DefaultBalance").toString()));
                }
                connectRecipients(activity);
            }
        });
    }

    public void addGame(String name, Number adminID, double defaultBalance) {
        GAMES.add(new Game(name, adminID, defaultBalance));
    }

    public void transferMoney(double amount, Number accountID, Number senderAccountID, boolean fromAdmin) {
        COL_ACCOUNT.whereEqualTo("KontoID", accountID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> m = document.getData();
                    transferMoneyOnComplete(amount, (Number) m.get("KontoID"), senderAccountID, fromAdmin);
                }
            }
        });
    }

    public void transferMoneyOnComplete(double amount, Number recipientAccountID, Number senderAccountID, boolean fromAdmin) {
        COL_ACCOUNT.document(recipientAccountID.toString()).update("Geld", FieldValue.increment(amount));

        if (!fromAdmin)
            COL_ACCOUNT.document(senderAccountID.toString()).update("Geld", FieldValue.increment(-1 * amount));
    }

    public String getName() {
        return user.getName();
    }

    public Number getAdminName(String gameName) {
        for (int i = 0; i < GAMES.size(); i++) {
            if (GAMES.get(i).name.equals(gameName))
                return GAMES.get(i).adminID;
        }

        return -1;
    }

    public Account getAccount(Integer i) {
        if (i > accounts.size() - 1)
            return null;

        return accounts.get(i);
    }

    @Nullable
    public Transaction getTransaction(Integer i) {
        if (i > TRANSACTIONS.size() - 1)
            return null;

        return TRANSACTIONS.get(i);
    }

    public void orderTransactions() {
        int i, j;
        for (i = 0; this.getAccount(i) != null; i++) {
            accounts.get(i).accountHistory.clear();
            for (j = 0; this.getTransaction(j) != null; j++) {
                boolean isSenderOrRecipient = TRANSACTIONS.get(j).sender.contentEquals(accounts.get(i).name) || TRANSACTIONS.get(j).recipient.contentEquals(accounts.get(i).name);
                if ((isSenderOrRecipient) && TRANSACTIONS.get(j).game.equals(accounts.get(i).gameName))
                    accounts.get(i).accountHistory.add(TRANSACTIONS.get(j));
            }
        }
    }

    public Game getGame(Integer i) {
        if (i > GAMES.size() - 1)
            return null;

        return GAMES.get(i);
    }

    public Recipient getRecipient(Integer i) {
        if (i > RECIPIENTS.size() - 1)
            return null;

        return RECIPIENTS.get(i);
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
            if (accounts.get(i).accountID.equals(accountId)) {
                Map<String, Object> m = new HashMap<>();
                m.put("Game", accounts.get(i).gameName);
                m.put("Geld", accounts.get(i).balance);
                m.put("KontoID", accounts.get(i).accountID);
                m.put("Kontoname", accounts.get(i).name);
                m.put("Nutzer", user.getUserID());

                DATABASE.collection("deletedKonten").document(accounts.get(i).accountID.toString()).set(m);
                accounts.remove(i);
                break;
            }
        }

        COL_ACCOUNT.document(accountId.toString()).delete();
        COL_ACCOUNT.whereEqualTo("Game", gameName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    DATABASE.collection("Game").whereEqualTo("Name", gameName).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                Number d = (Number) document.get("GameID");
                                DATABASE.collection("Game").document(d.toString()).delete();
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
        public static final String alphaNum = upper + lower + digits;

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
            this(32, new SecureRandom(), alphaNum);
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];

            return new String(buf);
        }
    }
}
