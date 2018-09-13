package com.subhamkumar.clipsy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class profiles_list extends wrapper {

    @Override
    public Map makeParams() {
        return null;
    }

    @Override
    public void handle_response(String response) {

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list);
    }
}
