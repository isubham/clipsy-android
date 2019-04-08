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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.subhamkumar.clipsy.utils.Tools;

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
    protected void handle_error_response(VolleyError error) {

        final Dialog noNetworkDialog = new Dialog(context);
        noNetworkDialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        noNetworkDialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            noNetworkDialog.dismiss();
        });

        noNetworkDialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            noNetworkDialog.dismiss();
            networkRetryRequest();
        });

        noNetworkDialog.show();

    }

    private NETWORKRETRY networkretry;

    private enum NETWORKRETRY {
        FOLLOWERS, FOLLOWING, PROFILE, RELATIONSHIPBUTTON;
    }

    private void networkRetryRequest() {
        Log.i("n/w retry", networkretry.name().toString());
        switch (networkretry) {
            case FOLLOWERS:
                getConnectedUsers();
                break;
            case FOLLOWING:
                getConnectedUsers();
                break;
            case PROFILE:
                fetchProfileMatrix();
                break;
            case RELATIONSHIPBUTTON:
                performRelationShipAction();
                break;
        }
    }

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
        networkretry = NETWORKRETRY.PROFILE;
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        return stringStringHashMap;
    }

    @Override
    public void handleResponse(String response) {

        refreshViewsAndRelationShipButton(response);
        Tools.hideNetworkLoadingDialog(networkRetryDialog, "profile hide");
        showProfileCardAndHideLoading();
    }

    private void showProfileCardAndHideLoading() {
        loadingContainer.stopShimmer();
        loadingContainer.setVisibility(View.GONE);
        V.findViewById(R.id.fragment_profile_contet_card).setVisibility(View.VISIBLE);
    }

    private void hideProfileCardAndShowLoading() {
        V.findViewById(R.id.fragment_profile_contet_card).setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
        loadingContainer.startShimmer();
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
        addRelationShipClickAction(profileMatrixApiResponse.message, searched_id);
    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);
    }

    public fragment_profile() {
    }

    private void addProfileClickActions(final String user_viewed) {
        fragment_profile_followers.setOnClickListener(view -> showPeopleDialog(user_viewed, id, token, "followers"));
        fragment_profile_following.setOnClickListener(view -> showPeopleDialog(user_viewed, id, token, "following"));
        _choose_avatar_icon.setOnClickListener(view -> toProfileImage(user_viewed));
    }


    private RecyclerView rv_profile;
    private LinearLayoutManager linearLayoutManager;
    private com.subhamkumar.clipsy.adapter.profile_adapter profile_adapter;
    private List<Profile> profileList;


    private void showPeopleDialog(String viewedId, String viewerId, String token, String typOfPeople) {

        final Dialog showPeopleDialog = new Dialog(context);
        showPeopleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(showPeopleDialog.getWindow()).setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);

        showPeopleDialog.setContentView(R.layout.dialog_connected_people);
        initializeShowPeopleDialogVariables(showPeopleDialog);

        ImageView closeButton = showPeopleDialog.findViewById(R.id.connectedPeopleCloseButton);
        closeButton.setOnClickListener(v -> showPeopleDialog.dismiss());

        TextView typeOfConnectedPeople = showPeopleDialog.findViewById(R.id.typeOfConnectedPeople);
        typeOfConnectedPeople.setText(getViewedProfileName().concat(" > ".concat(typOfPeople)));

        getProfiles(showPeopleDialog, typOfPeople, viewerId);
    }

    private void initializeShowPeopleDialogVariables(Dialog dialog) {
        rv_profile = dialog.findViewById(R.id.connected_people);
        linearLayoutManager = new LinearLayoutManager(context);
        profileList = new ArrayList<>();

        profile_adapter = new profile_adapter(profileList);
        rv_profile.setAdapter(profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
    }

    private StringRequest getConnectedUsers;

    private void getProfiles(Dialog dialog, String typeOfPeople, String viewerId) {

        String profileListUrl = String.format(typeOfPeople.equals("following")
                        ? getString(R.string.request_user_user_following) :
                        getString(R.string.request_user_user_follower),
                viewerId);


        getConnectedUsers = new StringRequest(
                Request.Method.GET,
                profileListUrl,
                response -> {

                    Gson gson = new Gson();
                    ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
                    profileList.clear();
                    profileList.addAll(profileApiResponse.data);
                    profile_adapter.notifyDataSetChanged();
                    profileListClickToProfilePage();

                    dialog.show();

                    Tools.hideNetworkLoadingDialog(networkRetryDialog, "profile hide");
                },

                error -> {
                    networkretry = typeOfPeople.equals("following") ? NETWORKRETRY.FOLLOWING : NETWORKRETRY.FOLLOWERS;
                    handle_error_response(error);
                }
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


        getConnectedUsers();

    }

    private void getConnectedUsers() {
        Volley.newRequestQueue(context).add(getConnectedUsers);
        Tools.showNetworkLoadingDialog(networkRetryDialog, "profile getConnected users show");
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

    StringRequest relationShipStringRequest;

    private void addRelationShipClickAction(final String message, final String searched_id) {

        relationshipButton.setOnClickListener(v -> {

            relationShipStringRequest = new StringRequest(
                    Request.Method.POST,
                    getUrlByResponseMessage(message, searched_id),
                    response -> {
                        refreshViewsAndRelationShipButton(response);
                        Tools.hideNetworkLoadingDialog(networkRetryDialog, "hide relationship click");
                    },
                    error -> {
                        networkretry = NETWORKRETRY.RELATIONSHIPBUTTON;
                        handle_error_response(error);
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

            performRelationShipAction();

        });
    }

    private void performRelationShipAction() {
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(relationShipStringRequest);
        Tools.showNetworkLoadingDialog(networkRetryDialog, "profile show");
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
        loadingContainer = V.findViewById(R.id.rl_profile_loading_container);

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
        if (getArguments().containsKey(getString(R.string.bundle_param_caller_activity_to_fragment_clips))) {
            return getArguments().getString(getString(R.string.params_id));
        }
        return searchedId;
    }

    ViewGroup fragment_profile;
    Dialog networkRetryDialog;
    ShimmerFrameLayout loadingContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        bundle = getArguments();
        networkRetryDialog = new Dialog(context, R.style.CustomDialogTheme);


        V = inflater.inflate(R.layout.fragment_profile, container, false);
        fragment_profile = (ViewGroup) V;
        findViewByIds();
        hideProfileCardAndShowLoading();

        try {
            setVariablesFromBundle();
            hideRelationshipButtonIfSameUser(id, searched_id);
        } catch (NumberFormatException e) {
            Log.e("0097", e.getMessage());
        }
        if (getUserVisibleHint()) {
            fetchProfileMatrix();
            addProfileClickActions(searched_id);
        }
        return V;
    }

    private void fetchProfileMatrix() {
        Tools.showNetworkLoadingDialog(networkRetryDialog, "profile show");
        make_request();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            fetchProfileMatrix();
            addProfileClickActions(searched_id);
        }
    }


}
