package com.example.roleplaybanking.database;

public class NutzerClass {

    private String Name;
    private Number NutzerID;
    private String NutzerName;
    private String NutzerPW;

    public NutzerClass() {
    }

    public NutzerClass(String Name, Number NutzerId, String NutzerName, String NutzerPW) {
        this.Name = Name;
        this.NutzerID = NutzerId;
        this.NutzerName = NutzerName;
        this.NutzerPW = NutzerPW;
    }

    public String getName() {
        return Name;
    }

    public Number getNutzerID() {
        return NutzerID;
    }

    public String getNutzerName() {
        return NutzerName;
    }

    public String getNutzerPW() {
        return NutzerPW;
    }
}
