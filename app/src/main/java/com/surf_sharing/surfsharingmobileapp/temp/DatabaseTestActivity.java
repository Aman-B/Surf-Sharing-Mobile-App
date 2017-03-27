package com.surf_sharing.surfsharingmobileapp.temp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

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

    public static TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        progressDialog = new ProgressDialog(this);

        text = (TextView) findViewById(R.id.textView3);

        Button submit = (Button) findViewById(R.id.database_test_submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText nameInput = (EditText) findViewById(R.id.database_test_submit_name);
                EditText ageInput = (EditText) findViewById(R.id.database_test_submit_age);
                String name = nameInput.getText().toString();
                String age = ageInput.getText().toString();

                //Database.setUserValue(name, age, "gender", "email", "type", "phone", "bio");

                //Toast.makeText(getApplicationContext(),x , Toast.LENGTH_LONG).show();

                /* FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null)
                {
                    popup(DatabaseTestActivity.this, "Please sign in to use this feature");
                }
                else
                {



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
                }*/



                //Database.postLift_(new Lift(new User(0, "type", "email"), "a", 5, 1));
                //Database.createUser_(new User(0, "type", "email"));
                //Database.getAllLifts_();

                //text.setText(Database.lifts);*/

                //Database.setUserValue(new User("", "type2", "email"));

                Lift lift = new Lift(new User("apMGnPrP8bXyIwztxjMcukxrEve2", "type", "email"), "a", 5, "1", "20:11", "22.11.2016");
                lift.id = "-KfLutnUEbm-OqT7JcNZ";
                //Database.addRefferencesInLiftAndUser(lift);
                //Database.postLift(lift);
                popup(DatabaseTestActivity.this, "test ");


            }
        });


        Button create = (Button) findViewById(R.id.button3);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //EditText ageInput = (EditText) findViewById(R.id.database_test_submit_age);
                //int id = Integer.parseInt(ageInput.getText().toString());

               //Database.postLift(new Lift(new User("77", "type", "email"), "a", 5, "" + 8));

                //Database.addLiftToUser(new Lift(new User("77", "type", "email"), "a", 5, "" + 8));

                /*User user = Database.getCurrentUser_();

                if (user != null)
                {
                    popup(DatabaseTestActivity.this, user.getUserType());
                }
                else
                {
                    popup(DatabaseTestActivity.this, "null");
                }*/
                //createNotification(getApplicationContext(), "testMessage", "test");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                User user = new User("0", "type", "email");
                user.id = currentUser.getUid();
                Lift lift = new Lift(new User("0", "type", "email"), "a", 5, "1", "20:11", "22.11.2016");
                lift.id = "-KfLutnUEbm-OqT7JcNZ";
                //Database.removeRefferencesInLiftAndUser(lift, user);
                popup(DatabaseTestActivity.this, "test remove");


            }
        });

    }

    public static void createNotification(Context context, String message, String messageText) {

        PendingIntent notificIntent = PendingIntent.getActivities(context, 0, new Intent[]{new Intent(context, AppCompatActivity.class)}, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(message);
        builder.setTicker("Alert Message");
        builder.setVibrate(new long[]{0, 200, 200, 200, 200});
        builder.setLights(Color.BLUE, 3000, 3000);
        builder.setContentText(messageText);
        builder.setContentIntent(notificIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }


}
