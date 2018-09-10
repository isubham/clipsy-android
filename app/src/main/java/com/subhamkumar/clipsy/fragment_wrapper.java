package com.subhamkumar.clipsy;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.Map;

public abstract class fragment_wrapper extends Fragment {

    public String text(EditText et) {
        return et.getText().toString().trim();
    }


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

        String url = "http://pitavya.com/clipsy/api/";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Resource volley_wrapper", response.toString());
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

                // Log.i("v_makeparam", makeParams().toString());
                return makeParams();

            }
        };

        make_volley_request(stringRequest);


    }

}


