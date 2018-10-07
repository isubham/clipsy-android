package com.subhamkumar.clipsy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.subhamkumar.clipsy.R;

public class image_adapter  extends BaseAdapter {
    private Context mContext;

    public image_adapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(180, 180));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.boy003, R.drawable.boy007,
            R.drawable.boy016, R.drawable.boy016,
            R.drawable.girl002, R.drawable.girl010,
            R.drawable.girl017, R.drawable.girl021,
            R.drawable.man001, R.drawable.man005,
            R.drawable.man011, R.drawable.man014,
            R.drawable.woman004, R.drawable.woman006,
            R.drawable.woman012, R.drawable.woman013,

    };
}
