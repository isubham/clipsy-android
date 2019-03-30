package com.subhamkumar.clipsy.panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;

public class view_avatar extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.header_authentication), token);
        return params;
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public String setHttpUrl() {
        return String.format(Constants.request_user_get_user, areSameUser ? id : searcheUserId);
    }

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap<String, String>();
        return params;
    }

    @Override
    public void handleResponse(String response) {

        Gson gson = new Gson();

        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);

        String profilePic = profileApiResponse.data.get(0).profile_pic;

        String parsedProficPic = profilePic.equals("") ? "0" : profilePic;

        int _profile_pic = Integer.parseInt(parsedProficPic);

        int imageResource = Constants.mThumbIds[_profile_pic];
        mediumAvatar.setImageResource(imageResource);


    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private String id;
    private String searcheUserId;
    private String token;
    private Button editAvatarButton;
    private ImageView mediumAvatar;
    private boolean areSameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_avatar);

        id = Objects.requireNonNull(getIntent().getExtras()).getString("id");
        searcheUserId = getIntent().getExtras().getString("searched_id");
        token = getIntent().getExtras().getString("token");


        editAvatarButton = findViewById(R.id.edit_avatar);
        mediumAvatar = findViewById(R.id.medium_avatar);

        areSameUser = id.equals(searcheUserId);
        showEditAction(areSameUser);

        makeRequest();

    }

    private void showEditAction(boolean are_same_user) {
        if(are_same_user) {
            editAvatarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(view_avatar.this,
                            choose_avatar.class)
                            .putExtra("token", token)
                            .putExtra("id", id)
                            .putExtra("searched_id", searcheUserId)
                    );

                }
            });
        }
        else {
            editAvatarButton.setVisibility(GONE);
        }
    }


    @Override
    protected void onRestart() {
        makeRequest();
        super.onRestart();
    }
}
