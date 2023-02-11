package com.example.roleplaybanking.database;

public class User {

    private final String NAME;
    private final Number USER_ID;
    private final String USER_PW;

    public User(String name, Number userID, String userPW) {
        this.NAME = name;
        this.USER_ID = userID;
        this.USER_PW = userPW;
    }

    public String getName() {
        return NAME;
    }

    public Number getUserID() {
        return USER_ID;
    }

    public String getUserPW() {
        return USER_PW;
    }
}
