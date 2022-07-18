package com.example.roleplaybanking.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.roleplaybanking.database.DatabaseCon;
import com.example.roleplaybanking.R;
import com.example.roleplaybanking.controllers.helper.AccountsAdapter;
import com.example.roleplaybanking.structures.Account;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AccountSelectionActivity extends AppCompatActivity {
    public static ArrayList<Account> accounts = new ArrayList<>();
    public static DatabaseCon DBc = new DatabaseCon();
    public static boolean Alreadyconnected = false;
    private AccountsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_selection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new AccountsAdapter(accounts);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> openNewAccountCreation());
    }

    @Override
    protected void onStart() {
        super.onStart();

        accounts.clear();
        if (!(Alreadyconnected)) {
            DBc.connectUser(this, getSharedPreferences("LoginFile", Context.MODE_PRIVATE));
            Alreadyconnected = true;
        }

        for (int i = 0; DBc.getAccount(i) != null; i++) {
            this.addAccountToList(DBc.getAccount(i));
        }

        final RecyclerView rvContacts = findViewById(R.id.rvTransactionHistory);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    public void notifyDBConnectionEstablished() {
        accounts.clear();
        int i;
        for (i = 0; DBc.getAccount(i) != null; i++) {
            this.addAccountToList(DBc.getAccount(i));
        }
    }

    public void addAccountToList(Account account) {
        accounts.add(account);
        adapter.notifyItemInserted(accounts.size() - 1);
    }

    private void openNewAccountCreation() {
        final Intent intent = new Intent(this, CreateNewActivity.class);
        startActivity(intent);
    }
}