package com.surf_sharing.surfsharingmobileapp.temp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.surf_sharing.surfsharingmobileapp.R;

import java.util.HashMap;
import java.util.Map;

import static com.surf_sharing.surfsharingmobileapp.utils.Display.popup;

/**
 * An example of storing data to the realtime database
 *
 * Issues:
 * If there is no network connection setValue does not complete, so onComplete is never called.
 */
public class DatabaseTestActivity extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        progressDialog = new ProgressDialog(this);

        Button submit = (Button) findViewById(R.id.database_test_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    popup(DatabaseTestActivity.this, "Please sign in to use this feature");
                } else {
                    // on screen spinner:
                    progressDialog.show();

                    // get user input:
                    EditText nameInput = (EditText) findViewById(R.id.database_test_submit_name);
                    EditText ageInput = (EditText) findViewById(R.id.database_test_submit_age);
                    String name = nameInput.getText().toString();
                    String age = ageInput.getText().toString();

                    // get ID of current user
                    String userId = currentUser.getUid();
                    // post data to database:
                    // location to write to
                    ref = database.getReference("user_test/" + userId);
                    // set (overwrite) the data at ref location
                    ref.setValue(new TestUser(name, age), new DatabaseReference.CompletionListener() {
                        @Override
                        // close spinner
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            progressDialog.dismiss();
                            if (databaseError == null) {
                                popup(DatabaseTestActivity.this, "Added!");
                            } else {
                                popup(DatabaseTestActivity.this, "Failed: " + databaseError.getDetails());
                            }
                        }
                    });
                }
            }
        });
    }

}
