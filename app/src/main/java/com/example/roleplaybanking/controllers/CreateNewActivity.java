package com.example.roleplaybanking.controllers;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.roleplaybanking.DatabaseCon;
import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Game;
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
        toolbar.setTitle("Add new Account");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        txtDefaultBalanceVis = findViewById(R.id.txtDefaultBalance);

        Button btnCreate = findViewById(R.id.create_button);
        btnCreate.setOnClickListener(view -> verifyAndCreate());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void verifyAndCreate()
    {
        TextInputEditText txtGameName = findViewById(R.id.txInGameName);
        TextInputEditText txtAccountName = findViewById(R.id.txInAccountName);
        TextInputEditText txtDefaultBalance = findViewById(R.id.txInDefaultBalance);
        CheckBox cbIsNew = findViewById(R.id.cbIsNewGame);
        DBc = AccountSelectionActivity.DBc;

        String gameName = txtGameName.getText().toString();
        String accountName = txtAccountName.getText().toString();
        String balanceString = txtDefaultBalance.getText().toString();
        //TODO: balanceString eingabe in Long umwandeln ohne Null Object zu generieren
        Long balance = (long)100000;

        //TODO snackbar verwenden
        if(accountName == null || accountName.equals("") || gameName == null || gameName.equals(""))
        {
            if((accountName == null || accountName.equals("")) && (gameName == null || gameName.equals(""))){
                txtAccountName.setText("Darf nicht leer sein!");
                txtAccountName.setBackgroundColor(Color.RED);
                txtGameName.setText("Darf nicht leer sein!");
                txtGameName.setBackgroundColor(Color.RED);
            } else if(gameName == null || gameName.equals("")) {
                txtGameName.setText("Darf nicht leer sein!");
                txtGameName.setBackgroundColor(Color.RED);
            } else {
                txtAccountName.setText("Darf nicht leer sein!");
                txtAccountName.setBackgroundColor(Color.RED);
            }
            return;
        }


        int i;
        boolean Gameexist = false;
        for (i = 0; DBc.getGame(i) != null; i++) {
            if(DBc.getGame(i).contentEquals(gameName)){
                Gameexist = true;
            }
        }

        if(cbIsNew.isChecked())
        {
            //TODO snackbar verwenden
            if(Gameexist){
                txtGameName.setText(gameName + " (Gibt es bereits!)");
                txtGameName.setBackgroundColor(Color.RED);
                return;
            }
            if(balanceString == null || balanceString.equals(""))
            {
                txtDefaultBalance.setText("Darf nicht leer sein!");
                txtDefaultBalance.setBackgroundColor(Color.RED);
                return;
            }

            AccountSelectionActivity.DBc.RegisterGame(gameName, DBc.getUser().getNutzerID());

        }
        else
        {
            //TODO snackbar verwenden
            if(!(Gameexist)){
                txtGameName.setText(gameName + " (Gibt es nicht!)");
                txtGameName.setBackgroundColor(Color.RED);
                return;
            }
        }

        if(cbIsNew.isChecked()){
            AccountSelectionActivity.DBc.RegisterKonto(gameName, balance, accountName);
        } else {
            AccountSelectionActivity.DBc.RegisterKonto(gameName, (long) 0, accountName);
        }
        finish();
    }

    public void checkBoxPressed(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();
        if(view.getId() == R.id.cbIsNewGame)
            txtDefaultBalanceVis.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
    }
}