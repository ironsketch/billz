package com.linuxduck.billz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BillsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        DBH dbh = new DBH(this);
        setUpPage(dbh);
        setUpNavigation();
    }

    public void setUpPage(DBH dbh){
        // Set up the bill list view
        List<Bill> billsList = dbh.getNonDeletedBills();
        ArrayList<Bill> bills = new ArrayList<>();
        for(int i = 0; i < billsList.size(); i++){
            bills.add(billsList.get(i));
            Log.e("deleted", String.valueOf(billsList.get(i).getDeleted()));
        }
        BillListAdapter adapter = new BillListAdapter(this,
                bills);
        ListView listView = (ListView) findViewById(R.id.billlistview);
        listView.setAdapter(adapter);

        // Set up add new bill button
        ImageView addbill = findViewById(R.id.addbill);
        addbill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(BillsActivity.this, AddBillActivity.class);
                myIntent.putExtra("addtogroup", false);
                myIntent.putExtra("group_id", -1);
                startActivity(myIntent);
            }
        });
    }

    public void setUpNavigation() {
        // Home button
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(BillsActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        // Look at bills button
        ImageView bills = findViewById(R.id.billsbutt);
        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(BillsActivity.this, BillsActivity.class);
                startActivity(myIntent);
            }
        });
    }
}