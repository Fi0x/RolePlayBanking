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

    private void verifyAndCreate()
    {
        TextInputLayout txtGameName = findViewById(R.id.txtGameName);
        TextInputLayout txtAccountName = findViewById(R.id.txtAccountName);
        TextInputLayout txtDefaultBalance = findViewById(R.id.txtDefaultBalance);
        CheckBox cbIsNew = findViewById(R.id.cbIsNewGame);

        Account ac = new Account();
        //TODO: Get correct values from text-fields

        if(cbIsNew.isChecked())
        {
            Game g = new Game();
            g.adminName = ac.name;
            //TODO: Create new game with the account as admin
        }
        //TODO: check if all fields are filled in correctly
        //TODO: Create new Account and upload to DB
    }
}