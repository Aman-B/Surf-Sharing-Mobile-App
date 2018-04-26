package com.surf_sharing.surfsharingmobileapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Lift;

import java.util.ArrayList;

/**
 * Created by ammarqureshi on 16/08/2017.
 */

public class CustomizedAdapter extends BaseAdapter implements Filterable {


    private Context ctx;
    private ArrayList<Lift> liftList;
    private ArrayList<Lift> filterList;
    private CustomFilter filter;

    public CustomizedAdapter(Context ctx, ArrayList<Lift> lifts) {

        this.ctx = ctx;
        this.liftList = lifts;
        this.filterList = lifts;

    }

    @Override
    public int getCount() {
        return liftList.size();
    }

    @Override
    public Object getItem(int i) {
        return liftList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return liftList.indexOf(i);
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {


        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //if no convertView, old ones to reuse, then render the layout xml file
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_layout, null);
        }

        TextView tvPlace = (TextView) view.findViewById(R.id.tvPlace);
        TextView tvDate = (TextView) view.findViewById(R.id.tvTime);
        TextView tvSeats = (TextView) view.findViewById(R.id.tvRemainingSeats);



        //SET DATA

        tvPlace.setText(liftList.get(pos).getDestination());
        tvDate.setText(liftList.get(pos).getDate() + "at " + liftList.get(pos).getTime());
        tvSeats.setText(liftList.get(pos).getSeatsAvailable() + " seats remaining");

        //Use below if asked for alternate colored list items.
        if(pos %2 == 1)
        {
            // Set a background color for ListView regular row/item
            view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorGrey));
            tvPlace.setTextColor(Color.parseColor("#FFFFFF"));
            tvDate.setTextColor(Color.parseColor("#FFFFFF"));
            tvSeats.setTextColor(Color.parseColor("#FFFFFF"));

        }
        else
        {
            // Set the background color for alternate row/item
            view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorWhite));
            tvPlace.setTextColor(Color.parseColor("#000000"));
            tvDate.setTextColor(Color.parseColor("#000000"));
            tvSeats.setTextColor(Color.parseColor("#000000"));

        }
        return view;
    }


    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }


    class CustomFilter extends Filter {


        public CustomFilter() {
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            //check if there is a constraint
            if(constraint != null && constraint.length() > 0){
                constraint = constraint.toString().toLowerCase();

                ArrayList<Lift> filtered = new ArrayList<>();

                for(int i =0;i<filterList.size();i++){

                    if(filterList.get(i).getDestination().toLowerCase().contains(constraint.toString())){

                        filtered.add(filterList.get(i));
                    }
                }

                results.count = filtered.size();
                results.values = filtered;


            }
            //else empty string
            else{

                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        //invoke the UI thread to publish the filtering results in the UI
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {

            liftList = (ArrayList<Lift>) results.values;
            notifyDataSetChanged();
        }
    }

}


