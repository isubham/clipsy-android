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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.clip_adapter;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.panel.editor;
import com.subhamkumar.clipsy.panel.profile_result;
import com.subhamkumar.clipsy.utils.Tools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class fragment_clips extends fragment_wrapper {

    private Dialog networkLoadingDialog;

    @Override
    protected void handle_error_response(VolleyError error) {

        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "clips hide");
        showNetworkUnavailableDialog();

    }

    private void showNetworkUnavailableDialog() {
        final Dialog networkUnavailableDialog = new Dialog(context);
        networkUnavailableDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        networkUnavailableDialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        networkUnavailableDialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            networkUnavailableDialog.dismiss();
        });

        networkUnavailableDialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            networkUnavailableDialog.dismiss();
            updateClips();
        });

        networkUnavailableDialog.show();
    }

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
    public void handleResponse(String response) {
        Gson gson = new Gson();
        ClipApiResonse clipApiResonse = gson.fromJson(response, ClipApiResonse.class);
        clipList.clear();


        for (int i = 0; i < clipApiResonse.data.size(); i++)
            clipApiResonse.data.get(i).viewer_id = id;

        clipList.addAll(clipApiResonse.data);
        clip_adapter.notifyDataSetChanged();


        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "clips hide");
        loadingContainer.stopShimmer();
        loadingContainer.setVisibility(View.GONE);
    }

    private void addClipFullyScrolledLisentener() {
        // TODO add 20 elements at first and then fetch when needed
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
        V.findViewById(R.id.rl_clip_menu).setOnClickListener(v -> clipMenuClickedDialog(V));
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

    private void clipMenuClickedDialog(View V) {
        String author_id = ((TextView) V.findViewById(R.id.rl_clip_author_id)).getText().toString();
        String viewer_id = ((TextView) V.findViewById(R.id.rl_clip_viewer_id)).getText().toString();
        String clip_id = ((TextView) V.findViewById(R.id.rl_clip_id)).getText().toString();
        setSelectedClipId(clip_id);
        if (author_id.equals(viewer_id)) {
            showSameUserDialog(author_id, clip_id);
        } else {
            showDifferentUserDialog(author_id);
            setSelectedUserId(author_id);
        }
    }

    private void showDifferentUserDialog(String authorId) {

        final Dialog other_user_menu_click_dialog = new Dialog(context);
        hidetitleofdialog(other_user_menu_click_dialog);
        other_user_menu_click_dialog.setContentView(R.layout.dialog_other_user_clip_menu_click);

        other_user_menu_click_dialog.findViewById(R.id.dialog_other_user_show_report).setOnClickListener(v -> {
            showReportDialog(v);
            other_user_menu_click_dialog.dismiss();
        });
        other_user_menu_click_dialog.findViewById(R.id.dialog_other_user_menu_close).setOnClickListener(v -> other_user_menu_click_dialog.dismiss());

        other_user_menu_click_dialog.show();

    }

    private static String selected_clip_id;

    private void setSelectedClipId(String clip_id) {
        selected_clip_id = clip_id;
    }

    private String getSelectedClipId() {
        return selected_clip_id;
    }

    private static String selected_user_id;

    private void setSelectedUserId(String user) {
        selected_user_id = user;
    }

    private String getSelectedUserId() {
        return selected_user_id;
    }

    private void showSameUserDialog(String authorId, String clipId) {
        final Dialog dialog = new Dialog(context);
        hidetitleofdialog(dialog);
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

        final Dialog delete_clip_confirmation = new Dialog(context);
        hidetitleofdialog(delete_clip_confirmation);
        delete_clip_confirmation.setContentView(R.layout.dialog_delete_clip_confirmation);
        TextView deleteAction = delete_clip_confirmation.findViewById(R.id.dialog_delete_clip_menu_delete);
        TextView closeAction = delete_clip_confirmation.findViewById(R.id.dialog_delete_clip_menu_close);

        deleteAction.setOnClickListener(v -> {
            deleteClip(getSelectedClipId());
            updateClips();
            delete_clip_confirmation.dismiss();
        });

        closeAction.setOnClickListener(v -> {
            delete_clip_confirmation.dismiss();
        });

        delete_clip_confirmation.show();

    }

    private void hidetitleofdialog(Dialog dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void deleteClip(String clipId) {

        StringRequest deleteClipRequest = new StringRequest(
                Request.Method.POST,
                String.format(Constants.request_clip_delete, clipId),
                response -> {

                    Tools.hideNetworkLoadingDialog(networkLoadingDialog, "clips hide");
                    ApiResponse deleteApiResponse = new Gson().fromJson(response, ApiResponse.class);
                    Toast.makeText(context, deleteApiResponse.message, Toast.LENGTH_SHORT).show();

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


        makeDeleteRequest(deleteClipRequest);
    }

    private void makeDeleteRequest(StringRequest deleteClipRequest) {
        Volley.newRequestQueue(context).add(deleteClipRequest);
        Tools.showNetworkLoadingDialog(networkLoadingDialog, "clip delete show");
    }

    // TODO handle loading bar.
    private void makeSendReportRequest(StringRequest sendReportRequest) {
        Volley.newRequestQueue(context).add(sendReportRequest);
        Tools.showNetworkLoadingDialog(networkLoadingDialog, "report send show");
    }

    private Context context;
    private int clipUpdateRequestCode = 123;

    private void gotoClip(View V, String action, String authorId, String clipId) {

        Bundle toClip = new Bundle();
        toClip.putString("token", token);
        toClip.putString("action", action);
        toClip.putString("id", authorId);
        toClip.putString("clip_id", clipId);
        toClip.putString("clip", "clip");
        Intent toClipIntent = new Intent(getActivity(), editor.class);
        toClipIntent.putExtras(toClip);
        startActivityForResult(toClipIntent, clipUpdateRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == clipUpdateRequestCode && resultCode == RESULT_OK) {
            updateClips();
        }
    }

    private void showReportDialog(View V) {
        showReportDialog(getSelectedUserId(), getSelectedClipId());
    }

    private void showReportDialog(String searchedUserId, String clipId) {

        // show dialog
        final Dialog reportDialog = new Dialog(context);
        hidetitleofdialog(reportDialog);
        reportDialog.setContentView(R.layout.dialog_other_user_clip_menu_click_report);

        // handle click actions
        reportDialog.findViewById(R.id.dialog_other_user_show_report_inappropriate)
                .setOnClickListener(v -> {

                    sendReport("1", searchedUserId, token, clipId);
                    reportDialog.dismiss();

                });

        reportDialog.findViewById(R.id.dialog_other_user_show_report_spam)
                .setOnClickListener(v -> {

                    sendReport("1", searchedUserId, token, clipId);
                    reportDialog.dismiss();


                });

        reportDialog.findViewById(R.id.dialog_other_user_show_report_close)
                .setOnClickListener(v -> {

                    reportDialog.dismiss();

                });

        reportDialog.show();
    }

    private void sendReport(String problem, String reportedId, String token, String clipId) {

        StringRequest sendReport = new StringRequest(
                Request.Method.POST,
                String.format(Constants.request_report_send, clipId),
                response -> {

                    Tools.hideNetworkLoadingDialog(networkLoadingDialog, "send report hide");
                    ApiResponse sendReportApiResponse = new Gson().fromJson(response, ApiResponse.class);
                    Toast.makeText(context, sendReportApiResponse.message, Toast.LENGTH_SHORT).show();

                },

                error -> handle_error_response(error)
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map params = new HashMap<String, String>();
                params.put("clipId", clipId);
                params.put("reportedId", reportedId);
                params.put("problem", problem);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(getString(R.string.header_authentication), token);
                return headers;
            }
        };


        makeSendReportRequest(sendReport);
    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(context).add(stringRequest);
    }

    private RecyclerView rv_clip;
    private LinearLayoutManager linearLayoutManager;
    private com.subhamkumar.clipsy.adapter.clip_adapter clip_adapter;
    private List<Clip> clipList;
    ViewGroup fragment_clip;


    private void init(View V) {
        // no_of_intent = 0;
        fragment_clip = (ViewGroup) V;
        rv_clip = V.findViewById(R.id.clip_fragment_recycleview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        clipList = new ArrayList<>();

        context = getActivity();
        networkLoadingDialog = new Dialog(context, R.style.CustomDialogTheme);

        loadingContainer = V.findViewById(R.id.rl_clip_loading_container);
        loadingContainer.startShimmer();

        clip_adapter = new clip_adapter(clipList) {
            @Override
            public void addViewClickListeners(View V) {
                addClickListener(V);
            }
        };

        rv_clip.setLayoutManager(linearLayoutManager);
        rv_clip.setAdapter(clip_adapter);

        addDummyClips();

        if (getUserVisibleHint()) {
            updateClips();
            addClipFullyScrolledLisentener();
        }

    }


    private void addDummyClips() {
        for (int i = 0; i < 4; i++) {
            clipList.add(new Clip(new Profile("", "", "", ""), "", "", ""));
        }
    }

    private void updateClips() {
        Tools.showNetworkLoadingDialog(networkLoadingDialog, "show clip update ");
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
    private ShimmerFrameLayout loadingContainer;


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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            updateClips();
            addClipFullyScrolledLisentener();
        }
    }

}
