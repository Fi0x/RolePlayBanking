package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roleplaybanking.DatabaseCon;
import com.example.roleplaybanking.R;
import com.example.roleplaybanking.controllers.helper.AccountsAdapter;
import com.example.roleplaybanking.controllers.helper.TransactionsAdapter;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Transaction;

import java.util.ArrayList;
import java.util.List;

public class AccountOverviewFragment extends Fragment {
    private List<Transaction> transactions = new ArrayList<>();
    public DatabaseCon DBc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_overview, container, false);

        TextView balance = view.findViewById(R.id.txt_balance);
        balance.setText(String.format("%s %s", Account.currentAccount.balance, Account.currentAccount.currencySign));
        DBc = AccountSelectionActivity.DBc;
        //TODO: Load right Transactions
        //DBc.ConnectUser("Nutzer0", "AdminAdmin");
        //DBc.ConnectTrans();
        //Log.d("ConnectKonto", DBc.getTrans(0).sender);
        int i;
        for (i = 0; DBc.getTrans(i) != null; i++) {
            //Log.d("onStart", DBc.getAccount(i).name);
            transactions.add(DBc.getTrans(i));
        }

        final RecyclerView rvTransactions = (RecyclerView) view.findViewById(R.id.rvTransactionHistory);
        rvTransactions.setAdapter(new TransactionsAdapter(transactions));
        rvTransactions.setLayoutManager(new LinearLayoutManager(view.getContext()));

        AccountOverviewActivity.fab.setVisibility(View.VISIBLE);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}