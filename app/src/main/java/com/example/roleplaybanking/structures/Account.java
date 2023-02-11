package com.example.roleplaybanking.structures;

import java.util.ArrayList;

public class Account {
    public static Account currentAccount = new Account();

    public String gameName = "Unknown Game";
    public String name = "Unknown Account";
    public double balance = 0;
    public String currencySign = "€";
    public Number accountID = -1;
    public ArrayList<Transaction> accountHistory = new ArrayList<>();

    public Account() {
    }

    public Account(String gameName, String name, double balance, Number accountID) {
        this.gameName = gameName;
        this.name = name;
        this.balance = balance;
        this.accountID = accountID;
    }
}