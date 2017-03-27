package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.media.Image;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.LiftsYouAreOn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * Use the {@link RequestResponse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestResponse extends Fragment {

    String userId;
    String liftId;

    User requestingUser;
    Lift requestedLift;

    NavDrawer nd;

    public RequestResponse() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestResponse.
     */
    public static RequestResponse newInstance() {
        RequestResponse fragment = new RequestResponse();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            liftId = getArguments().getString("liftId");

            nd = (NavDrawer) getActivity();

            final DatabaseReference usersRef = Database.userRoot.child(userId);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    try
                    {
                        DataSnapshot lifts = snapshot.child("passenger");
                        for (DataSnapshot liftSnapshot : lifts.getChildren()) {
                            String  liftId = (String) liftSnapshot.getKey();
                            String state = (String) liftSnapshot.getValue();

                            if (state.equals("Driver"))
                            {

                            }
                        }
                    }
                    catch (Throwable e)
                    { }
                }
                @Override public void onCancelled(DatabaseError error) { }
            });

            final DatabaseReference liftRef = Database.liftRoot.child(liftId);
            liftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    try
                    {
                        int seatsAvailable = Integer.parseInt((String) snapshot.child("seatsAvailable").getValue());
                        String car = (String) snapshot.child("car").getValue();
                        String destination = (String) snapshot.child("destination").getValue();
                        String date = (String) snapshot.child("date").getValue();
                        String time = (String) snapshot.child("time").getValue();

                        DataSnapshot driverRef = snapshot.child("driver");
                        String driverId = (String) driverRef.child("id").getValue();
                        String driverName = (String) driverRef.child("name").getValue();
                        String driverAge = (String) driverRef.child("age").getValue();
                        String driverGender = (String) driverRef.child("gender").getValue();
                        String driverEmail = (String) driverRef.child("email").getValue();
                        String driverType = (String) driverRef.child("type").getValue();
                        String driverPhone = (String) driverRef.child("phone").getValue();
                        String driverBio = (String) driverRef.child("bio").getValue();

                        User driver = new User(driverId, driverType, driverEmail);
                        driver.name = driverName;
                        driver.age = driverAge;
                        driver.gender = driverGender;
                        driver.phone = driverPhone;
                        driver.bio = driverBio;

                        Lift lift = new Lift(driver, destination, seatsAvailable, liftId, time, date);
                        lift.car = car;

                        DataSnapshot passengersRef = snapshot.child("passengers");
                        /*lift.passengers = new ArrayList<User>();
                        for (DataSnapshot PassengerSnapshot : passengersRef.getChildren()) {

                            String passengerId = (String) PassengerSnapshot.getKey();

                            String passengerName = (String) PassengerSnapshot.child("name").getValue();
                            String passengerAge = (String) PassengerSnapshot.child("age").getValue();
                            String passengerGender = (String) PassengerSnapshot.child("gender").getValue();
                            String passengerEmail = (String) PassengerSnapshot.child("email").getValue();
                            String passengerType = (String) PassengerSnapshot.child("type").getValue();
                            String passengerPhone = (String) PassengerSnapshot.child("phone").getValue();
                            String passengerBio = (String) PassengerSnapshot.child("bio").getValue();
                            String passengerState = (String) PassengerSnapshot.child("state").getValue();

                            User passenger = new User(passengerId, passengerType, passengerEmail);
                            passenger.name = passengerName;
                            passenger.age = passengerAge;
                            passenger.gender = passengerGender;
                            passenger.phone = passengerPhone;
                            passenger.bio = passengerBio;

                            if( passengerState.equals("accepted"))
                            {
                                lift.passengers.add(passenger);
                            }
                            else
                            {
                                lift.pendingPassengers.add(passenger);

                            }
                        }*/
                    }
                    catch (Throwable e)
                    { }
                }
                @Override public void onCancelled(DatabaseError error) { }
            });

            //requestingUser = Database.getUser(userId);
            //requestedLift = Database.getLift(liftId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_response, container, false);

        TextView liftDestinationText = (TextView) view.findViewById(R.id.text_view_lift_name);
        String liftDestination = requestedLift.getDestination();
        liftDestinationText.setText(liftDestination);

        TextView liftDepartureDateText = (TextView) view.findViewById(R.id.text_view_lift_date);
        String liftDepartureDate = requestedLift.getDate();
        liftDepartureDateText.setText(liftDepartureDate);

        TextView liftDepartureTimeText = (TextView) view.findViewById(R.id.text_view_lift_time);
        String liftDepartureTime = requestedLift.getTime();
        liftDepartureTimeText.setText(liftDepartureTime);

        //ImageView userProfileImage = (ImageView) view.findViewById(R.id.user_profile_image);
        //Image userImage = requestingUser.getUserImage();

        TextView userNameText = (TextView) view.findViewById(R.id.text_view_user);
        String userName = requestingUser.getUserName();
        userNameText.setText(userName);


        Button viewProfileButton = (Button) view.findViewById(R.id.view_full_profile_btn);
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nd = (NavDrawer) getActivity();
                Fragment userProfileScreen = ProfileScreen.newInstance(requestingUser.getUserId());
                nd.replaceContent(userProfileScreen);

            }
        });

        Button rejectButton = (Button) view.findViewById(R.id.reject_btn);
        rejectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                notifyUser(requestingUser, false);

                nd = (NavDrawer) getActivity();
                Fragment liftsYouAreOfferingScreen = LiftsYouAreOffering.newInstance();

                nd.replaceContent(liftsYouAreOfferingScreen);

            }
        });


        Button acceptButton = (Button) view.findViewById(R.id.accept_btn);
        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                notifyUser(requestingUser, true);

                requestedLift.addPassenger(requestingUser.id);
                // TODO undo comment below
                //requestingUser.addLift(requestedLift);

                Database.setUserValue(requestingUser);
                //Database.setLiftValue(requestedLift); // Database function not implemented yet

            }
        });

        return view;
    }

    public void notifyUser(User requestingUser, boolean accepted) {

        if (accepted)
        {
                // Wherever the user has an entry representing their pending lift
                // request, change it to show that they are now on this lift and
                // send a notification to inform them.
                Database.acceptLiftRequest(liftId, userId);

        }
        else
        {
                // Wherever the user has an entry representing their pending lift
                // request, remove it to show that they have not secured a seat
                // on this lift and send a notification to inform them.
                Database.rejectLiftRequest(liftId, userId);

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
