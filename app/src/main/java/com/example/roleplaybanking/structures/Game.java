package com.example.roleplaybanking.structures;

public class Game {
    public String name;
    public Number adminID;
    public double defaultBalance;

    public Game(String Name, Number AID, double dbalance) {
        this.name = Name;
        this.adminID = AID;
        this.defaultBalance = dbalance;
    }
}
