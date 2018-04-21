package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.graphics.Typeface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.iid.FirebaseInstanceId;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.screens.LiftsYouAreOn;
import com.surf_sharing.surfsharingmobileapp.utils.CustomRequestPassengersAdapter;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link RequestResponse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestResponse extends Fragment {

    ListView requestingUsersListView;
    ListView acceptedUsersListView;
    ArrayList<String> passenger_lift_details;
    ArrayList<String[]> requesting_users_list;
    ArrayList<String[]> accepted_users_list;

    String driverId;
    String userId;
    String liftId;
    String liftDate;
    String liftDestination;

    private String SENDER_ID = "561530043428";
    public static DatabaseReference liftRoot;

    private CustomRequestPassengersAdapter requestingAdapter, acceptedAdapter;

    private ProgressDialog dialog;

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
            passenger_lift_details = new ArrayList<String>();
            requesting_users_list = new ArrayList<String[]>();
            accepted_users_list = new ArrayList<String[]>();

            userId = getArguments().getString("userId");
            liftId = getArguments().getString("liftId");

            liftRoot = FirebaseDatabase.getInstance().getReference().child("lifts").child(liftId);

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

        getActivity().setTitle("Passengers");

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        if (!((Activity) getContext()).isFinishing()) {
            dialog.show();
        }
        dialog.setCanceledOnTouchOutside(false);

        requestingUsersListView = (ListView) view.findViewById(R.id.passengerRequestList);
        acceptedUsersListView = (ListView) view.findViewById(R.id.acceptedPassengersList);

        requestingAdapter = new CustomRequestPassengersAdapter(getContext(), requesting_users_list);
        acceptedAdapter = new CustomRequestPassengersAdapter(getContext(), accepted_users_list);


        liftRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // customAdapter.clear(); // remove all the items from previous event

                try {
                    liftDate = (String) snapshot.child("date").getValue();
                    liftDestination = (String) snapshot.child("destination").getValue();
                    DataSnapshot passengersRef = snapshot.child("passengers");

                    //iterate over the passengers
                    for (DataSnapshot passengersSnapshot : passengersRef.getChildren()) {

                        //get the information of individual passenger
                        final String passengerId = passengersSnapshot.getKey();
                        final String passengerName = (String) passengersRef.child(passengerId).child("name").getValue();
                        final String passengerState = (String) passengersRef.child(passengerId).child("status").getValue();
                        final String passengerPhone = (String) passengersRef.child(passengerId).child("phone").getValue();
                        final String passengerPickupStreet1 = (String) passengersRef.child(passengerId).child("pickup street 1").getValue();
                        final String passengerPickupStreet2 = (String) passengersRef.child(passengerId).child("pickup street 2").getValue();
                        final String passengerPickupCounty = (String) passengersRef.child(passengerId).child("pickup county").getValue();
                        final String passengerBoardLength = (String) passengersRef.child(passengerId).child("board length").getValue();

                        String[] passengerDetails = {passengerId, passengerName, passengerPhone, passengerBoardLength, passengerPickupStreet1,
                                passengerPickupStreet2, passengerPickupCounty};


                        if (passengerState.equals("pending")) {
                            requesting_users_list.add(passengerDetails);
                        } else if (passengerState.equals("accepted")) {
                            accepted_users_list.add(passengerDetails);
                        }

                    }

                    requestingUsersListView.setAdapter(requestingAdapter);
                    acceptedUsersListView.setAdapter(acceptedAdapter);

                    TextView requestingUsersView = new TextView(getContext());
                    requestingUsersView.setText("Requesting Passengers:");
                    requestingUsersView.setTextSize(20);
                    requestingUsersView.setTypeface(null, Typeface.BOLD);
                    requestingUsersListView.addHeaderView(requestingUsersView);

                    TextView acceptedUsersView = new TextView(getContext());
                    acceptedUsersView.setText("Accepted Passengers:");
                    acceptedUsersView.setTextSize(20);
                    acceptedUsersView.setTypeface(null, Typeface.BOLD);
                    acceptedUsersListView.addHeaderView(acceptedUsersView);

                    dialog.dismiss();

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


        requestingUsersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String[] requestingPassengerDetails = (String[]) adapterView.getAdapter().getItem(i);
                //show the requesting passenger's profile pic

                if (requestingPassengerDetails != null) {

                    String requestingPassengerId = requestingPassengerDetails[0];

                    NavDrawer nd = (NavDrawer) getActivity();
                    nd.replaceContent(ProfileScreen.newInstance(requestingPassengerId));

                    return true;
                } else {
                    return false;
                }
            }
        });

        requestingUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                String[] requestingPassengerDetails = (String[]) parent.getAdapter().getItem(position);

                if (requestingPassengerDetails != null) {

                    final String requestingPassengerId = requestingPassengerDetails[0];
                    final String requestingPassengerName = requestingPassengerDetails[1];

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setFocusableInTouchMode(true);

                    final AlertDialog alertDialog = builder.create();

                    alertDialog.setTitle("Accept Passenger");
                    alertDialog.setMessage("Do you want to accept " + requestingPassengerName + " as a Passenger?");

                    Button positiveButton = new Button(getContext());
                    positiveButton.setText("Accept");
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            notifyUser(requestingPassengerId, true);

                            Log.i("liftID", liftId);

                            liftRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    DataSnapshot seatsSnapshot = dataSnapshot.child("seatsAvailable");
                                    String remainingSeats = (String) seatsSnapshot.getValue();

                                    int seats = Integer.parseInt(remainingSeats) - 1;
                                    Map<String, Object> mapLift = new HashMap<>();
                                    String updatedRemainingSeats = Integer.toString(seats);

                                    mapLift.put("seatsAvailable", updatedRemainingSeats);
                                    liftRoot.updateChildren(mapLift);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Database.acceptLiftRequest(liftId, requestingPassengerId);

                            Display.popup(getActivity(), "Passenger Accepted!");

                            // requesting_users_list.remove(position);
                            requestingAdapter.notifyDataSetChanged();

                            NavDrawer nd = (NavDrawer) getActivity();
                            nd.replaceContent(LiftsYouAreOffering.newInstance());
                        }
                    });


                    Button negativeButton = new Button(getContext());
                    negativeButton.setText("Reject");
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                            notifyUser(requestingPassengerId, false);

                            Database.rejectLiftRequest(liftId, requestingPassengerId);
                            Display.popup(getActivity(), "Passenger Rejected!");

                            NavDrawer nd = (NavDrawer) getActivity();
                            nd.replaceContent(LiftsYouAreOffering.newInstance());

                        }
                    });


                    Button viewProfileButton = new Button(getContext());
                    viewProfileButton.setText("View Their Profile");
                    viewProfileButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                            //go to driver's profile screen
                            NavDrawer nd = (NavDrawer) getActivity();
                            nd.replaceContent(ProfileScreen.newInstance(requestingPassengerId));

                        }
                    });

                    Button neutralButton = new Button(getContext());
                    neutralButton.setText("Cancel");
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    layout.addView(viewProfileButton);
                    layout.addView(positiveButton);
                    layout.addView(negativeButton);
                    layout.addView(neutralButton);

                    alertDialog.setView(layout);
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();
                }

            }
        });

        return view;
    }

    public void notifyUser(String requestingUserId, boolean accepted) {

        if (accepted) {
            // Wherever the user has an entry representing their pending lift
            // request, change it to show that they are now on this lift and
            // send a notification to inform them.
            // Database.acceptLiftRequest(liftId, userId);

            String driverRegToken = FirebaseInstanceId.getInstance().getToken();

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
                    .addData("recipient", requestingUserId)
                    .addData("sender", driverRegToken)
                    .build());

        } else {
            // Wherever the user has an entry representing their pending lift
            // request, remove it to show that they have not secured a seat
            // on this lift and send a notification to inform them.
            // Database.rejectLiftRequest(liftId, userId);

            String driverRegToken = FirebaseInstanceId.getInstance().getToken();

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
                    .addData("recipient", requestingUserId)
                    .addData("sender", driverRegToken)
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
        //liftRoot.removeEventListener(liftListener);
    }


}
