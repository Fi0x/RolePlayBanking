package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;

public class NewTransactionFragment extends Fragment {
    //TODO: Fill and finish class
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        TextView txtBalance = view.findViewById(R.id.txtCurrentBalance);
        txtBalance.setText(String.format("%s %s", Account.currentAccount.balance, Account.currentAccount.currencySign));

        Button sendButton = view.findViewById(R.id.btnSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Check if recipient is selected and amount to send is not greater than account balance

                NavController nc = Navigation.findNavController(view);
                nc.navigate(R.id.action_SecondFragment_to_FirstFragment2);
                AccountOverviewActivity.fab.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}