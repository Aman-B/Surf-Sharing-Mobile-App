package com.surf_sharing.surfsharingmobileapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

import java.io.File;
import java.util.UUID;

public class SubmitLicenseActivity extends AppCompatActivity {


    private Button chooseImageBtn;
    private ImageView licenseImageView;
    private Bitmap bitmap;
    private Uri selectedImage;
    private UploadTask uploadTask;

    ProgressDialog mProgressDialog;

    boolean isImageUploaded =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_license);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        licenseImageView = (ImageView) findViewById(R.id.licenseImageView);

        chooseImageBtn = (Button) findViewById(R.id.chooseImage);

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserPhoto();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Uploading Licence...");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_submit_license, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_settings:
            if(bitmap!=null)
            {
                //bitmap exists upload to firebase
                //TODO : upload on firebase here using filepath.
                mProgressDialog.show();
                submitImageToFirebase();

            }
            else
            {
                //TODO : tell user no image selected
                Display.popup(SubmitLicenseActivity.this,"No image selected.");
            }
                break;

            case android.R.id.home:
            {
                finish();

            }
                break;
        }


        return  true;
    }

    private void submitImageToFirebase() {
        //128-bit alphnumeric used as name for the image
        UUID uuid = UUID.randomUUID() ;


        String imagePath = getPath(getApplicationContext(),selectedImage);
        Uri imageFile = Uri.fromFile(new File(imagePath));

       // Display.popup(SubmitLicenseActivity.this,"Path "+imagePath + "128 string "+uuid);

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference licenseImageRef= storageRef.child(getString(R.string.licence_image_node_firebase)+"/"+uuid.toString()+imagePath.substring(imagePath.lastIndexOf(".")));




        uploadTask = licenseImageRef.putFile(imageFile);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadImageUri = taskSnapshot.getDownloadUrl();
                updateImgaeURLInFirebase(downloadImageUri);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();


                Display.popup(SubmitLicenseActivity.this, "Some error occurred. Please try again!");

            }
        });

    }

    private void updateImgaeURLInFirebase(Uri downloadImageUri) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();
        Database.userRoot.child(userID).child("licenceImage").setValue(downloadImageUri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Display.popup(SubmitLicenseActivity.this, "Licence updated successfully!");
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Display.popup(SubmitLicenseActivity.this, "Some error occurred. Please try again!");

            }
        });


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

    //allow the user to upload their photo when the user clicks on the imageView
    public void addUserPhoto(){

        //  ImageView userImageView = (ImageView) view.findViewById(R.id.userImageView);

        //first ask the user to access photo folder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(SubmitLicenseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

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
    //When the user is done with the subsequent activity and returns, the system calls your activity's onActivityResult() method.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //data!=null to make sure the user has not cancelled the activity in which case do not
        //dispaly image
        //URI bit like URL for image/resources
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){

            selectedImage = data.getData();



            try {
                 //convert image to bitmap
                 bitmap = MediaStore.Images.Media.getBitmap(SubmitLicenseActivity.this.getContentResolver(), selectedImage);

                 licenseImageView.setImageBitmap(bitmap);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

}
