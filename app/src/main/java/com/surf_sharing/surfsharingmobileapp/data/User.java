package com.surf_sharing.surfsharingmobileapp.data;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Sean on 27/02/2017.
 */
public class User {

    public String id, type, name, gender, email, phone, bio, age;

    ArrayList<Lift> lifts;
    public User(String id, String type, String email) {
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

    public void addLift(Lift newLift) {
        this.lifts.add(newLift);
    }

    public ArrayList<Lift> getUserLifts() {
        return this.lifts;
    }

    public String getUserId() {
        return this.id;
    }

    public String getUserType() {
        return this.type;
    }

    public String getUserName() {
        return this.name;
    }

    public String getUserEmail() {
        return this.email;
    }

    public String getUserPhone() {
        return this.phone;
    }

    public String getUserAge() {
        return this.age;
    }

    public String getUserGender() {
        return this.gender;
    }

    public String getUserBio() {
        return this.bio;
    }
}