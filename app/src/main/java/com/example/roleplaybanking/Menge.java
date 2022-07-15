package com.example.roleplaybanking;

public class Menge {

    private Number Historys;
    private Number Kontos;
    private Number Nutzer;

    public Menge(){}
    public Menge(String hey){
        Historys = 1;
        Kontos = 1;
        Nutzer = 1;
    }

    public Number getHistorys() {
        return Historys;
    }

    public Number getKontos() {
        return Kontos;
    }

    public Number getNutzer() {
        return Nutzer;
    }
}
