package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.temp.Globals;
import com.surf_sharing.surfsharingmobileapp.data.LiftContainer;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AvailableLifts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AvailableLifts extends Fragment {

    ListView liftList;

    //Globals glob;
    public AvailableLifts() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AvailableLifts.
     */
    // retrieve info from database and display lifts
    public static AvailableLifts newInstance(/*LiftContainer lifts*/) {
        AvailableLifts fragment = new AvailableLifts();
        Bundle args = new Bundle();
        //args.putInt("", some);
        fragment.setArguments(args);
        return fragment;
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
        getActivity().setTitle(R.string.title_available_lift);
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_available_lifts, container, false);

        // populate list with lifts

        liftList = (ListView) view.findViewById(R.id.liftList);
        final ArrayList<Lift> lifts_list = new ArrayList<Lift>();

        Database.liftRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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

                        Lift lift = new Lift(driver, destination, seatsAvailable, id);
                        lift.car = car;
                        lift.date = date;
                        lift.time = time;

                        lifts_list.add(lift);
                    }
                    catch (Exception e)
                    {

                    }
                }

                setListView(lifts_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayList<Lift>  empty = new ArrayList<Lift>();
        setListView(empty);


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


    public void setListView(ArrayList<Lift>  lifts_list) {

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, lifts_list);
        liftList.setAdapter(adapter);
    }
}
