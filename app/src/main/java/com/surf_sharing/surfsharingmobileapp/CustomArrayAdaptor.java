package com.surf_sharing.surfsharingmobileapp;

/**
 * Created by shane on 26/03/17.
 */


        import android.app.Activity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

public class CustomArrayAdaptor extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] liftinfo;
    private final Integer[] imgid;

    public CustomArrayAdaptor(Activity context, String[] liftinfo, Integer[] imgid) {
        super(context, R.layout.listviewicontext, liftinfo);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.liftinfo=liftinfo;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listviewicontext, null,true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.text);


        imageView.setImageResource(imgid[position]);
        extratxt.setText(liftinfo[position]);
        return rowView;

    };
}