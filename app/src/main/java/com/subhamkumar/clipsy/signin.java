package com.subhamkumar.clipsy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class signin extends wrapper {

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(signin.this).add(stringRequest);
    }

    @Override
    public Map makeParams() {
        Map param = new HashMap<String, String>();
        param.put("fx", "signin");
        param.put("email", ((EditText) findViewById(R.id.signin_email)).getText().toString().trim());
        param.put("password", ((EditText) findViewById(R.id.signin_pass)).getText().toString().trim());
        return param;
    }

    @Override
    public void handle_response(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // if failed attempt
            if (jsonObject.has("status")) {
                Toast.makeText(this, jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
            }
            // TODO 3 : handle user data put in bundle and goto user panel
            else {

                JSONArray user_keys = (jsonObject.names());
                String user_id = (String) user_keys.get(0);

                String email = jsonObject.getJSONObject(user_id).getString("email");
                String type = jsonObject.getJSONObject(user_id).getString("type");

                startActivity(new Intent(signin.this, home.class)
                        .putExtra("email", email).putExtra("type", type).putExtra("user_id", user_id));

            }

        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }
    }


    public void start_signin(View V) {
        make_request();
    }

    public void gotosignup(View V) {
        startActivity(new Intent(signin.this, signup.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);


    }
}
