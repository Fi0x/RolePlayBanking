package com.example.roleplaybanking.controllers;

import android.content.Intent;
import android.os.Bundle;

import com.example.roleplaybanking.DatabaseCon;
import com.example.roleplaybanking.R;
import com.example.roleplaybanking.controllers.helper.AccountsAdapter;
import com.example.roleplaybanking.structures.Account;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

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
        //TODO: Load all accounts from backend to 'accounts'-ArrayList
        if(!(Alreadyconnected)){
            DBc.ConnectUser(this, "Nutzer0", "AdminAdmin");
            Alreadyconnected=true;
        }
        //When Zeitproblem gelöst accounts hinzufügen funktion
        //DBc.ConnectKontos(this);
        int i;
        for (i = 0; DBc.getAccount(i) != null; i++) {
            //Log.d("onStart", DBc.getAccount(i).name);
            accounts.add(DBc.getAccount(i));
        }
        final RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvTransactionHistory);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void notifyDBConnectionEstablished() {
        Account addAcc = DBc.getAccount(0);
        for (int i = 1; addAcc != null; i++) {
            addAccountToList(addAcc);
            addAcc = DBc.getAccount(i);
        }
    }

    public void addAccountToList(Account account) {
        accounts.add(account);
        adapter.notifyItemInserted(accounts.size() - 1);
    }

    private void openNewAccountCreation()
    {
        final Intent intent = new Intent(this, CreateNewActivity.class);
        startActivity(intent);
    }
}