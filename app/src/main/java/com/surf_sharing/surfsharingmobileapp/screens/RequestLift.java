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
import android.widget.TextView;
import android.widget.Toast;

import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;


public class RequestLift extends Fragment {

    //EditText dest;
    //EditText seats;
    //Database database;
    Lift requestedLift;
    String destText, idText;
    int seatsVal;
    public RequestLift() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OfferLift.
     */
    public static RequestLift newInstance() {
        RequestLift fragment = new RequestLift();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // handle bundle arguments

            // take in lift id
            destText=getArguments().getString("destination");
            seatsVal=getArguments().getInt("seatsAvailable");
            idText=getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_request_lift);
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_request_lift, container, false);

        TextView infoText = (TextView) view.findViewById(R.id.lift_info);
        infoText.setText("Destination: "+destText+
                            "\nSeats Available: "+seatsVal+
                            "\nDriver: "+"driverText"+
                            "\nLift id: "+idText);


        Button reqButton = (Button) view.findViewById(R.id.requestButton);

        reqButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NavDrawer nd = (NavDrawer) getActivity();
                nd.replaceContent(ManageAccount.newInstance());

                // request the lift

                
            }
        });


        return view;
    }

    public boolean constructLift(String id, String destination, int seatsAvailable, User driver){
        requestedLift = new Lift(driver, destination, seatsAvailable, id);
        return true;
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
