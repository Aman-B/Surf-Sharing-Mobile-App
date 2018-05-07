package com.surf_sharing.surfsharingmobileapp.screens;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.BackPressedInFragmentVisibleOnTopOfViewPager;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ManageAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageAccount extends Fragment {

    private DatabaseReference ref;
    private String nameInput, genderInput, dobInput, phoneInput, emailInput, image, bioInput, adrInput;
    public static String userLastSavedImage;


    //handle back button and show previous fragment (profile screen)
    private BackPressedInFragmentVisibleOnTopOfViewPager mBackPressedInFragmentVisibleOnTopOfViewPager;
    //get the state of image.
    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("userLastImage", (Serializable) userLastSavedImage);

        Log.i("saved Instance", "success");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //probably orientation change
            userLastSavedImage = (String) savedInstanceState.getSerializable("userLastImage");
            ImageView imgView = (ImageView) getActivity().findViewById(R.id.profileImageView);
            //setUserImage(userLastSavedImage);
            Toast.makeText(getActivity(), "SAVED IMAGE INSTANCE RESTORED", Toast.LENGTH_LONG).show();
        }
        else{
            userLastSavedImage = "";
        }



    }




    //get the photo from the gallery
    public void getPhoto(){

        Log.i("here", "here");
        //starts a new activity where action is picking media images
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //expecting a result, give a request code
        startActivityForResult(intent, 1);

    }

    //gallery permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 ){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                getPhoto();

            }


        }
    }



    //When the user is done with the subsequent activity and returns, the system calls your activity's onActivityResult() method.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //data!=null to make sure the user has not cancelled the activity in which case do not
        //dispaly image
        //URI bit like URL for image/resources
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();
            //convert to a bitmap image
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                //take bitmap and upload to the server
                //will allow us to convert image into a parse file
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //compress bitmap into PNG format
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //convert stream to byteArray
                byte[] byteArray = stream.toByteArray();

                //add to firebase as an encoded string

                userLastSavedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.i("image", userLastSavedImage);
                ImageView userImageView = (ImageView) getActivity().findViewById(R.id.profileImageView);

                userImageView.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    public void setUserImage(String base64String){

        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
        ImageView userImageView = (ImageView) getActivity().findViewById(R.id.profileImageView);
        userImageView.setImageBitmap(decodedByte);


    }


    public String convertImageViewToString() {

        String img_str = "";
        ImageView imgView = (ImageView) getActivity().findViewById(R.id.profileImageView);
        Drawable drawable = imgView.getDrawable();
        imgView.setImageDrawable(drawable);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), );

//        if (bitmap != null) {
//
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//
//            byte[] imageByteArray = stream.toByteArray();
//            System.out.println("byte array:" + imageByteArray);
//            img_str = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
//            System.out.println("string:" + img_str);
//            return img_str;
//        }


        return img_str;
    }




//    //allow the user to upload their photo when the user clicks on the imageView
    public void addUserPhoto(View view){

      //  ImageView userImageView = (ImageView) view.findViewById(R.id.userImageView);

        //first ask the user to access photo folder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
            else{
                getPhoto();
            }
        }

        else{
            //permission has already been granted
            getPhoto();
        }

    }





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


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final TextView nameText = (TextView) view.findViewById(R.id.text_view_name);
        final TextView genderText = (TextView) view.findViewById(R.id.text_view_gender);
        final TextView ageText = (TextView) view.findViewById(R.id.text_view_age);
        final TextView phoneText = (TextView) view.findViewById(R.id.text_view_phone);
        final TextView emailText = (TextView) view.findViewById(R.id.text_view_email);
        final TextView bioText = (TextView) view.findViewById(R.id.text_view_bio);
        final TextView adrText = (TextView) view.findViewById(R.id.textViewAddress);
        //final TextView bioText = (TextView) tempView.findViewById(R.id.text_view_bio);

        if (currentUser == null)
        {
            return null;
        }
        else
        {
            final String userId = currentUser.getUid();
            DatabaseReference usersRefChild = Database.userRoot.child(userId);

            final User user = new User(userId, "", "");

            usersRefChild.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Log.i("datasnaphshot count", Long.toString(dataSnapshot.getChildrenCount()));


                    Map<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();

                    ArrayList<Object> values =  new ArrayList<Object>(td.values());

                    for(Object value:values){
                        Log.i("value", value.toString());
                    }

                    String name = (String) dataSnapshot.child("name").getValue();
                    String age = (String) dataSnapshot.child("age").getValue();
                    String gender = (String) dataSnapshot.child("gender").getValue();
                    String email = (String) dataSnapshot.child("email").getValue();
                    String type = (String) dataSnapshot.child("type").getValue();
                    String phone = (String) dataSnapshot.child("phone").getValue();
                    String bio = (String) dataSnapshot.child("bio").getValue();
                    String img64 = (String) dataSnapshot.child("image").getValue();
                    String adr = (String) dataSnapshot.child("address").getValue();


                    if(img64 != null) {
                        if (img64.equals("")) {
                            Log.i("image from firebase:", "nothing");
                        } else {
                            Log.i("image from firebase:", img64);

                        }
                    }

                    user.name = name;
                    user.age = age;
                    user.gender = gender;
                    user.email = email;
                    user.type = type;
                    user.phone = phone;
                    user.image = img64;
                    user.bio = bio;
                    user.address = adr;

                    final String userID = user.getUserId();
                    final String userType = user.getUserType();
                    String userName = user.getUserName();
                    String userGender = user.getUserGender();
                    String userAge = user.getUserAge();
                    String userPhone = user.getUserPhone();
                    String userEmail = user.getUserEmail();
                    String userImage = user.getUserImage();
                    String userBio = user.getUserBio();
                    String userAdr = user.getUserAdr();
                    //ArrayList<Lift> userLifts = user.getUserLifts();
                    String liftDetails = "";


                    nameText.setText(userName + "");
                    genderText.setText(userGender + "");
                    ageText.setText(userAge + "");
                    phoneText.setText(userPhone + "");
                    emailText.setText(userEmail + "");
                    adrText.setText(userAdr + "");
                    bioText.setText(userBio);


                    if(userImage != null) {
                        //set user's image
                        if (!userImage.equals("")) {

//                        byte[] data = userImage.getBytes();
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        ImageView userImageView = (ImageView) getActivity().findViewById(R.id.imageView);
//                        userImageView.setImageBitmap(bitmap);

                            setUserImage(userImage);
                            userLastSavedImage = userImage;


                            Toast.makeText(getActivity(), "image restored from firebase", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "image NOT restored from firebase", Toast.LENGTH_LONG).show();

                        }
                    }

                    //bioText.setText(userBio + "");
                }






                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /*Button upgradeButton = (Button) view.findViewById(R.id.upgrade_btn);
            upgradeButton.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View view) {
                    // Begin process of upgrading the User Account
                   // to a Driver Account:

                   upgradeAccountToDriver();
               }
            });*/



            nameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("name");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nameInput = input.getText().toString();
                            nameText.setText(nameInput);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            });



            bioText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("bio");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bioInput = input.getText().toString();
                            bioText.setText(bioInput);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                }
            });


            adrText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("address");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adrInput = input.getText().toString();
                            adrText.setText(adrInput);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });


            genderText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Gender");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            genderInput = input.getText().toString();
                            genderText.setText(genderInput);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            });




            ageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar mcurrentDate = Calendar.getInstance();

                    int dayOfMonth = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                    int month = mcurrentDate.get(Calendar.MONTH);

                    int year = mcurrentDate.get(Calendar.YEAR);

                    DatePickerDialog mDatePicker;

                    mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                        @Override

                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            ageText.setText(String.format("%02d/%02d/%04d", dayOfMonth, month, year));

                        }

                    }, year, month, dayOfMonth);

                    mDatePicker.show();
                }
            });




            phoneText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Name");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            phoneInput = input.getText().toString();
                            phoneText.setText(phoneInput);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });





            emailText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Name");

                    // Set up the input
                    final EditText input = new EditText(getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            emailInput = input.getText().toString();
                            emailText.setText(emailInput);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });




//


            ImageView profileImgView = (ImageView) view.findViewById(R.id.profileImageView);
            profileImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUserPhoto(view);
                }
            });


//            Button addPhotoButton = (Button) view.findViewById(R.id.addPhotoButton);
//            addPhotoButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.i("Button has been ","clicked");
//
//                    //first ask the user to access photo folder
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                 //   getPhoto();
//                    addUserPhoto(getView());
//                           // requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
////                        } else {
////                            getPhoto();
////                        }
////                    } else {
////                        //permission has already been granted
////                        getPhoto();
////                    }
//
//
//                }
//            });

            Button okButton = (Button) view.findViewById(R.id.ok_btn);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // post data to database:
                    // location to write to

                    String newName = nameText.getText().toString();

                    String newGender = genderText.getText().toString();

                    String newAge = ageText.getText().toString();

                    String newPhone = phoneText.getText().toString();

                    String newEmail = emailText.getText().toString();

                    String newBio = bioText.getText().toString();

                    String newAdr = adrText.getText().toString();

                    User updatedUser = new User(userId, user.type, newEmail);
                    updatedUser.setName(newName);
                    updatedUser.setGender(newGender);
                    updatedUser.setAge(newAge);
                    updatedUser.setPhone(newPhone);
                    updatedUser.setBio(newBio);
                    updatedUser.setAddress(newAdr);
                    //set to latest image set

                    Log.i("new Image", userLastSavedImage);
                    updatedUser.setImage(userLastSavedImage);

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

                    //update done in firebase; detach.
                    onDetach();

                }
            });

            Button cancelButton = (Button) view.findViewById(R.id.cancel_btn);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                // Cancel any changes made to the Account and go back
                // to previous fragment

                @Override
                public void onClick(View view) {

                    //canceled by user; detach.
                    onDetach();
                }
            });

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
        mBackPressedInFragmentVisibleOnTopOfViewPager =(BackPressedInFragmentVisibleOnTopOfViewPager)getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(!getActivity().isFinishing())
        {
            boolean isLastFragment= true;
            mBackPressedInFragmentVisibleOnTopOfViewPager.onBackPressedInFragmentVisibleOnTopOfViewPager(isLastFragment,"Profile");


            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }

    }



}
