package com.subhamkumar.clipsy.panel.fragments;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileMatrixApiResponse;
import com.subhamkumar.clipsy.panel.profiles_list;
import com.subhamkumar.clipsy.panel.view_avatar;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_profile extends fragment_wrapper {

    String profile_pic = "0";
    ImageView _choose_avatar_icon;
    String id, searched_id;
    TextView _name, _email;
    Button relationshipButton, fragment_profile_followers, fragment_profile_following;
    View V;
    String token;

    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public String setHttpUrl() {
        return String.format(getString(R.string.request_user_user_profile_matrix), searched_id);
    }

    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.header_authentication), token);
        return params;
    }

    @Override
    public Map makeParams() {
        return null;
    }

    @Override
    public void handle_response(String response) {

        refreshViewsAndRelationShipButton(response);

    }

    private void refreshViewsAndRelationShipButton(String response) {
        Gson gson = new Gson();
        ProfileMatrixApiResponse profileMatrixApiResponse = gson.fromJson(response, ProfileMatrixApiResponse.class);
        setProfileElements(profileMatrixApiResponse.data.profile);
        relationshipButton.setText(profileMatrixApiResponse.message);
        setRelationshipAction(profileMatrixApiResponse.message, searched_id);
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    public fragment_profile() {
    }

    private void addProfileClickActions(final String user_viewed) {

        final Intent to_profiles_list = new Intent(getActivity(), profiles_list.class);

        fragment_profile_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_profiles_list.putExtra(getString(R.string.bundle_param_caller_button_to_profile_list),
                        getString(R.string.bundle_param_caller_button_followers))
                        .putExtra(getString(R.string.params_search_id), user_viewed)
                        .putExtra(getString(R.string.params_token), token)
                        .putExtra(getString(R.string.params_id), id);
                startActivity(to_profiles_list);
            }
        });

        fragment_profile_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                to_profiles_list.putExtra(getString(R.string.bundle_param_caller_button_to_profile_list),
                        getString(R.string.bundle_param_caller_button_following))
                        .putExtra(getString(R.string.params_search_id), user_viewed)
                        .putExtra(getString(R.string.params_token), token)
                        .putExtra(getString(R.string.params_id), id);
                startActivity(to_profiles_list);
            }
        });

        _choose_avatar_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), view_avatar.class)
                        .putExtra("id", id)
                        .putExtra("searched_id", user_viewed)
                        .putExtra("token", token));
            }
        });

    }

    private String getUrlByResponseMessage(String message, String searched_id) {
        switch (message) {
            case "Follow":
                return String.format(getString(R.string.request_user_user_follow), searched_id);
            case "Following":
                return String.format(getString(R.string.request_user_user_unfollow), searched_id);
            default:
                return String.format(getString(R.string.request_user_user_follow), searched_id);
        }
    }

    private void setRelationshipAction(final String message, final String searched_id) {

        relationshipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        getUrlByResponseMessage(message, searched_id),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                refreshViewsAndRelationShipButton(response);

                            }

                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                ) {

                    @Override
                    protected Map<String, String> getParams() {
                        return null;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map headers = new HashMap<String, String>();
                        headers.put(getString(R.string.header_authentication), token);
                        return headers;
                    }
                };

                Volley.newRequestQueue(getActivity()).add(stringRequest);

            }
        });
    }

    private void hideRelationshipButtonIfSameUser(String user_x, String user_y) {

        if (user_x.equals(user_y)) {
            relationshipButton.setVisibility(View.GONE);
        }

    }

    private void setProfileElements(Profile profile) {
        _email.setText(profile.email);
        _name.setText(profile.name);
        int _profile_pic;
        try {
            _profile_pic = Integer.parseInt(profile.profile_pic);
        } catch (NumberFormatException e) {
            _profile_pic = 0;
        }
        int imageResource = Constants.mThumbIds[_profile_pic];
        _choose_avatar_icon.setImageResource(imageResource);
    }

    private void findViewByIds() {

        relationshipButton = V.findViewById(R.id.rx);
        _name = V.findViewById(R.id.fragment_profile_name);
        _choose_avatar_icon = V.findViewById(R.id.choose_avatar_icon);
        _email = V.findViewById(R.id.fragment_profile_email);
        fragment_profile_followers = V.findViewById(R.id.fragment_profile_followers);
        fragment_profile_following = V.findViewById(R.id.fragment_profile_following);

    }

    private void setVariablesFromBundle() {
        id = getArguments().getString(getString(R.string.params_id));
        token = getArguments().getString(getString(R.string.params_token));
        searched_id = setSearchIdIfComingFromFragmentSearchOrCompleteProfileOrProfileList(id);
    }

    private String setSearchIdIfComingFromFragmentSearchOrCompleteProfileOrProfileList(String id) {
        String searchedId = id;
        if (getArguments().containsKey(getString(R.string.bundle_param_caller_activity_to_fragment_clips))) {
            if (getArguments().getString(getString(R.string.bundle_param_caller_activity_to_fragment_clips))
                    .equals(getString(R.string.bundle_param_caller_activity_fragment_search))) {
                return getArguments().getString(getString(R.string.bundle_param_profile_result_searched_user_id));
            }
        }
        if (getArguments().containsKey(getString(R.string.bundle_param_profile_result_searched_user_id))) {
            return getArguments().getString(getString(R.string.bundle_param_profile_result_searched_user_id));
        }
        if(getArguments().containsKey(getString(R.string.bundle_param_caller_activity_to_fragment_clips))){
            return getArguments().getString(getString(R.string.params_id));
        }
        return searchedId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        V = inflater.inflate(R.layout.fragment_profile, container, false);
        findViewByIds();
        try {
            setVariablesFromBundle();
            hideRelationshipButtonIfSameUser(id, searched_id);
        }catch (NumberFormatException e) {
            Log.e("0097", e.getMessage());
        }
        make_request();
        addProfileClickActions(searched_id);
        return V;
    }

    @Override
    public void onResume() {
        make_request();
        super.onResume();
    }
}
