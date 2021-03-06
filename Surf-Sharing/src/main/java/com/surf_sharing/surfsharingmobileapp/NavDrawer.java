package com.surf_sharing.surfsharingmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.screens.Home;
import com.surf_sharing.surfsharingmobileapp.screens.AvailableLifts;
import com.surf_sharing.surfsharingmobileapp.screens.LiftsYouAreOffering;
import com.surf_sharing.surfsharingmobileapp.screens.LiftsYouAreOn;
import com.surf_sharing.surfsharingmobileapp.screens.ManageAccount;
import com.surf_sharing.surfsharingmobileapp.screens.OfferLift;
import com.surf_sharing.surfsharingmobileapp.screens.ProfileScreen;
import com.surf_sharing.surfsharingmobileapp.screens.RequestLift;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Display Home as default fragment
        replaceContent(Home.newInstance());

        // listen for sign out event and go to login screen
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                    Display.popup(NavDrawer.this, "You have been logged out");
                    Intent intent = new Intent(NavDrawer.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String userType = (String) snapshot.child("type").getValue();
                if (userType != null && userType.equals("driver")) {
                    Menu menu = navigationView.getMenu();
                    menu.setGroupVisible(R.id.driver_nav_menu, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        switch (id) {
            case R.id.nav_home:
                fragment = Home.newInstance();
                break;
            case R.id.nav_available_lifts:
                fragment = AvailableLifts.newInstance();
                break;
            case R.id.nav_lifts_on:
                fragment = LiftsYouAreOn.newInstance();
                break;
            case R.id.nav_offer_lift:
                fragment = OfferLift.newInstance();
                break;
            case R.id.nav_lifts_offering:
                fragment = LiftsYouAreOffering.newInstance();
                break;
            case R.id.nav_profile:
                fragment = ProfileScreen.newInstance(mAuth.getCurrentUser().getUid());
                break;
            case R.id.nav_manage_account:
                fragment = ManageAccount.newInstance();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                break;
        }
        if (fragment != null) replaceContent(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Replace the main content with the passed fragment
     * @param fragment the fragment to be used as the new content
     */
    public void replaceContent(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_drawer_content, fragment)
                .addToBackStack(null)
                .commit();
    }

    //
    public void setupRequestLift(Fragment fragment, Lift lift){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle=new Bundle();

        bundle.putString("id", lift.id);
        bundle.putString("driverId", lift.driver.id);
        bundle.putString("driverName", lift.driver.name);
        bundle.putString("date", lift.date);
        bundle.putString("time", lift.time);
        bundle.putString("liftStr", lift.destination);
        bundle.putString("seatsStr", Integer.toString(lift.seatsAvailable));
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.nav_drawer_content, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void setupRequestResponse(Fragment fragment, String userId, String liftId,
                                     String l_user,
                                     int l_seats,
                                     String l_dest,
                                     String time,
                                     String date,
                                     String u_email){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle=new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("liftId", liftId);
        bundle.putString("l_user", l_user);
        bundle.putString("l_dest", l_dest);
        bundle.putString("time", time);
        bundle.putString("date", date);
        bundle.putString("u_email", u_email);

        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.nav_drawer_content, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void setupLiftInfo(Fragment fragment, String liftId) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("liftId", liftId);

        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.nav_drawer_content, fragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
