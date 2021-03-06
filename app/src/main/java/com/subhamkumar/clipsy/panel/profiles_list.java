package com.subhamkumar.clipsy.panel;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;

import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class profiles_list extends wrapper {

    // (following and followers) => showing a list of users


    @Override
    protected void handleErrorResponse(VolleyError error) {

    }

    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(Constants.header_authentication, getTokenFromBundle());
        return params;
    }

    @Override
    public Map makeParams() {
        return null;
    }

    private String getTokenFromBundle() {
        return bundle.getString(Constants.param_token);
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

        boolean isFollowingButtonClicked =
                Objects.requireNonNull(bundle.getString(Constants.PROFILE_LIST))
                        .equals(Constants.FOLLOWING);

        return String.format(isFollowingButtonClicked
                        ? Constants.request_user_user_following :
                        Constants.request_user_user_follower,
                searchedId);
    }

    private void profileListClickToProfilePage() {

        rv_profile.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        (view, position) -> gotToProfileResult(view))
        );
    }

    private void gotToProfileResult(View view) {
        Intent to_profile_result = new Intent(getApplicationContext(), profile_result.class);

        String searchedUserId = ((TextView) view.findViewById(R.id.rl_profile_id)).getText().toString().trim();

        to_profile_result.putExtra(Constants.SEARCHED_ID, searchedUserId)
                .putExtra(Constants.TO_HOME,
                        Constants.PROFILE_LIST_TO_COMPLETE_PROFILE)
                .putExtra(Constants.param_token, getTokenFromBundle())
                .putExtra(Constants.param_id, getIntent().getStringExtra(Constants.param_id));

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

    private void init() {
        rv_profile = findViewById(R.id.profiles_list_recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        profileList = new ArrayList<>();

        profile_adapter = new profile_adapter(profileList) {
            @Override
            protected void addViewClickListeners(View V) {
                V.findViewById(R.id.rl_profile_close).setOnClickListener(v -> {
                    String searchedProfileId = ((TextView)V.findViewById(R.id.rl_profile_id)).getText().toString().trim();
                    deleteSearchedProfile(searchedProfileId);
                });
            }
        };
        rv_profile.setAdapter(profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
    }
    
    private void deleteSearchedProfile(String searchedProfile) {
        // TODO delete profile search
        Toast.makeText(this, "will get removed in future.", Toast.LENGTH_SHORT).show();
    }

    private RecyclerView rv_profile;
    private profile_adapter profile_adapter;
    private List<Profile> profileList;
    private Bundle bundle;

    private String getSeacrchedId() {
        return getIntent().getStringExtra(Constants.param_search_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_list);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        bundle = getIntent().getExtras();

        init();
        makeRequest();
    }
}
