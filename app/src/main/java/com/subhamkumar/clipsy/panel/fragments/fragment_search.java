package com.subhamkumar.clipsy.panel.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static com.subhamkumar.clipsy.utils.Message.fragmentSearchToProfileResult;


public class fragment_search extends fragment_wrapper implements SearchView.OnQueryTextListener {


    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    protected void handle_error_response(VolleyError error) {


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            fetchSearchResult();
        });

        dialog.show();

    }

    @Override
    public String setHttpUrl() {
        return String.format(getString(R.string.request_user_search_user), query);
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


    private profile_adapter profile_adapter;
    private List<Profile> profileList;

    private EditText live_search;

    private void init(View V) {
        RecyclerView rv_profile = V.findViewById(R.id.profile_fragment_recycleview);
        content = V.findViewById(R.id.profile_fragment_recycleview);
        loadingContainer = V.findViewById(R.id.rl_fragment_search_loading_container);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        profileList = new ArrayList<>();
        profile_adapter = new profile_adapter(profileList) {
            @Override
            protected void addViewClickListeners(View V) {

                setClickListenerOnProfileCard(V);

            }
        };
        rv_profile.setLayoutManager(linearLayoutManager);
        rv_profile.setAdapter(profile_adapter);

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

                    dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

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

                    ClearAndFillSearchResult(response);
                },
                error -> {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

                    dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

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

    private void ClearAndFillSearchResult(String response) {
        profileList.clear();
        Gson gson = new Gson();
        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
        if (true) setProfileCrssIconFlag(profileApiResponse.data);
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

                    dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

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
        to_profile_result.putExtras(fragmentSearchToProfileResult(token, id, searchedUserId, Constants.fromActivity_Activity));
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
    private String token;
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

    private RecyclerView content;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle userDetails = getArguments();
        token = Objects.requireNonNull(userDetails).getString(Constants.TOKEN);
        id = Objects.requireNonNull((userDetails).getString("id"));
        context = getActivity();


        View fragment_search = inflater.inflate(R.layout.fragment_search, container, false);
        init(fragment_search);
        showLoadingAndHideCntent(loadingContainer, content);

        GetTop10Search();

        setHasOptionsMenu(true);

        if (searchView!=null) {
        }
        return fragment_search;
    }

    SearchView searchView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_icon);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(Constants.EMAIL_OR_NAME);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        fillSearchResult(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        fillSearchResult(newText);
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
