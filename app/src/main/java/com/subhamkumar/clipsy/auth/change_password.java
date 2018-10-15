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

public class change_password extends wrapper {
    @Override
    public Map makeParams() {
          Map param = new HashMap<String, String>();
        param.put("fx", "update_password");
        param.put("email", email);
        param.put("password", password);

        return param;
    }

    @Override
    public void handle_response(String response) {
        Log.i("change_pass", response);
      try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("status").equals("1")) {
                Toast.makeText(this, jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(change_password.this, signin.class).putExtra("email", email));
            }
            else{
                Toast.makeText(this, "error changing password", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    String email, password;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        bundle = getIntent().getExtras();
        email = "";
        if (bundle != null) {
            email = getIntent().getExtras().getString("email");
        }
    }

    public String text(int et) {
        return ((EditText) findViewById(et)).getText().toString().trim();
    }

    public void update_password(View V) {
        if (text(R.id.new_password).equals(text(R.id.confirm_new_password))) {
            password = text(R.id.new_password);
            make_request();
        } else {
            Toast.makeText(change_password.this, "Password dont match", Toast.LENGTH_SHORT).show();
        }
    }

}
