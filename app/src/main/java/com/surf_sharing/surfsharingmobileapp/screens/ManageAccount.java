package com.surf_sharing.surfsharingmobileapp.screens;

import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.temp.DatabaseTestActivity;
import static com.surf_sharing.surfsharingmobileapp.utils.Display.popup;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.data.Lift;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ManageAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageAccount extends Fragment {
    private DatabaseReference ref;

    public ManageAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ManageAccount.
     */
    public static ManageAccount newInstance() {
        ManageAccount fragment = new ManageAccount();
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
        getActivity().setTitle(R.string.title_manage_account);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);

        //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //User currentUser = Database.getCurrentUser();

        ///////////////////////////////////////////////////

        final EditText nameText = (EditText) view.findViewById(R.id.edit_text_name);
        final EditText genderText = (EditText) view.findViewById(R.id.edit_text_gender);
        final EditText ageText = (EditText) view.findViewById(R.id.edit_text_age);
        final EditText phoneText = (EditText) view.findViewById(R.id.edit_text_phone);
        final EditText emailText = (EditText) view.findViewById(R.id.edit_text_email);
        final EditText bioText = (EditText) view.findViewById(R.id.edit_text_bio);
        final TextView liftViewText = (TextView) view.findViewById(R.id.text_view_lifts);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null)
        {
            return null;
        }
        else {
            final String userId = currentUser.getUid();
            DatabaseReference usersRefChild = Database.userRoot.child(userId);

            final User user = new User(userId, "", "");

            usersRefChild.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = (String) dataSnapshot.child("name").getValue();
                    String age = (String) dataSnapshot.child("age").getValue();
                    String gender = (String) dataSnapshot.child("gender").getValue();
                    String email = (String) dataSnapshot.child("email").getValue();
                    String type = (String) dataSnapshot.child("type").getValue();
                    String phone = (String) dataSnapshot.child("phone").getValue();
                    String bio = (String) dataSnapshot.child("bio").getValue();

                    user.name = name;
                    user.age = age;
                    user.gender = gender;
                    user.email = email;
                    user.type = type;
                    user.phone = phone;
                    user.bio = bio;

                    final String userID = user.getUserId();
                    final String userType = user.getUserType();
                    String userName = user.getUserName();
                    String userGender = user.getUserGender();
                    String userAge = user.getUserAge();
                    String userPhone = user.getUserPhone();
                    String userEmail = user.getUserEmail();
                    String userBio = user.getUserBio();

                    //ArrayList<Lift> userLifts = user.getUserLifts();
                    String liftDetails = "";

/*
                    for (int index = 0; index < userLifts.size(); index++)
                    {
                        Lift currentLift = userLifts.get(index);
                        liftDetails = liftDetails + currentLift.toString() + "\n";
                    }*/

                    nameText.setText(userName);
                    genderText.setText(userGender);
                    ageText.setText(userAge);
                    phoneText.setText(userPhone);
                    emailText.setText(userEmail);
                    bioText.setText(userBio);
                    liftViewText.setText(liftDetails);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Button upgradeButton = (Button) view.findViewById(R.id.upgrade_btn);
            upgradeButton.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View view) {
                    // Begin process of upgrading the User Account
                   // to a Driver Account:

                   upgradeAccountToDriver();
               }
            });



            Button okButton = (Button) view.findViewById(R.id.ok_btn);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // post data to database:
                    // location to write to

                    final EditText newNameText = (EditText) view.findViewById(R.id.edit_text_name);
                    String newName = newNameText.getText().toString();

                    final EditText newGenderText = (EditText) view.findViewById(R.id.edit_text_gender);
                    String newGender = newGenderText.getText().toString();

                    final EditText newAgeText = (EditText) view.findViewById(R.id.edit_text_age);
                    String newAge = newAgeText.getText().toString();

                    final EditText newPhoneText = (EditText) view.findViewById(R.id.edit_text_phone);
                    String newPhone = newPhoneText.getText().toString();

                    final EditText newEmailText = (EditText) view.findViewById(R.id.edit_text_email);
                    String newEmail = newEmailText.getText().toString();

                    final EditText newBioText = (EditText) view.findViewById(R.id.edit_text_bio);
                    String newBio = newBioText.getText().toString();

                    final TextView newLiftViewText = (TextView) view.findViewById(R.id.text_view_lifts);
                    String newLiftDetails = newLiftViewText.getText().toString();

                    User updatedUser = new User(userId, user.type, newEmail);

                    Database.setUserValue(updatedUser);

                    /*ref = database.getReference("user_test/" + userID);

                    // set (overwrite) the data at ref location
                    ref.setValue(new User(), new DatabaseReference.CompletionListener() {
                        @Override
                        // close spinner
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {
                                System.out.println("Added!");
                            } else {
                                System.out.println("Failed: " + databaseError.getDetails());
                            }
                        }
                    });*/
                }
            });

            Button cancelButton = (Button) view.findViewById(R.id.cancel_btn);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                // Cancel any changes made to the Account and go back
                // to previous fragment

                @Override
                public void onClick(View view) {

                    //FragmentManager fm = getActivity().getSupportFragmentManager();
                    //fm.popBackStack();
                }
            });

        }


        /////////////////////////////////////////////////////



        if (currentUser == null) {
            popup(new AppCompatActivity(), "Please sign in to use this feature");
        } else {
/*
            final String userID = currentUser.getUserId();
            final String userType = currentUser.getUserType();
            String userName = currentUser.getUserName();
            String userGender = currentUser.getUserGender();
            String userAge = currentUser.getUserAge();
            String userPhone = currentUser.getUserPhone();
            String userEmail = currentUser.getUserEmail();
            String userBio = currentUser.getUserBio();

            ArrayList<Lift> userLifts = currentUser.getUserLifts();
            String liftDetails = "";

            for (int index = 0; index < userLifts.size(); index++)
            {
                Lift currentLift = userLifts.get(index);
                liftDetails = liftDetails + currentLift.toString() + "\n";
            }


            EditText nameText = (EditText) view.findViewById(R.id.edit_text_name);
            nameText.setText(userName);

            EditText genderText = (EditText) view.findViewById(R.id.edit_text_gender);
            genderText.setText(userGender);

            EditText ageText = (EditText) view.findViewById(R.id.edit_text_age);
            ageText.setText(userAge);

            EditText phoneText = (EditText) view.findViewById(R.id.edit_text_phone);
            phoneText.setText(userPhone);

            EditText emailText = (EditText) view.findViewById(R.id.edit_text_email);
            emailText.setText(userEmail);

            EditText bioText = (EditText) view.findViewById(R.id.edit_text_bio);
            bioText.setText(userBio);

            TextView liftViewText = (TextView) view.findViewById(R.id.text_view_lifts);
            liftViewText.setText(liftDetails);

*/


        }

        return view;
    }

    /**
     * Initiate process of upgrading User Account to
     *  Driver Account.
     *
     */
    public void upgradeAccountToDriver() {

        Fragment upgradeToDriver = UpgradeToDriver.newInstance();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                        .replace(R.id.nav_drawer_content, upgradeToDriver)
                        .addToBackStack(null)
                        .commit();

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
