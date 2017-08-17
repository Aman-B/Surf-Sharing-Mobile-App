package com.surf_sharing.surfsharingmobileapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.surf_sharing.surfsharingmobileapp.data.User;
import com.surf_sharing.surfsharingmobileapp.R;

import java.util.ArrayList;

/**
 * Created by ammarqureshi on 17/08/2017.
 */

public class CustomRequestPassengersAdapter extends BaseAdapter implements Filterable{

    private ArrayList<User> passengers;
    private Context context;
    public CustomRequestPassengersAdapter(Context context, ArrayList<User> passengers) {
        this.context = context;
        this.passengers = passengers;
    }

    @Override
    public int getCount() {
        return passengers.size();
    }

    @Override
    public Object getItem(int i) {
        return passengers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return passengers.indexOf(i);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        // no old views to reuse
        if(convertView == null){
            convertView = inflater.inflate(R.layout.request_response_adapter, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvBoardLen = (TextView)  convertView.findViewById(R.id.tvBoardLen);


        tvName.setText(passengers.get(pos).getUserName());
        tvBoardLen.setText(passengers.get(pos).getBoardLength());


        return convertView;
    }


    @Override
    public Filter getFilter() {
        return null;
    }
}
