package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText editTextDest, editTextSeats, editTextTime, editTextDate;
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

        editTextDest = (EditText) view.findViewById(R.id.destEnter);
        editTextSeats = (EditText) view.findViewById(R.id.seatsEnter);
        editTextTime = (EditText) view.findViewById(R.id.timeEnter);
        editTextDate = (EditText) view.findViewById(R.id.dateEnter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        Button enterButton = (Button) view.findViewById(R.id.buttonEnter);
        enterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // create Lift object
                String dest = editTextDest.getText().toString();
                String seats = editTextSeats.getText().toString();
                String time = editTextTime.getText().toString();
                String date = editTextDate.getText().toString();
                // check that text fields contain info
                if (dest.isEmpty()) {
                    Display.popup(getActivity(), "Please enter a destination");
                } else if (seats.isEmpty()) {
                    Display.popup(getActivity(), "Please enter number of seats available on lift");
                    } else if (time.isEmpty()) {
                        Display.popup(getActivity(), "Please enter a time");
                        } else if (date.isEmpty()) {
                            Display.popup(getActivity(), "Please enter a date");
                            }
                        else {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            userId = currentUser.getUid();

                            userEmail = currentUser.getEmail();

                            // right now this just sends a test lift to Database.postLift()
                            User testDriver = new User(userId, "driver", userEmail);
                            Lift l = new Lift(testDriver, dest, Integer.parseInt(seats), "" + 1, time, date); //TODO: replace with real acount
                            Database.postLift(l);
                            Display.popup(getActivity(), "postlift "+"\nvals: "+dest+", "+seats);
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
