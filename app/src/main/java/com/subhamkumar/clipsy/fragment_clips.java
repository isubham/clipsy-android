package com.subhamkumar.clipsy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.adapter.Clip_adapter;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.models.Clip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fragment_clips extends fragment_wrapper {

    // user_id => clips list
    public fragment_clips() {
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }


    @Override
    public Map makeParams() {
        Map<String, String> read_clips = new HashMap<String, String>();
        read_clips.put(CONSTANTS.FX, _fx); // "read_clips");

        if (_fx.equals("following_clips")) {
            read_clips.put("user_x", user_id);
            Log.i(CONSTANTS.FRAGMENT_CLIPS, read_clips.toString());
            return read_clips;
        }

        Log.i(CONSTANTS.FRAGMENT_CLIPS, read_clips.toString());
        read_clips.put(CONSTANTS.USER, user_id);
        return read_clips;
    }

    @Override
    public void handle_response(String response) {
        clipList.clear();
        Log.i("clips_data", response);
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray clip_ids = jsonObject.names();

            for (int i = 0; i < clip_ids.length(); i++) {
                String clip_id = clip_ids.get(i).toString();
                JSONObject clip = jsonObject.getJSONObject(clip_id);
                clipList.add(new Clip(clip_id,
                        clip.getString("u_name"),
                        clip.getString("u_id"),
                        clip.getString("clip_content"),
                        clip.getString("timestamp"),
                        clip.getString("visibility")
                ));

                clip_adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(context).add(stringRequest);
    }

    RecyclerView rv_clip;
    LinearLayoutManager linearLayoutManager;
    Clip_adapter clip_adapter;
    List<Clip> clipList;
    FloatingActionButton create_clip_floating_button;


    private void init(View V) {
        rv_clip = (RecyclerView) V.findViewById(R.id.clip_fragment_recycleview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        clipList = new ArrayList<>();

        clip_adapter = new Clip_adapter(clipList);
        rv_clip.setAdapter(clip_adapter);
        rv_clip.setLayoutManager(linearLayoutManager);

        make_request();

    }

    String user_id;
    String _fx;
    View V;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments().containsKey("user_id")) {
            user_id = getArguments().getString("user_id");
        }
        if (getArguments().containsKey("c_user_id")) {
            user_id = getArguments().getString("c_user_id");
        }
        _fx = getArguments().getString("fx");
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

}
