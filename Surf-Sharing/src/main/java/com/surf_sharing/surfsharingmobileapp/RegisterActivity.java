package com.surf_sharing.surfsharingmobileapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;

import android.text.TextUtils;
import android.text.InputType;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.data.Driver;
import com.surf_sharing.surfsharingmobileapp.utils.Display;
import com.surf_sharing.surfsharingmobileapp.utils.FirebaseError;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private EditText editTextCarMakeModel;
    private EditText editTextDriverReg;
    private EditText editTextLicenceNo;
    private EditText editTextMaxPassengers;
    private EditText editTextPassword;
    private EditText editTextPassword2;
    private EditText editTextLocation;
    private RadioGroup genderRadioGroup;
    private TextView textDateOfBirth;

    private String accountType;

    private Button buttonRegister, buttonDateOfBirth;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDlg;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String getAge(String dateOfBirth){

        String[] parts = dateOfBirth.split("/");

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accountType = getIntent().getStringExtra("ACCOUNT_TYPE");

        buttonDateOfBirth = (Button) findViewById(R.id.dateOfBirthButton);
        buttonRegister = (Button) findViewById(R.id.ok_btn);

        alertDlg = new AlertDialog.Builder(this);

        if (accountType.equals("passenger"))
        {
            setContentView(R.layout.activity_register_passenger);

            progressDialog = new ProgressDialog(this);

            buttonDateOfBirth = (Button) findViewById(R.id.dateOfBirthButton);
            buttonRegister = (Button) findViewById(R.id.ok_btn);

            editTextName = (EditText) findViewById(R.id.edit_text_name);
            editTextName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            editTextLocation = (EditText) findViewById(R.id.editTextLocation);
            editTextPhoneNumber =   (EditText) findViewById(R.id.edit_text_phone);
            editTextEmail = (EditText) findViewById(R.id.edit_text_email);
            editTextPassword = (EditText) findViewById(R.id.editText4);
            editTextPassword2 = (EditText) findViewById(R.id.editText5);
            textDateOfBirth = (TextView) findViewById(R.id.dateOfBirth);
            genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);

            buttonDateOfBirth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentDate = Calendar.getInstance();
                    int dayOfMonth = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                    int month = mcurrentDate.get(Calendar.MONTH);
                    int year = mcurrentDate.get(Calendar.YEAR);
                    DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            textDateOfBirth.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                        }

                    }, year, month, dayOfMonth);
                        mDatePicker.show();
                }
            });

            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //calling register method on click
                    registerUser();
                }
            });

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // user is logged in
                        // Send user to NavDrawer when
                        gotoNavDrawer();
                    } else {
                        // user is logged out
                    }
                }
            };

        }
        else
        {
            setContentView(R.layout.activity_register_driver);

            progressDialog = new ProgressDialog(this);

            buttonDateOfBirth = (Button) findViewById(R.id.driverDateOfBirthButton);
            buttonRegister = (Button) findViewById(R.id.ok_driver_btn);

            editTextName = (EditText) findViewById(R.id.edit_text_driver_name);
            editTextName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

            editTextPhoneNumber = (EditText) findViewById(R.id.edit_text_driver_phone);
            editTextEmail = (EditText) findViewById(R.id.edit_text_driver_email);
            editTextCarMakeModel = (EditText) findViewById(R.id.edit_text_car_make_model);
            editTextDriverReg = (EditText) findViewById(R.id.edit_text_driver_registration);
            editTextLicenceNo = (EditText) findViewById(R.id.edit_text_driver_licence);
            editTextMaxPassengers = (EditText) findViewById(R.id.edit_text_max_passengers);
            editTextPassword = (EditText) findViewById(R.id.edit_text_driver_password);
            editTextPassword2 = (EditText) findViewById(R.id.edit_text_driver_password_confirm);
            editTextLocation = (EditText) findViewById(R.id.editTextLocation);
            genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);

            textDateOfBirth = (TextView) findViewById(R.id.driverDateOfBirth);

            buttonDateOfBirth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentDate = Calendar.getInstance();
                    int dayOfMonth = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                    int month = mcurrentDate.get(Calendar.MONTH);
                    int year = mcurrentDate.get(Calendar.YEAR);
                    DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override

                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            textDateOfBirth.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                        }

                    }, year, month, dayOfMonth);
                    mDatePicker.show();
                }
            });

            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //calling register method on click
                    registerUser();
                }
            });

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // user is logged in
                        // Send user to NavDrawer when
                        gotoNavDrawer();
                    } else {
                        // user is logged out
                    }
                }
            };
        }


    }

    //check if a network connection is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private boolean hasConnectivity(){
        if (!isNetworkAvailable()) {

            alertDlg.setTitle("Something went wrong");
            alertDlg.setMessage("Could not retrieve information from database, please check your internet connection.");
            alertDlg.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    hasConnectivity();

                }
            });

            alertDlg.show();

            return false;
        }
        return true;
    }

    private void gotoNavDrawer() {
        Intent intent = new Intent(RegisterActivity.this, NavDrawer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String password2 = editTextPassword2.getText().toString().trim();
        boolean register = true;

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = editTextName.getText().toString().trim();
        String age = textDateOfBirth.getText().toString().trim();
        String phone = editTextPhoneNumber.getText().toString().trim();
        String loc = editTextLocation.getText().toString().trim();

        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton genderRadioButton = (RadioButton) findViewById(selectedId);
        String gender = genderRadioButton.getText().toString();

        if(name.isEmpty()) {
            Display.popup(RegisterActivity.this, "Please enter your Name");
            register = false;
        }
        else if(gender.isEmpty()) {
            Display.popup(RegisterActivity.this, "Please enter your Gender");
            register = false;
        }
        else if(age.isEmpty()) {
            Display.popup(RegisterActivity.this, "Please enter your Date Of Birth");
            register = false;
        }
        else if(phone.isEmpty()) {
            Display.popup(RegisterActivity.this, "Please enter your Phone Number");
            register = false;
        }
        else if(loc.isEmpty()){
            Display.popup(RegisterActivity.this, "Please enter your address");
            register = false;

        }
//        else if(Integer.parseInt(getAge(age)) < 18){
//            Display.popup(RegisterActivity.this, "Must be over 18 to register");
//            register = false;
//        }


        if (accountType.equals("driver"))
        {
            String carMakeModel = editTextCarMakeModel.getText().toString().trim();
            String licenceNo = editTextLicenceNo.getText().toString().trim();
            String driverReg = editTextDriverReg.getText().toString().trim();
            String maxPassengers = editTextMaxPassengers.getText().toString().trim();

            if (carMakeModel.isEmpty()) {
                Display.popup(RegisterActivity.this, "Please enter your Car Make and Model");
                register = false;
            }
            else if (licenceNo.isEmpty()) {
                Display.popup(RegisterActivity.this, "Please enter your Licence Number");
                register = false;
            }
            else if (driverReg.isEmpty()) {
                Display.popup(RegisterActivity.this, "Please enter your Driver Registration");
                register = false;
            }
            else if (maxPassengers.isEmpty())
            {
                Display.popup(RegisterActivity.this, "Please enter the Maximum number of passengers you can take");
                register = false;
            }
            else if(loc.isEmpty()){
                Display.popup(RegisterActivity.this, "Please enter your address");
                register = false;

            }

        }


        if(register == true) {
            if (password.equals(password2)) {

                if (hasConnectivity()) {

                    try {

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //user is registered
                                            //Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                            sendVerificationEmail();
                                            registerUserInfo();
                                            gotoNavDrawer();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                                            if (!task.isSuccessful()) {
                                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                                switch (e.getErrorCode()) {
                                                    case FirebaseError.INVALID_EMAIL:
                                                        editTextEmail.setError(getString(R.string.error_invalid_email));
                                                        editTextEmail.requestFocus();
                                                        break;
                                                    case FirebaseError.EMAIL_ALREADY_IN_USE:
                                                        editTextEmail.setError(getString(R.string.error_user_exists));
                                                        editTextEmail.requestFocus();
                                                        break;
                                                    case FirebaseError.WEAK_PASSWORD:
                                                        editTextPassword.setError(getString(R.string.error_invalid_password));
                                                        editTextPassword.requestFocus();
                                                        break;
                                                    default:
                                                        Display.popup(RegisterActivity.this, e.getMessage());
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                    catch (Exception ex)
                    {
                        alertDlg.setTitle("Something went wrong");
                        alertDlg.setMessage("Could not retrieve information from database, please check your internet connection.");
                        alertDlg.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                hasConnectivity();

                            }
                        });

                        alertDlg.show();
                    }
                }
            } else
                Display.popup(RegisterActivity.this, "Passwords do not match");
        }

    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "SignUp Succssful", Toast.LENGTH_SHORT).show();
                        }
                    }
            });

        }
    }
    public void registerUserInfo(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            String userId = currentUser.getUid();
            String type = accountType;
            String name = editTextName.getText().toString().trim();
            String age = textDateOfBirth.getText().toString().trim();
            String phone = editTextPhoneNumber.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String loc = editTextLocation.getText().toString().trim();

            Log.i("user adr", loc);

            int selectedId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton genderRadioButton = (RadioButton) findViewById(selectedId);
            String gender = genderRadioButton.getText().toString();

            //add the user's info into firebase
            if (type.equals("driver")) {
                type = "pending";
                String carMakeModel = editTextCarMakeModel.getText().toString().trim();
                String licenceNo = editTextLicenceNo.getText().toString().trim();
                String driverReg = editTextDriverReg.getText().toString().trim();
                int maxPassengers = Integer.parseInt(editTextMaxPassengers.getText().toString().trim());
                Driver driver = new Driver(userId, type, email, carMakeModel, licenceNo, driverReg, maxPassengers);

                driver.setGender(gender);
                driver.setAge(age);
                driver.setName(name);
                driver.setPhone(phone);
                driver.setAddress(loc);
                Database.setDriverValue(driver);

            } else {
                User user = new User(userId, type, email);
                user.setGender(gender);
                user.setAge(age);
                user.setName(name);
                user.setPhone(phone);
                user.setAddress(loc);
                Database.setUserValue(user);
            }
        }

    }
}
