package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.surf_sharing.surfsharingmobileapp.BackPressedInFragmentVisibleOnTopOfViewPager;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.TabActivity;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

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
    private Spinner spinner;
    private static final String[] boardLengths = {"5'2", "5'4", "5'8", "5'10", "6'2"};
    private int selectedBoard;


    private BackPressedInFragmentVisibleOnTopOfViewPager mOnBackPressedInFragmentVisibleOnTopOfViewPager;


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

        setHasOptionsMenu(true);
        selectedBoard = -1;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_request_lift);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);



        Toolbar mToolbar= (Toolbar) ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetach();
            }
        });

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
                        /*NavDrawer nd = (NavDrawer) getActivity();
                        nd.replaceContent(ProfileScreen.newInstance(driverId));*/

                        //Changes : removed above added tab
                        TabActivity tabActivity =(TabActivity) getActivity();
                        tabActivity.replaceContent(ProfileScreen.newInstance(driverId));

                    }
                });


                Button reqButton = (Button) view.findViewById(R.id.requestButton);

                reqButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if(selectedBoard !=-1) {

                            Display.popup(getActivity(), "Seat on lift requested!");
                            //send the lift id and the length of the surfboard of the passenger
                            Database.makeLiftRequest(idText, boardLengths[selectedBoard]);

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


                           /* NavDrawer nd = (NavDrawer) getActivity();
                            nd.replaceContent(AvailableLifts.newInstance());*/

                            //Changes : removed above, added removal of this fragment after request sent.
                            onDetach();
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
        mOnBackPressedInFragmentVisibleOnTopOfViewPager =(BackPressedInFragmentVisibleOnTopOfViewPager)getActivity();


    }



    @Override
    public void onDetach() {
        super.onDetach();
        //Toast.makeText(getContext(),"Detached",Toast.LENGTH_LONG).show();

        //this is the last fragment to show on top of viewpager, so pass true;
        boolean isLastFragment= true;
        mOnBackPressedInFragmentVisibleOnTopOfViewPager.onBackPressedInFragmentVisibleOnTopOfViewPager(isLastFragment);


        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();


    }



}
