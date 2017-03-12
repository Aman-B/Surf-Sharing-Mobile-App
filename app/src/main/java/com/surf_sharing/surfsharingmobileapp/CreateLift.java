package com.surf_sharing.surfsharingmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.temp.Globals;

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
        globs = (Globals)getApplication();
        destination = (EditText) findViewById(R.id.editText);
        seats = (EditText) findViewById(R.id.editText2);
        //Ok = (Button) findViewById(R.id.button2);
        /*Ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Ok.setVisibility(View.INVISIBLE);
                dest = destination.getText().toString();
                seatsInt = Integer.parseInt(seats.getText().toString());
                globs.getLifts().addLift(new Lift(globs.testDriver, dest, seatsInt, 0));

                //Ok.setVisibility(View.VISIBLE);
            } });*/
    }

    public void CreateLifts( View v ){

        // create new lift
        //Ok.setVisibility(View.INVISIBLE);
        dest = destination.getText().toString();
        seatsInt = Integer.parseInt(seats.getText().toString());
        Toast myToast = Toast.makeText(getApplicationContext(), "create lifts now "+dest+" "+seatsInt, Toast.LENGTH_LONG);
        myToast.show();
        globs.getLifts().addLift(new Lift(globs.testDriver, dest, seatsInt, 0));

        // create lift using entered details and add it to List.

        //Ok.setVisibility(View.VISIBLE);
        Intent i = new Intent(CreateLift.this, ViewLifts.class);
        startActivity(i);
        return;
    }
}