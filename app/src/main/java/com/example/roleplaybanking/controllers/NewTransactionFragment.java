package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;
import com.google.android.material.textfield.TextInputEditText;

public class NewTransactionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        TextView txtBalance = view.findViewById(R.id.txtCurrentBalance);
        txtBalance.setText(String.format("%s %s", Account.currentAccount.balance, Account.currentAccount.currencySign));

        Spinner sp = view.findViewById(R.id.spinRecipientList);
        //TODO: Add possible recipients

        Button sendButton = view.findViewById(R.id.btnSend);
        sendButton.setOnClickListener(view1 -> sendTransaction(view));

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void sendTransaction(View view)
    {
        //TODO: Get recipient name from spinner and validate

        TextInputEditText txtAmount = view.findViewById(R.id.txtTransferAmount);
        String amountString = txtAmount.getText().toString();

        if(amountString == null || amountString.equals(""))
        {
            //TODO: Send user error that amount can't be null
            return;
        }

        double amount = Double.parseDouble(amountString);
        if(amount <= 0 || amount > Account.currentAccount.balance)
        {
            //TODO: Notify user that amount is invalid
            return;
        }


        NavController nc = Navigation.findNavController(view);
        nc.navigate(R.id.action_SecondFragment_to_FirstFragment2);
        AccountOverviewActivity.fab.setVisibility(View.VISIBLE);
    }
}