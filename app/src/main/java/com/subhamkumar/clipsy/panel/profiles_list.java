package com.subhamkumar.clipsy.panel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Profile;

import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;
import com.subhamkumar.clipsy.utils.wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class profiles_list extends wrapper {

    // (following and followers) => showing a list of users


    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.header_authentication), getTokenFromBundle());
        return params;
    }

    @Override
    public Map makeParams() {
        return null;
    }

    private String getTokenFromBundle() {
        return bundle.getString(getString(R.string.params_token));
    }

     @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public String setHttpUrl() {
        return getUrlByIncomingFlag();

    }

    @NonNull
    private String getUrlByIncomingFlag() {

        String searchedId = getSeacrchedId();

        boolean isFollowingButtonClicked  =
                bundle.getString(getString(R.string.bundle_param_caller_button_to_profile_list))
                        .equals(getString(R.string.bundle_param_caller_button_following));

        String profileListUrl =  String.format( isFollowingButtonClicked
                ? getString(R.string.request_user_user_following) :
                getString(R.string.request_user_user_follower),
                searchedId);

        return profileListUrl;
    }

    private void profileListClickToProfilePage() {

        rv_profile.addOnItemTouchListener(
               new RecyclerItemClickListener(getApplicationContext(),
                       new RecyclerItemClickListener.OnItemClickListener() {
                           @Override
                           public void onItemClick(View view, int position) {

                               gotToProfileResult(view);

                           }
                       })
        );
    }

    private void gotToProfileResult(View view) {
        Intent to_profile_result = new Intent(getApplicationContext(), profile_result.class);

        String searchedUserId = ((TextView) view.findViewById(R.id.rl_profile_id)).getText().toString().trim();

        to_profile_result.putExtra(getString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId)
                         .putExtra(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                                    getString(R.string.bundle_param_caller_activity_fragment_profile_list_to_profile_result))
                         .putExtra(getString(R.string.params_token), getTokenFromBundle())
                         .putExtra(getString(R.string.params_id), getIntent().getStringExtra(getString(R.string.params_id)));


        startActivity(to_profile_result);
    }

    @Override
    public void handleResponse(String response) {
        Gson gson = new Gson();
        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
        profileList.clear();
        profileList.addAll(profileApiResponse.data);
        profile_adapter.notifyDataSetChanged();
        profileListClickToProfilePage();
    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void init() {
        rv_profile = (RecyclerView) findViewById(R.id.profiles_list_recycleview);
        linearLayoutManager = new LinearLayoutManager(this);
        profileList = new ArrayList<>();

        profile_adapter = new profile_adapter(profileList);
        rv_profile.setAdapter(profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
    }

    RecyclerView rv_profile;
    LinearLayoutManager linearLayoutManager;
    profile_adapter profile_adapter;
    List<Profile> profileList;
    Bundle bundle;

    private String getSeacrchedId() {
        String searchedId = getIntent().getStringExtra(getString(R.string.params_search_id));
        return searchedId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list);

        bundle = getIntent().getExtras();

        init();
        makeRequest();
    }
}
