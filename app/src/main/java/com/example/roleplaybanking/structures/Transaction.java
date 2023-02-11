package com.example.roleplaybanking.structures;

import com.google.firebase.Timestamp;

public class Transaction {
    public String sender;
    public String recipient;
    public String game;
    public double amount;
    public Timestamp timestamp;
    public String note;
    public Number transactionID;

    public Transaction(String sender, String recipient, double amount, Timestamp time, String note, Number transactionID, String game) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = time;
        this.note = note;
        this.transactionID = transactionID;
        this.game = game;
    }
}