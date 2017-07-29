package com.surf_sharing.surfsharingmobileapp.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Created by Sean on 27/02/2017.
 */
public class Lift {
    public String id, destination, car, time, date, name;
    public int seatsAvailable;
    public User driver;

    // car, time, date
    public static ArrayList<String> passengers;
    public static ArrayList<String> pendingPassengers;

    public Lift(User driver, String destination, int seatsAvailable, String id/*, String car*/, String time, String date){
        this.destination = destination;
        this.seatsAvailable = seatsAvailable;
        this.driver = driver;
        this.id = id;
        this.time = time;
        this.date = date;
    }

    /// returns 1 if passenger added
    public int addPassenger(String p){
        if(seatsAvailable > 0){
            passengers.add(p);
            this.seatsAvailable--;
            return 1;
        }
        return 0;

    }

    public String toString(){
        String s;

        Calendar mCurrentCalendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date formatedDate = mCurrentCalendar.getTime();

        try {
            formatedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        mCurrentCalendar.setTime(formatedDate);

        int dayResult = mCurrentCalendar.get(Calendar.DAY_OF_WEEK);
        String day;

        switch (dayResult) {
            case Calendar.MONDAY:
                day = "Monday";
                break;
            case Calendar.TUESDAY:
                day = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                day = "Wednesday";
                break;
            case Calendar.THURSDAY:
                day = "Thursday";
                break;
            case Calendar.FRIDAY:
                day = "Friday";
                break;
            case Calendar.SATURDAY:
                day = "Saturday";
                break;
            default:
                day = "Sunday";
                break;

        }

        s = destination + ", " + seatsAvailable + " seats\n" + day + ", " + date + " at " + time;
        return s;
    }

    public String getDestination() {
        return this.destination;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }
}

