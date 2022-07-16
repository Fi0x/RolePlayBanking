package com.example.roleplaybanking.structures;

import com.google.firebase.Timestamp;

public class Transaction {
    public String sender = "Nobody";
    public String recipient = "Someone";
    public double amount = 42;
    public Timestamp timestamp;
    public String Notiz;

    public Transaction(){};

    public Transaction(String sender, String recipient, double amount, Timestamp Time, String Notiz){
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = Time;
        this.Notiz = Notiz;
    }
}
