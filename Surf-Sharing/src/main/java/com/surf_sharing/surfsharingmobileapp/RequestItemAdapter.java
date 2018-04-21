package com.surf_sharing.surfsharingmobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.surf_sharing.surfsharingmobileapp.data.User;

import java.util.ArrayList;

/**
 * Created by aran on 27/03/17.
 */

public class RequestItemAdapter extends ArrayAdapter<User> {
    public RequestItemAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.requestPassengerName);
        // Populate the data into the template view using the data object
        tvName.setText(user.name);
        // Return the completed view to render on screen
        return convertView;
    }
}
