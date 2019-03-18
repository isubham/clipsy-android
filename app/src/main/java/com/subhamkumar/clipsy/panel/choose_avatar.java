package com.subhamkumar.clipsy.panel;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.image_adapter;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.utils.wrapper;
import java.util.HashMap;
import java.util.Map;

public class choose_avatar extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.header_authentication), token);
        return params;
    }


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
        return String.format(getString(R.string.request_user_update_avatar), id);
    }


    private String get_id()
    {
        return id;
    }

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap();
        params.put("profile_pic",get_profile_pic());
        return params;
    }

    @Override
    public void handleResponse(String response) {

        Gson gson = new Gson();
        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);

        Toast.makeText(this, profileApiResponse.message, Toast.LENGTH_SHORT).show();
        if(profileApiResponse.success.equals("1")) {
            choose_avatar.this.finish();
        }


    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    String id, token, searchedId, profile_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_avatar);

        token = getIntent().getExtras().getString("token");
        id = getIntent().getExtras().getString("id");
        searchedId = getIntent().getExtras().getString("searched_id");

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
