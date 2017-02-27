package com.surf_sharing.surfsharingmobileapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class ViewLifts extends AppCompatActivity {

    ListView viewLifts;
    User currentUser, driver;
    Lift testLift;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lifts);

        currentUser = new User(12345, "Passenger", "sean@example.com");
        driver = new User(54321, "Driver", "drive@example.com");
        testLift = new Lift(driver, "Bundoran", 4, 1);
        viewLifts = (ListView) findViewById(R.id.liftList);
        String[] liftStrings = {"lift1", "lift2", "Lift3",
                                "lift4", "lift5", "Lift6",
                                "lift7", "lift8", "Lift9",
                                "lift10", "lift11", "Lift12",
                                "lift13", "lift14", "Lift15"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, liftStrings);
        viewLifts.setAdapter(adapter);

        // ListView Item Click Listener
        viewLifts.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String  itemValue = (String) viewLifts.getItemAtPosition(position);

                // Show Alert
               Toast.makeText(getApplicationContext(),
                        "Request made for lift "+itemValue, Toast.LENGTH_LONG)
                        .show();
                currentUser.requestLift(testLift);

            }

        });
    }
}
