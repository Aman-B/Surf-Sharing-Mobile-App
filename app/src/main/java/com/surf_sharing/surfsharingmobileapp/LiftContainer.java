package com.surf_sharing.surfsharingmobileapp;



import java.util.ArrayList;

/**
 * Created by Sean on 27/02/2017.
 */
public class LiftContainer {
    ArrayList<Lift> lifts;
    LiftContainer(){
        lifts = new ArrayList<Lift>();
        lifts.clear();
    }

    public void addLift(Lift l){
        lifts.add(l);
    }

    public void removeLift(int id){
        // remove lift from arraylist using id
    }

    public String[] getLiftsAsStrings(){
        String[] asStrings;
        Lift[] allLifts;
        allLifts = this.lifts.toArray(new Lift[lifts.size()]);;
        asStrings = new String[allLifts.length];
        for(int i = 0 ; i < allLifts.length ; i++){
            asStrings[i] = allLifts[i].destination;
        }
        return asStrings;
    }
}