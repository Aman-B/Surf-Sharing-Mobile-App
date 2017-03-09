package com.surf_sharing.surfsharingmobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);



        buttonRegister.setOnClickListener(this);



    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //user is registered
                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Failed to Register", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });
    }
    @Override
    public void onClick(View view) {
        //calling register method on click
        registerUser();
    }
}