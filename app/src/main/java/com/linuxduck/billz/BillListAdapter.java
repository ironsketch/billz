package com.linuxduck.billz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BillListAdapter extends ArrayAdapter<Bill> {
    public BillListAdapter(Context context, ArrayList<Bill> bills){
        super(context, 0, bills);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final View tmpview = convertView;
        Bill bill = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bill_list_view, parent, false);
        }
        DBH dbh = new DBH(getContext());
        LinearLayout ll = convertView.findViewById(R.id.allbills);
        if(position % 2 == 0){
            ll.setBackgroundColor(getContext().getResources().getColor(R.color.black));
        } else {
            ll.setBackgroundColor(getContext().getResources().getColor(R.color.background));
        }
        // LL for all bills
        TextView nameTV = (TextView) convertView.findViewById(R.id.name);
        TextView amountTV = (TextView) convertView.findViewById(R.id.amount);

        // Set onClick listener for delete group button
        ImageView deleteButton = convertView.findViewById(R.id.deletebill);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbh.deleteBill(bill.getBid());
                Intent myIntent = new Intent(getContext(), BillsActivity.class);
                getContext().startActivity(myIntent);
            }
        });

        nameTV.setText(bill.getName());
        amountTV.setText("$" + bill.getAmount().toString()  + " Due: " + bill.getDuedate());
        return convertView;
    }
}
