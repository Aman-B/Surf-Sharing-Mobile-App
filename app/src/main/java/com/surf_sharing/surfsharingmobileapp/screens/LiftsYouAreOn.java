package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiftsYouAreOn.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiftsYouAreOn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiftsYouAreOn extends Fragment {

    ListView liftsYouAreOnView;
    ArrayList<Lift> lifts_on_list;


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
        lifts_on_list = new ArrayList<Lift>();





        final ArrayAdapter<Lift> arrayAdapter = new ArrayAdapter<Lift>(getActivity(), android.R.layout.simple_list_item_1, lifts_on_list){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view =super.getView(position, convertView, parent);

                TextView firstTV = (TextView) view.findViewById(android.R.id.text1);
                firstTV.setTextColor(Color.WHITE);




                return view;
            }
        };
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

                    for (DataSnapshot liftSnapshot : liftsRef.getChildren()) {


                        String liftId = liftSnapshot.getKey();
                        String liftState = (String) liftSnapshot.getValue();

                        if (liftState.equals("passenger"))
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

                               // String passengerState = (String) passengerSnapshot.getValue();
                               // Display.popup(getActivity(),"state"+passengerState);

                                    String passengerState = (String) passengerSnapshot.child("status").getValue();
                                    Display.popup(getActivity(),"state"+passengerState);
                                    if (passengerState.equals("pending"))
                                    {
                                        lift.pendingPassengers.add(passengerId);
                                    }
                                    else
                                    {
                                        lift.passengers.add(passengerId);
                                    }



                            }


                            lifts_on_list.add(lift);
                        }

                    }

                    liftsYouAreOnView.setAdapter(arrayAdapter);
                }
                catch (Exception e)
                {

                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_activity_lifts_you_are_on);
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
