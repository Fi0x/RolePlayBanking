package com.example.roleplaybanking.controllers.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {
    private final List<Account> mAccounts;

    public AccountsAdapter(List<Account> accounts) {
        mAccounts = accounts;
    }

    @Override
    public AccountsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View accountView = inflater.inflate(R.layout.item_account, parent, false);

        return new ViewHolder(accountView);
    }

    @Override
    public void onBindViewHolder(AccountsAdapter.ViewHolder viewHolder, int position) {
        Account account = mAccounts.get(position);

        TextView txtAccountName = viewHolder.nameTextView;
        txtAccountName.setText(account.name);
        TextView txtGameName = viewHolder.gameTextView;
        txtGameName.setText(account.gameName);
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView gameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.account_name);
            gameTextView = (TextView) itemView.findViewById(R.id.account_game);

            itemView.setOnClickListener(v -> {
                //TODO: Open specific account
            });
        }
    }
}
