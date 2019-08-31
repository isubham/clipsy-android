package com.subhamkumar.clipsy.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by faiz on 27/3/18.
 */

public class VolleySingleton {


    private RequestQueue mRequestQueue;
    private static VolleySingleton mInstance;
    private static Context mctx;
    public static final String TAG = VolleySingleton.class.getName();

    private VolleySingleton(Context context) {
        mctx = context;
    }


    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }

        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
            return mRequestQueue;
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);

    }


}

