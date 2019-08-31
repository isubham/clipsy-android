package com.subhamkumar.clipsy.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Notification;
import com.subhamkumar.clipsy.models.NotificationApiResponse;

import java.util.HashMap;
import java.util.Map;

public class NotificationBroadcast extends BroadcastReceiver {

    private static final String CHANNEL_ID = "relationShipNotification";


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent daemonIntent = new Intent(context, Daemon.class);
        Tools._log("restart_service");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(daemonIntent);
        }

        else{
            context.startService(daemonIntent);
        }

    }



}
