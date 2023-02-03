package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roleplaybanking.database.DatabaseCon;
import com.example.roleplaybanking.R;
import com.example.roleplaybanking.controllers.helper.TransactionsAdapter;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Transaction;

import java.util.ArrayList;
import java.util.List;

public class AccountOverviewFragment extends Fragment {
    private final List<Transaction> transactions = new ArrayList<>();
    public DatabaseCon DBc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_overview, container, false);

        TextView balance = view.findViewById(R.id.txt_balance);
        balance.setText(String.format("%s %s", Account.currentAccount.balance, Account.currentAccount.currencySign));
        DBc = AccountSelectionActivity.DBc;

        //TODO: Update this more often, not only in onCreate
        for (int i = 0; Account.currentAccount.getHistory(i) != null; i++) {
            transactions.add(Account.currentAccount.getHistory(i));
        }

        final RecyclerView rvTransactions = (RecyclerView) view.findViewById(R.id.rvTransactionHistory);
        rvTransactions.setAdapter(new TransactionsAdapter(transactions));
        rvTransactions.setLayoutManager(new LinearLayoutManager(view.getContext()));

        AccountOverviewActivity.fab.setVisibility(View.VISIBLE);
        AccountOverviewActivity.isSubFragment = false;

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}