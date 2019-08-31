package com.subhamkumar.clipsy.panel.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.subhamkumar.clipsy.adapter.clip_adapter;
import com.subhamkumar.clipsy.adapter.comment_adapter;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.Comment;
import com.subhamkumar.clipsy.models.CommentApiResonse;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.editor;
import com.subhamkumar.clipsy.panel.profile_result;
import com.subhamkumar.clipsy.utils.Tools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.subhamkumar.clipsy.utils.Message.fragmentClipToProfileResult;


public class fragment_clips extends Fragment {

    // token => clips list

    private void showNetworkUnavailableDialog() {
        final Dialog networkUnavailableDialog = new Dialog(context);
        networkUnavailableDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        networkUnavailableDialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        networkUnavailableDialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> networkUnavailableDialog.dismiss());

        networkUnavailableDialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            networkUnavailableDialog.dismiss();
            updateClips();
        });

        networkUnavailableDialog.show();
    }

    private String getClipUrl() {
        String from = Objects.requireNonNull(getArguments()).getString(Constants.TO_HOME);
        return getUrl(from);
    }

    private void fetchClips() {

        StringRequest fetchClip = new StringRequest(
                Request.Method.GET,
                getClipUrl(),
                this::showClipsFromResponse,
                error -> showNetworkUnavailableDialog()

        ) {

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(getString(R.string.header_authentication), token);
                return headers;
            }
        };
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(fetchClip);
    }

    private void showClipsFromResponse(String response) {

        Gson gson = new Gson();
        ClipApiResonse clipApiResonse = gson.fromJson(response, ClipApiResonse.class);
        clipList.clear();

        for (int i = 0; i < clipApiResonse.data.size(); i++) {
            clipApiResonse.data.get(i).viewer_id = id;
        }

        clipList.addAll(clipApiResonse.data);

        loadingContainer.stopShimmer();
        loadingContainer.setVisibility(View.GONE);

        clip_adapter.notifyDataSetChanged();

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
        V.findViewById(R.id.rl_clip_comment).setOnClickListener(v -> clipShowComments(V));
    }


    private void gotoProfileResult(View V) {
        String searchedUserId = ((TextView) V.findViewById(R.id.rl_clip_author_id)).getText().toString();
        startActivity(new Intent(getActivity(), profile_result.class).putExtras(fragmentClipToProfileResult(token, id, searchedUserId)));
    }


    private void clipMenuClickedDialog(View V) {
        String author_id = ((TextView) V.findViewById(R.id.rl_clip_author_id)).getText().toString();
        String viewer_id = ((TextView) V.findViewById(R.id.rl_clip_viewer_id)).getText().toString();
        String clip_id = ((TextView) V.findViewById(R.id.rl_clip_id)).getText().toString();
        setSelectedClipId(clip_id);
        if (author_id.equals(viewer_id)) {
            showSameUserDialog(author_id, clip_id);
        }
        else {
            showDifferentUserDialog(author_id);
            setSelectedUserId(author_id);
        }
    }

    private void hideLoadingContainer(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();
    }

    private void showLoadingContainer(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
    }

    private void updateClips() {
        fetchClips();
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
            gotoClip(v, authorId, clipId);
            dialog.dismiss();
        });

        deleteAction.setOnClickListener(v -> {
            dialog.dismiss();
            showConfirmationToDelete(v, authorId, clipId);
        });

        dialog.show();

    }

    private void showConfirmationToDelete(View V, String authorId, String clipId) {

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

        closeAction.setOnClickListener(v -> delete_clip_confirmation.dismiss());

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

                    ClipApiResonse deleteApiResponse = new Gson().fromJson(response, ClipApiResonse.class);
                    Toast.makeText(context, deleteApiResponse.message, Toast.LENGTH_SHORT).show();
                    showClipsFromResponse(response);

                },

                this::handle_error_response
        ) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.param_timestamp, Tools.getTimeStamp());
                return params;
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

    private void handle_error_response(VolleyError error) {

        showNetworkUnavailableDialog();

    }

    private void makeDeleteRequest(StringRequest deleteClipRequest) {
        Volley.newRequestQueue(context).add(deleteClipRequest);
    }

    // TODO handle loading bar.
    private void makeSendReportRequest(StringRequest sendReportRequest) {
        Volley.newRequestQueue(context).add(sendReportRequest);
    }

    private Context context;
    private final int clipUpdateRequestCode = 123;

    private void gotoClip(View V, String authorId, String clipId) {

        Bundle toClip = new Bundle();
        toClip.putString("token", token);
        toClip.putString("action", "update");
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

                    sendReport(Constants.report_inappropriate, searchedUserId, token, clipId);
                    reportDialog.dismiss();

                });

        reportDialog.findViewById(R.id.dialog_other_user_show_report_spam)
                .setOnClickListener(v -> {

                    sendReport(Constants.report_spam, searchedUserId, token, clipId);
                    reportDialog.dismiss();


                });

        reportDialog.findViewById(R.id.dialog_other_user_show_report_close)
                .setOnClickListener(v -> reportDialog.dismiss());

        reportDialog.show();
    }

    private void sendReport(String problem, String reportedId, String token, String clipId) {

        StringRequest sendReport = new StringRequest(
                Request.Method.POST,
                String.format(Constants.request_report_send, clipId),
                response -> {

                    ApiResponse sendReportApiResponse = new Gson().fromJson(response, ApiResponse.class);
                    Toast.makeText(context, sendReportApiResponse.message, Toast.LENGTH_SHORT).show();

                },

                this::handle_error_response
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


    private com.subhamkumar.clipsy.adapter.clip_adapter clip_adapter;
    private List<Clip> clipList;
    private ViewGroup fragment_clip;


    private void init(View V) {
        // no_of_intent = 0;
        fragment_clip = (ViewGroup) V;
        RecyclerView rv_clip = V.findViewById(R.id.clip_fragment_recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        clipList = new ArrayList<>();

        context = getActivity();

        loadingContainer = V.findViewById(R.id.shimmer_clips);
        loadingContainer.startShimmer();

        clip_adapter = new clip_adapter(clipList) {
            @Override
            public void addViewClickListeners(View V) {
                addClickListener(V);
            }
        };

        rv_clip.setLayoutManager(linearLayoutManager);
        rv_clip.setAdapter(clip_adapter);

    }


    private static String token;
    private String id;
    String _fx;

    private String getUrl(String from) {

        String fromPanel = getString(R.string.bundle_param_caller_activity_panel);
        String fromSearch = getString(R.string.bundle_param_caller_activity_fragment_search);
        String fromProfileResult = getString(R.string.bundle_param_caller_activity_fragment_profile_list_to_profile_result);

        if (from != null) {
            if (from.equals(fromPanel)) {
                return getString(R.string.request_clip_following);
            }
            if (from.equals(fromSearch) || from.equals(fromProfileResult)) {
                // if following clip is there
                String baseActivity = Objects.requireNonNull(getActivity()).getClass().getSimpleName();
                if (baseActivity.equals("panel")) {
                    return getString(R.string.request_clip_following);
                }
                String searched_id = Objects.requireNonNull(getArguments()).getString(getString(R.string.bundle_param_profile_result_searched_user_id));
                return String.format(getString(R.string.request_clip_reads_user), searched_id);
            }
        }

        return getString(R.string.request_clip_reads);

    }

    // comment

    private comment_adapter comment_adapter;
    private List<Comment> comments;
    private RecyclerView commentsRecyclerView;


    private List<Comment> commentList;

    private void clipShowComments(View V) {

        // show dialog

        final Dialog commentsDialog = new Dialog(context);
        commentsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        commentsDialog.setContentView(R.layout.dialog_clip_comments);

        showLoadingContainer(commentsDialog.findViewById(R.id.rl_comment_placeholder));
        commentsDialog.show();

        Objects.requireNonNull(commentsDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        String clipId = ((TextView) V.findViewById(R.id.rl_clip_id)).getText().toString().trim();

        addCommentDialogClickListeners(commentsDialog, clipId);

        initializeCommentsDialogRecyclerView(commentsDialog, clipId);

        StringRequest getClipComments = new StringRequest(
                Request.Method.GET,
                String.format(Constants.request_clip_get_comment, clipId),
                response -> {
                    // fill dialog with comments
                    fillComments(response, commentsDialog);
                },
                error -> {
                    // network unavailable
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

        Volley.newRequestQueue(context).add(getClipComments);
    }

    private void initializeCommentsDialogRecyclerView(Dialog commentDialog, String clipId) {


        commentsRecyclerView = commentDialog.findViewById(R.id.clip_comments);
        LinearLayoutManager commentlinearLayoutManager = new LinearLayoutManager(context);
        comments = new ArrayList<>();

        comment_adapter = new comment_adapter(comments) {
            @Override
            protected void addViewClickListeners(View V) {
                // TODO
                addCommentEditAndDeleteClickListener(V, commentDialog, clipId);
            }
        };

        commentsRecyclerView.setAdapter(comment_adapter);
        commentsRecyclerView.setLayoutManager(commentlinearLayoutManager);


    }

    private void addCommentEditAndDeleteClickListener(View V, Dialog CommentDialog, String clipId) {

        TextView editComment = V.findViewById(R.id.rl_comment_edit);
        TextView deleteComment = V.findViewById(R.id.rl_comment_delete);

        editComment.setOnClickListener(v -> {
            TextView commentIdTextView = V.findViewById(R.id.rl_comment_id);
            String commentId = commentIdTextView.getText().toString().trim();
            TextView commentText = V.findViewById(R.id.rl_comment_comment);
            showEditCommentDialog(CommentDialog, clipId, commentText, commentId);
        });

        deleteComment.setOnClickListener(v -> {
            TextView commentIdTextView = V.findViewById(R.id.rl_comment_id);
            String commentId = commentIdTextView.getText().toString().trim();
            setDeleteCommentAction(CommentDialog, clipId, commentId);
        });

    }

    private void showEditCommentDialog(Dialog CommentDialog, String clipId, TextView commentText, String commentId) {
        final Dialog commentEditDialog = new Dialog(context);
        commentEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        commentEditDialog.setContentView(R.layout.dialog_edit_comment);

        String commentWithName = commentText.getText().toString().trim();
        int afterNameIndex = commentWithName.indexOf(Constants.DOT);
        String comment = commentWithName.substring(afterNameIndex + 2);
        ((EditText) commentEditDialog.findViewById(R.id.editCommentContent)).setText(comment);

        commentEditDialog.findViewById(R.id.editCommentSubmit).setOnClickListener(v -> {
            String newComment = ((EditText) commentEditDialog.findViewById(R.id.editCommentContent)).getText().toString().trim();
            if (newComment.isEmpty()) {
                Toast.makeText(context, "Empty Comment", Toast.LENGTH_SHORT).show();
            } else {
                updateComment(context, commentEditDialog, CommentDialog, clipId, commentId, id, newComment);
            }
        });

        commentEditDialog.show();

        Objects.requireNonNull(commentEditDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private void setDeleteCommentAction(Dialog commentDialog, String clipId, String commentId) {
        StringRequest saveCommentRequest = new StringRequest(
                Request.Method.POST,
                "https://api.pitavya.com/clipsy/deleteComment/",
                response -> {
                    // fill comment
                    fillComments(response, commentDialog);
                    CommentApiResonse apiResonse = new Gson().fromJson(response, CommentApiResonse.class);
                    Toast.makeText(context, apiResonse.message, Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // network unavailable
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map saveClip = new HashMap<String, String>();
                saveClip.put(Constants.param_timestamp, Tools.getTimeStamp());

                return saveClip;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(getString(R.string.header_authentication), token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(saveCommentRequest);

    }


    private void updateComment(Context context, Dialog commentEditDialog, Dialog commentDialog, String clipId, String commentId, String userId, String newComment) {
        StringRequest updateCommentRequest = new StringRequest(
                Request.Method.POST,
                "https://api.pitavya.com/clipsy/updateComment/",
                response -> {
                    // fill comment
                    fillComments(response, commentDialog);

                    CommentApiResonse apiResonse = new Gson().fromJson(response, CommentApiResonse.class);
                    Toast.makeText(context, apiResonse.message, Toast.LENGTH_SHORT).show();
                    commentEditDialog.dismiss();
                },
                error -> {
                    // network unavailable
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map saveClip = new HashMap<String, String>();
                saveClip.put("clip", clipId);
                saveClip.put("id", commentId);
                saveClip.put("comment", newComment);
                saveClip.put("user", userId);
                saveClip.put(Constants.param_timestamp, Tools.getTimeStamp());

                return saveClip;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(getString(R.string.header_authentication), token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(updateCommentRequest);

    }


    private void addCommentDialogClickListeners(Dialog commentDialog, String clipId) {
        commentDialog.findViewById(R.id.commentCloseButton).setOnClickListener(v -> {
            updateClips();
            commentDialog.dismiss();
        });

        commentDialog.findViewById(R.id.commentSubmit).setOnClickListener(v -> {

            EditText commentEditText = commentDialog.findViewById(R.id.commentContent);
            String comment = (commentEditText).getText().toString();

            if (comment.isEmpty()) {
                Toast.makeText(context, "comment is Empty", Toast.LENGTH_SHORT).show();
            } else {
                showLoadingContainer(commentDialog.findViewById(R.id.rl_comment_placeholder));
                comments.clear();
                commentEditText.setText("");
                commentEditText.clearFocus();
                saveComment(commentDialog, comment, clipId);
            }

        });


    }


    private void saveComment(Dialog commentDialog, String comment, String clipId) {

        StringRequest saveCommentRequest = new StringRequest(
                Request.Method.POST,
                "https://api.pitavya.com/clipsy/comment/",
                response -> {
                    // fill comment
                    fillComments(response, commentDialog);
                },
                error -> {
                    // network unavailable
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map saveClip = new HashMap<String, String>();
                saveClip.put("clip", clipId);
                saveClip.put("comment", comment);
                saveClip.put("timestamp", Tools.getTimeStamp());
                return saveClip;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(getString(R.string.header_authentication), token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(saveCommentRequest);

    }


    private void fillComments(String response, Dialog commentDialog) {

        comments.clear();
        Log.e("Comments", response);
        hideLoadingContainer(commentDialog.findViewById(R.id.rl_comment_placeholder));

        CommentApiResonse commentApiResonse = new Gson().fromJson(response, CommentApiResonse.class);

        for (int i = 0; i < commentApiResonse.data.size(); i++) {
            commentApiResonse.data.get(i).viewer_id = id;
        }

        comments.addAll(commentApiResonse.data);
        comment_adapter.notifyDataSetChanged();
        commentsRecyclerView.scrollToPosition(comment_adapter.getItemCount() - 1);

    }

    // end of comment

    private ShimmerFrameLayout loadingContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();


        try {
            id = bundle.getString(getString(R.string.params_id));
            token = bundle.getString(getString(R.string.params_token));
            String from = bundle.getString(Constants.TO_HOME);

        } catch (NullPointerException e) {
            Toast.makeText(context, "null on" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("null on", e.getMessage());
        }


        View v = inflater.inflate(R.layout.fragment_clips, container, false);
        context = Objects.requireNonNull(container).getContext();
        init(v);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateClips();
        addClipFullyScrolledLisentener();
    }
}
