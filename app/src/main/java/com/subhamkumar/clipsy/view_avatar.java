package com.subhamkumar.clipsy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.utils.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class view_avatar extends wrapper {

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fx", "get_by_id");
        params.put("id", edit_avatar_button.getVisibility() == GONE ? user_y : user_x);
        return params;
    }

    @Override
    public void handle_response(String response) {
        // {"14":{"name":"isubham","email":"subhamkumarchandrawansi@gmail.com","type":"1","profile_pic":"1"}}

        try{
            JSONObject user_json = new JSONObject(response);

            JSONObject user_detail = user_json.getJSONObject(user_x);

            String profile_pic = user_detail.getString("profile_pic");

            try{
            int _profile_pic = Integer.parseInt(profile_pic);
            int imageResource = CONSTANTS.mThumbIds[_profile_pic];
            medium_avatar.setImageResource(imageResource);

        }catch (NumberFormatException e) {
            Log.i("002", "nullformatexception"+ e.getMessage());
        }

        }catch (JSONException e) {
            Log.i("jsonex", "inside view avatar" + e.getMessage());
        }
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    String user_x, user_y;
    Button edit_avatar_button;
    ImageView medium_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_avatar);

        user_x = getIntent().getExtras().getString("user_x");
        user_y = getIntent().getExtras().getString("user_y");
        edit_avatar_button = (Button) findViewById(R.id.edit_avatar);
        medium_avatar = (ImageView) findViewById(R.id.medium_avatar);

        showEditAction(user_x.equals(user_y));

        make_request();

    }

    private void showEditAction(boolean are_same_user) {
        if(are_same_user) {
            edit_avatar_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(view_avatar.this, choose_avatar.class)
                            .putExtra("user_id", user_x));
                }
            });
        }
        else {
            edit_avatar_button.setVisibility(GONE);
        }
    }


    @Override
    protected void onRestart() {
        make_request();
        super.onRestart();
    }
}
