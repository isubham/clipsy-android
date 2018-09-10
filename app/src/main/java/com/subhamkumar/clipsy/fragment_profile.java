package com.subhamkumar.clipsy;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.adapter.Clip_adapter;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.subhamkumar.clipsy.adapter.Profile_adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_profile extends fragment_wrapper {
    @Override
    public Map makeParams() {
        return null;
    }

    @Override
    public void handle_response(String response) {

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    public fragment_profile() {
        // Required empty public constructor
    }

    private void init_data() {
        make_request();
    }

    // follow, follow back, unfollow
    public void rx_init(){
        switch (rx) {

            case "":; break;
            case "1":; break;
            case "-1":; break;

        }

    }


    public void rx_action() {

        _rx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void init(View V) {

        _name = V.findViewById(R.id.fragment_profile_name);
        _name.setText(user_name);

        _email = V.findViewById(R.id.fragment_profile_email);
        _email.setText(user_email);

        _rx = V.findViewById(R.id.rx);

        init_data();

    }

    String user_id;
    String user_name;
    String user_email;
    String user_type;
    String rx;


    TextView _name, _email;
    Button _rx;

    View V;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            user_id = getArguments().getString("user_id");
            user_name = getArguments().getString( "name");
            user_email = getArguments().getString("email");
            user_type = getArguments().getString( "type");

            Log.i("fragment_profile", "nonempty bundle");

            if(getArguments().containsKey("type")){
                rx = getArguments().getString("type");

            }
        }
        else{

            Log.i("fragment_profile", "empty bundle");
        }

        V = inflater.inflate(R.layout.fragment_profile, container, false);
        init(V);
        return V;
    }

}
