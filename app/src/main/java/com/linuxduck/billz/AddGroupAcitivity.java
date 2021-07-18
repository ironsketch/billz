package com.linuxduck.billz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddGroupAcitivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        DBH dbh = new DBH(this);
        setUpPage(dbh);
        setUpNavigation();
    }

    public void setUpPage(DBH dbh) {
        // Set up Name Box
        EditText nameET = findViewById(R.id.name);

        // Set up Amount Box
        EditText amountET = findViewById(R.id.amount);

        // Set up submit button
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameS = nameET.getText().toString();
                String amountS = amountET.getText().toString();
                if(nameS.length() > 0 && amountS.length() > 0) {
                    Double amount = Double.parseDouble(amountS);
                    Group group = new Group(-1, nameS, amount);
                    dbh.addGroup(group);
                    Intent myIntent = new Intent(AddGroupAcitivity.this, MainActivity.class);
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
                Intent myIntent = new Intent(AddGroupAcitivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        // Look at bills button
        ImageView bills = findViewById(R.id.billsbutt);
        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AddGroupAcitivity.this, BillsActivity.class);
                startActivity(myIntent);
            }
        });
    }
}