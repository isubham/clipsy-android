package com.subhamkumar.clipsy.panel.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.Unidapter;
import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.auth.home;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.models.CommentApiResonse;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.models.ProfileDetail;
import com.subhamkumar.clipsy.models.ProfileMatrixApiResponse;
import com.subhamkumar.clipsy.panel.ProfileSetting;
import com.subhamkumar.clipsy.panel.editor;
import com.subhamkumar.clipsy.panel.profile_result;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.VolleySingleton;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.subhamkumar.clipsy.utils.Message.fragmentProfileToProfileResult;
import static com.subhamkumar.clipsy.utils.Message.getSearchId_forClips_orSearch_orProfileList;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_profile extends androidx.fragment.app.Fragment {


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

        Intent to_profile = new Intent(getActivity(), profile_result.class);
        startActivity(to_profile.putExtras(toProfileResult));
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


    private void hidetitleofdialog(Dialog dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            fetchProfile();
            delete_clip_confirmation.dismiss();
        });

        closeAction.setOnClickListener(v -> delete_clip_confirmation.dismiss());

        delete_clip_confirmation.show();

    }


    private void deleteClip(String clipId) {

        StringRequest deleteClipRequest = new StringRequest(
                Request.Method.POST,
                String.format(Constants.request_clip_delete, clipId),
                response -> {

                    ClipApiResonse deleteApiResponse = new Gson().fromJson(response, ClipApiResonse.class);
                    Toast.makeText(context, deleteApiResponse.message, Toast.LENGTH_SHORT).show();
                    ArrayList<Clip> clipList = new Gson().fromJson(response, ClipApiResonse.class).data;
                    // TODO regresh on deleting
                    // setItemsAndNotifyAdapter(clipList);
                },

                this::handle_error_response
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


        makeDeleteRequest(deleteClipRequest);
    }

    private void makeDeleteRequest(StringRequest deleteClipRequest) {
        Volley.newRequestQueue(context).add(deleteClipRequest);
    }


    private void gotoClip(View V, String authorId, String clipId) {

        Bundle toClip = new Bundle();
        toClip.putString("token", token);
        toClip.putString("action", "update");
        toClip.putString("id", authorId);
        toClip.putString("clip_id", clipId);
        toClip.putString("clip", "clip");
        Intent toClipIntent = new Intent(getActivity(), editor.class);
        toClipIntent.putExtras(toClip);
        int clipUpdateRequestCode = 123;
        startActivityForResult(toClipIntent, clipUpdateRequestCode);
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


    private void showReportDialog(View V) {
        showReportDialog(getSelectedUserId(), getSelectedClipId());
    }

    private String getSelectedUserId() {
        return selected_user_id;
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

        Volley.newRequestQueue(context).add(sendReport);
    }


    private String getSelectedClipId() {
        return selected_clip_id;
    }

    private static String selected_clip_id;

    private void setSelectedClipId(String clip_id) {
        selected_clip_id = clip_id;
    }

    private static String selected_user_id;

    private void setSelectedUserId(String user) {
        selected_user_id = user;
    }

    private void clipShowComments(View clipView) {

        // show dialog

        final Dialog commentsDialog = new Dialog(context);
        commentsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        commentsDialog.setContentView(R.layout.dialog_clip_comments);

        showLoadingContainer(commentsDialog.findViewById(R.id.rl_comment_placeholder));
        commentsDialog.show();

        Objects.requireNonNull(commentsDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        String clipId = ((TextView) clipView.findViewById(R.id.rl_clip_id)).getText().toString().trim();

        addCommentDialogClickListeners(commentsDialog, clipView);

        initializeCommentsDialogRecyclerView(commentsDialog, clipView);

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

    private void showProgressBar(boolean show) {

        ShimmerFrameLayout layout = V.findViewById(R.id.shimmer_profile_clips);
        if (show) {
            layout.setVisibility(View.VISIBLE);
            layout.startShimmer();
        } else {
            layout.stopShimmer();
            layout.setVisibility(View.GONE);
        }
    }

    private Unidapter unidapter;
    private ArrayList<Object> comments;
    private RecyclerView commentsRecyclerView;

    private void initializeCommentsDialogRecyclerView(Dialog commentDialog, View clipView) {

        commentsRecyclerView = commentDialog.findViewById(R.id.clip_comments);
        LinearLayoutManager commentlinearLayoutManager = new LinearLayoutManager(context);
        comments = new ArrayList<>();

        unidapter = new Unidapter(comments) {
            @Override
            protected void addViewClickListeners(View V) {
                // TODO
                addCommentEditAndDeleteClickListener(V, commentDialog, clipView);
            }
        };

        commentsRecyclerView.setAdapter(unidapter);
        commentsRecyclerView.setLayoutManager(commentlinearLayoutManager);

    }

    private void addCommentDialogClickListeners(Dialog commentDialog, View clipView) {
        commentDialog.findViewById(R.id.commentCloseButton).setOnClickListener(v -> {
            fetchProfile();
            commentDialog.dismiss();
        });

        commentDialog.findViewById(R.id.commentSubmit).setOnClickListener(v -> {
            String clipId = ((TextView) clipView.findViewById(R.id.rl_clip_id)).getText().toString().trim();

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

    private void addCommentEditAndDeleteClickListener(View V, Dialog CommentDialog, View clipView) {

        String clipId = ((TextView) clipView.findViewById(R.id.rl_clip_id)).getText().toString().trim();

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
                saveClip.put("clip", clipId);
                saveClip.put("id", commentId);
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


    private void fillComments(String response, Dialog commentDialog) {

        comments.clear();
        Log.e("Comments", response);
        hideLoadingContainer(commentDialog.findViewById(R.id.rl_comment_placeholder));

        CommentApiResonse commentApiResonse = new Gson().fromJson(response, CommentApiResonse.class);

        for (int i = 0; i < commentApiResonse.data.size(); i++) {
            commentApiResonse.data.get(i).viewer_id = id;
        }

        comments.addAll(commentApiResonse.data);
        unidapter.notifyDataSetChanged();
        commentsRecyclerView.scrollToPosition(unidapter.getItemCount() - 1);

    }
    // end of clips

    private void fetchProfile() {
        showLoadingContainer(clipAndprofileLoadingContainer);
        // showProfileAndClipUIAndHideLoading();
        StringRequest fetch = new StringRequest(Request.Method.GET, String.format(getString(R.string.request_user_user_profile_matrix), searched_id),
                this::getProfileAndClips,
                error -> showNetworkRetryDialog()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap<String, String>();
                headers.put(getString(R.string.header_authentication), token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                networkretry = NETWORKRETRY.PROFILE;
                return new HashMap<>();
            }

        };

        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(fetch);

    }

    private void handle_error_response(VolleyError error) {
        showNetworkRetryDialog();
    }


    private void showNetworkRetryDialog() {
        final Dialog noNetworkDialog = new Dialog(context);
        noNetworkDialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        noNetworkDialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> noNetworkDialog.dismiss());

        noNetworkDialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            noNetworkDialog.dismiss();
            networkRetryRequest();
        });

        noNetworkDialog.show();
    }

    private NETWORKRETRY networkretry;

    private enum NETWORKRETRY {
        FOLLOWERS, FOLLOWING, PROFILE, RELATIONSHIPBUTTON
    }

    private void networkRetryRequest() {
        Log.i("n/w retry", networkretry.name());
        switch (networkretry) {
            case PROFILE:
                fetchProfile();
                break;
            case RELATIONSHIPBUTTON:
                // TODO Correct this
                fetchProfile();
                break;
        }
    }


    private void getProfileAndClips(String response) {
        Log.i("invalid json", response);
        Gson gson = new Gson();
        ProfileMatrixApiResponse profileMatrixApiResponse = gson.fromJson(response, ProfileMatrixApiResponse.class);
        setItemsAndNotifyAdapter(profileMatrixApiResponse.data);

    }


    public fragment_profile() {
    }


    private RecyclerView rv_profile;
    private com.subhamkumar.clipsy.adapter.profile_adapter profile_adapter;
    private List<Profile> profileList;


    private void showPeopleDialog(String viewedId, String viewerId, String token, String typOfPeople, String profile_name) {

        final Dialog showPeopleDialog = new Dialog(context);
        showPeopleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        showPeopleDialog.setContentView(R.layout.dialog_connected_people);
        initializeShowPeopleDialogVariables(showPeopleDialog);

        ImageView closeButton = showPeopleDialog.findViewById(R.id.connectedPeopleCloseButton);
        closeButton.setOnClickListener(v -> showPeopleDialog.dismiss());

        TextView typeOfConnectedPeople = showPeopleDialog.findViewById(R.id.typeOfConnectedPeople);
        typeOfConnectedPeople.setText(profile_name.concat(" > ".concat(typOfPeople)));

        getProfiles(showPeopleDialog, typOfPeople, viewedId);
    }

    private void initializeShowPeopleDialogVariables(Dialog dialog) {
        rv_profile = dialog.findViewById(R.id.connected_people);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        profileList = new ArrayList<>();

        profile_adapter = new profile_adapter(profileList) {
            @Override
            protected void addViewClickListeners(View V) {

            }
        };
        rv_profile.setAdapter(profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
    }

    private void hideLoadingContainer(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();
    }

    private void showLoadingContainer(ShimmerFrameLayout profileLoadingContainer) {
        profileLoadingContainer.setVisibility(View.VISIBLE);
        profileLoadingContainer.startShimmer();
    }

    private ShimmerFrameLayout clipAndprofileLoadingContainer;
    private ShimmerFrameLayout connectedProfileContainer;

    private void getProfiles(Dialog dialog, String typeOfPeople, String viewerId) {

        connectedProfileContainer = dialog.findViewById(R.id.rl_fragment_connected_people_loading_container);
        dialog.show();

        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        showLoadingContainer(connectedProfileContainer);

        String profileListUrl = String.format(typeOfPeople.equals("following")
                        ? getString(R.string.request_user_user_following) :
                        getString(R.string.request_user_user_follower),
                viewerId);


        StringRequest getConnectedUsers = new StringRequest(
                Request.Method.GET,
                profileListUrl,
                response -> {

                    hideLoadingContainer(connectedProfileContainer);

                    Gson gson = new Gson();
                    ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
                    profileList.clear();
                    profileList.addAll(profileApiResponse.data);
                    profile_adapter.notifyDataSetChanged();
                    profileListClickToProfilePage();

                },

                error -> {
                    networkretry = typeOfPeople.equals("following") ? NETWORKRETRY.FOLLOWING : NETWORKRETRY.FOLLOWERS;
                    handle_error_response(error);
                }
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

        Volley.newRequestQueue(context).add(getConnectedUsers);

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
        to_profile_result.putExtras(fragmentProfileToProfileResult(token, id, searchedUserId));
        startActivity(to_profile_result);
    }

    private Bundle bundle;

    protected String getTokenFromBundle() {
        return bundle.getString(getString(R.string.params_token));
    }

    protected String getIdFromToken() {
        return bundle.getString(getString(R.string.params_id));
    }


    private Context context;

    private String id;
    private String token;
    private String searched_id;

    private void setVariablesFromBundle() {
        id = Objects.requireNonNull(getArguments()).getString(getString(R.string.params_id));
        token = getArguments().getString(getString(R.string.params_token));
        searched_id = getSearchId_forClips_orSearch_orProfileList(getArguments(), id);
    }

    private LinearLayoutManager clipLayoutManager;
    private ViewGroup fragment_profile;
    private Dialog networkRetryDialog;
    private RecyclerView rv_clip;
    private Unidapter uniDapterForClipAndProfile;

    private void inializeUnidapter(View V) {
        clipLayoutManager = new LinearLayoutManager(getActivity());
        rv_clip = V.findViewById(R.id.fragment_profile_clip_profile);

        uniDapterForClipAndProfile = new Unidapter(objects) {
            @Override
            public void addViewClickListeners(View V) {
                addTouchListener(V);
            }
        };

        rv_clip.setLayoutManager(clipLayoutManager);
        rv_clip.setAdapter(uniDapterForClipAndProfile);
    }

    private View V;
    // start of clips
    private ArrayList<Object> objects;

    private void setItemsAndNotifyAdapter(ArrayList<Object> objects) {
        this.objects.clear();

        Gson gson = new Gson();
        // TODO add viewer id
        String viewer_id = "";
        //

        for (int i = 0; i < objects.size(); i++) {
            if (i == 0) {
                Type type = new TypeToken<ProfileDetail>() {
                }.getType();
                ProfileDetail profileDetail = gson.fromJson(gson.toJson(objects.get(i)), type);
                viewer_id = profileDetail.viewer_id;
                objects.set(i, profileDetail);
            } else {
                Type type = new TypeToken<Clip>() {
                }.getType();
                Clip clip = gson.fromJson(gson.toJson(objects.get(i)), type);
                clip.viewer_id = viewer_id;
                objects.set(i, clip);
            }
        }
        //

        this.objects.addAll(objects);
        hideLoadingContainer(clipAndprofileLoadingContainer);
        uniDapterForClipAndProfile.notifyDataSetChanged();

    }


    private void addTouchListener(View V) {
        if (V.getId() == R.id.rl_clip_layout) {

            V.findViewById(R.id.rl_clip_author).setOnClickListener(v -> gotoProfileResult(V));
            V.findViewById(R.id.rl_clip_extra_space).setOnClickListener(v -> gotoProfileResult(V));
            V.findViewById(R.id.rl_clip_profile_pic).setOnClickListener(v -> gotoProfileResult(V));
            V.findViewById(R.id.rl_clip_date_privacy_container).setOnClickListener(v -> gotoProfileResult(V));
            V.findViewById(R.id.rl_clip_menu).setOnClickListener(v -> clipMenuClickedDialog(V));
            V.findViewById(R.id.rl_clip_comment).setOnClickListener(v -> clipShowComments(V));
        }

        if (V.getId() == R.id.profile_landing_layout) {
            addProfileDetailClickActions(V);
        }
    }

    private StringRequest relationShipStringRequest;

    private String getUrlByResponseMessage(String message, String searched_id) {
        switch (message) {
            case "Follow":
                return String.format(getString(R.string.request_user_user_follow), searched_id);
            case "Unfollow":
                return String.format(getString(R.string.request_user_user_unfollow), searched_id);
            default:
                return String.format(getString(R.string.request_user_user_follow), searched_id);
        }
    }

    private void addProfileDetailClickActions(View V) {

        V.findViewById(R.id.fragment_profile_followers_container).setOnClickListener(view -> {
            String user_viewed = ((TextView) V.findViewById(R.id.profile_landing_id)).getText().toString().trim();
            String profile_name = ((TextView) V.findViewById(R.id.fragment_profile_name)).getText().toString().trim();
            showPeopleDialog(user_viewed, id, token, "followers", profile_name);
        });

        V.findViewById(R.id.fragment_profile_following_container).setOnClickListener(view -> {
            String user_viewed = ((TextView) V.findViewById(R.id.profile_landing_id)).getText().toString().trim();
            String profile_name = ((TextView) V.findViewById(R.id.fragment_profile_name)).getText().toString().trim();
            showPeopleDialog(user_viewed, id, token, "following", profile_name);
        });

        V.findViewById(R.id.profile_edit).setOnClickListener(view -> {
            String user_viewed = ((TextView) V.findViewById(R.id.profile_landing_id)).getText().toString().trim();
            startActivity(new Intent(getActivity(), ProfileSetting.class)
                    .putExtra("id", id)
                    .putExtra("searched_id", user_viewed)
                    .putExtra("token", token));
        });

        V.findViewById(R.id.rx).setOnClickListener(v -> {

            v.setEnabled(false);
            ViewCompat.setBackgroundTintList(v, ContextCompat.getColorStateList(Objects.requireNonNull(getActivity()), R.color.grey_300));

            String message = ((Button) V.findViewById(R.id.rx)).getText().toString().trim();
            String searched_id = ((TextView) V.findViewById(R.id.profile_landing_id)).getText().toString().trim();

            relationShipStringRequest = new StringRequest(
                    Request.Method.POST,
                    getUrlByResponseMessage(message, searched_id),
                    response -> {
                        getProfileAndClips(response);
                        v.setEnabled(true);
                        ViewCompat.setBackgroundTintList(v, ContextCompat.getColorStateList(getActivity(), R.color.blue_A700));
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

            VolleySingleton.getInstance(Objects.requireNonNull(getActivity())).addToRequestQueue(relationShipStringRequest);

        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        bundle = getArguments();
        networkRetryDialog = new Dialog(Objects.requireNonNull(context), R.style.CustomDialogTheme);
        objects = new ArrayList<>();

        V = inflater.inflate(R.layout.fragment_profile, container, false);
        clipAndprofileLoadingContainer = V.findViewById(R.id.shimmer_profile_clips);
        showLoadingContainer(clipAndprofileLoadingContainer);
        inializeUnidapter(V);
        fragment_profile = (ViewGroup) V;

        setHasOptionsMenu(true);
        try {
            setVariablesFromBundle();
        } catch (NumberFormatException e) {
        }
        if (getUserVisibleHint()) {
            fetchProfile();
        }
        return V;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!getActivity().getClass().getSimpleName().equals("profile_result")) {
            inflater.inflate(R.menu.profile_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        goToSignUp();
        return super.onOptionsItemSelected(item);
    }

    public void goToSignUp() {
        Intent toSignUp = new Intent(getActivity(), home.class).putExtra(Constants.SIGNOUT, "1");
        toSignUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toSignUp);
        this.getActivity().finish();
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchProfile();
    }
}
