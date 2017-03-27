package com.surf_sharing.surfsharingmobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.surf_sharing.surfsharingmobileapp.utils.Display;
import com.surf_sharing.surfsharingmobileapp.utils.FirebaseError;

import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private static final String TAG = LoginActivity.class.getSimpleName();

    // Firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressDialog progressDialog;
    private View mLoginFormView;
    private Button passengerAccButon,driverAccButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialise firebase app
        FirebaseApp.initializeApp(this);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        progressDialog = new ProgressDialog(this);

        passengerAccButon = (Button) findViewById(R.id.passangerAccButon);
        passengerAccButon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("ACCOUNT_TYPE", "passenger");
                startActivity(intent);
            }
        });
        driverAccButton = (Button) findViewById(R.id.driverAccButton);
        driverAccButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("ACCOUNT_TYPE", "driver");
                startActivity(intent);
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
                    Intent intent = new Intent(LoginActivity.this, NavDrawer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // user is logged out
                }
            }
        };
    }

    /**
     * Attempts to sign in the account specified by the login form.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.setMessage("Attempting to sign in");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                // user has signed in, go to NavDrawer
                                Display.popup(LoginActivity.this, "Signed in!");
                            }
                            else{
                                Display.popup(LoginActivity.this, "Failed to sign in!");
                                if(!task.isSuccessful()) {
                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                    switch (e.getErrorCode()) {
                                        case FirebaseError.INVALID_EMAIL:
                                            mEmailView.setError(getString(R.string.error_invalid_email));
                                            mEmailView.requestFocus();
                                            break;
                                        case FirebaseError.USER_NOT_FOUND:
                                            mEmailView.setError(getString(R.string.error_user_not_found));
                                            mEmailView.requestFocus();
                                            break;
                                        case FirebaseError.WRONG_PASSWORD:
                                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                                            mPasswordView.requestFocus();
                                            break;
                                        default:
                                            Display.popup(LoginActivity.this,e.getMessage());
                                    }
                                }
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}

