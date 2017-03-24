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
     * Function to post a new lift to the database
     *
     * @param lift the lift information to be posted
     * @return return whether the post succeeded or failed
     */
    public static boolean postLift(Lift lift) {

        DatabaseReference usersRef = root.child("lifts");
        String id = usersRef.push().getKey();

        Map<String,Object> map = new HashMap<String, Object>();
        map.put(id, "");
        usersRef.updateChildren(map);

        DatabaseReference Child = usersRef.child(id);

        Map<String,Object> mapChild = new HashMap<String, Object>();
        mapChild.put("driver", "");
        mapChild.put("car", "" + lift.car);
        mapChild.put("seatsAvailable", "" + lift.seatsAvailable);
        mapChild.put("destination", "" + lift.destination);
        mapChild.put("date", "" + lift.date);
        mapChild.put("time", "" + lift.time);
        mapChild.put("passengers", "");
        Child.updateChildren(mapChild);

        DatabaseReference usersChild = Child.child("driverId");

        Map<String,Object> mapUserChild = new HashMap<String, Object>();
        mapUserChild.put("id", "" + lift.driver.name);
        mapUserChild.put("name", "" + lift.driver.name);
        mapUserChild.put("age", "" + lift.driver.age);
        mapUserChild.put("gender", "" + lift.driver.gender);
        mapUserChild.put("email", "" + lift.driver.email);
        mapUserChild.put("type", "" + lift.driver.type);
        mapUserChild.put("phone", "" + lift.driver.phone);
        mapUserChild.put("bio", "" + lift.driver.bio);
        usersChild.updateChildren(mapUserChild);

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

    public static void addLiftToUser(Lift lift) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
        { }
        else
        {
            String userId = currentUser.getUid();

            DatabaseReference usersRef = userRoot.child(userId);

            Map<String,Object> map = new HashMap<String, Object>();
            map.put("lifts", "");
            usersRef.updateChildren(map);

            DatabaseReference Child = usersRef.child("lifts");

            Map<String,Object> mapChild = new HashMap<String, Object>();

            if (lift.driver.id != null && userId.equals(lift.driver.id))
            {
                mapChild.put(lift.id, "Driver");
            }
            else
            {
                mapChild.put(lift.id, "Passenger");
            }

            Child.updateChildren(mapChild);
        }
    }

    /*public static void getLift(int liftId){

    }*/
}







