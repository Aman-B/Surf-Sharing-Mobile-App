package com.surf_sharing.surfsharingmobileapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.service.textservice.SpellCheckerService;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Database;
import com.surf_sharing.surfsharingmobileapp.utils.BackgroundMail;
import com.surf_sharing.surfsharingmobileapp.utils.SendMailActivity;
import com.surf_sharing.surfsharingmobileapp.utils.SendMailTask;
import com.surf_sharing.surfsharingmobileapp.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static java.security.AccessController.getContext;

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


        final EditText carMakeModelText = (EditText) view.findViewById(R.id.carMakeModelInput);
        final EditText carRegistrationText = (EditText) view.findViewById(R.id.registrationInput);
        final EditText licenceNumberText = (EditText) view.findViewById(R.id.licenceNumberInput);
        final EditText maxPassengersText = (EditText) view.findViewById(R.id.maxPassengersInput);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String carMakeModel = carMakeModelText.getText().toString();
                String carRegistration = carRegistrationText.getText().toString();
                String licenceNumber = licenceNumberText.getText().toString();
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

                Toast.makeText(getContext(), "submited", Toast.LENGTH_SHORT).show();


                Database.upgradeToDriver(carMakeModel, carRegistration, licenceNumber, maxPassengers);



                //send email with the driver's detail to verify legitimacy


                try {
                    Log.i("SendMailActivity", "Send Button Clicked.");

                    String fromEmail = "your@gmail.com";
                    String fromPassword = "yourPassword";
                    String toEmails = "ammarqureshi1995@gmail.com, agnewl@tcd.ie";
                    List toEmailList = Arrays.asList(toEmails
                            .split("\\s*,\\s*"));
                    Log.i("SendMailActivity", "To List: " + toEmailList);
                    String emailSubject = "NEED DRIVER VERIFICATION FOR ID:" + FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String emailBody =
                            "car make model: " + carMakeModel + "\n" +
                                    "\n\ncar registration: " + carRegistration + "\n" +
                                    "\n\nlicence number: " + licenceNumber + "\n" +
                                    "\n\nmax passengers:" + maxPassengers;
                    new com.surf_sharing.surfsharingmobileapp.utils.SendMailTask(getActivity()).execute(fromEmail,
                            fromPassword, toEmailList, emailSubject, emailBody);
                }
                catch(Exception e){
                    e.printStackTrace();
                }



                //go to LiftsYouAreOffering fragment

                Fragment upgradeToDriver = LiftsYouAreOffering.newInstance();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_drawer_content, upgradeToDriver)
                        .addToBackStack(null)
                        .commit();



            }
        });


        return view;
    }

    protected void sendMail() {
        final String username = "ammarqurehsi1995@gmail.com";
        final String password = "humsubammardublin";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("qureshm@tcd.ie"));
            message.setSubject("Sent from MobileApp");
         //   message.setText("Message : ," + "hello this is an email from android");

            new SendMailTask().execute(message);

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }


    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



public class SendMailTask extends AsyncTask<javax.mail.Message,String, String> {
//    private ProgressDialog progressDialog;

    @Override
    protected String doInBackground(javax.mail.Message... messages) {
        try {
            Transport.send(messages[0]);
            return "Success";
        } catch (SendFailedException ee) {
            return "error1";
        } catch (MessagingException e) {

            return "error2";
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(String result) {
        if (result.equals("Success")) {
            super.onPostExecute(result);
            Log.i("email result:", "success");
        } else if (result.equals("error1"))
            Log.i("email result:", "error");
        else if (result.equals("error2"))
            Log.i("email result:", "error");
    }


    protected void sendMail() {
        final String username = "ammarqurehsi1995@gmail.com";
        final String password = "humsubammardublin";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("qureshm@tcd.ie"));
            message.setSubject("Sent from MobileApp");
            message.setText("Message : ,"
                    + "\n\n" + "hello this is an email from android");

            new SendMailTask().execute((Runnable) message);

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }


    }
}
}



