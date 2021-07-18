package com.linuxduck.billz;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBH dbh = new DBH(this);
        setUpPage(dbh);
        setUpNavigation();
    }

    public void setUpPage(DBH dbh){
        // Set up the main list view
        GroupListAdapter adapter = new GroupListAdapter(this,
                dbh.getGroupLists());
        ListView listView = (ListView) findViewById(R.id.grouplistview);
        listView.setAdapter(adapter);
    }

    public void setUpNavigation() {
        // Add new group button
        ImageView addGroup = findViewById(R.id.addgroup);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AddGroupAcitivity.class);
                startActivity(myIntent);
            }
        });

        // Home button
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        // Look at bills button
        ImageView bills = findViewById(R.id.billsbutt);
        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, BillsActivity.class);
                startActivity(myIntent);
            }
        });
    }
}