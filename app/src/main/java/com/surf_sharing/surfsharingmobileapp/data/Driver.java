package com.surf_sharing.surfsharingmobileapp.data;

import com.surf_sharing.surfsharingmobileapp.data.User;

/**
 * Created by lukeagnew on 24/03/2017.
 */

public class Driver extends User {

    String car;
    String licence;
    String registration;
    int maximumPassengers;


    public Driver(String id, String type, String email, String car,
                  String licence, String registration, int maximumPassengers) {

        super(id, type, email);

        this.car = car;
        this.licence = licence;
        this.registration = registration;
        this.maximumPassengers = maximumPassengers;

    }



}
