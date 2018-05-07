package com.surf_sharing.surfsharingmobileapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterDriverSecondActivity extends AppCompatActivity {

    private Button buttonOK;
    private EditText editTextLocation;
    private EditText editTextPhoneNumber;
    private TextView textDateOfBirth;
    private RadioGroup genderRadioGroup;
    private Button buttonDateOfBirth;
    private ProgressDialog progressDialog;
    private EditText editTextCarMakeModel;
    private EditText editTextDriverReg;
    private EditText editTextLicenceNo;
    private EditText editTextMaxPassengers;

    private boolean hasError = false;
    private Spinner genderSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver_second);
        progressDialog = new ProgressDialog(this);


        buttonOK = (Button) findViewById(R.id.ok_btn);
        buttonDateOfBirth = (Button) findViewById(R.id.driverDateOfBirthButton);

        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        editTextPhoneNumber =   (EditText) findViewById(R.id.edit_text_driver_phone);


        textDateOfBirth = (TextView) findViewById(R.id.driverDateOfBirth);
        genderSelectionSpinner = (Spinner) findViewById(R.id.gender_selection_spinner);

        ArrayList <String> genderTypes= new ArrayList<>();
        genderTypes.add("Male");
        genderTypes.add("Female");
        genderTypes.add("Prefer not to say.");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSelectionSpinner.setAdapter(dataAdapter);

        editTextCarMakeModel = (EditText) findViewById(R.id.edit_text_car_make_model);
        editTextDriverReg = (EditText) findViewById(R.id.edit_text_driver_registration);
       // editTextLicenceNo = (EditText) findViewById(R.id.edit_text_driver_licence);
//        editTextMaxPassengers = (EditText) findViewById(R.id.edit_text_max_passengers);


        editTextDriverReg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {

                    buttonOK.performClick();
                }
                return  false;
            }
        });

        buttonDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int dayOfMonth = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(RegisterDriverSecondActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textDateOfBirth.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    }

                }, year, month, dayOfMonth);
                mDatePicker.show();
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : calling submit details on click;

                progressDialog.show();
                submitDetailsToFirebase();

            }
        });
    }

    private void submitDetailsToFirebase() {
        boolean canSubmitDetails = true;

        String age = textDateOfBirth.getText().toString().trim();
        String phone = editTextPhoneNumber.getText().toString().trim();
        String loc = editTextLocation.getText().toString().trim();


        String gender = genderSelectionSpinner.getSelectedItem().toString();

        String carMakeModel = editTextCarMakeModel.getText().toString().trim();
       // String licenceNo = editTextLicenceNo.getText().toString().trim();
        String driverReg = editTextDriverReg.getText().toString().trim();
        //String maxPassengers = editTextMaxPassengers.getText().toString().trim();


        if(gender.isEmpty()) {
            Display.popup(RegisterDriverSecondActivity.this, "Please enter your Gender");
            canSubmitDetails = false;
        }
        else if(age.isEmpty()) {
            Display.popup(RegisterDriverSecondActivity.this, "Please enter your Date Of Birth");
            canSubmitDetails = false;
        }
        else if(phone.isEmpty()) {
            Display.popup(RegisterDriverSecondActivity.this, "Please enter your Phone Number");
            canSubmitDetails = false;
        }
        else if(loc.isEmpty()){
            Display.popup(RegisterDriverSecondActivity.this, "Please enter your address");
            canSubmitDetails = false;

        }



        if (carMakeModel.isEmpty()) {
            Display.popup(RegisterDriverSecondActivity.this, "Please enter your Car Make and Model");
            canSubmitDetails = false;
        }
        /*else if (licenceNo.isEmpty()) {
            Display.popup(RegisterDriverSecondActivity.this, "Please enter your Licence Number");
            canSubmitDetails = false;
        }*/
        else if (driverReg.isEmpty()) {
            Display.popup(RegisterDriverSecondActivity.this, "Please enter your Driver Registration");
            canSubmitDetails = false;
        }
        /*else if (maxPassengers.isEmpty())
        {
            Display.popup(RegisterDriverSecondActivity.this, "Please enter the Maximum number of passengers you can take");
            canSubmitDetails = false;
        }*/

//        else if(Integer.parseInt(getAge(age)) < 18){
//            Display.popup(canSubmitDetailsPassengerSecondActitivity.this, "Must be over 18 to canSubmitDetails");
//            canSubmitDetails = false;
//        }

        final AlertDialog alertDialog = new AlertDialog.Builder(RegisterDriverSecondActivity.this).create();
        alertDialog.setTitle("Profile Created!");
        alertDialog.setMessage("Your profile has been created.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gotoMainTabActivity();
                    }
                });


        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                if(!hasError)
                {
                    alertDialog.cancel();
                    gotoMainTabActivity();

                }
                else
                {
                    alertDialog.cancel();
                }
            }
        });

        if(canSubmitDetails == true) {
            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabaseReference.child("users").child(currentUserID).child("age").setValue(age);

            mDatabaseReference.child("users").child(currentUserID).child("gender").setValue(gender);
            mDatabaseReference.child("users").child(currentUserID).child("phone").setValue(phone);
            mDatabaseReference.child("users").child(currentUserID).child("car").setValue(carMakeModel);
            mDatabaseReference.child("users").child(currentUserID).child("driver registration").setValue(driverReg);
            mDatabaseReference.child("users").child(currentUserID).child("address").setValue(loc, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    if(databaseError==null)
                    {
                        alertDialog.show();
                    }
                    else
                    {
                        alertDialog.setTitle("Oops!");
                        alertDialog.setMessage("Some error occurred, please try again!" );
                        alertDialog.show();
                        hasError=true;

                    }
                }

            });
        }
        else
        {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }


    }
    private void gotoMainTabActivity() {
//        Intent intent = new Intent(RegisterActivity.this, NavDrawer.class);
        Intent intent = new Intent(RegisterDriverSecondActivity.this, TabActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
