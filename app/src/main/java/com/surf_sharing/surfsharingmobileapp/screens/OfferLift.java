package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import java.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link OfferLift#newInstance} factory method to
 * create an instance of this fragment.
 */

// create a lift object containing the info that will be passed to the database
public class OfferLift extends Fragment {

    private TextView textViewTime, textViewDate;
    private EditText editTextDest, editTextSeats;
    private Button enterButton, pickTimeButton, pickDateButton;
    String userId, userEmail;
    //EditText dest;
    //EditText seats;
    //Database database;
    public OfferLift() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OfferLift.
     */
    public static OfferLift newInstance() {
        OfferLift fragment = new OfferLift();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // handle bundle arguments
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_offer_lift);
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_offer_lift, container, false);

        textViewTime = (TextView) view.findViewById(R.id.offerLiftTime);
        textViewDate = (TextView) view.findViewById(R.id.offerLiftDate);

        editTextDest = (EditText) view.findViewById(R.id.destEnter);
        editTextSeats = (EditText) view.findViewById(R.id.seatsEnter);

        pickDateButton = (Button) view.findViewById(R.id.offerLiftDateButton);
        pickTimeButton = (Button) view.findViewById(R.id.offerLiftTimeButton);
        enterButton = (Button) view.findViewById(R.id.buttonEnter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        pickTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        textViewTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int dayOfMonth = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentDate.get(Calendar.MONTH);
                int year = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textViewDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, month, year));
                    }
                }, year, month, dayOfMonth);
                mDatePicker.show();
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // create Lift object
                String dest = editTextDest.getText().toString();
                String seats = editTextSeats.getText().toString();
                String time = textViewTime.getText().toString();
                String date = textViewDate.getText().toString();
//                String time = editTextTime.getText().toString();
//                String date = editTextDate.getText().toString();
                // check that text fields contain info
                if (dest.isEmpty()) {
                    Display.popup(getActivity(), "Please enter a destination");
                } else if (date.isEmpty()) {
                    Display.popup(getActivity(), "Please enter a date");
                } else if (time.isEmpty()) {
                    Display.popup(getActivity(), "Please enter a time");
                } else if (seats.isEmpty()) {
                    Display.popup(getActivity(), "Please enter number of seats available on lift");
                } else {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    userId = currentUser.getUid();

                    userEmail = currentUser.getEmail();

                    User testDriver = new User(userId, "driver", userEmail);
                    Lift l = new Lift(testDriver, dest, Integer.parseInt(seats), "" + 1, time, date); //TODO: replace with real acount
                    Database.postLift(l);
                    Display.popup(getActivity(), "You're lift offer has been created:\n"+l.toString());
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
