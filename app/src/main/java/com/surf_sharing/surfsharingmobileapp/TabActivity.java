package com.surf_sharing.surfsharingmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.screens.AvailableLifts;
import com.surf_sharing.surfsharingmobileapp.screens.LiftsYouAreOn;
import com.surf_sharing.surfsharingmobileapp.screens.NewSelfProfileScreen;
import com.surf_sharing.surfsharingmobileapp.screens.OfferLift;
import com.surf_sharing.surfsharingmobileapp.utils.Display;

public class TabActivity extends AppCompatActivity implements BackPressedInFragmentVisibleOnTopOfViewPager {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String USER_TYPE="passenger";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar toolbar;
    private TabLayout tabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //read user type first, then set layout
        if(getIntent().getStringExtra("userType")!=null)
        {
            USER_TYPE= getIntent().getStringExtra("userType");
            Display.popup(this, "User type "+USER_TYPE);
        }

        setContentView(R.layout.activity_tab);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        tabLayout = (TabLayout)findViewById(R.id.mytablayout);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(),R.color.colorWhite));
        tabLayout.setTabTextColors(ContextCompat.getColor(getApplicationContext(),R.color.colorLightGrey),
                ContextCompat.getColor(getApplicationContext(),R.color.colorWhite));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /*if(USER_TYPE.equals("driver"))
                {
                    switch (position)
                    {
                        case 0:
                            toolbar.setTitle("Offer Lift");
                            break;

                        case 1:
                            toolbar.setTitle("Lifts You Are Offering");
                            break;
                        case 2:
                            toolbar.setTitle("Profile");
                            break;
                    }
                }
                else
                {
                    switch (position)
                    {
                        case 0:
                            toolbar.setTitle("Available Lifts");
                            break;

                        case 1:
                            toolbar.setTitle("Lifts You Are On");
                            break;

                        case 2:
                            toolbar.setTitle("Profile");
                            break;
                    }
                }*/
            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(getApplicationContext(),"Type "+USER_TYPE,Toast.LENGTH_SHORT).show();

                if(USER_TYPE.equals("driver"))
                {
                    switch (position)
                    {
                        case 0:
                            toolbar.setTitle("Offer Lift");

                            break;

                        case 1:
                            toolbar.setTitle("Lifts You Are Offering");
                            break;
                        case 2:
                            toolbar.setTitle("Profile");
                            break;
                    }
                }
                else
                {
                    switch (position)
                    {
                        case 0:
                            toolbar.setTitle("Available Lifts");
                            break;

                        case 1:
                            toolbar.setTitle("Lifts You Are On");
                            break;

                        case 2:
                            toolbar.setTitle("Profile");
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //fireabse user
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
                    Display.popup(TabActivity.this, "You have been logged out");
                    Intent intent = new Intent(TabActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference usersRef = Database.userRoot.child(currentUser.getUid());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String userType = (String) snapshot.child("type").getValue();
                if (userType != null && userType.equals("driver")) {
                    USER_TYPE="driver";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setCurrentItem (int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
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

    public void replaceContent(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainframe, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressedInFragmentVisibleOnTopOfViewPager(boolean isLastFragment,String toolbarTitle) {
        //hide the overlapping fragment and show main fragment from viewpager.
        if(isLastFragment)
        {
            mViewPager.setVisibility(View.VISIBLE);
            this.setTitle(toolbarTitle);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        }

    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(USER_TYPE.equals("driver"))
            {
                switch (position)
                {

                    case 0:
                        return AvailableLifts.newInstance();

                    case 1:
                        return LiftsYouAreOn.newInstance();

                    case 2:
                        return NewSelfProfileScreen.newInstance(mAuth.getCurrentUser().getUid(),USER_TYPE);

                    default:
                        return OfferLift.newInstance();

                }
            }
            else
            {
                //if passenger show different tab
                switch (position)
                {
                    case 0:
                        return AvailableLifts.newInstance();

                    case 1:
                        return LiftsYouAreOn.newInstance();

                    case 2:
                        return NewSelfProfileScreen.newInstance(mAuth.getCurrentUser().getUid(), USER_TYPE);

                    default:
                        return AvailableLifts.newInstance();


                }
            }

            /*switch (position) {
                case 0:
                    return Home.newInstance();
                    break;


                case 5:
                    return ProfileScreen.newInstance(mAuth.getCurrentUser().getUid());
                    break;
                case 6:
                    return ManageAccount.newInstance();
                    break;
                *//*case 7:
                    FirebaseAuth.getInstance().signOut();
                    break;*//*
                default:
                    return PlaceholderFragment.newInstance(position + 1);
                    break;
            }*/

           //default screen

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
           if(USER_TYPE.equals("driver"))
           {
               switch (position)
               {
                   case 0:

                       return "Available Lifts";

                   case 1:

                       return "Lifts You Are On";

                   case 2:
                       return "Profile";

               }
           }
           else
           {
               switch (position)
               {
                   case 0:
                       return "Available Lifts";

                   case 1:
                       return "Lifts You Are On";

                   case 2:
                       return "Profile";
               }
           }

            /*switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }*/
            return null;
        }
    }


    //methods which were predefined
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



        mViewPager.setVisibility(View.INVISIBLE);

        fragmentManager.beginTransaction()
                .replace(R.id.mainframe, fragment)
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
        if(mViewPager.getVisibility()== View.VISIBLE)
            mViewPager.setVisibility(View.INVISIBLE);

        fragmentManager.beginTransaction()
                .replace(R.id.mainframe, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void showThisFragmentOnTop(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(mViewPager.getVisibility()== View.VISIBLE)
        mViewPager.setVisibility(View.INVISIBLE);

        fragmentManager.beginTransaction()
                .replace(R.id.mainframe, fragment)
                .addToBackStack(null)
                .commit();
    }


}
