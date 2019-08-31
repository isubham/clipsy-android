package com.subhamkumar.clipsy.utils;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.Map;

public abstract class wrapper extends AppCompatActivity {

    protected abstract Map makeParams();
    protected abstract void handleResponse(String response);
    protected abstract void makeVolleyRequest(StringRequest stringRequest);
    protected abstract int setHttpMethod();
    protected abstract String setHttpUrl();
    protected abstract Map<String, String> _getHeaders();

    protected abstract void handleErrorResponse(VolleyError error);



    public void handleJsonexceptionError(JSONException e) {
       // Log.e("v_json_excep", e.getMessage());
    }

    public void makeRequest() {

        // Log.e("base", response.toString());
        StringRequest stringRequest = new StringRequest(
                setHttpMethod(),
                setHttpUrl(),
                this::handleResponse,

                this::handleErrorResponse
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


