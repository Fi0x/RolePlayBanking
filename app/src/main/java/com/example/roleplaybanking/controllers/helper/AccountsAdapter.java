package com.example.roleplaybanking.controllers.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {

    private List<Account> mAccounts;

    public AccountsAdapter(List<Account> accounts) {
        mAccounts = accounts;
    }

    @Override
    public AccountsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View accountView = inflater.inflate(R.layout.item_account, parent, false);

        ViewHolder viewHolder = new ViewHolder(accountView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AccountsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Account account = mAccounts.get(position);

        //TODO: Find out what to do here
        TextView textView = viewHolder.nameTextView;
        textView.setText(account.getName());
        Button button = viewHolder.messageButton;
        button.setText(account.isOnline() ? "Message" : "Offline");
        button.setEnabled(account.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nameTextView;
        public Button messageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.account_name);
            //TODO: Set all stats correctly
            messageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messageButton.getText() == "activated") {
                        messageButton.setText("deactivated");
                    } else {
                        messageButton.setText("activated");
                    }
                }
            });
        }
    }
}
