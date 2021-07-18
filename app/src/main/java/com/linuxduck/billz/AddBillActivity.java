package com.linuxduck.billz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddBillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        DBH dbh = new DBH(this);
        setUpPage(dbh);
        setUpNavigation();
    }

    public void setUpPage(DBH dbh) {
        SimpleDateFormat sdp = new SimpleDateFormat("yyyy.MM.dd");
        // Get intent
        Intent intent = getIntent();
        boolean addtogroup = intent.getBooleanExtra("addtogroup", false);
        Integer group_id = intent.getIntExtra("group_id", -1);

        // Set up Name Box
        EditText nameET = findViewById(R.id.name);
        // Set up Amount Box
        EditText amountET = findViewById(R.id.amount);
        // Set up Date Picker
        DatePicker dp = findViewById(R.id.datep);
        // Set up submit button
        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameS = nameET.getText().toString();
                String amountS = amountET.getText().toString();
                Integer month = dp.getMonth() + 1;
                String date = dp.getYear() + "." + month + "." + dp.getDayOfMonth();

                if(nameS.length() > 0 && amountS.length() > 0){
                    Double amount = Double.parseDouble(amountS);
                    Bill bill = new Bill(-1, nameS, amount, false, date);
                    Long newRow = dbh.addBill(bill);
                    if(addtogroup){
                        dbh.addXREF(group_id, newRow.intValue());
                    }
                    Intent myIntent = new Intent(AddBillActivity.this, MainActivity.class);
                    startActivity(myIntent);
                }
            }
        });
    }

    public void setUpNavigation() {
        // Home button
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AddBillActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        // Look at bills button
        ImageView bills = findViewById(R.id.billsbutt);
        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AddBillActivity.this, BillsActivity.class);
                startActivity(myIntent);
            }
        });
    }
}