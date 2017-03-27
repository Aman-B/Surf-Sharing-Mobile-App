package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.media.Image;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.LiftsYouAreOn;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * Use the {@link RequestResponse#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestResponse extends Fragment {

    String userId, userEmail, thisUserId, thisUserEmail;
    String liftId, liftDest, liftTime, liftDate;
    int liftSeats;

    User requestingUser, thisUser;
    Lift requestedLift;

    NavDrawer nd;

    public RequestResponse() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestResponse.
     */
    public static RequestResponse newInstance() {
        RequestResponse fragment = new RequestResponse();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            userEmail = getArguments().getString("u_email");
            liftId = getArguments().getString("liftId");
            liftDate = getArguments().getString("date");
            liftDest = getArguments().getString("l_dest");
            liftSeats = getArguments().getInt("l_seats");
            liftTime = getArguments().getString("time");

            requestingUser = new User(userId, "", userEmail);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            thisUserId = currentUser.getUid();
            //thisUserId = currentUser.toString();
            thisUserEmail = currentUser.getEmail();

            thisUser = new User(thisUserId, "", thisUserEmail);
            requestedLift = new Lift(thisUser, liftDest, liftSeats, liftId, liftTime, liftDate);

            nd = (NavDrawer) getActivity();

            //requestingUser = Database.getUser(userId);
            //requestedLift = Database.getLift(liftId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_response, container, false);

        TextView liftDestinationText = (TextView) view.findViewById(R.id.text_view_lift_name);
        String liftDestination = requestedLift.getDestination();
        liftDestinationText.setText(liftDestination);

        TextView liftDepartureDateText = (TextView) view.findViewById(R.id.text_view_lift_date);
        String liftDepartureDate = requestedLift.getDate();
        liftDepartureDateText.setText(liftDepartureDate);

        TextView liftDepartureTimeText = (TextView) view.findViewById(R.id.text_view_lift_time);
        String liftDepartureTime = requestedLift.getTime();
        liftDepartureTimeText.setText(liftDepartureTime);

        //ImageView userProfileImage = (ImageView) view.findViewById(R.id.user_profile_image);
        //Image userImage = requestingUser.getUserImage();

        TextView userNameText = (TextView) view.findViewById(R.id.text_view_user);
        String userName = requestingUser.getUserName();
        userNameText.setText(userName);


        Button viewProfileButton = (Button) view.findViewById(R.id.view_full_profile_btn);
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nd = (NavDrawer) getActivity();
                Fragment userProfileScreen = ProfileScreen.newInstance(requestingUser.getUserId());
                nd.replaceContent(userProfileScreen);

            }
        });

        Button rejectButton = (Button) view.findViewById(R.id.reject_btn);
        rejectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                notifyUser(requestingUser, false);

                nd = (NavDrawer) getActivity();
                Fragment liftsYouAreOfferingScreen = LiftsYouAreOffering.newInstance();

                nd.replaceContent(liftsYouAreOfferingScreen);

            }
        });


        Button acceptButton = (Button) view.findViewById(R.id.accept_btn);
        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                notifyUser(requestingUser, true);

                requestedLift.addPassenger(requestingUser);
                // TODO undo comment below
                //requestingUser.addLift(requestedLift);

                Database.setUserValue(requestingUser);
                //Database.setLiftValue(requestedLift); // Database function not implemented yet

            }
        });

        return view;
    }

    public void notifyUser(User requestingUser, boolean accepted) {

        if (accepted)
        {
                // Wherever the user has an entry representing their pending lift
                // request, change it to show that they are now on this lift and
                // send a notification to inform them.
                Database.acceptLiftRequest(liftId, userId);

        }
        else
        {
                // Wherever the user has an entry representing their pending lift
                // request, remove it to show that they have not secured a seat
                // on this lift and send a notification to inform them.
                Database.rejectLiftRequest(liftId, userId);

        }

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
