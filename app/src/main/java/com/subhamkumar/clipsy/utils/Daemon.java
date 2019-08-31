package com.subhamkumar.clipsy.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
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

    private Context ctx;

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
    long oldTime = 0;
    private int counter;

    public void startHandler() {
        Handler handler = new Handler();
        Runnable fetchNotifications = () -> {
            counter++;
            if (counter % 2 == 0) fetchNotificationResouse();
            handler.postDelayed(this::fetchNotificationResouse, 5000);
        };
        handler.post(fetchNotifications);
    }

    private void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 10000); //
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                counter++;
                if (counter % 2 == 0) {
                    Tools._log("fetching");
                    fetchNotificationResouse();
                }
            }
        };
    }

    private void stoptimertask() {
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
        if (LoginPersistance.GetToken(getApplicationContext()) != null) {
            // Intent broadcastIntent = new Intent(this, NotificationBroadcast.class);
            // sendBroadcast(broadcastIntent);
            stoptimertask();
        }

    }


    private void fetchNotificationResouse() {


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
                return new HashMap<String, String>();
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
        return LoginPersistance.GetToken(getApplicationContext());
    }
}
