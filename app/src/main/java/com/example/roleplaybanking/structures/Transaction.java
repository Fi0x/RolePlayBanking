package com.example.roleplaybanking.structures;

import com.google.firebase.Timestamp;

public class Transaction {
    public String sender;
    public String recipient;
    public String Game;
    public double amount;
    public Timestamp timestamp;
    public String Notiz;
    public Number TransactioID;

    public Transaction(String sender, String recipient, double amount, Timestamp Time, String Notiz, Number TransactionID, String Game) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = Time;
        this.Notiz = Notiz;
        this.TransactioID = TransactionID;
        this.Game = Game;
    }
}