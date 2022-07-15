package com.example.roleplaybanking.controllers;

import android.content.Intent;
import android.os.Bundle;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.controllers.helper.AccountsAdapter;
import com.example.roleplaybanking.structures.Account;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class AccountSelectionActivity extends AppCompatActivity {

    private ArrayList<Account> accounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_selection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: Load all accounts from backend to 'accounts'-ArrayList
        accounts.add(new Account());//Placeholder for testing
        Account acc2 = new Account();//Placeholder for testing
        acc2.name = "TEST";//Placeholder for testing
        accounts.add(acc2);//Placeholder for testing

        final RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvAccounts);
        rvContacts.setAdapter(new AccountsAdapter(accounts));
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Open popup to create new account
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings)
            return true;

        return super.onOptionsItemSelected(item);
    }
}