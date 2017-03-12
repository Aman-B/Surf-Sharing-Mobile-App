package com.surf_sharing.surfsharingmobileapp.data;

import java.util.ArrayList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.surf_sharing.surfsharingmobileapp.temp.DatabaseTestActivity;

import static com.surf_sharing.surfsharingmobileapp.utils.Display.popup;

/**
 * Created by aran on 07/03/17.
 *
 * Contains functions for interacting with the Firebase realtime databases
 */
public class Database {

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

}
