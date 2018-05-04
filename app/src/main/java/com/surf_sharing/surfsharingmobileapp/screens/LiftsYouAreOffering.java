package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.BackPressedInFragmentVisibleOnTopOfViewPager;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.TabActivity;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
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

    private ProgressDialog mProgressDialog;
    private BackPressedInFragmentVisibleOnTopOfViewPager mOnBackPressedInFragmentVisibleOnTopOfViewPager;

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

        mProgressDialog=new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Fectching Lifts You Are Offering...");
        mProgressDialog.show();

        final ArrayAdapter<Lift> arrayAdapter = new ArrayAdapter<Lift>(getActivity(), android.R.layout.simple_list_item_1, lifts_list)
        {
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
                    userId = currentUser.getUid();
                    Log.i("offerLift-userId:", userId);

                    DataSnapshot userRef = snapshot.child("users").child(userId);

                    DataSnapshot userLiftsRef = userRef.child("lifts");

                    //get all the lifts where the user is the driver
                    for (DataSnapshot lifttSnapshot : userLiftsRef.getChildren()) {

                        String liftId = lifttSnapshot.getKey();
                        String liftState = (String) lifttSnapshot.getValue();
                        Log.i("offerLift-liftID:", liftId);

                        if (liftState.equals("driver"))
                        {
                            Log.i("offerLift-liftState:", liftState);

                            //goto lift root
                            DataSnapshot liftRef = snapshot.child("lifts").child(liftId);

                            String id = liftRef.getKey();
                            String car = (String) liftRef.child("car").getValue();
                            String destination = (String) liftRef.child("destination").getValue();
                            String date = (String) liftRef.child("date").getValue();
                            String time = (String) liftRef.child("time").getValue();

                            int seatsAvailable = Integer.parseInt((String) liftRef.child("seatsAvailable").getValue());

                            //get all the driver's information
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


                            //iterate over the passengers
                            //--passengers
                            //|------passenger1
                            //|             |
                            //|             |status:"pending|accepted"
                            //|             |boardLength
                            //|
                            //|------passenger2

                            //iterate over the passengers
                            for (DataSnapshot passengersSnapshot : passengersRef.getChildren()) {

                                String passengerId = passengersSnapshot.getKey();

                                //get individual passenger
                                DataSnapshot passengerSnapshot = passengersRef.child(passengerId);
                                String passengerState =  passengerSnapshot.child("status").getValue().toString();
                                String passengerBoardLength = passengerSnapshot.child("board length").getValue().toString();
                                Log.i("board len:lift offer", passengerBoardLength);
                                Log.i("passenger status offer:", passengerState);
                                //String passengerState = (String) passengerSnapshot.getValue();

                                if (passengerState.equals("pending"))
                                {
                                    lift.pendingPassengers.add(passengerId);
                                }
                                else
                                {
                                    lift.passengers.add(passengerId);
                                }
                            }

                            for(int i=0;i<lift.pendingPassengers.size();i++){
                                Log.i("passenger " + i, lift.pendingPassengers.get(i));
                            }

                            lifts_list.add(lift);
                        }
                        else
                        { }
                    }

                    liftList.setAdapter(arrayAdapter);
                    if(mProgressDialog.isShowing())
                        mProgressDialog.cancel();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    if(mProgressDialog.isShowing())
                        mProgressDialog.cancel();
                    Display.popup(getActivity(),"Some error occurred. Try again, later!");
                }
            }
            @Override public void onCancelled(DatabaseError error) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.cancel();
                Display.popup(getActivity(),"Some error occurred. Try again, later!");
            }
        });


        liftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Intent myIntent = new Intent(getActivity(), NextActivity.class);
                //startActivity(myIntent);

                //Toast.makeText(getContext(), "Test ", Toast.LENGTH_SHORT).show();

                Lift l = (Lift) parent.getAdapter().getItem(position);
                // TODO replace with actual condition which will check if a request has been made for this lift
                if(true){
                    TabActivity nd = (TabActivity) getActivity();
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
        mOnBackPressedInFragmentVisibleOnTopOfViewPager =(BackPressedInFragmentVisibleOnTopOfViewPager)getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        liftRoot.removeEventListener(liftListener);
        if(!getActivity().isFinishing())
        {
            //this is the last fragment to show on top of viewpager, so pass true;
            boolean isLastFragment= true;
            mOnBackPressedInFragmentVisibleOnTopOfViewPager.onBackPressedInFragmentVisibleOnTopOfViewPager(isLastFragment,"Available Lifts");


            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

}
