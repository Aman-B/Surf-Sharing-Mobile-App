package com.surf_sharing.surfsharingmobileapp;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputType;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.utils.Display;
import com.surf_sharing.surfsharingmobileapp.utils.FirebaseError;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.surf_sharing.surfsharingmobileapp.R.id.email;
import static com.surf_sharing.surfsharingmobileapp.data.Database.userRoot;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextGender;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPassword2;

    private TextView textDateOfBirth;

    private String accountType;

    private Button buttonRegister, buttonDateOfBirth;
    private ProgressDialog progressDialog;

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


        progressDialog = new ProgressDialog(getApplicationContext());

        buttonDateOfBirth = (Button) findViewById(R.id.dateOfBirthButton);
        buttonRegister = (Button) findViewById(R.id.ok_btn);

        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        editTextGender = (EditText) findViewById(R.id.edit_text_gender);
        editTextPhoneNumber = (EditText) findViewById(R.id.edit_text_phone);
        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.editText4);
        editTextPassword2 = (EditText) findViewById(R.id.editText5);

        textDateOfBirth = (TextView) findViewById(R.id.dateOfBirth);

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
        String gender = editTextGender.getText().toString().trim();
        String age = textDateOfBirth.getText().toString().trim();
        String phone = editTextPhoneNumber.getText().toString().trim();




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
//        else if(Integer.parseInt(getAge(age)) < 18){
//            Display.popup(RegisterActivity.this, "Must be over 18 to register");
//            register = false;
//        }




        if(register == true) {
            if (password.equals(password2)) {

                progressDialog.setMessage("Registering User...");
                progressDialog.show();

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
                                progressDialog.dismiss();
                            }
                        });
            } else
                Display.popup(RegisterActivity.this, "Passwords do not match");
        }

    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "SignUp Succssful, Email Verification Sent", Toast.LENGTH_SHORT).show();
                        }
                    }
        });

    }
}
    public void registerUserInfo(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User newUserValue = null;

        if(currentUser != null)
        {
            String userId = currentUser.getUid();
            String type = accountType;
            String name = editTextName.getText().toString().trim();
            String gender = editTextGender.getText().toString().trim();
            String age = textDateOfBirth.getText().toString().trim();
            String phone = editTextPhoneNumber.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();

            if(type.equals("driver")){
                type = "pending";
            }

            User user = new User(userId, type, email);
            user.gender = gender;
            user.age = age;
            user.name = name;
            user.phone = phone;
            user.type = type;

            Database.setUserValue(user);
        }



    }
}
