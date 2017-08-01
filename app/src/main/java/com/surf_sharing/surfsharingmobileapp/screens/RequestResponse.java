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
import com.surf_sharing.surfsharingmobileapp.RequestItemAdapter;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.screens.LiftsYouAreOn;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

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

    private DatabaseReference liftRoot;
    private ValueEventListener liftListener;

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

            //List which keeps track of the Users who have requested the particular lift from the driver
            requesting_users_list = new ArrayList<User>();

            userId = getArguments().getString("userId");
            liftId = getArguments().getString("liftId");

            nd = (NavDrawer) getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_response, container, false);

        getActivity().setTitle(R.string.title_request_responce);


        requestingUsers = (ListView) view.findViewById(R.id.passengerRequestList);
        //requesting_users_list = Database.getRequestingUsers();

        final RequestItemAdapter arrayAdapter = new RequestItemAdapter(
                getActivity(), requesting_users_list);

        liftRoot = Database.root;
        liftListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                arrayAdapter.clear(); // remove all the items from previous event

                try
                {
//                    Toast.makeText(getContext(), "liftId: " + liftId, Toast.LENGTH_SHORT).show();
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
        };

        liftRoot.addValueEventListener(liftListener);



        requestingUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final User requestingPassenger = (User) adapterView.getAdapter().getItem(i);
                //show the requesting passenger's profile pic
                NavDrawer nd = (NavDrawer) getActivity();
                nd.replaceContent(ProfileScreen.newInstance(requestingPassenger.getUserId()));

                return true;
            }
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
                        .setPositiveButton("accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                notifyUser(requestingPassenger, true);

                                Database.acceptLiftRequest(liftId, requestingPassenger.getUserId());
                                Display.popup(getActivity(), "Passenger Accepted!");

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
        liftRoot.removeEventListener(liftListener);
    }


}
