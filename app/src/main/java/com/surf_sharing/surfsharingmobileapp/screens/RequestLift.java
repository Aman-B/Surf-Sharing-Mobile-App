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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;

import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;


public class RequestLift extends Fragment {

    //EditText dest;
    //EditText seats;
    //Database database;
    Lift requestedLift;
    String destText, idText;
    int seatsVal;
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
            destText=getArguments().getString("destination");
            seatsVal=getArguments().getInt("seatsAvailable");
            idText=getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_request_lift);
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_request_lift, container, false);

        TextView infoText = (TextView) view.findViewById(R.id.lift_info);
        infoText.setText("Destination: "+destText+
                            "\nSeats Available: "+seatsVal+
                            "\nDriver: "+"driverText"+
                            "\nLift id: "+idText);


        Button reqButton = (Button) view.findViewById(R.id.requestButton);

        reqButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NavDrawer nd = (NavDrawer) getActivity();
                nd.replaceContent(ManageAccount.newInstance());

                // request the lift
                String messageTitle = "New Lift Request";
                String messageDetail = "Request for seat on Lift: _ from User: _";
                sendNotification(getContext(), messageTitle, messageDetail);
                
            }
        });


        return view;
    }

    public static void sendNotification(Context context, String message, String messageText) {

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

    public boolean constructLift(String id, String destination, int seatsAvailable, User driver){
        requestedLift = new Lift(driver, destination, seatsAvailable, id);
        return true;
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
