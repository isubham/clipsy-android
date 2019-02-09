package com.subhamkumar.clipsy.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.clip_adapter;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.profile_result;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;

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


    /*
        [fx : following_clips, user_x : id]
        [fx : read_clips,      user   : id]
     */
    @Override
    public Map makeParams() {
        Map<String, String> read_clips = new HashMap<String, String>();
        read_clips.put(CONSTANTS.FX, _fx); // "read_clips", "following_clips"

        if (_fx.equals("following_clips")) {
            read_clips.put("user_x", user_id);
            return read_clips;
        }

        Log.i(CONSTANTS.FRAGMENT_CLIPS, read_clips.toString());
        read_clips.put(CONSTANTS.USER, user_id);
        return read_clips;
    }

    // int no_of_intent;
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

                clipList.add(
                        new Clip(clip_id,
                        clip.getString("u_name"),
                        clip.getString("u_id"),
                        clip.getString("clip_content"),
                        clip.getString("timestamp"),
                        clip.getString("visibility"),
                        clip.getString("profile_pic")
                        )
                );

                clip_adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
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
                                   // if c_user_id and user_id are same


                                   to_profile_result
                                           .putExtra("c_user_id", c_user_id)
                                           .putExtra(CONSTANTS.FIELD_USER_ID, user_id);
                                   startActivity(to_profile_result);
                               }

                               // no_of_intent++;

                           }

                       }));

            */

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // in case of panel => following user posts
        if(getArguments().containsKey(getString(R.string.user_id))) {
            user_id = getArguments().getString("user_id");
        }

        // in case of fragment_complete_profile
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
