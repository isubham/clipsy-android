package com.subhamkumar.clipsy.requests;

import android.content.Context;
import com.android.volley.Request;

import java.util.Map;

public abstract class base_network {

    private Context context;
    private String url;
    private Request.Method request_method;

    public abstract String set_url(String url);
    public abstract String set_request_method(Request.Method method);
    public abstract void perform_action();
    public abstract void set_handle_volley_error();
    public abstract Map<String, String> params();
    public abstract Map<String, String> fetch();

}
