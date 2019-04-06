package com.subhamkumar.clipsy.panel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.image_adapter;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.wrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class choose_avatar extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.header_authentication), token);
        return params;
    }

    @Override
    protected void handleErrorResponse(VolleyError error) {

        showNetworkUnavailableDialog();

    }

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(choose_avatar.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            makeRequest();
        });

        dialog.show();
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

    private Dialog networkLoadingDialog;
    @Override
    public void handleResponse(String response) {

        Gson gson = new Gson();
        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);

        Toast.makeText(this, profileApiResponse.message, Toast.LENGTH_SHORT).show();
        if(profileApiResponse.success.equals("1")) {
            choose_avatar.this.finish();
        }

        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "choose avatar hide");

    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private String id;
    private String token;
    private String searchedId;
    private String profile_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_avatar);
        networkLoadingDialog = new Dialog(choose_avatar.this, R.style.TranslucentDialogTheme);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        token = Objects.requireNonNull(getIntent().getExtras()).getString("token");
        id = getIntent().getExtras().getString("id");
        searchedId = getIntent().getExtras().getString("searched_id");

        GridView gridview = findViewById(R.id.choose_avatar_gridview);
        gridview.setAdapter(new image_adapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                profile_pic  = "" + position;
                makeRequest();
                Tools.showNetworkLoadingDialog(networkLoadingDialog, "chooose avatar show");
            }
        });
    }
}
