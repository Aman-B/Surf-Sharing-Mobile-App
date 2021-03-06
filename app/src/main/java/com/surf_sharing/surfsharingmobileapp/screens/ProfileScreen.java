    package com.surf_sharing.surfsharingmobileapp.screens;

    import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.BackPressedInFragmentVisibleOnTopOfViewPager;
import com.surf_sharing.surfsharingmobileapp.MapsActivity;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

// import com.pkmmte.view.CircularImageView;

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
    private String userAdr;
    private String userLoc;
    private User profileUser;
    private boolean ownProfile;
    private View view;

    private ProgressDialog dialog;
    private AlertDialog.Builder alertDlg;


    private BackPressedInFragmentVisibleOnTopOfViewPager mOnBackPressedInFragmentVisibleOnTopOfViewPager;


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

    alertDlg = new AlertDialog.Builder(getContext());
    setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // change the title of the activity

        //for back arrow on top [START]
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);


        //for back arrow on top [END]





    // Inflate the layout for this fragment
    if(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
        Toast.makeText(getContext(), "OWN PROFILE", Toast.LENGTH_SHORT).show();


        view =  inflater.inflate(R.layout.user_content_profile, container, false);
        ownProfile = true;


    }
    else{
        Toast.makeText(getContext(), "OTHER'S PROFILE", Toast.LENGTH_SHORT).show();
        view =  inflater.inflate(R.layout.user_content_profile, container, false);
        ownProfile = false;

    }




    final TextView profileUserName = (TextView) view.findViewById(R.id.profileNameTextView);
    final TextView profileUserGender = (TextView) view.findViewById(R.id.profileGenderTextView);
    final TextView profileUserAge = (TextView) view.findViewById(R.id.profileAgeTextView);
    final TextView profileUserBio = (TextView) view.findViewById(R.id.bioTextView);
    final TextView profileUserEmail = (TextView) view.findViewById(R.id.emailTextView);
    final TextView profileUserPhone = (TextView) view.findViewById(R.id.phoneTextView);
    final TextView profileUserAdr = (TextView) view.findViewById(R.id.locationTextView);

    //final ImageView imgView = (ImageView) view.findViewById(R.id.profile_image);


    final ImageView profileImageView = (ImageView) view.findViewById(R.id.profileImageView);
    final ImageView phoneIcon = (ImageView) view.findViewById(R.id.ivPhone);
    final ImageView emailIcon = (ImageView) view.findViewById(R.id.ivEmail);
    final ImageView locIcon = (ImageView) view.findViewById(R.id.ivLocation);




    dialog = new ProgressDialog(getActivity());

    dialog.setMessage("Loading...");
    dialog.show();
    dialog.setCanceledOnTouchOutside(true);


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
                userAdr = (String) userRef.child("address").getValue();

                Log.i("userName", userName);


                if(ownProfile){
                    getActivity().setTitle(R.string.title_profile);

                }else{
                    getActivity().setTitle(userName + "'s profile");

                }


                if (userImage != null && !userImage.equals("")) {

                    Log.i("profile Image", userImage);
                    DownloadImageTask downloadImageTask = new DownloadImageTask();
                    Bitmap userBitmap = downloadImageTask.execute(userImage).get();

                    //set the image obtained from ImageView
                    //Bitmap roundedBitmap = ImageHelper.getRoundedCornerBitmap(userBitmap, 100);
                   // Bitmap roundedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cliffs_of_moher);

                  //  profileImageView.setImageBitmap(roundedBitmap);


//                    RoundedImageView roundedImageView = new RoundedImageView(userBitmap);
//                    profileImageView.setImageDrawable(roundedImageView);

                    profileImageView.setImageBitmap(userBitmap);
                //    imgView.setImageBitmap(userBitmap);
                    //ImageView userImageView = (ImageView) getView().findViewById(R.id.profileImageView);
                    //  userImageView.setImageBitmap(bitmap);

                    // setUserImage(userImage);
                }

                profileUser = new User(userId, userType, userEmail);
                profileUser.name = userName;
                profileUser.gender = userGender;

                if(userDob != null ){
                    if(!userDob.equals("")){

                        String userAge = getAge(userDob);
                        System.out.print("age is:" + userAge);
                        Log.i("profile age", userAge);
                        profileUserAge.setText(userAge.replaceAll("\\s+",""));
                    }
                    profileUserAge.setText("");


                }
                //Toast.makeText(getContext(),"Here here"+userAdr,Toast.LENGTH_SHORT).show();
                profileUserName.setText(userName);
                profileUserGender.setText(userGender );
                profileUserBio.setText(userBio);
                profileUserEmail.setText(userEmail);
                profileUserPhone.setText(userPhone);
                profileUserAdr.setText(userAdr);


                //open the dial when the phone icon is clicked
                phoneIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("phone icon", "working");
                        Uri number = Uri.parse("tel:" + userPhone);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(callIntent);
                    }
                });


                //open email application to send email to the users who profile was being viewed
                emailIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("email icon", "working");
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { userEmail });
                        startActivity(Intent.createChooser(intent, ""));
                        startActivity(Intent.createChooser(intent, ""));



                    }
                });


                //open google maps with the specified location
                locIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.i("loc icon", "working");
                                double longitude=0.0;
                                double latitude = 0.0;
                                Geocoder coder = new Geocoder(getContext());
                                try {
                                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(userAdr, 1);
                                    for(Address add : adresses){
                                        if (add !=null) {//Controls to ensure it is right address such as country etc.
                                            longitude = add.getLongitude();
                                            latitude = add.getLatitude();

                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Address not found", Toast.LENGTH_SHORT).show();
                                }

                        Log.i("long", String.valueOf(longitude));
                        Log.i("lat", String.valueOf(latitude));

                       LatLng latLng = new LatLng(latitude, longitude);
                       Intent intent = new Intent(getContext(), MapsActivity.class);
                       intent.putExtra("LatLng", latLng);
                       intent.putExtra("adr", userAdr);
                       startActivity(intent);



                    }
                });
            //    Toast.makeText(getContext(),"Here hersdfjkjkse"+userAdr,Toast.LENGTH_SHORT).show();

                if (dialog.isShowing()) dialog.dismiss();

            }
            catch (Exception e)
            {
               // Toast.makeText(getContext(),"Here here errorororor"+userAdr,Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        }
        @Override public void onCancelled(DatabaseError error) {
            Log.i("Database error", error.toString());
            if (dialog.isShowing()) dialog.dismiss();

            alertDlg.setTitle("Something went wrong");
            alertDlg.setMessage("Could not retrieve information from database, please check your internet connection.");
            alertDlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDlg.show();
        }
    });




    return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnBackPressedInFragmentVisibleOnTopOfViewPager =(BackPressedInFragmentVisibleOnTopOfViewPager)getActivity();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(!getActivity().isFinishing())
        {
            //not the last fragment to show on top so pass false;
            boolean isLastFragment=false;
            mOnBackPressedInFragmentVisibleOnTopOfViewPager.onBackPressedInFragmentVisibleOnTopOfViewPager(isLastFragment,"");
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }





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
    String ageS = ageInt.toString().trim();

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



    }
    }

    }

