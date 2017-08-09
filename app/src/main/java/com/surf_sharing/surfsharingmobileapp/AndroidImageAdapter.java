package com.surf_sharing.surfsharingmobileapp;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by lukeagnew on 13/07/2017.
 */

public class AndroidImageAdapter extends PagerAdapter {

    private Context mContext;

    public AndroidImageAdapter(Context context) {
        this.mContext = context;

    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    public int[] sliderImagesId = new int[]{
            R.drawable.driver_avatar, R.drawable.cliffs_of_moher, R.drawable.surfer,
            R.drawable.surfer_lift
    };

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(sliderImagesId[i]);
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}