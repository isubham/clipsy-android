package com.subhamkumar.clipsy.panel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.image_adapter;
import com.subhamkumar.clipsy.utils.wrapper;
import java.util.HashMap;
import java.util.Map;

public class choose_avatar extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        return null;
    }

    String id, profile_pic;

    private String get_profile_pic()
    {
        return profile_pic;
    }


    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return getString(R.string.request_user_update_avatar);
    }


    private String get_id()
    {
        return id;
    }

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap();
        params.put("id",get_id());
        params.put("profile_pic",get_profile_pic());
        return params;
    }

    @Override
    public void handleResponse(String response) {

        Log.i("0001", "choose avatar : response" + response);
        choose_avatar.this.finish();

    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_avatar);

        id = getIntent().getExtras().getString("token");

        GridView gridview = (GridView) findViewById(R.id.choose_avatar_gridview);
        gridview.setAdapter(new image_adapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                profile_pic  = "" + position;
                makeRequest();
            }
        });
    }
}
