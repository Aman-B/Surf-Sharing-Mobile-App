package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.surf_sharing.surfsharingmobileapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link UpgradeToDriver#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpgradeToDriver extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public UpgradeToDriver() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpgradeToDriver.
     */
    // TODO: Rename and change types and number of parameters
    public static UpgradeToDriver newInstance() {
        UpgradeToDriver fragment = new UpgradeToDriver();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upgrade_to_driver, container, false);





        Button submitButton = (Button) view.findViewById(R.id.submitDriverUpgrade);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText carMakeModelText = (EditText) view.findViewById(R.id.carMakeModelInput);
                String carMakeModel = carMakeModelText.getText().toString();

                final EditText carRegistrationText = (EditText) view.findViewById(R.id.registrationInput);
                String carRegistration = carRegistrationText.getText().toString();

                final EditText licenceNumberText = (EditText) view.findViewById(R.id.licenceNumberInput);
                String licenceNumber = licenceNumberText.getText().toString();

                final EditText maxPassengersText = (EditText) view.findViewById(R.id.maxPassengersInput);
                String maxPassengers = maxPassengersText.getText().toString();


                if (TextUtils.isEmpty(carMakeModel))
                {
                    Toast.makeText(getContext(), "Please enter a Car Make and Model", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(carRegistration))
                {
                    Toast.makeText(getContext(), "Please enter a Car Registration", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(licenceNumber))
                {
                    Toast.makeText(getContext(), "Please enter your Licence number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(maxPassengers))
                {
                    Toast.makeText(getContext(), "Please enter the Maximum number of Passengers you're willing to take", Toast.LENGTH_SHORT).show();
                    return;
                }




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
}
