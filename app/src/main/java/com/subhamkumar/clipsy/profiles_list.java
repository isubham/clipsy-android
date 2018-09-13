package com.subhamkumar.clipsy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.models.Profile;

import com.subhamkumar.clipsy.adapter.Clip_adapter;
import com.subhamkumar.clipsy.adapter.Profile_adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class profiles_list extends wrapper {

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap<>();
        params.put("fx", fx);
        params.put("user_x", user_x);
        Log.i("prlist make", params.toString());
        return params;
    }

    @Override
    public void handle_response(String response) {

        try{
            Log.i("profileList", response);

        // fill the result
        JSONObject profiles = new JSONObject(response);
            JSONArray profile_ids = profiles.names();

            for(int profile_index = 0; profile_index < profile_ids.length(); profile_index++) {
                String profile_id = profile_ids.get(profile_index).toString();
                JSONObject _profile =  profiles.getJSONObject(profile_id);
                profileList.add(new Profile(profile_id, _profile.getString("email"), _profile.getString("name")));

                Profile_adapter.notifyDataSetChanged();
            }

        }catch (JSONException e) {

            Log.e("json ex", e.getMessage());
        }

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void init() {
        rv_profile = (RecyclerView) findViewById(R.id.profiles_list_recycleview);
        linearLayoutManager = new LinearLayoutManager(this);
        profileList = new ArrayList<>();

        Profile_adapter = new Profile_adapter(profileList);
        rv_profile.setAdapter(Profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
    }

    RecyclerView rv_profile;
    LinearLayoutManager linearLayoutManager;
    Profile_adapter Profile_adapter;
    List<Profile> profileList;
    Bundle bundle;
    String user_x, fx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list);

        bundle = getIntent().getExtras();
        user_x = bundle.getString("user_x");
        fx = bundle.getString("fx");

        init();
        make_request();
    }
}
