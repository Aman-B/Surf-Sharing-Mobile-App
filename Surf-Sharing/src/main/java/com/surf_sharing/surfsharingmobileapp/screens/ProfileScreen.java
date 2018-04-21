    package com.surf_sharing.surfsharingmobileapp.screens;

    import android.app.Activity;
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
    import android.util.Base64;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.android.gms.maps.model.LatLng;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import com.surf_sharing.surfsharingmobileapp.MapsActivity;
    import com.surf_sharing.surfsharingmobileapp.R;
    import com.surf_sharing.surfsharingmobileapp.data.Database;
    import com.surf_sharing.surfsharingmobileapp.data.User;
    import com.surf_sharing.surfsharingmobileapp.utils.ImageHelper;
    import com.surf_sharing.surfsharingmobileapp.utils.RoundedImageView;

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
    private String userBio;
    private User profileUser;
    private boolean ownProfile;
    private View view;

    private ProgressDialog dialog;
    private AlertDialog.Builder alertDlg;

        private DatabaseReference userRoot;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
        //get the user id to access info from firebase
        userId = getArguments().getString("userId");
        Log.i("userId", userId);
    }

    alertDlg = new AlertDialog.Builder(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // change the title of the activity

    // Inflate the layout for this fragment
    if(userId == FirebaseAuth.getInstance().getCurrentUser().getUid()){
        Toast.makeText(getContext(), "OWN PROFILE", Toast.LENGTH_SHORT).show();

        view =  inflater.inflate(R.layout.user_content_profile, container, false);
        ownProfile = true;

    }
    else{
        Toast.makeText(getContext(), "OTHER'S PROFILE", Toast.LENGTH_SHORT).show();
        view =  inflater.inflate(R.layout.content_profile, container, false);
        ownProfile = false;

    }

    final TextView profileUserName = (TextView) view.findViewById(R.id.profileNameTextView);
    final TextView profileUserGender = (TextView) view.findViewById(R.id.profileGenderTextView);
    final TextView profileUserAge = (TextView) view.findViewById(R.id.profileAgeTextView);
    final TextView profileUserBio = (TextView) view.findViewById(R.id.bioTextView);
    final TextView profileUserEmail = (TextView) view.findViewById(R.id.emailTextView);

    //final ImageView imgView = (ImageView) view.findViewById(R.id.profile_image);

    final ImageView profileImageView = (ImageView) view.findViewById(R.id.profileImageView);

    dialog = new ProgressDialog(getActivity());

    dialog.setMessage("Loading...");

        if (!((Activity) getContext()).isFinishing()) {
            dialog.show();
        }

    dialog.setCanceledOnTouchOutside(false);

        userRoot = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRoot.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {

            try {


                userType = (String) snapshot.child("type").getValue();
                userName = (String) snapshot.child("name").getValue();
                userGender = (String) snapshot.child("gender").getValue();
                userEmail = (String) snapshot.child("email").getValue();
                userDob = (String) snapshot.child("age").getValue();
                userImage = (String) snapshot.child("image").getValue();
                userBio = (String) snapshot.child("bio").getValue();

                Log.d("profileUserName", userName);

                if(ownProfile){
                    getActivity().setTitle(R.string.title_profile);

                }else{
                    getActivity().setTitle(userName + "'s profile");

                }

                if (userImage != null && !userImage.equals("")) {
                    Log.d("profile Image", userImage);
                    DownloadImageTask downloadImageTask = new DownloadImageTask();
                    Bitmap userBitmap = downloadImageTask.execute(userImage).get();

                    profileImageView.setImageBitmap(userBitmap);
                    profileUserName.setText(userName);
                    profileUserGender.setText(userGender );
                    profileUserBio.setText(userBio);
                    profileUserEmail.setText(userEmail);

                }

                profileUser = new User(userId, userType, userEmail);
                profileUser.name = userName;
                profileUser.gender = userGender;

                if(userDob != null){
                    String userAge = getAge(userDob);
                    System.out.print("age is:" + userAge);
                    Log.i("profile age", userAge);
                    profileUserAge.setText(userAge.replaceAll("\\s+",""));

                }

                profileUserName.setText(userName);
                profileUserGender.setText(userGender );
                profileUserBio.setText(userBio);
                profileUserEmail.setText(userEmail);

                dialog.dismiss();


                //open google maps with the specified location
                /*locIcon.setOnClickListener(new View.OnClickListener() {
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
                        startActivity(intent);



                    }
                });*/
            }
            catch (Exception e)
            {

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
            Log.d("Image found", base64String);
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);

            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Log.d("Image conversion", "successful");

            return decodedByte;
        }
        else{
            Log.d("Image conversion", "failure");

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

