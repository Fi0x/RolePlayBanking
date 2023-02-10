package com.example.roleplaybanking.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.roleplaybanking.database.DatabaseCon;
import com.example.roleplaybanking.R;
import com.example.roleplaybanking.structures.Account;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewTransactionFragment extends Fragment {
    private ArrayList<String> recipientList = new ArrayList<>();
    public DatabaseCon DBc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        TextView txtBalance = view.findViewById(R.id.txtCurrentBalance);
        txtBalance.setText(String.format("%s %s", Account.currentAccount.balance, Account.currentAccount.currencySign));

        DBc = AccountSelectionActivity.DBc;
        int i;
        for (i = 0; DBc.getEmpfaenger(i) != null; i++) {
            //Log.d("onStart", DBc.getAccount(i).name);
            if(Account.currentAccount.gameName.contentEquals(DBc.getEmpfaenger(i).GameName)){
                if(!recipientList.contains(DBc.getEmpfaenger(i).Name)){
                    recipientList.add(DBc.getEmpfaenger(i).Name);
                }
            }
        }

        Button sendButton = view.findViewById(R.id.btnSend);
        sendButton.setOnClickListener(view1 -> sendTransaction(view));

        AutoCompleteTextView txtAutoComplete = view.findViewById(R.id.txtRecipientAutoComplete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, recipientList);
        txtAutoComplete.setAdapter(adapter);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void sendTransaction(View view)
    {
        AutoCompleteTextView txtAutoComplete = view.findViewById(R.id.txtRecipientAutoComplete);
        String Ename = txtAutoComplete.getText().toString();
        boolean found = false;
        int i;
        for (i = 0; DBc.getEmpfaenger(i) != null; i++) {
            if(DBc.getEmpfaenger(i).Name.contentEquals(Ename) && DBc.getEmpfaenger(i).GameName.contentEquals(Account.currentAccount.gameName)){
                found = true;
                break;
            }
        }

        if(!found){
            Snackbar.make(view, "Empfaenger not found", Snackbar.LENGTH_LONG).show();
            return;
        }

        TextInputEditText txtAmount = view.findViewById(R.id.txtTransferAmount);
        String amountString = txtAmount.getText().toString();

        if (amountString == null || amountString.equals("")) {
            Snackbar.make(view, getString(R.string.error_transaction_amount_null), Snackbar.LENGTH_LONG).show();
            return;
        }

        double amount = Double.parseDouble(amountString);
        Number AID = DBc.getAdminName(Account.currentAccount.gameName);
        boolean FromAdmin = Account.currentAccount.AccountID.equals(AID);
        if (!FromAdmin && (amount <= 0 || amount > Account.currentAccount.balance)) {
            Snackbar.make(view, getString(R.string.error_transaction_amount_invalid), Snackbar.LENGTH_LONG).show();
            return;
        }

        Date date = Calendar.getInstance().getTime();
        Timestamp time = new Timestamp(date);
        DBc.registerTransaction(amount, DBc.getEmpfaenger(i).KontoID, "", Account.currentAccount.AccountID, time, Account.currentAccount);

        DBc.transferMoney(amount, DBc.getEmpfaenger(i).KontoID, Account.currentAccount.AccountID, FromAdmin);
        if(!FromAdmin){
            Account.currentAccount.balance -= amount;
        }

        NavController nc = Navigation.findNavController(view);
        nc.navigate(R.id.action_SecondFragment_to_FirstFragment2);
        AccountOverviewActivity.fab.setVisibility(View.VISIBLE);
        AccountOverviewActivity.isSubFragment = false;
    }
}