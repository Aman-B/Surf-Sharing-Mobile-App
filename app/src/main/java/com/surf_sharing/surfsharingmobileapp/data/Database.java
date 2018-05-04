package com.surf_sharing.surfsharingmobileapp.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
     * //@param lift the lift information to be posted
     * @return return whether the post succeeded or failed
     */
    public static boolean postLift(final String car, final String seatsAvailable, final String destination, final String date, final String time) {

        ///////////////////////////////////////////////////////////

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
                        Map<String,Object> mapLiftPassengerChild = new HashMap<String, Object>();
                        mapLiftPassengerChild.put(userId, "");

                        String userName = (String) snapshot.child("name").getValue();
                        String userAge = (String) snapshot.child("age").getValue();
                        String userGender = (String) snapshot.child("gender").getValue();
                        String userEmail = (String) snapshot.child("email").getValue();
                        String userType = (String) snapshot.child("type").getValue();
                        String userPhone = (String) snapshot.child("phone").getValue();
                        String userBio = (String) snapshot.child("bio").getValue();

                        String id = liftRoot.push().getKey();

                        Map<String,Object> map = new HashMap<String, Object>();
                        map.put(id, "");
                        liftRoot.updateChildren(map);

                        DatabaseReference Child = liftRoot.child(id);

                        Map<String,Object> mapChild = new HashMap<String, Object>();
                        mapChild.put("driver", "");
                        mapChild.put("car", car);
                        mapChild.put("seatsAvailable", seatsAvailable);
                        mapChild.put("destination", "" + destination);
                        mapChild.put("date", "" + date);
                        mapChild.put("time", "" + time);
                        mapChild.put("passengers", "");
                        Child.updateChildren(mapChild);

                        DatabaseReference usersChild = Child.child("driver");

                        Map<String,Object> mapUserChild = new HashMap<String, Object>();
                        mapUserChild.put("id", userId);
                        mapUserChild.put("name", userName);
                        mapUserChild.put("age", userAge);
                        mapUserChild.put("gender", userGender);
                        mapUserChild.put("email", userEmail);
                        mapUserChild.put("type", userType);
                        mapUserChild.put("phone", userPhone);
                        mapUserChild.put("bio", userBio);
                        usersChild.updateChildren(mapUserChild);

                        //add a Lift to driver
                        DatabaseReference driverRef = userRoot.child(userId);

                        //Map<String,Object> UserMap = new HashMap<String, Object>();
                        //UserMap.put("lifts", "");

                        //driverRef.updateChildren(UserMap);
                        DatabaseReference driverChild = driverRef.child("lifts");

                        Map<String,Object> mapDriverChild = new HashMap<String, Object>();
                        mapDriverChild.put(id, "driver");

                        driverChild.updateChildren(mapDriverChild);

                    }
                    catch (Throwable e)
                    { }
                }
                @Override public void onCancelled(DatabaseError error) { }
            });
        }

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
            map.put(userId, "");
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
            mapChild.put("address", newUserValue.address);
            mapChild.put("lifts", "");

            if(newUserValue.image == null){
                mapChild.put("image", "");
                Log.i("Saving", "nothing");

            }
            else {
                mapChild.put("image", newUserValue.image);
                Log.i("Saving", "image");
            }
            usersRefChild.updateChildren(mapChild);

            return true;
        }
    }




    public static void makeLiftRequest(final String liftId, final String boardLength) {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
        { }
        else
        {
            final String userId = currentUser.getUid();

            final DatabaseReference usersRef = userRoot.child(userId);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    try
                    {


                        // in the User's field, add the lift as pending
                        DatabaseReference userLiftsChild = usersRef.child("lifts");
                        Map<String,Object> mapUserLiftChild = new HashMap<String, Object>();
                        mapUserLiftChild.put(liftId, "pending");

                        //in the Lifts field, add the passenger as pending
                        DatabaseReference liftRef = liftRoot.child(liftId);
                        DatabaseReference liftPassengerChild = liftRef.child("passengers");
                        DatabaseReference passengerChild = liftPassengerChild.child(userId);

                        Map<String, Object> passengerDetailMap = new HashMap<>();

                        //add board length
                        passengerDetailMap.put("board length", boardLength);
                        passengerDetailMap.put("status", "pending");
                        passengerChild.updateChildren(passengerDetailMap);

                        DatabaseReference passenger = liftPassengerChild.child(userId);

                       // passengerChild.updateChildren(mapLiftPassengerChild);
                        userLiftsChild.updateChildren(mapUserLiftChild);

                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                    }
                }


                @Override public void onCancelled(DatabaseError error) { }
            });
        }
    }

    /**
     * Function to reference the lift id inside the user object and in the case of the passenger
     * it adds the passenger id to the list of passengers in the lift object in a pending status
     */


    //EXAMPLE OF PASSENGERS DB

    //---passengers
    //|
    //|-----passenger1
    //       |
    //       |---status:accepted
    //       |----board length:5'2

    public static void acceptLiftRequest(String liftId, String userId) {

        liftId = liftId;
        final DatabaseReference liftRef = liftRoot.child(liftId);


        DatabaseReference usersRef = userRoot.child(userId);
        DatabaseReference userLiftsChild = usersRef.child("lifts");

        //associate the lift to this user as passenger
        Map<String,Object> mapUserLiftsChild = new HashMap<String, Object>();
        mapUserLiftsChild.put(liftId, "passenger");
        userLiftsChild.updateChildren(mapUserLiftsChild);

        DatabaseReference liftPassengers = liftRef.child("passengers");
        DatabaseReference passenger = liftPassengers.child(userId);
        Map<String,Object> mapPassengerState = new HashMap<String, Object>();
        mapPassengerState.put("status", "accepted");
        passenger.updateChildren(mapPassengerState);




//        DatabaseReference liftSeats = liftRef.child("seatsAvailable");
//
//
//        String remainingSeats = liftSeats.getV
//        Map<String,Object> mapLift = new HashMap<>();
//        int seats = Integer.parseInt(remainingSeats) - 1;
//        String updatedRemainingSeats = Integer.toString(seats);
//        mapLift.put("seatsAvailable",updatedRemainingSeats );
//        liftRef.



    }



    public static void rejectLiftRequest(String liftId, String userId) {

        DatabaseReference usersRef = userRoot.child(userId);
        DatabaseReference userLiftsChild = usersRef.child("lifts");
        Map<String,Object> mapUserLiftsChild = new HashMap<String, Object>();
        mapUserLiftsChild.put(liftId, "rejected");

        DatabaseReference liftRef = liftRoot.child(liftId);
        DatabaseReference liftPassengers = liftRef.child("passengers");
        Map<String,Object> mapLiftChild = new HashMap<String, Object>();
        liftPassengers.child(userId).removeValue();

        liftPassengers.updateChildren(mapLiftChild);

        userLiftsChild.updateChildren(mapUserLiftsChild);
    }

    public static boolean upgradeToDriver(String carModel, String carRegistration, String licenceNumber, String maxPassengers) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
        {
            return false;
        }
        else
        {
            String userId = currentUser.getUid();

 //           Map<String,Object> map = new HashMap<String, Object>();
 //           map.put(userId, "");
 //           userRoot.updateChildren(map);

            DatabaseReference usersRefChild = userRoot.child(userId);

            Map<String,Object> mapChild = new HashMap<String, Object>();
            mapChild.put("car_model", carModel);
            mapChild.put("car_registration", carRegistration);
            mapChild.put("licence_number", licenceNumber);
            mapChild.put("max_passengers", maxPassengers);
            mapChild.put("type", "pending");
            usersRefChild.updateChildren(mapChild);

            return true;
        }
    }
}







