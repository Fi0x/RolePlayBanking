package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AccountOverviewActivity extends AppCompatActivity {
    private NavController navCon;
    public static FloatingActionButton fab;
    public static boolean isSubFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_overview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Account.currentAccount.name + " (" + Account.currentAccount.gameName + ")");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        navCon = Navigation.findNavController(this, R.id.nav_host_fragment);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            navCon.navigate(R.id.action_FirstFragment_to_SecondFragment2);
            fab.setVisibility(View.INVISIBLE);
            isSubFragment = true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isSubFragment) {
                navCon.navigate(R.id.action_SecondFragment_to_FirstFragment2);
                fab.setVisibility(View.VISIBLE);
                isSubFragment = false;
            } else
                finish();
        } else if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.warning_title))
                    .setMessage(getString(R.string.warning_text))
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) ->
                    {
                        AccountSelectionActivity.DBc.deleteAccount(Account.currentAccount.gameName, Account.currentAccount.accountID);
                        finish();
                    })
                    .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}