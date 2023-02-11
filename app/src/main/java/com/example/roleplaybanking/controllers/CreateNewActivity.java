package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.roleplaybanking.database.DatabaseCon;
import com.example.roleplaybanking.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreateNewActivity extends AppCompatActivity {
    TextInputLayout txtDefaultBalanceVis;

    public DatabaseCon DBc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.new_account_title));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        txtDefaultBalanceVis = findViewById(R.id.txtDefaultBalance);

        Button btnCreate = findViewById(R.id.create_button);
        btnCreate.setOnClickListener(this::verifyAndCreate);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void verifyAndCreate(View view) {
        TextInputEditText txtGameName = findViewById(R.id.txInGameName);
        TextInputEditText txtAccountName = findViewById(R.id.txInAccountName);
        TextInputEditText txtDefaultBalance = findViewById(R.id.txInDefaultBalance);
        CheckBox cbIsNew = findViewById(R.id.cbIsNewGame);

        DBc = AccountSelectionActivity.DBc;

        String gameName = txtGameName.getText().toString();
        String accountName = txtAccountName.getText().toString();
        String balanceString = txtDefaultBalance.getText().toString();

        double balance;
        try {
            balance = Double.parseDouble(balanceString);
        } catch (NumberFormatException e) {
            balance = 0;
        }

        if (gameName == null || gameName.equals("")) {
            Snackbar.make(view, getString(R.string.error_account_game_null), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (accountName == null || accountName.equals("")) {
            Snackbar.make(view, getString(R.string.error_account_name_null), Snackbar.LENGTH_LONG).show();
            return;
        }


        boolean gameExists = false;
        for (int i = 0; DBc.getGame(i) != null; i++) {
            if (DBc.getGame(i).name.contentEquals(gameName))
                gameExists = true;
        }

        if (cbIsNew.isChecked()) {
            if (gameExists) {
                Snackbar.make(view, getString(R.string.error_account_game_exists), Snackbar.LENGTH_LONG).show();
                return;
            }
            if (balanceString == null || balanceString.equals("")) {
                Snackbar.make(view, getString(R.string.error_account_balance_null), Snackbar.LENGTH_LONG).show();
                return;
            }

        } else {
            if (!(gameExists)) {
                Snackbar.make(view, gameName + getString(R.string.error_account_game_not_found), Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        if (cbIsNew.isChecked())
            AccountSelectionActivity.DBc.registerAccount(this, gameName, balance, accountName, true, balance);
        else {
            double Geld = 0;
            for (int g = 0; DBc.getGame(g) != null; g++) {
                if (DBc.getGame(g).name.contentEquals(gameName)) {
                    Geld = DBc.getGame(g).defaultBalance;
                    break;
                }
            }
            AccountSelectionActivity.DBc.registerAccount(this, gameName, Geld, accountName, false, balance);
        }
    }

    public void checkBoxPressed(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (view.getId() == R.id.cbIsNewGame)
            txtDefaultBalanceVis.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
    }

    public void closeActivityWhenDone() {
        finish();
    }
}