package com.surf_sharing.surfsharingmobileapp;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Sean on 27/02/2017.
 */
public class User {
    int id;
    String type, email;
    ArrayList<Lift> lifts;
    User(int id, String type, String email){
        this.id = id;
        this.type = type;
        this.email = email;
        this.lifts = new ArrayList<Lift>();
        this.lifts.clear();
    }



    public void requestLift(Lift lift){
        lift.driver.receiveRequest(this, lift);
    }

    public void receiveRequest(User user, Lift lift){
        System.out.print("request made\nAccept? ");
        Scanner s = new Scanner(System.in);
        // send notification to driver
    }

    public void acceptRequest(){
        // accept request and add user to lift
    }

}