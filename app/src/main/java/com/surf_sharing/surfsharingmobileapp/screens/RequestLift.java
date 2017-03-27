package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.utils.FirebaseError;

import java.util.ArrayList;


public class RequestLift extends Fragment {

    //EditText dest;
    //EditText seats;
    //Database database;
    Lift requestedLift;
    String driverId, idText, userId, dateStr, timeStr, liftStr, seatsStr;
    int seatsVal;

    private DatabaseReference liftRoot;
    private ValueEventListener liftListener;

    private TextView locationText, timeText, dateText, driverText, seatsText;

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
            dateStr=getArguments().getString("date");
            timeStr=getArguments().getString("time");
            liftStr=getArguments().getString("liftStr");
            seatsStr=getArguments().getString("seatsStr");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_request_lift);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_lift, container, false);

        locationText = (TextView) view.findViewById(R.id.requestLiftLocation);
        timeText = (TextView) view.findViewById(R.id.requestLiftTime);
        dateText = (TextView) view.findViewById(R.id.requestLiftDate);
        driverText = (TextView) view.findViewById(R.id.requestLiftDriver);
        seatsText = (TextView) view.findViewById(R.id.requestLiftSeats);

        // set the driver and lift info
        locationText.setText(liftStr);
        timeText.setText(timeStr);
        dateText.setText(dateStr);
        driverText.setText(driverId);
        seatsText.setText(String.format("(%s seats remaining)", seatsStr));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        userId = currentUser.toString();
        userId = currentUser.getEmail();

        Button reqButton = (Button) view.findViewById(R.id.requestButton);

        reqButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "request Lift: " + idText, Toast.LENGTH_SHORT).show();
                Database.makeLiftRequest(idText);

                //-------------------------------------------------------------------------------------
                //Database.acceptLiftRequest(idText, "apMGnPrP8bXyIwztxjMcukxrEve2");
                //Database.rejectLiftRequest(idText, "apMGnPrP8bXyIwztxjMcukxrEve2");

                //Database.findUser("apMGnPrP8bXyIwztxjMcukxrEve2");
                //-------------------------------------------------------------------------------------

                // request the lift
                String messageTitle = "New Lift Request";
                String messageDetail = "Request for seat on Lift: "+liftStr+" from User: "+userId;
                sendNotification(getContext(), messageTitle, messageDetail);

                NavDrawer nd = (NavDrawer) getActivity();
                nd.replaceContent(AvailableLifts.newInstance());

            }
        });


        return view;
    }

    public static void sendNotification(Context context, String message, String messageText) {

        PendingIntent notificationIntent = PendingIntent.getActivities(context, 0, new Intent[]{new Intent(context, AppCompatActivity.class)}, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(message);
        builder.setTicker("Alert Message");
        builder.setVibrate(new long[]{0, 200, 200, 200, 200});
        builder.setLights(Color.BLUE, 3000, 3000);
        builder.setContentText(messageText);
        builder.setContentIntent(notificationIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
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
