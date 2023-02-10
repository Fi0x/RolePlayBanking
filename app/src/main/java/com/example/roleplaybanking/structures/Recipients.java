package com.example.roleplaybanking.structures;

public class Recipients {
    public String Name;
    public String GameName;
    public Number KontoID;

    public Recipients(String Name, String GameName, Number ID) {
        this.Name = Name;
        this.GameName = GameName;
        this.KontoID = ID;
    }
}