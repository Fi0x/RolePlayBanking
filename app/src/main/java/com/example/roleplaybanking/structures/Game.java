package com.example.roleplaybanking.structures;

public class Game {
    public String name;
    public Number adminID;
    public double defaultBalance;

    public Game(String name, Number adminID, double defaultBalance) {
        this.name = name;
        this.adminID = adminID;
        this.defaultBalance = defaultBalance;
    }
}
