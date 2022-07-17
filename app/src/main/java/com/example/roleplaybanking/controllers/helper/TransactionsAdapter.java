package com.example.roleplaybanking.controllers.helper;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;
import com.example.roleplaybanking.structures.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    private final List<Transaction> transactionList;

    public TransactionsAdapter(List<Transaction> transactions) {
        transactionList = transactions;
    }

    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View transactionView = inflater.inflate(R.layout.item_transaction, parent, false);

        return new ViewHolder(transactionView);
    }

    @Override
    public void onBindViewHolder(TransactionsAdapter.ViewHolder viewHolder, int position) {
        Transaction transaction = transactionList.get(position);

        TextView txtSender = viewHolder.sender;
        txtSender.setText(String.format("From %s", transaction.sender));
        TextView txtRecipient = viewHolder.recipient;
        txtRecipient.setText(String.format("To %s", transaction.recipient));
        TextView txtAmount = viewHolder.amount;
        txtAmount.setText(String.format("%s %s", transaction.amount, Account.currentAccount.currencySign));
        TextView txtTime = viewHolder.timestamp;
        String date = DateFormat.format("dd.MM.yyyy HH:mm", transaction.timestamp.toDate()).toString();
        txtTime.setText(date);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sender;
        public TextView recipient;
        public TextView amount;
        public TextView timestamp;

        public ViewHolder(View itemView) {
            super(itemView);

            sender = (TextView) itemView.findViewById(R.id.senderName);
            recipient = (TextView) itemView.findViewById(R.id.recipientName);
            amount = (TextView) itemView.findViewById(R.id.transactionAmount);
            timestamp = (TextView) itemView.findViewById(R.id.transactionTimestamp);
        }
    }
}
