package com.surf_sharing.surfsharingmobileapp.data;

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

    /**
     * Function to post a new lift to the database
     *
     * @param lift the lift information to be posted
     * @return return whether the post succeeded or failed
     */
    public static boolean postLift(Lift lift) {
        // TODO: write data in lift object to remote database
        return false;
    }

    /**
     * Function to get a list of all available lifts
     *
     * @return list of all available Lifts
     */
    public static ArrayList<Lift> getAllLifts() {
        // TODO: create list of lifts pulled from the database
        // so far it just returns a dummy list with one lift
        User testUser = new User(1, "driver", "email");
        Lift dummy = new Lift(testUser, "bray", 5, 1);
        ArrayList<Lift> list = new ArrayList<>();
        list.add(dummy);
        return list;
    }

    /**
     * Function to return the currently signed in User,
     * at the moment it returns a dummy User, and needs
     * to be updated to actually communicate with Firebase
     * to retrieve and return the User
     * @return User
     */
    public static User getCurrentUser() {

        User user = new User(1, "passenger", "user1@example.com");
        return user;
    }

    /**
     * Function to update the values of a particular user, may want to change
     * the return type from 'void' to some relevant return type e.g.
     * 'DatabaseError'
     */
    public static void setUserValue(int userID, User newUserValue) {
        // get ID of current user

    }

    //-------------------------------------------------------------------------------------------------------------------------------

    public static boolean createUser_(User user) {

        DatabaseReference usersRef = root.child("users");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("" + user.id, "");
        usersRef.updateChildren(map);

        DatabaseReference usersRefChild = usersRef.child("" + user.id);

        Map<String,Object> mapChild = new HashMap<String, Object>();
        mapChild.put("name", "" + user.name);
        mapChild.put("age", "" + user.age);
        mapChild.put("gender", "" + user.gender);
        mapChild.put("email", "" + user.email);
        mapChild.put("type", "" + user.type);
        mapChild.put("phone", "" + user.phone);
        mapChild.put("bio", "" + user.bio);
        usersRefChild.updateChildren(mapChild);

        return false;
    }

    public static boolean postLift_(Lift lift) {

        DatabaseReference usersRef = root.child("lifts");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("" + lift.id, "");
        usersRef.updateChildren(map);

        DatabaseReference usersRefChild = usersRef.child("" + lift.id);

        Map<String,Object> mapChild = new HashMap<String, Object>();
        mapChild.put("driverId", "" + lift.driver.id);
        mapChild.put("car", "" + lift.car);
        mapChild.put("seatsAvailable", "" + lift.seatsAvailable);
        mapChild.put("destination", "" + lift.destination);
        mapChild.put("date", "" + lift.date);
        mapChild.put("time", "" + lift.time);
        usersRefChild.updateChildren(mapChild);

        return false;
    }

    public static String lifts;
    public static ArrayList<Lift> getAllLifts_() {

        DatabaseReference usersRef = root.child("lifts");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String x = "";

                ArrayList<Lift> list = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    int id = Integer.parseInt((String) postSnapshot.getKey());
                    int driverId = Integer.parseInt((String) postSnapshot.child("driverId").getValue());
                    int seatsAvailable = Integer.parseInt((String) postSnapshot.child("seatsAvailable").getValue());
                    String car = (String) postSnapshot.child("car").getValue();
                    String destination = (String) postSnapshot.child("destination").getValue();
                    String date = (String) postSnapshot.child("date").getValue();
                    String time = (String) postSnapshot.child("time").getValue();

                    User driver = new User(driverId, "", "");

                    Lift lift = new Lift(driver, destination, seatsAvailable, id);
                    lift.car = car;
                    lift.date = date;
                    lift.time = time;

                    list.add(lift);
                }

                liftList = list;

                DatabaseTestActivity.text.setText("" + liftList.size());


/*                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext())
                {
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                DatabaseTestActivity.text.setText(set.toString());
                lifts = set.toString();

                liftList.addAll(set);

                //showText.setText("" + set.toString());*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*

        //DataSnapshot dataSnapshot = root.

        Set<String> set = new HashSet<String>();
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext())
        {
             set.add(((DataSnapshot)i.next()).getKey());
        }

*/
        User testUser = new User(1, "driver", "email");
        Lift dummy = new Lift(testUser, "bray", 5, 1);
        ArrayList<Lift> list = new ArrayList<>();
        list.add(dummy);
        return list;
    }



}
