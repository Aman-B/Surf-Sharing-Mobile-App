package com.surf_sharing.surfsharingmobileapp.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private String userId;
    private String userType;
    private String userName;
    private String userEmail;
    private String userGender;
    private String userDob;
    private String userImage;
    private String userPhone;
    private String userBio;
    private User profileUser;
    private boolean ownProfile;
    private View view;

    private ProgressDialog dialog;



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
            ImageView userImageView = (ImageView) getView().findViewById(R.id.profileImageView);
            if(decodedByte !=null) {
                userImageView.setImageBitmap(decodedByte);
                Toast.makeText(getContext(), "image is set", Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(getContext(), "unable to set image", Toast.LENGTH_SHORT).show();
            }
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





        // Inflate the layout for this fragment
        if(userId == FirebaseAuth.getInstance().getCurrentUser().getUid()){
            Toast.makeText(getContext(), "OWN PROFILE", Toast.LENGTH_SHORT).show();


            view =  inflater.inflate(R.layout.fragment_profile, container, false);
            ownProfile = true;


        }
        else{
            Toast.makeText(getContext(), "OTHER'S PROFILE", Toast.LENGTH_SHORT).show();
            view =  inflater.inflate(R.layout.fragment_users_view_profile, container, false);
            ownProfile = false;

        }

        final TextView profileUserName = (TextView) view.findViewById(R.id.profileNameTextView);
        final TextView profileUserGender = (TextView) view.findViewById(R.id.profileGenderTextView);
        final TextView profileUserAge = (TextView) view.findViewById(R.id.profileAgeTextView);
        final TextView profileUserBio = (TextView) view.findViewById(R.id.bioTextView);

        dialog = new ProgressDialog(getActivity());

        dialog.setMessage("Loading...");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);


        Database.root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                try {
                    DataSnapshot userRef = snapshot.child("users").child(userId);

                    userType = (String) userRef.child("type").getValue();
                    userName = (String) userRef.child("name").getValue();
                    userGender = (String) userRef.child("gender").getValue();
                    userEmail = (String) userRef.child("email").getValue();
                    userDob = (String) userRef.child("age").getValue();
                    userImage = (String) userRef.child("image").getValue();
                    userPhone = (String) userRef.child("phone").getValue();
                    userBio = (String) userRef.child("bio").getValue();


                    Log.i("userName", userName);

                    if(ownProfile){
                        getActivity().setTitle(R.string.title_profile);

                    }else{
                        getActivity().setTitle(userName + "'s Profile");

                    }


                    if (userImage != null) {
                        Log.i("profile Image", userImage);
                        DownloadImageTask downloadImageTask = new DownloadImageTask();
                        downloadImageTask.execute(userImage);
                        //ImageView userImageView = (ImageView) getView().findViewById(R.id.profileImageView);
                        //  userImageView.setImageBitmap(bitmap);

                        // setUserImage(userImage);
                    }

                    profileUser = new User(userId, userType, userEmail);
                    profileUser.name = userName;
                    profileUser.gender = userGender;

                    if(userDob != null){
                        String userAge = getAge(userDob);
                        Log.i("profile age", userAge);
                        profileUserAge.setText(userAge);

                    }
                    profileUserName.setText(userName);
                    profileUserGender.setText(userGender + " ,");
                    profileUserBio.setText(userBio);


                }
                catch (Exception e)
                {

                    e.printStackTrace();
                }
            }
            @Override public void onCancelled(DatabaseError error) {
                Log.i("Database error", error.toString());
            }
        });



        if(!ownProfile){
            Button callButton = (Button) view.findViewById(R.id.callUserButton);

            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Uri number = Uri.parse("tel:" + userPhone);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }
            });

        }


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

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


//       ProgressDialog dialog = new ProgressDialog(getActivity());


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


//
//            dialog.setMessage("Loading...");
//            dialog.show();
//            dialog.setCanceledOnTouchOutside(false);


        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String base64String = params[0];
            Bitmap decodedByte = null;

            if(!base64String.equals("") | base64String !=null) {
                Log.i("Image found", base64String);
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Log.i("Image conversion", "successful");

                return decodedByte;
            }
            else{
                Log.i("Image conversion", "failure");

            }
            return decodedByte;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Bitmap bitmap) {
            if (dialog.isShowing()) dialog.dismiss();
            ImageView userImageView = (ImageView) getView().findViewById(R.id.profileImageView);
            userImageView.setImageBitmap(bitmap);


        }
    }

}

