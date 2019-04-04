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

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.panel.profile_result;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;
import com.subhamkumar.clipsy.utils.Tools;

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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        profileList = new ArrayList<Profile>();
        profile_adapter = new profile_adapter(profileList);
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

        rv_profile.addOnItemTouchListener(
               new RecyclerItemClickListener(getActivity(),
                       (view, position) -> gotToProfileResult(view))
        );
    }


    private void gotToProfileResult(View view) {
        Intent to_profile_result = new Intent(getActivity(), profile_result.class);

        String searchedUserId = ((TextView) view.findViewById(R.id.rl_profile_id)).getText().toString().trim();

        userDetails.putString(getString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId);
        userDetails.putString(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                getString(R.string.bundle_param_caller_activity_fragment_search));

        to_profile_result.putExtras(userDetails);
        startActivity(to_profile_result);
    }

    private String query;
    private void fillSearchResult(CharSequence charSequence){
         query = charSequence.toString();
         if(query.length() > 2) {
             fetchSearchResult();
         }
         else {
             profileList.clear();
             profile_adapter.notifyDataSetChanged();
         }

    }

    private void fetchSearchResult() {
        make_request();
    }

    private Context context;
    private Bundle userDetails;
    private String token;
    private View fragment_search;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        userDetails = getArguments();
        token = Objects.requireNonNull(userDetails).getString(Constants.TOKEN);
        context = getActivity();

        fragment_search = inflater.inflate(R.layout.fragment_search, container, false);
        init(fragment_search);
        showKeyboarWhenSearchFragmentLoads();

        return fragment_search;
    }

    private void showKeyboarWhenSearchFragmentLoads() {
        // to show keyboard
        live_search.requestFocus();
        InputMethodManager mgr = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(live_search, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint())
        {
            return;
        }
    }

}
