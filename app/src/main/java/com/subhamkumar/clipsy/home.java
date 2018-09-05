package com.subhamkumar.clipsy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.adapter.Clip_adapter;
import com.subhamkumar.clipsy.models.Clip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home extends Activity {

    RecyclerView rv_clip;
    LinearLayoutManager linearLayoutManager;
    Clip_adapter clip_adapter;
    List<Clip> clipList;

    public void to_create(View V) {
        startActivity(
                new Intent(home.this, create.class)
                    .putExtra("user_id", user_id));
    }

    private void init() {

        rv_clip = (RecyclerView) findViewById(R.id.rv_clip);
        linearLayoutManager = new LinearLayoutManager(this);
        clipList = new ArrayList<>();

        clip_adapter = new Clip_adapter(clipList);
        rv_clip.setAdapter(clip_adapter);
        rv_clip.setLayoutManager(linearLayoutManager);

        user_id = getIntent().getExtras().getString("user_id");
        user_name = getIntent().getExtras().getString("user_id");
        user_type = getIntent().getExtras().getString("type");

    }

    // TODO set this via bundle
    String user_id, user_name, user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        init();

        wrapper fetch_clips = new wrapper() {
            @Override
            public Map makeParams() {
                Map param = new HashMap<String, String>();
                param.put("fx", "read_clip");
                param.put("email", text(R.id.signup_email));
                param.put("password", text(R.id.signup_password));
                return param;
            }

            @Override
             public void make_volley_request(StringRequest stringRequest) {
                 Volley.newRequestQueue(home.this).add(stringRequest);
             }

            @Override
            public void handle_response(String response) {
                /*
                     id :
                            clip_title :
                            timestamp  :
                            clip_content :
                 */
                try {

                    JSONObject clips = new JSONObject(response);

                    JSONArray clip_keys = clips.names();

                    for (int i = 0; i < clip_keys.length(); i++) {

                        String current_clip_id = (String) clip_keys.get(i);
                        JSONObject current_clip = clips.getJSONObject(current_clip_id);

                        clipList.add(
                                new Clip(
                                        current_clip_id,
                                        "subham",// TODO current_clip.getString("author_name"),
                                        "1", // TODO current_clip.getString("author_name"),
                                        current_clip.getString("clip_title"),
                                        current_clip.getString("clip_content"),
                                        current_clip.getString("timestamp")
                                )
                        );
                    }

                    clip_adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e("json ex", e.getMessage());
                }

            }
        };

        fetch_clips.make_request();
    }
}
