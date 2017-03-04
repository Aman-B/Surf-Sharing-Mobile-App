package com.surf_sharing.surfsharingmobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    //Button viewButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void ViewLifts(View v){
        Toast myToast = Toast.makeText(getApplicationContext(), "viewing lifts now", Toast.LENGTH_LONG);
        myToast.show();
        Intent i = new Intent(MainActivity.this, ViewLifts.class);
        startActivity(i);
        return;
    }

    public void CreateLifts( View v ){
        Toast myToast = Toast.makeText(getApplicationContext(), "create lifts now", Toast.LENGTH_LONG);
        myToast.show();
        Intent i = new Intent(MainActivity.this, CreateLift.class);
        startActivity(i);
        return;
    }
}
