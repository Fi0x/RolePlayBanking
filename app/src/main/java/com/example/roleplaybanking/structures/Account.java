package com.example.roleplaybanking.structures;

public class Account
{
    public static Account currentAccount = new Account();

    public String gameName = "Unknown Game";
    public String name = "Unknown Account";
    public double balance = 0;
    public String currencySign = "€";
}
