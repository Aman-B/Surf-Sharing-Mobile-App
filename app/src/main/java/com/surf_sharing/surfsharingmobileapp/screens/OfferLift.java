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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link OfferLift#newInstance} factory method to
 * create an instance of this fragment.
 */

// create a lift object containing the info that will be passed to the database
public class OfferLift extends Fragment {

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

        // example of a button that replaces the current fragment with another
        Button testButton = (Button) view.findViewById(R.id.offer_lift_test_button);
        testButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NavDrawer nd = (NavDrawer) getActivity();
                nd.replaceContent(ManageAccount.newInstance());
            }
        });


        Button enterButton = (Button) view.findViewById(R.id.buttonEnter);
        enterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // create Lift object
                EditText dest = (EditText) view.findViewById(R.id.destEnter);
                EditText seats = (EditText) view.findViewById(R.id.seatsEnter);
                Log.d("crash", "test crash");
                //dest = (EditText) view.findViewById(R.id.destEnter);
                Log.d("tag", "dest safe");
                //seats = (EditText) view.findViewById(R.id.seatsEnter);
                NavDrawer nd = (NavDrawer) getActivity();
                String[] fields = nd.getTextValues();
                // right now this just sends a test lift to Database.postLift()
                User testDriver = new User("" + 1, "driver", "x@gmail.com");
                Lift l = new Lift(testDriver, fields[0], Integer.parseInt(fields[1]), "" + 1);
                Log.d("postlift", ""+Database.postLift(l)+"\nvals: "+fields[0]+", "+fields[1]);
                Toast.makeText(getActivity(),
                        "postlift "+Database.postLift(l)+"\nvals: "+fields[0]+", "+fields[1], Toast.LENGTH_LONG)
                        .show();
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
