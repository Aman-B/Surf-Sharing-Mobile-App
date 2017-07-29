package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.User;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ProfileScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileScreen extends Fragment {

    String userId;
    String userType;
    String userName;
    String userEmail;
    String userGender;
    String userDob;
    String userImage;
    String userBio;
    User profileUser;

    public ProfileScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AvailableLifts.
     */
    public static ProfileScreen newInstance(String userId) {
        ProfileScreen fragment = new ProfileScreen();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setUserImage(String base64String){

        if(!base64String.equals("") | base64String !=null) {
            Log.i("Image found", base64String);
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView userImageView = (ImageView) getActivity().findViewById(R.id.profileImageView);
            userImageView.setImageBitmap(decodedByte);
        }
        else{
            Log.i("Image NOT FOUND", "");

        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //get the user id to access info from firebase
            userId = getArguments().getString("userId");
            Log.i("userId", userId);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change the title of the activity
        getActivity().setTitle(R.string.title_profile);
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);


        final TextView profileUserName = (TextView) view.findViewById(R.id.profileNameTextView);
        final TextView profileUserGender = (TextView) view.findViewById(R.id.profileGenderTextView);
        final TextView profileUserAge = (TextView) view.findViewById(R.id.profileAgeTextView);


        Database.root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                try
                {
                    DataSnapshot userRef = snapshot.child("users").child(userId);

                    userType = (String) userRef.child("type").getValue();
                    userName = (String) userRef.child("name").getValue();
                    userGender = (String) userRef.child("gender").getValue();
                    userEmail = (String) userRef.child("email").getValue();
                    userDob = (String)  userRef.child("age").getValue();
                    userImage = (String) userRef.child("image").getValue();




                    profileUser = new User(userId, userType, userEmail);
                    profileUser.name = userName;
                    profileUser.gender = userGender;

                    String userAge = getAge(userDob);
                    Log.i("profile name", userName);
                    Log.i("profile age", userAge);
                    Log.i("profile gender", userName);
                    profileUserName.setText(userName );
                    profileUserGender.setText(userGender + " ,");
                    profileUserAge.setText(userAge);
//                    if(userImage != null) {
//                        Log.i("profile Image", userImage);
                        setUserImage(userImage);
//                    }
//                    else{
//                        Log.i("profile Image", userImage);
//
//                    }
                }
                catch (Exception e)
                {

                    e.printStackTrace();
                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        });


        // example of a button that replaces the current fragment with another
        /*Button testButton = (Button) view.findViewById(R.id.offer_lift_test_button);
        testButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NavDrawer nd = (NavDrawer) getActivity();
                nd.replaceContent(ManageAccount.newInstance());
            }
        });*/

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

    private String getAge(String dateOfBirth){

        String[] parts = dateOfBirth.split("/");

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
