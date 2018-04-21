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

    private ArrayList<String[]> passengerDetails;
    private Context context;
    public CustomRequestPassengersAdapter(Context context, ArrayList<String[]> passengerDetails) {
        this.context = context;
        this.passengerDetails = passengerDetails;
    }

    @Override
    public int getCount() {
        return passengerDetails.size();
    }

    @Override
    public Object getItem(int i) {
        return passengerDetails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return passengerDetails.indexOf(i);
    }


    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // no old views to reuse
        if(convertView == null){
            convertView = inflater.inflate(R.layout.request_response_adapter, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
        TextView tvBoardLen = (TextView)  convertView.findViewById(R.id.tvBoardLen);
        TextView tvPickupStreet1 = (TextView)  convertView.findViewById(R.id.tvPickupStreet1);
        TextView tvPickupStreet2 = (TextView)  convertView.findViewById(R.id.tvPickupStreet2);
        TextView tvPickupCounty = (TextView)  convertView.findViewById(R.id.tvPickupCounty);

        String[] passengerDetailParts = passengerDetails.get(pos);

        tvName.setText(passengerDetailParts[1]);
        tvPhone.setText(passengerDetailParts[2]);
        tvBoardLen.setText(passengerDetailParts[3]);
        tvPickupStreet1.setText(passengerDetailParts[4]);
        tvPickupStreet2.setText(passengerDetailParts[5]);
        tvPickupCounty.setText(passengerDetailParts[6]);

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return null;
    }
}
