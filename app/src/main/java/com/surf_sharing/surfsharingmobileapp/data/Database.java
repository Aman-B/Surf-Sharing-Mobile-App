package com.surf_sharing.surfsharingmobileapp.data;

import java.util.ArrayList;

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

}
