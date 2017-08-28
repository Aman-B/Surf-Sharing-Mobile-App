package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
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
import com.surf_sharing.surfsharingmobileapp.utils.CustomizedAdapter;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AvailableLifts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AvailableLifts extends Fragment {

    //ListView liftList;
    ArrayList<Lift> lifts_list;
    ArrayList<Lift> origLiftList;

    private DatabaseReference liftRoot;
    private ValueEventListener liftListener;
    private ArrayAdapter adapter;
    private Menu menu;
    private CustomizedAdapter customizedAdapter;
    private ListView listView;
    private SearchView searchView;


    String userId;
    boolean alreadyRequested;
    private AlertDialog.Builder alertDlg;



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
        setHasOptionsMenu(true);

        liftRoot = FirebaseDatabase.getInstance().getReference("lifts");
        alreadyRequested = false;
        alertDlg = new AlertDialog.Builder(getContext());

    }


    //check if a network connection is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private boolean hasConnectivity(){
        if (!isNetworkAvailable()) {

            alertDlg.setTitle("Something went wrong");
            alertDlg.setMessage("Could not retrieve information from database, please check your internet connection.");
            alertDlg.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    hasConnectivity();

                }
            });
            alertDlg.show();
            return false;
        }
        return true;
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_available_lift);
        // Inflate the layout for this fragment
        //  final View view =  inflater.inflate(R.layout.fragment_available_lifts, container, false);

        final View view = inflater.inflate(R.layout.list_item_layout, container, false);
        // populate list with lifts

        listView = (ListView) view.findViewById(R.id.liftList);
        searchView = (SearchView) view.findViewById(R.id.searchView);


        lifts_list = new ArrayList<Lift>();
        origLiftList = new ArrayList<Lift>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Intent myIntent = new Intent(getActivity(), NextActivity.class);
                //startActivity(myIntent);

                final Lift l = (Lift) parent.getAdapter().getItem(position);

                NavDrawer nd = (NavDrawer) getActivity();

                //pass in the lift
                nd.setupRequestLift(RequestLift.newInstance(), l);

            }
        });



        hasConnectivity();

//        if (!isNetworkAvailable()) {
//
//            alertDlg.setTitle("Something went wrong");
//            alertDlg.setMessage("Could not retrieve information from database, please check your internet connection.");
//            alertDlg.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                }
//            });
//            alertDlg.show();
//        }
//        else{


        liftListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lifts_list = new ArrayList<Lift>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    try {
                        String id = postSnapshot.getKey();
                        Log.i("availableLift-ID", id);

                        //check if the seatsAvailable is greater than 0
                        int seatsAvailable = Integer.parseInt((String) postSnapshot.child("seatsAvailable").getValue());
                        if (seatsAvailable > 0) {

                            String car = (String) postSnapshot.child("car").getValue();
                            String destination = (String) postSnapshot.child("destination").getValue();
                            String date = (String) postSnapshot.child("date").getValue();
                            String time = (String) postSnapshot.child("time").getValue();

                            DataSnapshot driverRef = postSnapshot.child("driver");
                            String driverId = (String) driverRef.child("id").getValue();
                            String driverName = (String) driverRef.child("name").getValue();
                            String driverAge = (String) driverRef.child("age").getValue();
                            String driverGender = (String) driverRef.child("gender").getValue();
                            String driverEmail = (String) driverRef.child("email").getValue();
                            String driverType = (String) driverRef.child("type").getValue();
                            String driverPhone = (String) driverRef.child("phone").getValue();
                            String driverBio = (String) driverRef.child("bio").getValue();

                            Log.i("availableLiftDriverName", driverName);
                            Log.i("driverMail", driverEmail);

                            User driver = new User(driverId, driverType, driverEmail);
                            driver.name = driverName;
                            driver.age = driverAge;
                            driver.gender = driverGender;
                            driver.phone = driverPhone;
                            driver.bio = driverBio;

                            Lift lift = new Lift(driver, destination, seatsAvailable, id, time, date);
                            lift.car = car;

                            DataSnapshot passengersRef = postSnapshot.child("passengers");
                            lift.passengers = new ArrayList<String>();
                            lift.pendingPassengers = new ArrayList<String>();


                            //iterate over the passengers
                            for (DataSnapshot snapshot : passengersRef.getChildren()) {

                                String passengerId = snapshot.getKey();
                                Log.i("passengerId", passengerId);
                                String passengerState = snapshot.child("status").getValue().toString();
                                String passengerBoardLen = snapshot.child("board length").getValue().toString();

                                Log.i("pass-boardLen", passengerBoardLen);

                                if (passengerState.equals("pending")) {
                                    lift.pendingPassengers.add(passengerId);
                                } else {
                                    lift.passengers.add(passengerId);
                                }
                            }

                            Log.i("liftDes", destination);
                            Log.i("liftDriver", driver.name);

                            lifts_list.add(lift);
                            for (int i = 0; i < lifts_list.size(); i++) {
                                Log.i("lift " + i, lifts_list.get(i).getDestination());
                            }
                            Log.i("size", lifts_list.size() + "");
                            origLiftList.add(lift);

                        }
                    } catch (Exception e) {

                    }
                }

                try {


                    customizedAdapter = new CustomizedAdapter(getContext(), lifts_list);

                    listView.setAdapter(customizedAdapter);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            customizedAdapter.getFilter().filter(s);
                            return false;
                        }
                    });


                    sortByDestination();
                } catch (Exception e) {
                }

            }

            public void removeListener() {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                alertDlg.setTitle("Something went wrong");
                alertDlg.setMessage("Could not retrieve information from database, please check your internet connection.");
                alertDlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDlg.show();
            }
        };

        liftRoot.addValueEventListener(liftListener);

    //}
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        liftRoot.removeEventListener(liftListener);
    }


    public void sortByDestination(){

        //sorting
        Collections.sort(lifts_list, new Comparator<Lift>() {
            @Override
            public int compare(Lift lift1, Lift lift2) {
                return lift1.destination.toLowerCase().compareTo(lift2.destination.toLowerCase());
            }
        });


        customizedAdapter.notifyDataSetChanged();


    }



    public void sortByRemainingSeats(){

        Collections.sort(lifts_list, new Comparator<Lift>() {
            @Override
            public int compare(Lift lift1, Lift lift2) {

                return ((Integer) lift1.seatsAvailable).compareTo(lift2.seatsAvailable);
            }
        });


        customizedAdapter.notifyDataSetChanged();

    }

    public void sortByLatestAdded(){


      lifts_list.clear();

        for (int i = 0; i < origLiftList.size(); i++) {
            lifts_list.add(origLiftList.get(i));
        }

        customizedAdapter.notifyDataSetChanged();


    }


    public long getMillis(Lift lift){

        String strDate = lift.getDate() + " " + lift.getTime();
        long millis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = sdf.parse(strDate);
            millis =  date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
            return millis;
    }

    public void sortByTime(){

            Collections.sort(lifts_list, new Comparator<Lift>() {
                @Override
                public int compare(Lift lift1, Lift lift2) {
                    return ((Long) getMillis(lift1)).compareTo(getMillis(lift2));
                }
            });

        customizedAdapter.notifyDataSetChanged();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.sort_by, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);


        for(int i=0; i<menu.size(); i++){
            if(menu.getItem(i).getItemId() != R.id.sortByDestination){
                menu.getItem(i).setChecked(false);

            }
            else{
                menu.getItem(i).setChecked(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        check(item);

        switch(item.getItemId()){

            case R.id.sortByDestination: sortByDestination(); return true;
            case R.id.sortByAvailableSeats: sortByRemainingSeats(); return true;
            //case R.id.sortByLatestAdded: sortByLatestAdded(); return true;
            case R.id.sortByTime: sortByTime(); return true;
            default: return false;
        }

    }


    public void check( MenuItem item) {
        for(int i=0; i<menu.size(); i++){
            if(menu.getItem(i).getItemId() != item.getItemId()){
                menu.getItem(i).setChecked(false);

            }
        }

        item.setChecked(true);

    }
}
