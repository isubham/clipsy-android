package com.subhamkumar.clipsy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.utils.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class send_verify_token extends wrapper {

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap();
        params.put("fx", "send_forgot_password");
        params.put("email", email_to_send.getText().toString().trim());
        return params;
    }

    @Override
    public void handle_response(String response) {

        Log.i("send_verify_token", response);
                Log.i("message", response);
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("status").equals("1") ) {
                // TODO : 1 go to verify token
                startActivity(new Intent(send_verify_token.this, email_verification.class)
                        .putExtra("email", email_to_send.getText().toString().trim())
                .putExtra("callback", "2"));
            } else {
                // TODO : 2 account didnot created give message show message
                Toast.makeText(send_verify_token.this, "Some Network Issue !!", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    EditText email_to_send;

    public void init() {
        email_to_send = (EditText) findViewById(R.id.email_to_send_verify_token);
    }

    public void send_verify_token(View V) {
        make_request();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_verify_token);

        init();

    }
}
