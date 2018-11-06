package com.subhamkumar.clipsy.fragments;

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
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.adapter.Profile_adapter;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.profile_result;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fragment_search extends fragment_wrapper {

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap<String, String>();
        params .put("fx", "_search");
        params.put("pattern", query);
        return params;
    }

    @Override
    public void handle_response(String response) {
        Log.i("search_response", response);
        // empty set
        profileList.clear();
        // notify
        Profile_adapter.notifyDataSetChanged();

        try{

        // fill the result
        JSONObject profiles = new JSONObject(response);
            JSONArray profile_ids = profiles.names();

            for(int profile_index = 0; profile_index < profile_ids.length(); profile_index++) {
                String profile_id = profile_ids.get(profile_index).toString();
                JSONObject _profile =  profiles.getJSONObject(profile_id);
                profileList.add(new Profile(profile_id, _profile.getString("email"), _profile.getString("name"), _profile.getString("profile_pic")));

                Profile_adapter.notifyDataSetChanged();
            }

        }catch (JSONException e) {

            Log.e("json ex", e.getMessage());
        }

        // notify

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }


    RecyclerView rv_profile;
    LinearLayoutManager linearLayoutManager;
    Profile_adapter Profile_adapter;
    List<Profile> profileList;

    EditText live_search;

    private void init(View V) {
        rv_profile = (RecyclerView) V.findViewById(R.id.profile_fragment_recycleview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        profileList = new ArrayList<>();

        Profile_adapter = new Profile_adapter(profileList);
        rv_profile.setAdapter(Profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
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

                               Intent to_profile_result = new Intent(getActivity(), profile_result.class);

                               String c_user_id = ((TextView)view.findViewById(R.id.rl_profile_id)).getText().toString().trim();
                               // if c_user_id and user_id are same

                               /* TODO will it be added or not.
                               if(user_id.equals(c_user_id)) {
                                   TabHost host = (TabHost) getActivity().findViewById(android.R.id.tabhost);
                                   host.setCurrentTab(3);
                               }
                               */

                                   to_profile_result
                                           .putExtra("c_user_id", c_user_id)
                                           .putExtra(CONSTANTS.FIELD_USER_ID, user_id);
                                    startActivity(to_profile_result);

                           }
                       })
        );
    }

    String query;
    public void fillSearchResult(CharSequence charSequence){
         query = charSequence.toString();
         if(query.length() > 2)
            make_request();
         else
             profileList.clear();

         Profile_adapter.notifyDataSetChanged();
    }

    String user_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        user_id = getArguments().getString(CONSTANTS.USER_ID);

        View V = inflater.inflate(R.layout.fragment_search, container, false);
        init(V);

        // to show keyboard
        live_search.requestFocus();
        InputMethodManager mgr = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(live_search, InputMethodManager.SHOW_IMPLICIT);

        return V;
    }
}
