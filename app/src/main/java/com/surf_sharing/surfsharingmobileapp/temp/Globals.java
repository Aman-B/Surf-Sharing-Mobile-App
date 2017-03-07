package com.surf_sharing.surfsharingmobileapp.temp;

import android.app.Application;

import com.surf_sharing.surfsharingmobileapp.data.LiftContainer;
import com.surf_sharing.surfsharingmobileapp.data.User;

/**
 * Created by Sean on 28/02/2017.
 */

public class Globals extends Application {
    LiftContainer lifts = new LiftContainer();
    User testDriver = new User(1, "driver", "x@gmail.com");
    int liftId = 0;

    LiftContainer getLifts(){
        return this.lifts;
    }

    int getLiftId(){
        return liftId++;
    }

}
