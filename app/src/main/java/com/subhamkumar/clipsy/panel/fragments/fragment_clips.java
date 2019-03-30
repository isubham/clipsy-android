package com.subhamkumar.clipsy.panel.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.clip_adapter;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.editor;
import com.subhamkumar.clipsy.panel.profile_result;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class fragment_clips extends fragment_wrapper {
    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public Map makeParams() {
        return new HashMap<String, String>();
    }

    @Override
    public String setHttpUrl() {
        String from = Objects.requireNonNull(getArguments()).getString(getString(R.string.bundle_param_caller_activity_to_fragment_clips));
        return getUrl(from);
    }

    @Override
    public Map<String, String> _getHeaders() {
        Map headers = new HashMap<String, String>();
        headers.put(getString(R.string.header_authentication), token);
        return headers;
    }

    // token => clips list
    public fragment_clips() {
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }


    // int no_of_intent;
    @Override
    public void handle_response(String response) {

        Gson gson = new Gson();
        ClipApiResonse clipApiResonse = gson.fromJson(response, ClipApiResonse.class);
        clipList.clear();

        for (int i = 0; i < clipApiResonse.data.size(); i++)
            clipApiResonse.data.get(i).viewer_id = id;

        clipList.addAll(clipApiResonse.data);
        clip_adapter.notifyDataSetChanged();

    }

    private void addClipFullyScrolledLisentener() {
        /*
        rv_clip.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 1 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    //you have reached to the bottom of your recycler view
                    Toast.makeText(context, "fetch new clips", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
    }

    private void addClickListener(View V) {


        V.findViewById(R.id.rl_clip_author).setOnClickListener(v -> gotoProfileResult(V));
        V.findViewById(R.id.rl_clip_profile_pic).setOnClickListener(v -> gotoProfileResult(V));
        V.findViewById(R.id.rl_clip_menu).setOnClickListener(v -> showDialog(V));


    }

    private void showDialog(View V) {
        String author_id = ((TextView) V.findViewById(R.id.rl_clip_author_id)).getText().toString();
        String viewer_id = ((TextView) V.findViewById(R.id.rl_clip_viewer_id)).getText().toString();
        String clip_id = ((TextView) V.findViewById(R.id.rl_clip_id)).getText().toString();
        setSelectedClipId(clip_id);
        if (author_id.equals(viewer_id)) {
            showSameUserDialog(author_id, clip_id);
        } else {
            showDifferentUserDialog(author_id);
        }

    }

    private void showDifferentUserDialog(String authorId) {

        final Dialog dialog = new Dialog(context);
        hidetitleOFDialog(dialog);
        dialog.setContentView(R.layout.dialog_other_user_clip_menu_click);

        dialog.findViewById(R.id.dialog_other_user_show_profile).setOnClickListener(v -> {
            gotoProfileResult(authorId);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.dialog_other_user_menu_close).setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    private void setSelectedClipId(String clip_id) {
        selected_clip_id = clip_id;
    }

    private String getSelectedClipId() {
        return selected_clip_id;
    }


    private static String selected_clip_id;

    private void showSameUserDialog(String authorId, String clipId) {
        final Dialog dialog = new Dialog(context);
        hidetitleOFDialog(dialog);
        dialog.setContentView(R.layout.dialog_same_user_clip_menu_click);

        TextView editAction = dialog.findViewById(R.id.dialog_same_user_menu_edit);
        TextView deleteAction = dialog.findViewById(R.id.dialog_same_user_menu_delete);
        TextView closeAction = dialog.findViewById(R.id.dialog_same_user_menu_close);

        closeAction.setOnClickListener(v -> dialog.dismiss());

        editAction.setOnClickListener(v -> {
            gotoClip(v, "update", authorId, clipId);
            dialog.dismiss();
        });

        deleteAction.setOnClickListener(v -> {
            dialog.dismiss();
            showConfirmationToDelete(v, "delete", authorId, clipId);
        });

        dialog.show();

    }

    private void showConfirmationToDelete(View V, String action, String authorId, String clipId) {

        final Dialog dialog = new Dialog(context);
        hidetitleOFDialog(dialog);
        dialog.setContentView(R.layout.dialog_delete_clip_confirmation);
        TextView deleteAction = dialog.findViewById(R.id.dialog_delete_clip_menu_delete);
        TextView closeAction = dialog.findViewById(R.id.dialog_delete_clip_menu_close);

        deleteAction.setOnClickListener(v -> {
            deleteClip(getSelectedClipId());

            dialog.dismiss();
        });

        closeAction.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void hidetitleOFDialog(Dialog dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void deleteClip(String clipId) {

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                String.format(Constants.request_clip_delete, clipId),
                response -> {

                    ApiResponse deleteApiResponse = new Gson().fromJson(response, ApiResponse.class);
                    Toast.makeText(context, deleteApiResponse.message, Toast.LENGTH_SHORT).show();
                    updateClips();

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

    private Context context;

    private void gotoClip(View V, String action, String authorId, String clipId) {

        startActivity(new Intent(getActivity(), editor.class)
                .putExtra("token", token).putExtra("action", action)
                .putExtra("id", authorId).putExtra("clip_id", clipId)
                .putExtra("clip", "clip"));
    }

    private void gotoProfileResult(View V) {
        String searchedUserId = ((TextView) V.findViewById(R.id.rl_clip_author_id)).getText().toString();

        gotoProfileResult(searchedUserId);
    }

    private void gotoProfileResult(String searchedUserId) {
        Bundle toProfileResult = new Bundle();
        toProfileResult.putString(getString(R.string.params_token), token);
        toProfileResult.putString(getString(R.string.params_id), id);
        toProfileResult.putString(getString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId);

        toProfileResult.putString(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                getString(R.string.bundle_param_caller_activity_fragment_search));

        startActivity(new Intent(getActivity(), profile_result.class).putExtras(toProfileResult));
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(context).add(stringRequest);
    }

    private RecyclerView rv_clip;
    private LinearLayoutManager linearLayoutManager;
    private com.subhamkumar.clipsy.adapter.clip_adapter clip_adapter;
    private List<Clip> clipList;


    private void init(View V) {
        // no_of_intent = 0;
        rv_clip = V.findViewById(R.id.clip_fragment_recycleview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        clipList = new ArrayList<>();

        context = getActivity();

        clip_adapter = new clip_adapter(clipList) {
            @Override
            public void addViewClickListeners(View V) {
                addClickListener(V);
            }
        };

        rv_clip.setLayoutManager(linearLayoutManager);
        rv_clip.setAdapter(clip_adapter);

        updateClips();

        addClipFullyScrolledLisentener();

    }

    private void updateClips() {
        make_request();
    }

    private static String token;
    private String id;
    private String searched_id;
    String _fx;
    private View V;

    private String getUrl(String from) {
        String fromPanel = getString(R.string.bundle_param_caller_activity_panel);
        String fromSearch = getString(R.string.bundle_param_caller_activity_fragment_search);
        String fromProfileResult = getString(R.string.bundle_param_caller_activity_fragment_profile_list_to_profile_result);

        if (from != null) {
            if (from.equals(fromPanel)) {
                return getString(R.string.request_clip_following);
            }
            if (from.equals(fromSearch) || from.equals(fromProfileResult)) {
                searched_id = Objects.requireNonNull(getArguments()).getString(getString(R.string.bundle_param_profile_result_searched_user_id));
                return String.format(getString(R.string.request_clip_reads_user), searched_id);
            }
        }

        return getString(R.string.request_clip_reads);

    }


    private Bundle bundle;
    private String from;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        bundle = getArguments();

        try {
            id = bundle.getString(getString(R.string.params_id));
            token = bundle.getString(getString(R.string.params_token));
            from = bundle.getString(getString(R.string.bundle_param_caller_activity_to_fragment_clips));

        } catch (NullPointerException e) {
            Toast.makeText(context, "null on" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("null on", e.getMessage());
        }


        V = inflater.inflate(R.layout.fragment_clips, container, false);
        context = Objects.requireNonNull(container).getContext();
        init(V);
        return V;
    }

    @Override
    public void onResume() {
        init(V);
        super.onResume();
    }

}
