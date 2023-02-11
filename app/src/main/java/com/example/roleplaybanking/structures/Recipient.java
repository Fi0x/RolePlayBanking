package com.example.roleplaybanking.structures;

public class Recipient {
    public String name;
    public String gameName;
    public Number accountID;

    public Recipient(String name, String gameName, Number accountID) {
        this.name = name;
        this.gameName = gameName;
        this.accountID = accountID;
    }
}