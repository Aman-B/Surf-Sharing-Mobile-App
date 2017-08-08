package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.utils.Display;
import com.surf_sharing.surfsharingmobileapp.utils.FirebaseError;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestLift extends Fragment {

    //EditText dest;
    //EditText seats;
    //Database database;
    Lift requestedLift;
    String driverId, driverName, idText, userId, dateStr, timeStr, liftStr, seatsStr;
    int seatsVal;
    boolean alreadyRequested;

    private DatabaseReference liftRoot;
    private ValueEventListener liftListener;
    private TextView locationText, timeText, dateText, driverNameText, seatsText, driverIDText;
    private String SENDER_ID = "561530043428";

    public RequestLift() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OfferLift.
     */
    public static RequestLift newInstance() {
        RequestLift fragment = new RequestLift();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // handle bundle arguments

            // take in lift id
            idText=getArguments().getString("id");
            driverId=getArguments().getString("driverId");
            driverName = getArguments().getString("driverName");
            dateStr=getArguments().getString("date");
            timeStr=getArguments().getString("time");
            liftStr=getArguments().getString("liftStr");
            seatsStr=getArguments().getString("seatsStr");
            alreadyRequested = false;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_request_lift);


        /*Database.root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    userId = currentUser.getUid();

                    Log.d("RequestLift Activity", userId);

                    DataSnapshot liftRef = snapshot.child("lifts").child(idText);

                    DataSnapshot passengersRef = liftRef.child("passengers");

                    for (DataSnapshot passengerSnapshot : passengersRef.getChildren()) {

                        String passengerId = passengerSnapshot.getKey();
                        Log.d("RequestLift Activity", passengerId);
                        String passengerState = (String) passengerSnapshot.getValue();

                        if (passengerId.equals(userId) && (passengerState.equals("pending") || passengerState.equals("accepted"))) {

                            Log.d("RequestLift Activity", "Reached here!");
                            alreadyRequested = true;

                        }
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });*/

        if (alreadyRequested) {

                Log.d("RequestLift Activity", "We even got here somehow!");

                View view = inflater.inflate(R.layout.fragment_already_requested, container, false);

                locationText = (TextView) view.findViewById(R.id.requestLiftLocation);
                timeText = (TextView) view.findViewById(R.id.requestLiftTime);
                dateText = (TextView) view.findViewById(R.id.requestLiftDate);
                seatsText = (TextView) view.findViewById(R.id.requestLiftSeats);

                // set the driver and lift info
                locationText.setText(liftStr);
                timeText.setText(timeStr);
                dateText.setText(dateStr);
                seatsText.setText(String.format("(%s seats remaining)", seatsStr));

                return view;

        } else {

                Log.d("RequestLift Activity", "But this was the one that was executed...");
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_request_lift, container, false);

                locationText = (TextView) view.findViewById(R.id.requestLiftLocation);
                timeText = (TextView) view.findViewById(R.id.requestLiftTime);
                dateText = (TextView) view.findViewById(R.id.requestLiftDate);
                driverNameText = (TextView) view.findViewById(R.id.requestLiftDriverName);
                seatsText = (TextView) view.findViewById(R.id.requestLiftSeats);

                // set the driver and lift info
                locationText.setText(liftStr);
                timeText.setText(timeStr);
                dateText.setText(dateStr);
                driverNameText.setText(driverName);
                seatsText.setText(String.format("(%s seats remaining)", seatsStr));

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                userId = currentUser.getUid();
                userId = currentUser.toString();
                userId = currentUser.getEmail();

                Button viewProfileButton = (Button) view.findViewById(R.id.viewProfileButton);

                viewProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //go to driver's profile screen
                        NavDrawer nd = (NavDrawer) getActivity();
                        nd.replaceContent(ProfileScreen.newInstance(driverId));

                    }
                });


                Button reqButton = (Button) view.findViewById(R.id.requestButton);

                reqButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Toast.makeText(getContext(), "request Lift: " + idText, Toast.LENGTH_SHORT).show();
                        Display.popup(getActivity(), "Seat on lift requested!");
                        Database.makeLiftRequest(idText);

                        //-------------------------------------------------------------------------------------
                        //Database.acceptLiftRequest(idText, "apMGnPrP8bXyIwztxjMcukxrEve2");
                        //Database.rejectLiftRequest(idText, "apMGnPrP8bXyIwztxjMcukxrEve2");

                        //Database.findUser("apMGnPrP8bXyIwztxjMcukxrEve2");
                        //-------------------------------------------------------------------------------------

                        // request the lift
                        String messageTitle = "New Lift Request";
                        String message = "You have received a new seat request for your Lift to: " + liftStr;
                        String actionType = "com.surfsharing.MESSAGE";

                        FirebaseMessaging fm = FirebaseMessaging.getInstance();
                        AtomicInteger msgId = new AtomicInteger();
                        fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                                .setMessageId(Integer.toString(msgId.incrementAndGet()))
                                .addData("message_title", messageTitle)
                                .addData("message", message)
                                .addData("action", actionType)
                                .addData("lift", liftStr)
                                .addData("recipient", driverId)
                                .addData("sender", userId)
                                .build());



                        NavDrawer nd = (NavDrawer) getActivity();
                        nd.replaceContent(AvailableLifts.newInstance());

                    }
                });


            return view;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
