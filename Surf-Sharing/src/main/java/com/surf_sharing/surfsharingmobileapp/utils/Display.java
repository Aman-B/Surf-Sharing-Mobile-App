package com.surf_sharing.surfsharingmobileapp.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * A util class for displaying information on screen.
 *
 * Created by aran on 09/03/17.
 */

public class Display {

    /**
     * Display a message on screen.
     *
     * Typical usage would be to call from within an activity, and use the *this* keyword.
     * i.e. popup(this, "my message");
     *
     * @param activity The activity calling this function
     * @param message The message to display on screen
     */
    public static void popup(Activity activity, String message) {
        Toast.makeText(
                activity.getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
