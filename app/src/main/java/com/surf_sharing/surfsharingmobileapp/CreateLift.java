package com.surf_sharing.surfsharingmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateLift extends AppCompatActivity {

    Globals globs;
    EditText destination, seats;
    Button Ok;
    String dest;
    int seatsInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lift2);
        /*globs = (Globals)getApplication();
        destination = (EditText) findViewById(R.id.editText);
        seats = (EditText) findViewById(R.id.editText2);
        Ok = (Button) findViewById(R.id.button2);
        Ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Ok.setVisibility(View.INVISIBLE);
                dest = destination.getText().toString();
                seatsInt = Integer.parseInt(seats.getText().toString());
                globs.getLifts().addLift(new Lift(globs.testDriver, dest, seatsInt, 0));

                Ok.setVisibility(View.VISIBLE);
            } });*/
    }

    public void CreateLifts( View v ){
        Toast myToast = Toast.makeText(getApplicationContext(), "create lifts now", Toast.LENGTH_LONG);
        myToast.show();
        // create new lift

        Intent i = new Intent(CreateLift.this, ViewLifts.class);
        startActivity(i);
        return;
    }
}