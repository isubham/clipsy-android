package com.subhamkumar.clipsy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class create extends wrapper {

    String user_id, clip_type;

    @Override
    public Map makeParams() {
        Map params = new HashMap<String, String>();

        params.put("visibility", clip_type);
        params.put("user", clip_type);
        params.put("clip_title", text(R.id.clip_title));
        params.put("clip_content", text(R.id.clip_content));

        params.put("fx", "create_clip");


        return params;
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(create.this).add(stringRequest);
    }

    @Override
    public void handle_response(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("status") == "1") {
                // TODO : 1 go to signin after saying account created
                Toast.makeText(this, "Clip Created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(create.this, home.class));
            } else {
                // TODO : 2 account didnot created give message show message
                Toast.makeText(this, jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        // getting user id
        user_id = getIntent().getExtras().getString("user_id");

    }

    public void select_type(View V) {
        clip_type = (V.getId()) == R.id._private ? "1" : "2";
    }


    public void create_clip(View V) {
        make_request();
    }

}
