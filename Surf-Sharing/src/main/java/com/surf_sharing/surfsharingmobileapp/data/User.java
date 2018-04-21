package com.surf_sharing.surfsharingmobileapp.data;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Sean on 27/02/2017.
 */
public class User {


    public String id, type, name, gender, email, phone, bio, age, image, address, boardLength, pickupStreet1, pickupStreet2, pickupCounty;

    ArrayList<Lift> lifts;
    public User(String id, String type, String email) {
        this.id = id;
        this.type = type;
        this.email = email;
        this.lifts = new ArrayList<Lift>();
        this.lifts.clear();
        this.phone = "";
        this.bio = "";
        this.age = "";
        this.image = "";
        this.address = "";
        this.boardLength = "";
        this.pickupStreet1 = "";
        this.pickupStreet2 = "";
        this.pickupCounty = "";
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

    public String getUserImage(){return this.image;}

    public String getUserAdr(){return this.address;}

    public String getBoardLength() {
        return boardLength;
    }

    public String getPickupStreet1() {
        return pickupStreet1;
    }

    public String getPickupStreet2() {
        return pickupStreet2;
    }

    public String getPickupCounty() {
        return pickupCounty;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setImage(String image){this.image = image;}

    public void setAddress(String adr){this.address = adr;}

    public void setBoardLength() {
        this.boardLength = boardLength;
    }

    public void setPickupStreet1() { this.pickupStreet1 = pickupStreet1; }

    public void setPickupStreet2() { this.pickupStreet2 = pickupStreet2; }

    public void setPickupCounty() {
        this.pickupCounty = pickupCounty;
    }

}