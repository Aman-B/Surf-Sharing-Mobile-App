package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.data.Lift;
import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.temp.Globals;
import com.surf_sharing.surfsharingmobileapp.data.LiftContainer;
import com.surf_sharing.surfsharingmobileapp.NavDrawer;
import com.surf_sharing.surfsharingmobileapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AvailableLifts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AvailableLifts extends Fragment {

    //Globals glob;
    public AvailableLifts() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AvailableLifts.
     */
    // retrieve info from database and display lifts
    public static AvailableLifts newInstance(/*LiftContainer lifts*/) {
        AvailableLifts fragment = new AvailableLifts();
        Bundle args = new Bundle();
        //args.putInt("", some);
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
        getActivity().setTitle(R.string.title_available_lift);
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_available_lifts, container, false);

        // populate list with lifts

        ListView liftList = (ListView) view.findViewById(R.id.liftList);
        ArrayList<Lift> lifts_list = Database.getAllLifts();
       // Lift[] lifts_list_array = lifts_list.toArray(new Lift[lifts_list.size()]);
       // if(lifts_list != null){

       // }
      //  else{
            //ArrayAdapter adapter = new ArrayAdapter(getActivity(), view.findViewById(R.id.liftList), lifts_list);
      //  }
        /*
            I AM CURRENTLY STILL WORKING ON THIS
         */
        //Object[] o = lifts_list.toArray(new Object[lifts_list.size()]);
        //String[] lifts = {"bray", "bundoran"};
        //User testDriver = new User(1, "driver", "x@gmail.com");
        //Lift l = new Lift(testDriver, "bray", 5, 1);
        //ArrayAdapter<Lift> adapter = new ArrayAdapter<String>(/*getActivity().getApplicationContext(), view.findViewById(R.id.liftList*/), lifts));
        //liftList.setAdapter(adapter);
        //liftList.getAdapter();

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
}
