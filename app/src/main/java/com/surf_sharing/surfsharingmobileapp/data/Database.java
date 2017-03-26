package com.surf_sharing.surfsharingmobileapp.data;

import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.temp.DatabaseTestActivity;


import static com.surf_sharing.surfsharingmobileapp.utils.Display.popup;

/**
 * Created by aran on 07/03/17.
 *
 * Contains functions for interacting with the Firebase realtime databases
 */



public class Database {

    public static ArrayList<Lift> liftList;

    public static DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    public static DatabaseReference userRoot = root.child("users");
    public static DatabaseReference liftRoot = root.child("lifts");

    /**
     * Function to post a new lift to the database and it makes a driver refference inside the driver object
     *
     *
     * @param lift the lift information to be posted
     * @return return whether the post succeeded or failed
     */
    public static boolean postLift(Lift lift) {

        String id = liftRoot.push().getKey();

        Map<String,Object> map = new HashMap<String, Object>();
        map.put(id, "");
        liftRoot.updateChildren(map);

        DatabaseReference Child = liftRoot.child(id);

        Map<String,Object> mapChild = new HashMap<String, Object>();
        mapChild.put("driver", "");
        mapChild.put("car", "" + lift.car);
        mapChild.put("seatsAvailable", "" + lift.seatsAvailable);
        mapChild.put("destination", "" + lift.destination);
        mapChild.put("date", "" + lift.date);
        mapChild.put("time", "" + lift.time);
        mapChild.put("passengers", "");
        Child.updateChildren(mapChild);

        DatabaseReference usersChild = Child.child("driver");

        Map<String,Object> mapUserChild = new HashMap<String, Object>();
        mapUserChild.put("id", "" + lift.driver.id);
        mapUserChild.put("name", "" + lift.driver.name);
        mapUserChild.put("age", "" + lift.driver.age);
        mapUserChild.put("gender", "" + lift.driver.gender);
        mapUserChild.put("email", "" + lift.driver.email);
        mapUserChild.put("type", "" + lift.driver.type);
        mapUserChild.put("phone", "" + lift.driver.phone);
        mapUserChild.put("bio", "" + lift.driver.bio);
        usersChild.updateChildren(mapUserChild);

        //add a Lift to driver
        DatabaseReference driverRef = userRoot.child(lift.driver.id);

        Map<String,Object> UserMap = new HashMap<String, Object>();
        UserMap.put("lifts", "");
        driverRef.updateChildren(UserMap);
        DatabaseReference driverChild = driverRef.child("lifts");
        Map<String,Object> mapDriverChild = new HashMap<String, Object>();
        mapDriverChild.put(lift.id, "Driver");

        driverChild.updateChildren(mapDriverChild);

        return false;
    }

    /**
     * Function to update the values of the current user, may want to change
     * the return type from 'void' to some relevant return type e.g.
     * 'DatabaseError'
     */
    public static boolean setUserValue(User newUserValue) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
        {
            return false;
        }
        else
        {
            String userId = currentUser.getUid();

            Map<String,Object> map = new HashMap<String, Object>();
            map.put("" + userId, "");
            userRoot.updateChildren(map);

            DatabaseReference usersRefChild = userRoot.child("" + userId);

            Map<String,Object> mapChild = new HashMap<String, Object>();
            mapChild.put("name", newUserValue.name);
            mapChild.put("age", newUserValue.age);
            mapChild.put("gender", newUserValue.gender);
            mapChild.put("email", newUserValue.email);
            mapChild.put("type", newUserValue.type);
            mapChild.put("phone", newUserValue.phone);
            mapChild.put("bio", newUserValue.bio);
            mapChild.put("lifts", "");
            usersRefChild.updateChildren(mapChild);

            return true;
        }
    }

    /**
     * Function to reference the lift id inside the user object and in the case of the passenger
     * it adds the passenger id to the list of passengers in the lift object in a pending status
     */
    public static void makeLiftRequest(final String liftId) {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
        { }
        else
        {
            final String userId = currentUser.getUid();

            //add a Lift to a User
            final DatabaseReference usersRef = userRoot.child(userId);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    try
                    {

                        DatabaseReference userLiftsChild = usersRef.child("lifts");
                        Map<String,Object> mapUserLiftChild = new HashMap<String, Object>();
                        mapUserLiftChild.put(liftId, "Requested");

                        //add a User to a Lift
                        DatabaseReference liftRef = liftRoot.child(liftId);
                        DatabaseReference liftPassengerChild = liftRef.child("passengers");
                        Map<String,Object> mapLiftPassengerChild = new HashMap<String, Object>();
                        mapLiftPassengerChild.put(userId, "");

                        String userName = (String) snapshot.child("name").getValue();
                        String userAge = (String) snapshot.child("age").getValue();
                        String userGender = (String) snapshot.child("gender").getValue();
                        String userEmail = (String) snapshot.child("email").getValue();
                        String userType = (String) snapshot.child("type").getValue();
                        String userPhone = (String) snapshot.child("phone").getValue();
                        String userBio = (String) snapshot.child("bio").getValue();

                        DatabaseReference passenger = liftPassengerChild.child(userId);

                        Map<String,Object> mapPassenger = new HashMap<String, Object>();
                        mapPassenger.put("state", "panding");
                        mapPassenger.put("name", userName);
                        mapPassenger.put("age", userAge);
                        mapPassenger.put("gender", userGender);
                        mapPassenger.put("email", userEmail);
                        mapPassenger.put("type", userType);
                        mapPassenger.put("phone", userPhone);
                        mapPassenger.put("bio", userBio);


                        liftPassengerChild.updateChildren(mapLiftPassengerChild);

                        userLiftsChild.updateChildren(mapUserLiftChild);

                        passenger.updateChildren(mapPassenger);
                    }
                    catch (Throwable e)
                    { }
                }
                @Override public void onCancelled(DatabaseError error) { }
            });
        }
    }

    /**
     * Function to reference the lift id inside the user object and in the case of the passenger
     * it adds the passenger id to the list of passengers in the lift object in a pending status
     */
    public static void acceptLiftRequest(String liftId, String userId) {

        DatabaseReference usersRef = userRoot.child(userId);
        DatabaseReference userLiftsChild = usersRef.child("lifts");
        Map<String,Object> mapUserLiftsChild = new HashMap<String, Object>();
        mapUserLiftsChild.put(liftId, "Passenger");

        DatabaseReference liftRef = liftRoot.child(liftId);
        DatabaseReference liftPassengers = liftRef.child("passengers");
        DatabaseReference liftPassengersState = liftPassengers.child(userId);
        Map<String,Object> mapPassengerState = new HashMap<String, Object>();
        mapPassengerState.put("state", "Passenger");

        liftPassengersState.updateChildren(mapPassengerState);

        userLiftsChild.updateChildren(mapUserLiftsChild);
    }

    public static void rejectLiftRequest(String liftId, String userId) {

        DatabaseReference usersRef = userRoot.child(userId);
        DatabaseReference userLiftsChild = usersRef.child("lifts");
        Map<String,Object> mapUserLiftsChild = new HashMap<String, Object>();
        mapUserLiftsChild.put(liftId, "Rejected");

        DatabaseReference liftRef = liftRoot.child(liftId);
        DatabaseReference liftPassengers = liftRef.child("passengers");
        Map<String,Object> mapLiftChild = new HashMap<String, Object>();
        liftPassengers.child(userId).removeValue();
        liftPassengers.updateChildren(mapLiftChild);

        liftPassengers.updateChildren(mapLiftChild);

        userLiftsChild.updateChildren(mapUserLiftsChild);
    }
}







