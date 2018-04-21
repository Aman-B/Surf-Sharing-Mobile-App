package com.surf_sharing.surfsharingmobileapp.utils;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.surf_sharing.surfsharingmobileapp.R;
import com.surf_sharing.surfsharingmobileapp.data.Lift;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

        String calendarDate = liftList.get(pos).getDate();
        String[] calendarDateParts = calendarDate.split("/");
        Calendar calendar = new GregorianCalendar(Integer.parseInt(calendarDateParts[2]), Integer.parseInt(calendarDateParts[1]), Integer.parseInt(calendarDateParts[0])); // Note that Month value is 0-based. e.g., 0 for January.

        String userReadableDate = "";

        int result = calendar.get(Calendar.DAY_OF_WEEK);
        switch (result) {
            case Calendar.MONDAY:
                userReadableDate = "Monday, " + calendarDate;
                break;
            case Calendar.TUESDAY:
                userReadableDate = "Tuesday, " + calendarDate;
                break;
            case Calendar.WEDNESDAY:
                userReadableDate = "Wednesday, " + calendarDate;
                break;
            case Calendar.THURSDAY:
                userReadableDate = "Thursday, " + calendarDate;
                break;
            case Calendar.FRIDAY:
                userReadableDate = "Friday, " + calendarDate;
                break;
            case Calendar.SATURDAY:
                userReadableDate = "Saturday, " + calendarDate;
                break;
            case Calendar.SUNDAY:
                userReadableDate = "Sunday, " + calendarDate;
                break;
        }

        tvDate.setText(userReadableDate + " at " + liftList.get(pos).getTime());
        tvSeats.setText(liftList.get(pos).getSeatsAvailable() + " seats remaining");

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


