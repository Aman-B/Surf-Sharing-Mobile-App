package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiftsYouAreOn.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiftsYouAreOn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiftsYouAreOn extends Fragment {

    private DatabaseReference userRoot;
    private DatabaseReference liftsRoot;

    ListView liftsYouAreOnView;
    ArrayList<String> lifts_id_list;
    ArrayList<Lift> lifts_on_list;



    private ProgressDialog dialog;


    public LiftsYouAreOn() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment LiftsYouAreOn.
     */
    public static LiftsYouAreOn newInstance() {
        LiftsYouAreOn fragment = new LiftsYouAreOn();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Lifts You Are On");

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_lifts_you_are_on, container, false);


        liftsYouAreOnView = (ListView) view.findViewById(R.id.userLiftList);
        lifts_id_list = new ArrayList<String>();
        lifts_on_list = new ArrayList<Lift>();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        if (!((Activity) getContext()).isFinishing()) {
            dialog.show();
        }
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        userRoot = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        liftsRoot = FirebaseDatabase.getInstance().getReference("lifts");

        final ArrayAdapter<Lift> arrayAdapter = new ArrayAdapter<Lift>(getActivity(), android.R.layout.simple_list_item_1, lifts_on_list);
        //liftRoot = FirebaseDatabase.getInstance().getReference("lifts");

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try
                {
                    DataSnapshot liftsSnapshot = snapshot.child("lifts");

                    for (DataSnapshot lift : liftsSnapshot.getChildren())
                    {
                        if (lift.getValue().equals("passenger")) {
                            lifts_id_list.add(lift.getKey());
                        }
                    }

                    liftsRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            try {
                                for (String liftId : lifts_id_list)
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

                                        DataSnapshot driverRef = liftRef.child("driver");

                                        String driverId = driverRef.getKey();
                                        String driverName = (String) driverRef.child("name").getValue();
                                        String driverAge = (String) driverRef.child("age").getValue();
                                        String driverGender = (String) driverRef.child("gender").getValue();
                                        String driverEmail = (String) driverRef.child("email").getValue();
                                        String driverType = (String) driverRef.child("type").getValue();
                                        String driverPhone = (String) driverRef.child("phone").getValue();
                                        String driverBio = (String) driverRef.child("bio").getValue();

                                        final User driver = new User(driverId, driverType, driverEmail);
                                        driver.name = driverName;
                                        driver.age = driverAge;
                                        driver.gender = driverGender;
                                        driver.phone = driverPhone;
                                        driver.bio = driverBio;

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

                                        lifts_on_list.add(lift);
                                    }
                                    else
                                    {
                                        Log.d("Delete Lift: Lift ID:", liftId);

                                        DataSnapshot driverRef = liftRef.child("driver");
                                        String driverId = driverRef.getKey();
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

                                liftsYouAreOnView.setAdapter(arrayAdapter);
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

                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        });

        liftsYouAreOnView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Intent myIntent = new Intent(getActivity(), NextActivity.class);
                //startActivity(myIntent);

                //Toast.makeText(getContext(), "Test ", Toast.LENGTH_SHORT).show();

                Lift l = (Lift) parent.getAdapter().getItem(position);
                // TODO replace with actual condtion which will check if a request has been made for this lift
                if(true){
                    NavDrawer nd = (NavDrawer) getActivity();
                    nd.setupLiftInfo(LiftInfo.newInstance(), l.id);
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

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
