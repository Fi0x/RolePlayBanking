package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Game;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreateNewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add new Account");
        setSupportActionBar(toolbar);

        Button btnCreate = findViewById(R.id.create_button);
        btnCreate.setOnClickListener(view -> verifyAndCreate());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    private void verifyAndCreate()
    {
        TextInputEditText txtGameName = findViewById(R.id.txInGameName);
        TextInputEditText txtAccountName = findViewById(R.id.txInAccountName);
        TextInputEditText txtDefaultBalance = findViewById(R.id.txInDefaultBalance);
        CheckBox cbIsNew = findViewById(R.id.cbIsNewGame);

        String gameName = txtGameName.getText().toString();
        String accountName = txtAccountName.getText().toString();
        String balanceString = txtDefaultBalance.getText().toString();

        if(accountName == null || accountName.equals("") || gameName == null || gameName.equals(""))
        {
            //TODO: Send user error that game name and account name can't be null
            return;
        }

        Account ac = new Account();
        ac.name = accountName;
        ac.gameName = gameName;

        if(cbIsNew.isChecked())
        {
            //TODO: Check if game-name already exists in Firebase and if not create new game with the account as admin
            if(balanceString == null || balanceString.equals(""))
            {
                //TODO: Send user error that balance can't be null
                return;
            }

            //TODO: If game name already exists in DB, send error to user

            Game g = new Game();
            g.name = gameName;
            g.adminName = accountName;
            g.defaultBalance = Double.parseDouble(balanceString);
        }
        else
        {
            //TODO: Check if game-name exists in DB. If not, send a user error and abort
            return;
        }

        //TODO: Get correct default-balance from DB and set it in local account

        //TODO: Upload new Account to DB

        finish();
    }
}