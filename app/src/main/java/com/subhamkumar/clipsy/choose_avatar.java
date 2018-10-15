package com.subhamkumar.clipsy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.adapter.image_adapter;
import com.subhamkumar.clipsy.utils.wrapper;
import java.util.HashMap;
import java.util.Map;

public class choose_avatar extends wrapper {

    String id, profile_pic;

    private String get_profile_pic()
    {
        return profile_pic;
    }


    private String get_id()
    {
        return id;
    }

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap();
        params.put("fx","_update_profile_pic_avatar");
        params.put("id",get_id());
        params.put("profile_pic",get_profile_pic());
        return params;
    }

    @Override
    public void handle_response(String response) {

        Log.i("0001", "choose avatar : response" + response);
        choose_avatar.this.finish();

    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_avatar);

        id = getIntent().getExtras().getString("id");

        GridView gridview = (GridView) findViewById(R.id.choose_avatar_gridview);
        gridview.setAdapter(new image_adapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                profile_pic  = "" + position;
                make_request();
            }
        });
    }
}
