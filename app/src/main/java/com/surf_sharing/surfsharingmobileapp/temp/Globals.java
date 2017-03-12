package com.surf_sharing.surfsharingmobileapp.temp;

import android.app.Application;

import com.surf_sharing.surfsharingmobileapp.data.LiftContainer;
import com.surf_sharing.surfsharingmobileapp.data.User;

/**
 * Created by Sean on 28/02/2017.
 */

public class Globals extends Application {
    public LiftContainer lifts = new LiftContainer();
    public User testDriver = new User(1, "driver", "x@gmail.com");
    public int liftId = 0;

    public LiftContainer getLifts(){
        return this.lifts;
    }

    public int getLiftId(){
        return liftId++;
    }

}
