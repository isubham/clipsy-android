package com.subhamkumar.clipsy.panel.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.Map;

public abstract class fragment_wrapper extends Fragment {

    public String text(EditText et) {
        return et.getText().toString().trim();
    }


    protected abstract Map makeParams();
    protected abstract void handleResponse(String response);
    protected abstract void makeVolleyRequest(StringRequest stringRequest);
    protected abstract int setHttpMethod();
    protected abstract String setHttpUrl();
    protected abstract Map<String, String> _getHeaders();
    protected abstract void handle_error_response(VolleyError error);

    public void handle_jsonexception_error(JSONException e) {
        Log.e("v_json_excep", e.getMessage());
    }

    void make_request() {

        String url = setHttpUrl();

        StringRequest stringRequest = new StringRequest(
                setHttpMethod(),
                url,
                response -> {

                    Log.e("Resource volley_wrapper", response);
                    handleResponse(response);

                },

                error -> handle_error_response(error)
        ) {

            @Override
            protected Map<String, String> getParams() {

                // Log.i("v_makeparam", makeParams().toString());
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


