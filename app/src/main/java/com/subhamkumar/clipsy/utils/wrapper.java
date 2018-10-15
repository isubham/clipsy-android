package com.subhamkumar.clipsy.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.Map;

public abstract class wrapper extends AppCompatActivity {

    public abstract Map makeParams();

    public abstract void handle_response(String response);

    public abstract void make_volley_request(StringRequest stringRequest);

    public void handle_error_response(VolleyError error) {
        Log.e("v_handle_error_res", error.getMessage());
    }

    public void handle_jsonexception_error(JSONException e) {
        Log.e("v_json_excep", e.getMessage());
    }

    public void make_request() {

        String url = "http://api.pitavya.com/clipsy/";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("base", response.toString());
                        handle_response(response);

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_error_response(error);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                return makeParams();

            }
        };

        make_volley_request(stringRequest);

    }

}


