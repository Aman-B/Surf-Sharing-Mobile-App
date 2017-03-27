package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.media.Image;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

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

    ListView requestingUsers;
    ArrayList<User> requesting_users_list;

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

            requesting_users_list = new ArrayList<User>();

            userId = getArguments().getString("userId");
            liftId = getArguments().getString("liftId");

            nd = (NavDrawer) getActivity();

            //requestingUser = Database.getUser(userId);
            //requestedLift = Database.getLift(liftId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_response, container, false);

        /*TextView liftDestinationText = (TextView) view.findViewById(R.id.text_view_lift_name);
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
        userNameText.setText(userName);*/

        requestingUsers = (ListView) view.findViewById(R.id.passengerRequestList);
        //requesting_users_list = Database.getRequestingUsers();

        final ArrayAdapter<User> arrayAdapter = new ArrayAdapter<User>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                requesting_users_list);

        Database.root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try
                {Toast.makeText(getContext(), "liftId: " + liftId, Toast.LENGTH_SHORT).show();
                    DataSnapshot liftRef = snapshot.child("lifts").child(liftId);
                    DataSnapshot passengersRef = liftRef.child("passengers");



                    for (DataSnapshot passengersSnapshot : passengersRef.getChildren()) {

                        String passengerId = passengersSnapshot.getKey();
                        String passengerState = (String) passengersSnapshot.getValue();



                        if (passengerState.equals("pending"))
                        {
                            DataSnapshot userRef = snapshot.child("users").child(passengerId);
                            String passengerName = (String) userRef.child("name").getValue();
                            String passengerAge = (String) userRef.child("age").getValue();
                            String passengerGender = (String) userRef.child("gender").getValue();
                            String passengerEmail = (String) userRef.child("email").getValue();
                            String passengerType = (String) userRef.child("type").getValue();
                            String passengerPhone = (String) userRef.child("phone").getValue();
                            String passengerBio = (String) userRef.child("bio").getValue();

                            User passenger = new User(passengerId, passengerType, passengerEmail);
                            passenger.name = passengerName;
                            passenger.age = passengerAge;
                            passenger.gender = passengerGender;
                            passenger.phone = passengerPhone;
                            passenger.bio = passengerBio;

                            requesting_users_list.add(passenger);
                        }
                        else
                        { }
                    }

                    requestingUsers.setAdapter(arrayAdapter);
                }
                catch (Exception e)
                {

                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        });


        requestingUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Intent myIntent = new Intent(getActivity(), NextActivity.class);
                //startActivity(myIntent);

                final User requestingPassenger = (User) parent.getAdapter().getItem(position);


                new AlertDialog.Builder(getContext())
                        .setTitle("Accept Passenger")
                        .setMessage("Do you want to accept " + requestingPassenger.getUserName() + " as a Passenger?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                notifyUser(requestingPassenger, true);

                                //requestedLift.addPassenger(requestingPassenger.getUserId());
                                // TODO undo comment below
                                //requestingPassenger.addLift(requestedLift);

                                //Database.setUserValue(requestingPassenger);
                                //Database.setLiftValue(requestedLift); // Database function not implemented yet


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                notifyUser(requestingPassenger, false);

                            }
                        })


                        //.setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



            }
        });



        /*Button viewProfileButton = (Button) view.findViewById(R.id.view_full_profile_btn);
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
        });*/

        return view;
    }

    public void notifyUser(User requestingUser, boolean accepted) {

        if (accepted)
        {
                // Wherever the user has an entry representing their pending lift
                // request, change it to show that they are now on this lift and
                // send a notification to inform them.
                //Database.acceptLiftRequest(liftId, userId);

                // TODO send notification message to user informing them
                // that their request was accepted

        }
        else
        {
                // Wherever the user has an entry representing their pending lift
                // request, remove it to show that they have not secured a seat
                // on this lift and send a notification to inform them.
                //Database.rejectLiftRequest(liftId, userId);

                // TODO send notification message to user informing them
                // that their request was rejected.
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
