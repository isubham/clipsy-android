package com.subhamkumar.clipsy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.choose_avatar;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.profiles_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_profile extends fragment_wrapper {

    /*
    @dependency
    name, user_id, type, c_user_id
     */
    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fx", "now_type");
        params.put("user_x", user_x);
        params.put("user_y", user_y);
        return params;
    }

    @Override
    public void handle_response(String response) {

        response = response.trim();
        // if nothing => follow
        // []
        if (response.equals("[]")) {
            rx_action = "follow";
            rx_title = "follow";
        } else {
            try {
                JSONObject rx_detais = new JSONObject(response);

                JSONArray rx_array = rx_detais.getJSONArray(user_x);

                if (rx_array.length() == 1) {
                    // if y follow x => follow back
                    // {"user_x":[{"user_y":"","type":"-1"}]}
                    // if x follows y => unfollow
                    // {"user_x":[{"user_y":"","type":"1"}]}
                    JSONObject user_y_type = (rx_array.getJSONObject(0));
                    String type = user_y_type.getString("type");

                    if (type.equals("1")) {
                        rx_action = "unfollow";
                        rx_title = "unfollow";
                    } else if (type.equals("-1")) {
                        rx_action = "follow";
                        rx_title = "follow back";
                    }

                } else {
                    // if x and y follows each other => unfollow
                    // {"user_x":[{"user_y":"","type":"-1"},{"user_y":"","type":"1"}]}
                    Log.e("mutual rx", response);

                    rx_action = "unfollow";
                    rx_title = "unfollow";

                }
            }
            catch (JSONException e) {
                Log.e("json Ex", "json exception in fragment_profile");
            }
        }

        _rx.setText(rx_title);
        _rx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                update_rx();

            }
        });
    }

    private void update_rx() {

        String url = "http://api.pitavya.com/clipsy/";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Resource volley_wrapper", response.toString());

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_error_response(error);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("fx", rx_action);
                params.put("user_x", user_x);
                params.put("user_y", user_y);

                Log.i("makeParams Rx", params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);

        set_rx(user_x, user_y);


    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }


    public fragment_profile() {
        // Required empty public constructor
    }

    // follow, follow back, unfollow

    private void set_ui_vars() {

        _rx = V.findViewById(R.id.rx);
        _name = V.findViewById(R.id.fragment_profile_name);
        _choose_avatar_icon = V.findViewById(R.id.choose_avatar_icon);
        _email = V.findViewById(R.id.fragment_profile_email);
        fragment_profile_followers = V.findViewById(R.id.fragment_profile_followers);
        fragment_profile_following = V.findViewById(R.id.fragment_profile_following);

    }

    private void click_listeners(final String user_viewed) {

        final Intent to_profiles_list = new Intent(getActivity(), profiles_list.class);
        fragment_profile_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_profiles_list.putExtra("fx", "followers")
                        .putExtra("user_x", user_viewed);
                startActivity(to_profiles_list);
            }
        });

        fragment_profile_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_profiles_list.putExtra("fx", "following")
                        .putExtra("user_x", user_viewed);
                startActivity(to_profiles_list);
            }
        });


        _choose_avatar_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), choose_avatar.class).putExtra("id", user_y));
            }
        });

    }

    private void set_ui_text(String user_email, String user_name, String profile_pic){
        _email.setText(user_email);
        _name.setText(user_name);

        try{
            int _profile_pic = Integer.parseInt(profile_pic);
            int imageResource = CONSTANTS.mThumbIds[_profile_pic];
            _choose_avatar_icon.setImageResource(imageResource);

        }catch (NumberFormatException e) {
            Log.i("002", "nullformatexception"+ e.getMessage());
        }
    }
    
    private void set_rx(String user_x, String user_y) {

        if (user_x.equals(user_y)) {
            _rx.setVisibility(View.GONE);
        } else {
            make_request();
        }

    }

    private void set_variables_from_bundle() {
        if (getArguments() != null) {
            Log.i("fragment_profile", "nonempty bundle");

            profile_pic = getArguments().getString("profile_pic");
            user_name = getArguments().getString("name");
            user_email = getArguments().getString("email");
            user_x = getArguments().getString("user_id");

            if (getArguments().getString("type") != null) {
                type = getArguments().getString("type");
            }

            if (getArguments().containsKey("c_user_id")) {
                user_y = getArguments().getString("c_user_id");

            } else {
                user_y = user_x;
            }
        } else {
            Log.i("fragment_profile", "empty bundle");
        }
    }

    String profile_pic = "0";
    ImageView _choose_avatar_icon;
    String type, user_x, user_y, user_name, user_email, rx_title, rx_action;
    TextView _name, _email;
    Button _rx, fragment_profile_followers, fragment_profile_following;
    View V;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getActivity(), "fragment profile oncreate", Toast.LENGTH_SHORT).show();

        V = inflater.inflate(R.layout.fragment_profile, container, false);

        set_variables_from_bundle();
        set_ui_vars();
        set_ui_text(user_email, user_name, profile_pic);
        set_rx(user_x, user_y);
        click_listeners(user_y);

        return V;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
