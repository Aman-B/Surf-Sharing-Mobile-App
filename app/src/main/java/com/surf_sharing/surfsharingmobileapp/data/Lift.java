package com.surf_sharing.surfsharingmobileapp.data;

import java.util.ArrayList;

/**
 * Created by Sean on 27/02/2017.
 */
public class Lift {
    String destination, car, time, date;
    int seatsAvailable;
    User driver;
    int id;
    // car, time, date
    ArrayList<User> passengers;
    public Lift(User driver, String destination, int seatsAvailable, int id/*, String car, String time, String date*/){
        this.destination = destination;
        this.seatsAvailable = seatsAvailable;
        this.driver = driver;
        this.id = id;
    }

    /// returns 1 if passenger added
    public int addPassenger(User p){
        if(seatsAvailable > 0){
            passengers.add(p);
            this.seatsAvailable--;
            return 1;
        }
        return 0;

    }

    public String toString(){
        String s;
        s = destination+", "+seatsAvailable+" seats";
        return s;
    }
}

