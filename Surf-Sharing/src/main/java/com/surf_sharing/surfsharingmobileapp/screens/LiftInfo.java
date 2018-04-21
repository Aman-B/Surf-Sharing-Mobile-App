package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiftInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiftInfo extends Fragment {

    String liftId;
    String userId;

    public static DatabaseReference liftRoot;

    public LiftInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LiftInfo.
     */
    public static LiftInfo newInstance() {
        LiftInfo fragment = new LiftInfo();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            liftId = getArguments().getString("liftId");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            userId = currentUser.getUid();

            liftRoot = FirebaseDatabase.getInstance().getReference().child("lifts").child(liftId);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Lift Info");

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_lift_info, container, false);


        liftRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // customAdapter.clear(); // remove all the items from previous event

                try {

                    String driverCar = (String) snapshot.child("car").getValue();
                    DataSnapshot driverSnapshot = snapshot.child("driver");
                    String driverName = (String) driverSnapshot.child("name").getValue();
                    String driverPhone = (String) driverSnapshot.child("phone").getValue();

                    DataSnapshot passengerSnapshot = snapshot.child("passengers").child(userId);
                    String passengerPickupStreet1 = (String) passengerSnapshot.child("pickup street 1").getValue();
                    String passengerPickupStreet2 = (String) passengerSnapshot.child("pickup street 2").getValue();
                    String passengerPickupCounty = (String) passengerSnapshot.child("pickup county").getValue();

                    TextView tvDriverName = (TextView) view.findViewById(R.id.tvDriverName);
                    TextView tvDriverPhone = (TextView) view.findViewById(R.id.tvDriverPhone);
                    TextView tvDriverCar = (TextView)  view.findViewById(R.id.tvDriverCar);
                    TextView tvYourPickupStreet1 = (TextView)  view.findViewById(R.id.tvYourPickupStreet1);
                    TextView tvYourPickupStreet2 = (TextView)  view.findViewById(R.id.tvYourPickupStreet2);
                    TextView tvYourPickupCounty = (TextView)  view.findViewById(R.id.tvYourPickupCounty);

                    tvDriverName.setText(driverName);
                    tvDriverPhone.setText(driverPhone);
                    tvDriverCar.setText(driverCar);
                    tvYourPickupStreet1.setText(passengerPickupStreet1);
                    tvYourPickupStreet2.setText(passengerPickupStreet2);
                    tvYourPickupCounty.setText(passengerPickupCounty);

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


        // Inflate the layout for this fragment
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

}
