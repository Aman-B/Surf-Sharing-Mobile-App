package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.text.InputType;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestLift extends Fragment {

    Lift requestedLift;
    String driverId, driverName, idText, userId, dateStr, timeStr, liftStr, seatsStr;
    String pickupLocationStreet1, pickupLocationStreet2, pickupLocationCounty = "";
    String userName, userPhone;
    int seatsVal;
    boolean alreadyRequested;

    private DatabaseReference userRoot;
    private TextView locationText, timeText, dateText, driverNameText, seatsText;
    private String SENDER_ID = "561530043428";
    private Spinner spinner;
    private static final String[] boardLengths = {"5'10", "6'2", "6'4", "6'6", "6'8", "6'10", "7'0",
            "7'2", "7'4", "7'6", "7'8", "7'10", "8'0 +"};
    private int selectedBoard;

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

        selectedBoard = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_request_lift);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        userRoot = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    userName = (String) snapshot.child("name").getValue();
                    userPhone = (String) snapshot.child("phone").getValue();
                } catch (Exception ex) {

                }
            }

            @Override public void onCancelled(DatabaseError error) { }
        });

        String userReadableDate = "";

        String[] calendarDateParts = dateStr.split("/");
        Calendar calendar = new GregorianCalendar(Integer.parseInt(calendarDateParts[2]), Integer.parseInt(calendarDateParts[1]), Integer.parseInt(calendarDateParts[0])); // Note that Month value is 0-based. e.g., 0 for January.

        int result = calendar.get(Calendar.DAY_OF_WEEK);
        switch (result) {
            case Calendar.MONDAY:
                userReadableDate = "Monday, " + dateStr;
                break;
            case Calendar.TUESDAY:
                userReadableDate = "Tuesday, " + dateStr;
                break;
            case Calendar.WEDNESDAY:
                userReadableDate = "Wednesday, " + dateStr;
                break;
            case Calendar.THURSDAY:
                userReadableDate = "Thursday, " + dateStr;
                break;
            case Calendar.FRIDAY:
                userReadableDate = "Friday, " + dateStr;
                break;
            case Calendar.SATURDAY:
                userReadableDate = "Saturday, " + dateStr;
                break;
            case Calendar.SUNDAY:
                userReadableDate = "Sunday, " + dateStr;
                break;
        }

        if (alreadyRequested) {

                View view = inflater.inflate(R.layout.fragment_already_requested, container, false);

                locationText = (TextView) view.findViewById(R.id.requestLiftLocation);
                timeText = (TextView) view.findViewById(R.id.requestLiftTime);
                dateText = (TextView) view.findViewById(R.id.requestLiftDate);
                seatsText = (TextView) view.findViewById(R.id.requestLiftSeats);

                // set the driver and lift info
                locationText.setText(liftStr);
                timeText.setText(timeStr);
                dateText.setText(userReadableDate);
                seatsText.setText(String.format("(%s seats remaining)", seatsStr));

                return view;

        } else {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_request_lift, container, false);

                locationText = (TextView) view.findViewById(R.id.requestLiftLocation);
                timeText = (TextView) view.findViewById(R.id.requestLiftTime);
                dateText = (TextView) view.findViewById(R.id.requestLiftDate);
                driverNameText = (TextView) view.findViewById(R.id.requestLiftDriverName);
                seatsText = (TextView) view.findViewById(R.id.requestLiftSeats);

                //set the drop down lest menu/spinner
                spinner = (Spinner) view.findViewById(R.id.spinner);
                //array adapter will convert the arrayList into view objects for the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, boardLengths);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        switch(pos){

                            case 0:
                                selectedBoard = 0;
                                break;
                            case 1:
                                selectedBoard = 1;
                                break;
                            case 2:
                                selectedBoard = 2;
                                break;
                            case 3:
                                selectedBoard = 3;
                                break;
                            case 4:
                                selectedBoard = 4;
                                break;
                            case 5:
                                selectedBoard = 5;
                                break;
                            case 6:
                                selectedBoard = 6;
                                break;
                            case 7:
                                selectedBoard = 7;
                                break;
                            case 8:
                                selectedBoard = 8;
                                break;
                            case 9:
                                selectedBoard = 9;
                                break;
                            case 10:
                                selectedBoard = 10;
                                break;
                            case 11:
                                selectedBoard = 11;
                                break;
                            case 12:
                                selectedBoard = 12;
                                break;
                            default:
                                selectedBoard = -1;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Toast.makeText(getContext(), "Select your surfboard length", Toast.LENGTH_LONG);

                    }
                });

                // set the driver and lift info
                locationText.setText(liftStr);
                timeText.setText(timeStr);
                dateText.setText(userReadableDate);
                driverNameText.setText(driverName);
                seatsText.setText(String.format("(%s seats remaining)", seatsStr));


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

                        if (selectedBoard != -1) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Enter your desired pickup location");

                            // Set up the layout group which will contain the inputs
                            LinearLayout layout = new LinearLayout(getContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setFocusableInTouchMode(true);

                            // Set up the inputs
                            final EditText pickupStreetAddress1 = new EditText(getContext());
                            final EditText pickupStreetAddress2 = new EditText(getContext());
                            final EditText pickupCounty = new EditText(getContext());

                            // Specify the type of input expected
                            pickupStreetAddress1.setInputType(InputType.TYPE_CLASS_TEXT);
                            pickupStreetAddress1.setHint("Street Address 1");
                            pickupStreetAddress1.setHintTextColor(Color.GRAY);
                            pickupStreetAddress1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus)
                                        pickupStreetAddress1.setHint("");
                                    else
                                        pickupStreetAddress1.setHint("Street Address 1");
                                }
                            });

                            pickupStreetAddress2.setInputType(InputType.TYPE_CLASS_TEXT);
                            pickupStreetAddress2.setHint("Street Address 2");
                            pickupStreetAddress2.setHintTextColor(Color.GRAY);
                            pickupStreetAddress2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus)
                                        pickupStreetAddress2.setHint("");
                                    else
                                        pickupStreetAddress2.setHint("Street Address 2");
                                }
                            });

                            pickupCounty.setInputType(InputType.TYPE_CLASS_TEXT);
                            pickupCounty.setHint("County");
                            pickupCounty.setHintTextColor(Color.GRAY);
                            pickupCounty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus)
                                        pickupCounty.setHint("");
                                    else
                                        pickupCounty.setHint("County");
                                }
                            });

                            // Add the inputs to the layout group
                            layout.addView(pickupStreetAddress1);
                            layout.addView(pickupStreetAddress2);
                            layout.addView(pickupCounty);

                            builder.setView(layout);

                            // Set up the buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pickupLocationStreet1 = pickupStreetAddress1.getText().toString();
                                    pickupLocationStreet2 = pickupStreetAddress2.getText().toString();
                                    pickupLocationCounty = pickupCounty.getText().toString();

                                    if (pickupLocationStreet1.isEmpty() || pickupLocationStreet1.equals("")) {

                                        Display.popup(getActivity(), "Please enter something for Street Address 1");

                                    } else if (pickupLocationStreet2.isEmpty() || pickupLocationStreet2.equals("")) {

                                        Display.popup(getActivity(), "Please enter something for Street Address 2");

                                    } else if (pickupLocationCounty.isEmpty() || pickupLocationCounty.equals("")) {

                                        Display.popup(getActivity(), "Please enter something for County");

                                    } else {


                                        Display.popup(getActivity(), "Seat on lift requested!");
                                        //send the lift id and the length of the surfboard of the passenger
                                        Database.makeLiftRequest(idText, userName, userPhone, pickupLocationStreet1, pickupLocationStreet2, pickupLocationCounty, boardLengths[selectedBoard]);

                                        String userRegToken = FirebaseInstanceId.getInstance().getToken();

                                        // request the lift
                                        String messageTitle = "New Lift Request";
                                        String message = "You have received a new seat request for your Lift to " + liftStr + ".\nCheck your Lift in the 'Lifts You Are Offering' page.";
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
                                                .addData("sender", userRegToken)
                                                .build());


                                        NavDrawer nd = (NavDrawer) getActivity();
                                        nd.replaceContent(AvailableLifts.newInstance());
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();

                        }
                        else{
                            Toast.makeText(getContext(), "Select your surfboard length", Toast.LENGTH_LONG);

                        }

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
