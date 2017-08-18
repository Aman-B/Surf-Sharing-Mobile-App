package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.RequestItemAdapter;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.screens.LiftsYouAreOn;
import com.surf_sharing.surfsharingmobileapp.utils.CustomRequestPassengersAdapter;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.surf_sharing.surfsharingmobileapp.data.Database.root;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * Use the {@link RequestResponse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestResponse extends Fragment {

    ListView requestingUsersListView;
    ArrayList<User> requesting_users_list;


    String driverId;
    String userId;
    String liftId;
    String liftDate;
    String liftDestination;

    private String SENDER_ID = "561530043428";
    public static DatabaseReference liftRoot = root.child("lifts");
    private ValueEventListener liftListener;
    private CustomRequestPassengersAdapter customAdapter;
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

            //List which keeps track of the Users who have requested the particular lift from the driver
            requesting_users_list = new ArrayList<User>();

            userId = getArguments().getString("userId");
            liftId = getArguments().getString("liftId");

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            driverId = currentUser.getUid();

            nd = (NavDrawer) getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_response, container, false);

        getActivity().setTitle(R.string.title_request_responce);


        requestingUsersListView = (ListView) view.findViewById(R.id.passengerRequestList);
        //requesting_users_list = Database.getRequestingUsers();


        customAdapter = new CustomRequestPassengersAdapter(getContext(),requesting_users_list);


//        final RequestItemAdapter arrayAdapter = new RequestItemAdapter(
//                getActivity(), requesting_users_list);


        liftRoot = root;

        liftListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

               // customAdapter.clear(); // remove all the items from previous event

                try
                {
                    DataSnapshot liftRef = snapshot.child("lifts").child(liftId);
                    liftDate = liftRef.child("date").getValue().toString();
                    liftDestination = liftRef.child("destination").getValue().toString();
                    DataSnapshot passengersRef = liftRef.child("passengers");

                    //iterate over the passengers
                    for (DataSnapshot passengersSnapshot : passengersRef.getChildren()) {


                        //get the information of individual passenger
                        String passengerId = passengersSnapshot.getKey();

                        String passengerState = passengersRef.child(passengerId).child("status").getValue().toString();
                        String passengerBoardLength = passengersRef.child(passengerId).child("board length").getValue().toString();

                        Log.i("requestResponse id:", passengerId);
                        Log.i("req resp board len:", passengerBoardLength);
                       // DataSnapshot passengerRef = passengersRef.child(passengerId);
                      //  for (DataSnapshot passengerSnapshot : passengerRef.getChildren()) {

//                            String passengerState = passengerSnapshot.child("state").getValue().toString();
//                            String passengerBoardLength = passengerSnapshot.child("board length").getValue().toString();
//                      //  }
//
//
//                            Log.i("passenger id:", passengerId);
//                            Log.i("board length:", passengerBoardLength);

//                        String passengerId = passengersSnapshot.getKey();
//                        String passengerState = (String) passengersSnapshot.getValue();

                        if (passengerState.equals("pending")) {
                            DataSnapshot userRef = snapshot.child("users").child(passengerId);
                            String passengerName = (String) userRef.child("name").getValue();
                            String passengerAge = (String) userRef.child("age").getValue();
                            String passengerGender = (String) userRef.child("gender").getValue();
                            String passengerEmail = (String) userRef.child("email").getValue();
                            String passengerType = (String) userRef.child("type").getValue();
                            String passengerPhone = (String) userRef.child("phone").getValue();
                            String passengerBio = (String) userRef.child("bio").getValue();

                            //get the passenger's surfboard length


                            User passenger = new User(passengerId, passengerType, passengerEmail);
                            passenger.name = passengerName;
                            passenger.age = passengerAge;
                            passenger.gender = passengerGender;
                            passenger.phone = passengerPhone;
                            passenger.bio = passengerBio;
                            passenger.boardLength = passengerBoardLength;
                            requesting_users_list.add(passenger);
                        } else {
                        }
                  //   }
                    }

                    requestingUsersListView.setAdapter(customAdapter);
                }
                catch (Exception e)
                {

                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        };

        liftRoot.addValueEventListener(liftListener);



        requestingUsersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final User requestingPassenger = (User) adapterView.getAdapter().getItem(i);
                //show the requesting passenger's profile pic
                NavDrawer nd = (NavDrawer) getActivity();
                nd.replaceContent(ProfileScreen.newInstance(requestingPassenger.getUserId()));

                return true;
            }
        });

        requestingUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                //Intent myIntent = new Intent(getActivity(), NextActivity.class);
                //startActivity(myIntent);

                final User requestingPassenger = (User) parent.getAdapter().getItem(position);

                new AlertDialog.Builder(getContext())
                        .setTitle("Accept Passenger")
                        .setMessage("Do you want to accept " + requestingPassenger.getUserName() + " as a Passenger?")
                        .setPositiveButton("accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                notifyUser(requestingPassenger, true);



                                //subtract remaining seats by 1.
                                //DataSnapshot liftRef = snapshot.child("lifts").child(liftId);

                                final DatabaseReference liftRef = liftRoot.child("lifts").child(liftId);

                                Log.i("liftID",liftId);

                                liftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        DataSnapshot liftSnapshot = dataSnapshot.child("seatsAvailable");
                                        String remainingSeats = liftSnapshot.getValue().toString();
                                        Log.i("remaining seats before", remainingSeats);
                                        int seats = Integer.parseInt(remainingSeats) - 1;
                                        Map<String,Object> mapLift = new HashMap<>();
                                        String updatedRemainingSeats = Integer.toString(seats);
                                        Log.i("remainingSeats now", updatedRemainingSeats);
                                        mapLift.put("seatsAvailable",updatedRemainingSeats );
                                        liftRef.updateChildren(mapLift);


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                Database.acceptLiftRequest(liftId, requestingPassenger.getUserId());


                                Display.popup(getActivity(), "Passenger Accepted!");

                                requesting_users_list.remove(position);
                                customAdapter.notifyDataSetChanged();

                                //requestedLift.addPassenger(requestingPassenger.getUserId());
                                // TODO undo comment below
                                //requestingPassenger.addLift(requestedLift);

                                //Database.setUserValue(requestingPassenger);
                                //Database.setLiftValue(requestedLift); // Database function not implemented yet



                            }
                        })
                        .setNegativeButton("reject", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                notifyUser(requestingPassenger, false);

                                Database.rejectLiftRequest(liftId, requestingPassenger.getUserId());
                                Display.popup(getActivity(), "Passenger Rejected!");
                            }
                        })
                        .setNeutralButton("cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        //.setCancelable(true)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

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
                // Database.acceptLiftRequest(liftId, userId);

                // TODO send notification message to user informing them
                // that their request was accepted

                String messageTitle = "Lift Request Accepted";
                String message = "Your request for a seat on the lift to " + liftDestination + " on the " + liftDate + " has been accepted";
                String actionType = "com.surfsharing.MESSAGE";

                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                AtomicInteger msgId = new AtomicInteger();
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId(Integer.toString(msgId.incrementAndGet()))
                        .addData("message_title", messageTitle)
                        .addData("message", message)
                        .addData("action", actionType)
                        .addData("lift", liftDestination)
                        .addData("recipient", userId)
                        .addData("sender", driverId)
                        .build());

        }
        else
        {
                // Wherever the user has an entry representing their pending lift
                // request, remove it to show that they have not secured a seat
                // on this lift and send a notification to inform them.
                // Database.rejectLiftRequest(liftId, userId);

                // TODO send notification message to user informing them
                // that their request was rejected.

                String messageTitle = "Lift Request Rejected";
                String message = "Unfortunately your request for a seat on the lift to " + liftDestination + " on the " + liftDate + " has been rejected by the Driver";
                String actionType = "com.surfsharing.MESSAGE";

                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                AtomicInteger msgId = new AtomicInteger();
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                    .setMessageId(Integer.toString(msgId.incrementAndGet()))
                    .addData("message_title", messageTitle)
                    .addData("message", message)
                        .addData("action", actionType)
                    .addData("lift", liftDestination)
                    .addData("recipient", userId)
                    .addData("sender", driverId)
                    .build());
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        liftRoot.removeEventListener(liftListener);
    }


}
