package com.subhamkumar.clipsy.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public abstract class wrapper extends AppCompatActivity {

    public abstract Map makeParams();
    public abstract void handleResponse(String response);
    public abstract void makeVolleyRequest(StringRequest stringRequest);
    public abstract int setHttpMethod();
    public abstract String setHttpUrl();
    public abstract Map<String, String> _getHeaders();

    public void handleErrorResponse(VolleyError error) {
        Log.e("v_handle_error_res", error.getMessage());
    }

    public void handleJsonexceptionError(JSONException e) {
        Log.e("v_json_excep", e.getMessage());
    }

    public void makeRequest() {

        StringRequest stringRequest = new StringRequest(
                setHttpMethod(),
                setHttpUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("base", response.toString());
                        handleResponse(response);

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleErrorResponse(error);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                return makeParams();

            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return _getHeaders();
            }
        };

        makeVolleyRequest(stringRequest);

    }

}

