package com.example.roleplaybanking.structures;

public class Game {
    public String name;
    public Number adminID;
    public double defaultBalance;

    public Game() {
    }

    public Game(String Name, Number AID) {
        this.name = Name;
        this.adminID = AID;
    }
}
