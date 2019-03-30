package com.subhamkumar.clipsy.panel.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.models.ProfileMatrixApiResponse;
import com.subhamkumar.clipsy.panel.profile_result;
import com.subhamkumar.clipsy.panel.view_avatar;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_profile extends fragment_wrapper {

    String profile_pic = "0";
    private ImageView _choose_avatar_icon;
    private String id;
    private String searched_id;
    private TextView _name;
    private TextView _email;
    private Button relationshipButton;
    private TextView fragment_profile_followers;
    private TextView fragment_profile_following;
    private View V;
    private String token;

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
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        return stringStringHashMap;
    }

    @Override
    public void handle_response(String response) {

        refreshViewsAndRelationShipButton(response);

    }

    String viewedProfileName;

    private String getViewedProfileName() {
        return viewedProfileName;
    }

    private void setViewedProfileName(String name) {
        viewedProfileName = name;
    }


    private void refreshViewsAndRelationShipButton(String response) {
        Gson gson = new Gson();
        ProfileMatrixApiResponse profileMatrixApiResponse = gson.fromJson(response, ProfileMatrixApiResponse.class);
        setProfileElements(profileMatrixApiResponse.data.profile);
        setViewedProfileName(profileMatrixApiResponse.data.profile.name);
        relationshipButton.setText(profileMatrixApiResponse.message.equals("Following") ? "Unfollow" : profileMatrixApiResponse.message);
        setRelationshipAction(profileMatrixApiResponse.message, searched_id);
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);
    }

    public fragment_profile() {
    }

    private void addProfileClickActions(final String user_viewed) {
        fragment_profile_followers.setOnClickListener(view -> showPeopleDialog(user_viewed, id, token, "followers"));
        fragment_profile_following.setOnClickListener(view ->  showPeopleDialog(user_viewed, id, token, "following"));
        _choose_avatar_icon.setOnClickListener(view -> toProfileImage(user_viewed));
    }


    private RecyclerView rv_profile;
    private LinearLayoutManager linearLayoutManager;
    private com.subhamkumar.clipsy.adapter.profile_adapter profile_adapter;
    private List<Profile> profileList;


    private void showPeopleDialog(String viewedId, String viewerId, String token, String typOfPeople) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_connected_people);
        initForDialog(dialog);

        ImageView closeButton = dialog.findViewById(R.id.connectedPeopleCloseButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        TextView typeOfConnectedPeople = dialog.findViewById(R.id.typeOfConnectedPeople);
        typeOfConnectedPeople.setText( getViewedProfileName().concat(" ▶ ".concat(typOfPeople)) );

        getProfiles(dialog, typOfPeople, viewerId);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
    }

    private void initForDialog(Dialog dialog) {
        rv_profile = dialog.findViewById(R.id.connected_people);
        linearLayoutManager = new LinearLayoutManager(context);
        profileList = new ArrayList<>();

        profile_adapter = new profile_adapter(profileList);
        rv_profile.setAdapter(profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
    }

    private void getProfiles(Dialog dialog, String typeOfPeople, String viewerId) {


        String profileListUrl =  String.format( typeOfPeople.equals("following")
                ? getString(R.string.request_user_user_following) :
                getString(R.string.request_user_user_follower),
                viewerId);


        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                profileListUrl,
                response -> {

                    Gson gson = new Gson();
                    ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
                    profileList.clear();
                    for (int i=0; i < 100; i++) profileList.addAll(profileApiResponse.data);
                    profile_adapter.notifyDataSetChanged();
                    profileListClickToProfilePage();

                    dialog.show();
                },

                error -> handle_error_response(error)
        ) {

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<String, String>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(getString(R.string.header_authentication), token);
                return headers;
            }
        };


        Volley.newRequestQueue(context).add(stringRequest);

    }

    private void profileListClickToProfilePage() {

        rv_profile.addOnItemTouchListener(
               new RecyclerItemClickListener(context,
                       (view, position) -> gotToProfileResult(view))
        );
    }

    private void gotToProfileResult(View view) {
        Intent to_profile_result = new Intent(context, profile_result.class);

        String searchedUserId = ((TextView) view.findViewById(R.id.rl_profile_id)).getText().toString().trim();

        to_profile_result.putExtra(getString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId)
                         .putExtra(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                                    getString(R.string.bundle_param_caller_activity_fragment_profile_list_to_profile_result))
                         .putExtra(getString(R.string.params_token), getTokenFromBundle())
                         .putExtra(getString(R.string.params_id), getIdFromToken());


        startActivity(to_profile_result);
    }

    private Bundle bundle;

    private String getTokenFromBundle() {
        return bundle.getString(getString(R.string.params_token));
    }

    private String getIdFromToken() {
        return bundle.getString(getString(R.string.params_id));
    }


    private Context context;

    private void toProfileImage(String user_viewed) {
        startActivity(new Intent(getActivity(), view_avatar.class)
                .putExtra("id", id)
                .putExtra("searched_id", user_viewed)
                .putExtra("token", token));
    }

    private void toFollowing(String user_viewed, Intent to_profiles_list) {
        to_profiles_list.putExtra(getString(R.string.bundle_param_caller_button_to_profile_list),
                getString(R.string.bundle_param_caller_button_following))
                .putExtra(getString(R.string.params_search_id), user_viewed)
                .putExtra(getString(R.string.params_token), token)
                .putExtra(getString(R.string.params_id), id);
        startActivity(to_profiles_list);
    }

    private void toFollowers(String user_viewed, Intent to_profiles_list) {
        to_profiles_list.putExtra(getString(R.string.bundle_param_caller_button_to_profile_list),
                getString(R.string.bundle_param_caller_button_followers))
                .putExtra(getString(R.string.params_search_id), user_viewed)
                .putExtra(getString(R.string.params_token), token)
                .putExtra(getString(R.string.params_id), id);
        startActivity(to_profiles_list);
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
                        response -> refreshViewsAndRelationShipButton(response),

                        error -> {

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

                Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);

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
        id = Objects.requireNonNull(getArguments()).getString(getString(R.string.params_id));
        token = getArguments().getString(getString(R.string.params_token));
        searched_id = setSearchIdIfComingFromFragmentSearchOrCompleteProfileOrProfileList(id);
    }

    private String setSearchIdIfComingFromFragmentSearchOrCompleteProfileOrProfileList(String id) {
        String searchedId = id;
        if (Objects.requireNonNull(getArguments()).containsKey(getString(R.string.bundle_param_caller_activity_to_fragment_clips))) {
            if (Objects.requireNonNull(getArguments().getString(getString(R.string.bundle_param_caller_activity_to_fragment_clips)))
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        bundle = getArguments();

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
