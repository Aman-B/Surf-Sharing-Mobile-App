package com.surf_sharing.surfsharingmobileapp.temp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;

public class TestRequestLiftActivity extends AppCompatActivity {

    Lift requestedLift;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_request_lift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public boolean constructLift(String id, String destination, int seatsAvailable, User driver){
        requestedLift = new Lift(driver, destination, seatsAvailable, id);
        return true;
    }

}
