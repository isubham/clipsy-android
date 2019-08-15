package com.subhamkumar.clipsy.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import java.util.Timer;
import java.util.TimerTask;

public class Daemon extends Service {

    Context ctx;
    public Daemon(Context context) {
        super();
        ctx = context;
    }

    public Daemon() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public int counter;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 5000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                fetchNotificationResouse();
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, NotificationBroadcast.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }


    public void fetchNotificationResouse() {


        StringRequest fetchNotification = new StringRequest(Request.Method.GET, Constants.newNotificationRequest,
                response -> {
                    // TODO
                    NotificationApiResponse notificationApiResponse = new Gson()
                            .fromJson(response, NotificationApiResponse.class);


                    Log.e("nres", response);
                    if (notificationApiResponse.success) {
                        for (Notification notification :
                                notificationApiResponse.data) {

                            NotificationHelper.notificationClickAction(getApplicationContext(), notification);

                        }
                    }
                },

                error -> {

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(Constants.header_authentication, getToken(getApplicationContext()));
                return headers;
            }

        };

        Volley.newRequestQueue(getApplicationContext()).add(fetchNotification);
    }

    private String getToken(Context context) {
        return new LoginDb(context).getLoginDetails().TOKEN;
    }
}
