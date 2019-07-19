package com.subhamkumar.clipsy.panel.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.panel.profile_result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class fragment_search extends fragment_wrapper {
    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    protected void handle_error_response(VolleyError error) {


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            fetchSearchResult();
        });

        dialog.show();

    }

    @Override
    public String setHttpUrl() {
        String url = String.format(getString(R.string.request_user_search_user), query);
        return url;
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
    public void handleResponse(String response) {
        Log.i("search_response", response);

        Gson gson = new Gson();
        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
        profileList.clear();
        hideLoadingAndShowCntent(loadingContainer, content);
        profileList.addAll(profileApiResponse.data);
        profile_adapter.notifyDataSetChanged();
    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);
    }


    private RecyclerView rv_profile;
    private LinearLayoutManager linearLayoutManager;
    private profile_adapter profile_adapter;
    private List<Profile> profileList;

    private EditText live_search;

    private void init(View V) {
        rv_profile = V.findViewById(R.id.profile_fragment_recycleview);
        content = V.findViewById(R.id.profile_fragment_recycleview);
        loadingContainer = V.findViewById(R.id.rl_fragment_search_loading_container);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        profileList = new ArrayList<Profile>();
        profile_adapter = new profile_adapter(profileList) {
            @Override
            protected void addViewClickListeners(View V) {

                setClickListenerOnProfileCard(V);

            }
        };
        rv_profile.setLayoutManager(linearLayoutManager);
        rv_profile.setAdapter(profile_adapter);
        live_search = V.findViewById(R.id.live_search);
        live_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                fillSearchResult(live_search.getText().toString().trim());
            }
        });

    }

    private void setClickListenerOnProfileCard(View V) {


        V.findViewById(R.id.rl_profile_close).setOnClickListener(v -> {
            String searchedUserId = ((TextView) V.findViewById(R.id.rl_profile_id)).getText().toString().trim();
            deleteSearchResult(searchedUserId);
        });

        V.findViewById(R.id.rl_profile_name).setOnClickListener(v -> {
            String searchedUserId = ((TextView) V.findViewById(R.id.rl_profile_id)).getText().toString().trim();
            gotToProfileResult(searchedUserId);
        });
        V.findViewById(R.id.rl_profile_profile_pic).setOnClickListener(v -> {
            String searchedUserId = ((TextView) V.findViewById(R.id.rl_profile_id)).getText().toString().trim();
            gotToProfileResult(searchedUserId);
        });
        V.findViewById(R.id.rl_profile_email).setOnClickListener(v -> {
            String searchedUserId = ((TextView) V.findViewById(R.id.rl_profile_id)).getText().toString().trim();
            gotToProfileResult(searchedUserId);
        });
    }

    private void deleteSearchResult(String deleteProfileId) {


        StringRequest deleteProfileFromSearch = new StringRequest(
                Request.Method.POST,
                String.format(getString(R.string.request_user_delete_searched), deleteProfileId),
                response -> {
                    Log.i("deleteProfile", response);

                    Gson gson = new Gson();
                    ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
                    profileList.clear();
                    setProfileCrssIconFlag(profileApiResponse.data);
                    hideLoadingAndShowCntent(loadingContainer, content);
                    profileList.addAll(profileApiResponse.data);
                    profile_adapter.notifyDataSetChanged();
                },
                error -> {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

                    dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
                        dialog.dismiss();
                    });

                    dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
                        dialog.dismiss();
                        // TODO add retry logic
                        deleteSearchResult(deleteProfileId);
                    });

                    dialog.show();
                }

        ) {

            @Override
            protected Map<String, String> getParams() {
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map params = new HashMap<String, String>();
                params.put(getString(R.string.header_authentication), token);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(deleteProfileFromSearch);

    }


    private void GetTop10Search() {
        StringRequest getTop10Searches = new StringRequest(
                Request.Method.GET,
                getString(R.string.request_user_searched_top10),
                response -> {
                    Log.i("top10Search", response);

                    ClearAndFillSearchResult(response, true);
                },
                error -> {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

                    dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
                        dialog.dismiss();
                    });

                    dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
                        dialog.dismiss();
                        // TODO add retry logic
                        GetTop10Search();
                    });

                    dialog.show();
                }

        ) {

            @Override
            protected Map<String, String> getParams() {
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map params = new HashMap<String, String>();
                params.put(getString(R.string.header_authentication), token);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(getTop10Searches);

    }

    private void ClearAndFillSearchResult(String response, boolean showCrossIcon) {
        profileList.clear();
        Gson gson = new Gson();
        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
        if (showCrossIcon) setProfileCrssIconFlag(profileApiResponse.data);
        hideLoadingAndShowCntent(loadingContainer, content);
        profileList.addAll(profileApiResponse.data);
        profile_adapter.notifyDataSetChanged();
    }

    private void setProfileCrssIconFlag(List<Profile> profileApiResponse) {

        for (int profile = 0; profile < profileApiResponse.size(); profile++) {
            profileApiResponse.get(profile).showCloseIcon = "1";
            Log.i("CROSS", profileApiResponse.get(profile).name + " " + profileApiResponse.get(profile).showCloseIcon);
        }
    }

    private void saveSearchResult(String searchedUserId) {

        StringRequest saveResult = new StringRequest(
                Request.Method.POST,
                String.format(getString(R.string.request_user_searched_user), searchedUserId),
                response -> {
                    ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
                    // TODO handle profile
                },
                error -> {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

                    dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
                        dialog.dismiss();
                    });

                    dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
                        dialog.dismiss();
                        saveSearchResult(searchedUserId);
                    });

                    dialog.show();
                }

        ) {

            @Override
            protected Map<String, String> getParams() {
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map params = new HashMap<String, String>();
                params.put(getString(R.string.header_authentication), token);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(saveResult);

    }


    private void gotToProfileResult(String searchedUserId) {

        Intent to_profile_result = new Intent(getActivity(), profile_result.class);

        saveSearchResult(searchedUserId);

        userDetails.putString(getString(R.string.params_token), token);
        userDetails.putString(getString(R.string.params_id), id);
        userDetails.putString(getString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId);
        userDetails.putString(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                getString(R.string.bundle_param_caller_activity_fragment_search));

        to_profile_result.putExtras(userDetails);
        startActivity(to_profile_result);
    }

    private String query;

    private void fillSearchResult(CharSequence charSequence) {
        query = charSequence.toString();
        if (query.length() > 2) {
            fetchSearchResult();
        }
        if (query.length() == 0){
            GetTop10Search();
        }

    }

    private void fetchSearchResult() {
        make_request();
    }

    private Context context;
    private Bundle userDetails;
    private String token;
    private View fragment_search;
    private ShimmerFrameLayout loadingContainer;
    private String id;

    private void showLoadingAndHideCntent(View V, View content) {
        loadingContainer.setVisibility(View.VISIBLE);
        loadingContainer.startShimmer();
        content.setVisibility(View.GONE);
    }

    private void hideLoadingAndShowCntent(View V, View content) {
        loadingContainer.setVisibility(View.GONE);
        loadingContainer.startShimmer();
        content.setVisibility(View.VISIBLE);
    }

    RecyclerView content;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        userDetails = getArguments();
        token = Objects.requireNonNull(userDetails).getString(Constants.TOKEN);
        id = Objects.requireNonNull((userDetails).getString("id"));
        context = getActivity();


        fragment_search = inflater.inflate(R.layout.fragment_search, container, false);
        init(fragment_search);
        showLoadingAndHideCntent(loadingContainer, content);
        showKeyboarWhenSearchFragmentLoads();

        GetTop10Search();
        return fragment_search;
    }

    private void showKeyboarWhenSearchFragmentLoads() {
        // to show keyboard
        live_search.requestFocus();
        InputMethodManager mgr = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(live_search, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
    }

}
