package com.subhamkumar.clipsy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;

public class email_verification extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        return null;
    }

    @Override
    public Map makeParams() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.params_email), email);
        params.put(getString(R.string.params_verify_token), verify_token.getText().toString().trim());
        return params;
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return getString(R.string.request_user_verify_email);
    }

    @Override
    public void handleResponse(String response) {
        Log.i("response", "email_verification : " + response);

        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

        if (apiResponse.success.equals(getString(R.string.status_email_verification_email_verified))) {
            startActivity(new Intent(email_verification.this, signin.class));
        }
        else if(apiResponse.success.equals(getString(R.string.status_email_verification_email_unverified))){
            ((TextView) findViewById(R.id.email_verification_status_message)).setText(apiResponse.message);
        }

    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    String email;
    EditText verify_token;
    Button button_to_email;

    private void init() {
        verify_token = (EditText) findViewById(R.id.verify_token);
        button_to_email = (Button) findViewById(R.id.send_verify_token);
    }

    String callback;
    Bundle bundle;

    private void setCallbackFromBundle() {
        bundle = getIntent().getExtras();
        email = "";
        if (bundle != null) {
            email = getIntent().getExtras().getString("email");
            callback = getIntent().getExtras().getString(getString(R.string.bundle_param_caller_activity_to_email_verification));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);
        setCallbackFromBundle();
        init();
        button_to_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRequest();
            }
        });

    }
}
