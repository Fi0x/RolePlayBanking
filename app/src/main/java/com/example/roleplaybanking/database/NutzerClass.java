package com.example.roleplaybanking.database;

public class NutzerClass {

    private final String Name;
    private final Number NutzerID;
    private final String NutzerPW;

    public NutzerClass(String Name, Number NutzerId, String NutzerPW) {
        this.Name = Name;
        this.NutzerID = NutzerId;
        this.NutzerPW = NutzerPW;
    }

    public String getName() {
        return Name;
    }

    public Number getNutzerID() {
        return NutzerID;
    }

    public String getNutzerPW() {
        return NutzerPW;
    }
}
