package com.subhamkumar.clipsy.panel.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.panel.profile_result;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fragment_search extends fragment_wrapper {
    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public String setHttpUrl() {
        String url = String.format(getString(R.string.request_user_search_user), query);
        return url;
    }

    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.header_authentication), token);
        return params;
    }

    @Override
    public Map makeParams() {
        return null;
    }

    @Override
    public void handle_response(String response) {
        Log.i("search_response", response);

        Gson gson = new Gson();

        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
        profileList.clear();
        profileList.addAll(profileApiResponse.data);
        profile_adapter.notifyDataSetChanged();
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }


    RecyclerView rv_profile;
    LinearLayoutManager linearLayoutManager;
    profile_adapter profile_adapter;
    List<Profile> profileList;

    EditText live_search;

    private void init(View V) {
        rv_profile = (RecyclerView) V.findViewById(R.id.profile_fragment_recycleview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        profileList = new ArrayList<Profile>();
        profile_adapter = new profile_adapter(profileList);
        rv_profile.setLayoutManager(linearLayoutManager);
        rv_profile.setAdapter(profile_adapter);
        live_search = V.findViewById(R.id.live_search);
        live_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                fillSearchResult(live_search.getText().toString().trim());
            }
        });

        rv_profile.addOnItemTouchListener(
               new RecyclerItemClickListener(getActivity(),
                       new RecyclerItemClickListener.OnItemClickListener() {
                           @Override
                           public void onItemClick(View view, int position) {

                               gotToProfileResult(view);

                           }
                       })
        );
    }

    private void fillDummyAndCheck() {
        List<Profile> dummyProfileList = new ArrayList<Profile>();
        for(int i = 0; i < 10; i++) {
            dummyProfileList.add(Profile.dummyProfile());
        }
        profileList.addAll(dummyProfileList);
        profile_adapter.notifyDataSetChanged();
    }

    private void gotToProfileResult(View view) {
        Intent to_profile_result = new Intent(getActivity(), profile_result.class);

        String searchedUserId = ((TextView) view.findViewById(R.id.rl_profile_id)).getText().toString().trim();

        // if c_user_id and token are same

        /* TODO will it be added or not.
        if(token.equals(c_user_id)) {
        TabHost host = (TabHost) getActivity().findViewById(android.R.id.tabhost);
        host.setCurrentTab(3);
        }
        */

        userDetails.putString(getString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId);
        userDetails.putString(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                getString(R.string.bundle_param_caller_activity_fragment_search));

        to_profile_result.putExtras(userDetails);
        startActivity(to_profile_result);
    }

    String query;
    public void fillSearchResult(CharSequence charSequence){
         query = charSequence.toString();
         if(query.length() > 2) {
             make_request();
         }
         else {
             profileList.clear();
             profile_adapter.notifyDataSetChanged();
         }

    }

    Bundle userDetails;
    String token;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        userDetails = getArguments();
        token = userDetails.getString(Constants.TOKEN);

        View V = inflater.inflate(R.layout.fragment_search, container, false);
        init(V);

        // to show keyboard
        live_search.requestFocus();
        InputMethodManager mgr = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(live_search, InputMethodManager.SHOW_IMPLICIT);

        return V;
    }
}
