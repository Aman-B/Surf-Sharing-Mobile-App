package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.temp.Globals;
import com.surf_sharing.surfsharingmobileapp.data.LiftContainer;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AvailableLifts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiftsYouAreOffering extends Fragment {

    ListView liftList;
    ArrayList<Lift> lifts_list;

    private DatabaseReference liftRoot;
    private ValueEventListener liftListener;

    private String userId;

    //Globals glob;
    public LiftsYouAreOffering() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AvailableLifts.
     */
    // retrieve info from database and display lifts
    public static LiftsYouAreOffering newInstance(/*LiftContainer lifts*/) {
        LiftsYouAreOffering fragment = new LiftsYouAreOffering();
        Bundle args = new Bundle();
        //args.putInt("", some);
        fragment.setArguments(args);
        return fragment;
    }

    // Interface for passing lift id to activity
    public interface OnLiftSelectedListener {
        public void onArticleSelected( int liftId );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // handle bundle arguments

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_lifts_offering);
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_lifts_you_are_offering, container, false);

        // populate list with lifts

        liftList = (ListView) view.findViewById(R.id.driverLiftList);
        lifts_list = new ArrayList<Lift>();


        // added by Sean, working on requestLift
        liftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Intent myIntent = new Intent(getActivity(), NextActivity.class);
                //startActivity(myIntent);

                //Toast.makeText(getContext(), "Test ", Toast.LENGTH_SHORT).show();

                Lift l = (Lift) parent.getAdapter().getItem(position);
                // TODO replace with actual condtion which will check if a request has been made for this lift
                if(true){
                    NavDrawer nd = (NavDrawer) getActivity();
                    nd.setupRequestResponse(RequestResponse.newInstance(), userId, l.id, userId, l.seatsAvailable, l.destination, l.time, l.date, l.driver.email);
                }


                //nd.replaceContent(RequestResponse.newInstance());
                // get lift id



            }
        });

        lifts_list = new ArrayList<Lift>();
        final ArrayAdapter<Lift> arrayAdapter = new ArrayAdapter<Lift>(getActivity(), android.R.layout.simple_list_item_1, lifts_list);
        //liftRoot = FirebaseDatabase.getInstance().getReference("lifts");

        Database.root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try
                {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = currentUser.getUid();

                    DataSnapshot userRef = snapshot.child("users").child(userId);

                    DataSnapshot liftsRef = userRef.child("lifts");

                    for (DataSnapshot lifttSnapshot : liftsRef.getChildren()) {

                        String liftId = lifttSnapshot.getKey();
                        String liftState = (String) lifttSnapshot.getValue();

                        if (liftState.equals("driver"))
                        {
                            DataSnapshot liftRef = snapshot.child("lifts").child(liftId);

                            String id = liftRef.getKey();
                            int seatsAvailable = Integer.parseInt((String) liftRef.child("seatsAvailable").getValue());
                            String car = (String) liftRef.child("car").getValue();
                            String destination = (String) liftRef.child("destination").getValue();
                            String date = (String) liftRef.child("date").getValue();
                            String time = (String) liftRef.child("time").getValue();

                            DataSnapshot driverRef = liftRef.child("driver");
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

                            Lift lift = new Lift(driver, destination, seatsAvailable, id, time, date);
                            lift.car = car;

                            DataSnapshot passengersRef = liftRef.child("passengers");
                            lift.passengers = new ArrayList<String>();
                            lift.pendingPassengers = new ArrayList<String>();

                            for (DataSnapshot passengerSnapshot : passengersRef.getChildren()) {

                                String passengerId = passengerSnapshot.getKey();
                                String passengerState = (String) passengerSnapshot.getValue();

                                if (passengerState.equals("pending"))
                                {
                                    lift.pendingPassengers.add(passengerId);
                                }
                                else
                                {
                                    lift.passengers.add(passengerId);
                                }
                            }

                            lifts_list.add(lift);
                        }
                        else
                        { }
                    }

                    liftList.setAdapter(arrayAdapter);
                }
                catch (Exception e)
                {

                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        });




        /*liftListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lifts_list = new ArrayList<Lift>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    try
                    {
                        String id = postSnapshot.getKey();
                        int seatsAvailable = Integer.parseInt((String) postSnapshot.child("seatsAvailable").getValue());
                        String car = (String) postSnapshot.child("car").getValue();
                        String destination = (String) postSnapshot.child("destination").getValue();
                        String date = (String) postSnapshot.child("date").getValue();
                        String time = (String) postSnapshot.child("time").getValue();

                        DataSnapshot driverRef = postSnapshot.child("driverId");
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

                        Lift lift = new Lift(driver, destination, seatsAvailable, id, time, date);
                        lift.car = car;
                        //lift.date = date;
                        //lift.time = time;

                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        userId = currentUser.getUid();


                        Log.d("lift: ", driverId+" + "+userId);


                        if(driverId.equals("apMGnPrP8bXyIwztxjMcukxrEve2")){
                            lifts_list.add(lift);
                        }

//                        if(driverId.equals(userId)){
//                            lifts_list.add(lift);
//                        }




                    }
                    catch (Exception e)
                    {

                    }
                }

                try
                {
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, lifts_list);
                    liftList.setAdapter(adapter);
                }
                catch (Exception e)
                { }

            }

            public void removeListener() {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        liftRoot.addValueEventListener(liftListener);*/

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        liftRoot.removeEventListener(liftListener);
    }

}
