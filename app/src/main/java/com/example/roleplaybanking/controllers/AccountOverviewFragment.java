package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Collections;
import java.util.List;

public class AccountOverviewFragment extends Fragment {
    private final List<Transaction> transactions = new ArrayList<>();
    public DatabaseCon DBc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_overview, container, false);
        DBc = AccountSelectionActivity.DBc;
        TextView balance = view.findViewById(R.id.txt_balance);

        balance.setText(String.format("%s %s", Account.currentAccount.balance, Account.currentAccount.currencySign));


        loadTransactions();

        final RecyclerView rvTransactions = (RecyclerView) view.findViewById(R.id.rvTransactionHistory);
        TransactionsAdapter adapter = new TransactionsAdapter(transactions);
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(new LinearLayoutManager(view.getContext()));

        AccountOverviewActivity.fab.setVisibility(View.VISIBLE);
        AccountOverviewActivity.isSubFragment = false;

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            loadTransactions();
            adapter.notifyDataSetChanged();
        }, 1000);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadTransactions()
    {
        DBc.orderTransactions();
        transactions.clear();
        transactions.addAll(Account.currentAccount.AccountHistory);

        Collections.sort(transactions, (transaction, t1) -> t1.timestamp.compareTo(transaction.timestamp));
    }
}