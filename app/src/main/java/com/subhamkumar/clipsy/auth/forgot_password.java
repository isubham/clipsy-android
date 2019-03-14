package com.subhamkumar.clipsy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class forgot_password extends wrapper {

    @Override
    public Map<String, String> _getHeaders() {
        return null;
    }
    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap();
        params.put("email", email_to_send.getText().toString().trim());
        return params;
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return getString(R.string.request_user_forgot_password);
    }

    @Override
    public void handleResponse(String response) {

        Log.i("forgot_password", response);

        Gson gson = new Gson();

        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

        if (apiResponse.success.equals(getString(R.string.status_success))) {
            startActivity(new Intent(forgot_password.this, change_password.class)
                    .putExtra("email", email_to_send.getText().toString().trim()));
        } else {
            status.setText(apiResponse.message);
        }

    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    EditText email_to_send;
    TextView email_label, status;

    public void init() {
        email_to_send = (EditText) findViewById(R.id.forgot_password_email);
        email_label = (TextView) findViewById(R.id.forgot_password_email_label);
        status = (EditText) findViewById(R.id.forgot_password_status);
    }

    public void send_verify_token(View V) {
        makeRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        init();

    }
}
