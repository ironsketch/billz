package com.linuxduck.billz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class GroupListAdapter extends ArrayAdapter<GroupList> {
    public GroupListAdapter(Context context, ArrayList<GroupList> groups){
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View tmpview = convertView;
        GroupList group = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_list_view, parent, false);
        }
        DBH dbh = new DBH(getContext());
        LinearLayout ll = convertView.findViewById(R.id.allgroup);
        if(position % 2 == 0){
            ll.setBackgroundColor(getContext().getResources().getColor(R.color.black));
        } else {
            ll.setBackgroundColor(getContext().getResources().getColor(R.color.background));
        }
        // LL for all bills
        TextView nameTV = (TextView) convertView.findViewById(R.id.name);
        TextView amountTV = (TextView) convertView.findViewById(R.id.amount);

        // Set onClick listener for delete group button
        ImageView deleteButton = convertView.findViewById(R.id.deletegroup);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbh.deleteGroup(group.group.getGid());
                Intent myIntent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(myIntent);
            }
        });

        // Set onClick listener for add bill button
        ImageView addButton = convertView.findViewById(R.id.addBill);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Bill> bills = new ArrayList<>();
                try {
                    bills = dbh.getNonDeletedBills();
                }
                catch (Exception e){
                    System.out.println("Something went wrong.");
                }

                PopupMenu popup = new PopupMenu(getContext(), addButton);
                Menu menu = popup.getMenu();
                menu.add(0, -1, 0, "Add New Bill");
                for(int i = 0; i < bills.size(); i++){
                    menu.add(0, bills.get(i).getBid(), i + 1, bills.get(i).getName() + ": $" +
                            bills.get(i).getAmount().toString() + " due: " + bills.get(i).getDuedate().toString());
                }
                popup.getMenuInflater().inflate(R.menu.bills, popup.getMenu());

                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle() == "Add New Bill"){
                            Intent myIntent = new Intent(getContext(), AddBillActivity.class);
                            myIntent.putExtra("addtogroup", true);
                            myIntent.putExtra("group_id", group.group.getGid());
                            getContext().startActivity(myIntent);
                        } else {
                            dbh.addXREF(group.group.getGid(), item.getItemId());
                            Intent myIntent = new Intent(getContext(), MainActivity.class);
                            getContext().startActivity(myIntent);
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        addBillsToView(convertView, group, dbh, position);
        nameTV.setText(group.group.getName());
        amountTV.setText("$" + group.group.getAmount().toString());
        return convertView;
    }

    public void addBillsToView(View convertView, GroupList group, DBH dbh, Integer position){
        // Get total bill
        Double minusAmount = 0.0;
        // Add list of bills
        LinearLayout billsLL = convertView.findViewById(R.id.bills);
        billsLL.removeAllViews();
        for(int i = 0; i < group.bills.size(); i++){
            // LinearLayout to hold the following
            LinearLayout billLL = new LinearLayout(getContext());
            billLL.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if(position % 2 == 0){
                if(i % 2 == 0){
                    billLL.setBackgroundColor(getContext().getResources().getColor(R.color.darkgrey));
                } else {
                    billLL.setBackgroundColor(getContext().getResources().getColor(R.color.lighterdarkgrey));
                }
            } else {
                if(i % 2 == 0){
                    billLL.setBackgroundColor(getContext().getResources().getColor(R.color.darkback));
                } else {
                    billLL.setBackgroundColor(getContext().getResources().getColor(R.color.lighterdarkback));
                }
            }
            billLL.setOrientation(LinearLayout.HORIZONTAL);

            // The name of the bill
            TextView billNameTV = new TextView(getContext());
            billNameTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            billNameTV.setTextSize(18f);
            billNameTV.setText(group.bills.get(i).getName());

            // Delete Image
            ImageView deleteIV = new ImageView(getContext());
            deleteIV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            deleteIV.setImageResource(R.drawable.ic_baseline_delete_forever_32);
            Integer bill_id = group.bills.get(i).getBid();
            deleteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbh.deleteXREF(group.group.getGid(), bill_id);
                    Intent myIntent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(myIntent);
                }
            });

            // The amount and due date of the bill
            TextView billAmountTV = new TextView(getContext());
            billAmountTV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            billAmountTV.setTextSize(14f);
            billAmountTV.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            billAmountTV.setText("$" + group.bills.get(i).getAmount().toString() + " Due: " +
                    group.bills.get(i).getDuedate());

            billLL.addView(billNameTV);
            billLL.addView(deleteIV);
            billLL.addView(billAmountTV);
            billsLL.addView(billLL);
            minusAmount += group.bills.get(i).getAmount();
        }

        TextView currAmount = convertView.findViewById(R.id.curramount);
        Double newAmount = group.group.getAmount() - minusAmount;
        currAmount.setText(newAmount.toString());
    }
}
