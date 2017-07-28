package com.surf_sharing.surfsharingmobileapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.LiftContainer;
import com.surf_sharing.surfsharingmobileapp.data.User;

public class ViewLifts extends AppCompatActivity {

    ListView viewLifts;
    User currentUser, driver;
    Lift testLift;
    LiftContainer testCont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lifts);

        viewLifts = (ListView) findViewById(R.id.liftList);

        //DatabaseReference usersRef = Database.root.child("lifts");
        Database.liftRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                testCont = new LiftContainer();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    try
                    {
                        String id = postSnapshot.getKey();
                        int seatsAvailable = Integer.parseInt((String) postSnapshot.child("seatsAvailable").getValue());
                        String car = (String) postSnapshot.child("car").getValue();
                        String destination = (String) postSnapshot.child("destination").getValue();
                        String date = (String) postSnapshot.child("date").getValue();
                        String time = (String) postSnapshot.child("time").getValue();

                        DataSnapshot driverRef = postSnapshot.child("driverId");
                        String driverId = (String) driverRef.child("id").getValue();
                        String driverName = (String) driverRef.child("name").getValue();
                        String driverAge = (String) driverRef.child("age").getValue();
                        String driverGender = (String) driverRef.child("gender").getValue();
                        String driverEmail = (String) driverRef.child("email").getValue();
                        String driverType = (String) driverRef.child("type").getValue();
                        String driverPhone = (String) driverRef.child("phone").getValue();
                        String driverBio = (String) driverRef.child("bio").getValue();

                        User driver = new User(driverId, driverType, driverEmail);
                        driver.name = driverName;
                        driver.age = driverAge;
                        driver.gender = driverGender;
                        driver.phone = driverPhone;
                        driver.bio = driverBio;

                        Lift lift = new Lift(driver, destination, seatsAvailable, id, time, date);
                        lift.car = car;
                        //lift.date = date;
                        //lift.time = time;

                        testCont.addLift(lift);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                String[] liftStrings = testCont.getLiftsAsStrings();
                setListView(liftStrings);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String[] empty = new String[0];
        setListView(empty);

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

    public void setListView(String[] liftStrings) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, liftStrings);
        viewLifts.setAdapter(adapter);
    }
}
