package com.subhamkumar.clipsy.panel.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.clip_adapter;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.models.Clip;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fragment_clips extends fragment_wrapper {
    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public Map makeParams() {
        return null;
    }

    @Override
    public String setHttpUrl() {
        String from = getArguments().getString(getString(R.string.bundle_param_caller_activity_to_fragment_clips));
        return getUrl(from);
    }

    @Override
    public Map<String, String> _getHeaders() {
        Map headers = new HashMap<String, String>();
        headers.put(getString(R.string.header_authentication), token);
        return headers;
    }

    // token => clips list
    public fragment_clips() {
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }


    // int no_of_intent;
    @Override
    public void handle_response(String response) {

        Gson gson = new Gson();
        ClipApiResonse  clipApiResonse = gson.fromJson(response, ClipApiResonse.class);
        clipList.clear();
        clipList.addAll(clipApiResonse.data);
        clip_adapter.notifyDataSetChanged();

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(context).add(stringRequest);
    }

    RecyclerView rv_clip;
    LinearLayoutManager linearLayoutManager;
    com.subhamkumar.clipsy.adapter.clip_adapter clip_adapter;
    List<Clip> clipList;


    private void init(View V) {
        // no_of_intent = 0;
        rv_clip = (RecyclerView) V.findViewById(R.id.clip_fragment_recycleview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        clipList = new ArrayList<>();

        clip_adapter = new clip_adapter(clipList);
        rv_clip.setLayoutManager(linearLayoutManager);
        rv_clip.setAdapter(clip_adapter);

        make_request();

    }

    String token, id, searched_id;
    String _fx;
    View V;
    Context context;

    public String getUrl(String from) {
        String fromPanel = getString(R.string.bundle_param_caller_activity_panel);
        String fromSearch = getString(R.string.bundle_param_caller_activity_fragment_search);
        String fromProfileResult = getString(R.string.bundle_param_caller_activity_fragment_profile_list_to_profile_result);

        if(from != null) {
            if (from.equals(fromPanel)) {
                return String.format(getString(R.string.request_clip_following));
            }
            if (from.equals(fromSearch) || from.equals(fromProfileResult)) {
                searched_id = getArguments().getString(getString(R.string.bundle_param_profile_result_searched_user_id));
                return String.format(getString(R.string.request_clip_reads_user), searched_id);
            }
        }

        return String.format(getString(R.string.request_clip_reads));

    }

    Bundle bundle;
    String from;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        bundle = getArguments();

        try {
            id = bundle.getString(getString(R.string.params_id));
            token = bundle.getString(getString(R.string.params_token));
            from = bundle.getString(getString(R.string.bundle_param_caller_activity_to_fragment_clips));

        }
        catch (NullPointerException e) {
            Toast.makeText(context, "null on" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("null on" , e.getMessage());
        }


        V = inflater.inflate(R.layout.fragment_clips, container, false);
        context = container.getContext();
        init(V);
        return V;
    }

    @Override
    public void onResume() {
        init(V);
        super.onResume();
    }
/*
         rv_clip.addOnItemTouchListener(
               new RecyclerItemClickListener(getActivity(),
                       new RecyclerItemClickListener.OnItemClickListener() {
                           @Override
                           public void onItemClick(View view, int position) {

                               if (no_of_intent == 0) {

                                   Intent to_profile_result = new Intent(getActivity(), profile_result.class);

                                   String c_user_id = ((TextView) view.findViewById(R.id.rl_clip_author_id)).getText().toString().trim();
                                   // if c_user_id and token are same


                                   to_profile_result
                                           .putExtra("c_user_id", c_user_id)
                                           .putExtra(Constants.FIELD_USER_ID, token);
                                   startActivity(to_profile_result);
                               }

                               // no_of_intent++;

                           }

                       }));

            */
}