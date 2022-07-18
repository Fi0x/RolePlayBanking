package com.example.roleplaybanking.structures;

import com.google.firebase.Timestamp;

import java.util.Date;


public class Transaction {
    public String sender = "Nobody";
    public String recipient = "Someone";
    public double amount = 42;
    public Timestamp timestamp = new Timestamp(new Date(0));
    public String Notiz;

    public Transaction() {
    }

    public Transaction(String sender, String recipient, double amount, Timestamp Time, String Notiz) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = Time;
        this.Notiz = Notiz;
    }
}