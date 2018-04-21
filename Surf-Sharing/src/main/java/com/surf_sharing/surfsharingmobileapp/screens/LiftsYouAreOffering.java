package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import com.surf_sharing.surfsharingmobileapp.data.LiftContainer;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AvailableLifts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiftsYouAreOffering extends Fragment {

    ArrayList<String> driver_lift_ids;
    ListView liftList;
    ArrayList<Lift> lifts_list;

    private DatabaseReference userRoot;
    private DatabaseReference liftsRoot;

    private String userId;

    private ProgressDialog dialog;

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

        driver_lift_ids = new ArrayList<String>();

        // populate list with lifts
        liftList = (ListView) view.findViewById(R.id.driverLiftList);
        lifts_list = new ArrayList<Lift>();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        if (!((Activity) getContext()).isFinishing()) {
            dialog.show();
        }
        dialog.setCanceledOnTouchOutside(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        final ArrayAdapter<Lift> arrayAdapter = new ArrayAdapter<Lift>(getActivity(), android.R.layout.simple_list_item_1, lifts_list);
        userRoot = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        liftsRoot = FirebaseDatabase.getInstance().getReference("lifts");

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try
                {
                    final String driverId = snapshot.getKey();
                    String driverName = (String) snapshot.child("name").getValue();
                    String driverAge = (String) snapshot.child("age").getValue();
                    String driverGender = (String) snapshot.child("gender").getValue();
                    String driverEmail = (String) snapshot.child("email").getValue();
                    String driverType = (String) snapshot.child("type").getValue();
                    String driverPhone = (String) snapshot.child("phone").getValue();
                    String driverBio = (String) snapshot.child("bio").getValue();

                    final User driver = new User(driverId, driverType, driverEmail);
                    driver.name = driverName;
                    driver.age = driverAge;
                    driver.gender = driverGender;
                    driver.phone = driverPhone;
                    driver.bio = driverBio;

                    DataSnapshot driverLiftsSnapshot = snapshot.child("lifts");

                    for (DataSnapshot liftSnapshot : driverLiftsSnapshot.getChildren())
                    {
                        String liftState = (String) liftSnapshot.getValue();

                        if (liftState.equals("driver"))
                        {
                            String liftId = liftSnapshot.getKey();
                            driver_lift_ids.add(liftId);
                        }
                    }

                    liftsRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                for (String liftId : driver_lift_ids)
                                {
                                    DataSnapshot liftRef = snapshot.child(liftId);

                                    String id = liftRef.getKey();
                                    String car = (String) liftRef.child("car").getValue();
                                    String destination = (String) liftRef.child("destination").getValue();
                                    String date = (String) liftRef.child("date").getValue();

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    Date strDepartureDate = sdf.parse(date);

                                    long dayInMillis = 86400000;
                                    if (strDepartureDate.getTime() > (System.currentTimeMillis() - dayInMillis)) {

                                        String time = (String) liftRef.child("time").getValue();
                                        int seatsAvailable = Integer.parseInt((String) liftRef.child("seatsAvailable").getValue());

                                        Lift lift = new Lift(driver, destination, seatsAvailable, id, time, date);
                                        lift.car = car;

                                        lift.passengers = new ArrayList<String>();
                                        lift.pendingPassengers = new ArrayList<String>();

                                        DataSnapshot liftPassengersRef = liftRef.child("passengers");

                                        for (DataSnapshot passengerSnapshot : liftPassengersRef.getChildren()) {
                                            String passengerId = passengerSnapshot.getKey();
                                            String passengerState = (String) passengerSnapshot.child("status").getValue();

                                            if (passengerState.equals("pending")) {
                                                lift.pendingPassengers.add(passengerId);
                                            } else {
                                                lift.passengers.add(passengerId);
                                            }

                                        }

                                        lifts_list.add(lift);
                                    }
                                    else
                                    {
                                        Log.d("Delete Lift: Lift ID:", liftId);
                                        Log.d("Delete Lift: Driver ID:", driverId);

                                        DatabaseReference driverLiftRef = FirebaseDatabase.getInstance().getReference().child("users").child(driverId).child(liftId);
                                        driverLiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot driverSnapshot) {
                                                driverSnapshot.getRef().removeValue();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }

                                        });

                                        for (DataSnapshot passengerSnapshot : liftRef.child("passengers").getChildren())
                                        {
                                            String passengerId = passengerSnapshot.getKey();
                                            Log.d("Delete Lift: Pass. ID:", passengerId);
                                            DatabaseReference passengerLiftRef = FirebaseDatabase.getInstance().getReference().child("users").child(passengerId).child("lifts").child(liftId);
                                            passengerLiftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot passengerSnapshot) {
                                                    passengerSnapshot.getRef().removeValue();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }

                                            });
                                        }

                                        liftRef.getRef().removeValue();
                                    }

                                }

                                liftList.setAdapter(arrayAdapter);
                                dialog.dismiss();

                            }
                            catch (Exception e)
                            {
                                Log.d("offerLift-anyErrors:", e.toString());
                            }
                        }

                        @Override public void onCancelled(DatabaseError error) { }
                    });

                }
                catch (Exception e)
                {
                    Log.d("offerLift-anyOuErrors:", e.toString());
                }
            }

            @Override public void onCancelled(DatabaseError error) { }
        });

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
