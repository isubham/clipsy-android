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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.subhamkumar.clipsy.adapter.profile_adapter;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.ClipApiResonse;
import com.subhamkumar.clipsy.models.Comment;
import com.subhamkumar.clipsy.models.CommentApiResonse;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.models.ProfileMatrixApiResponse;
import com.subhamkumar.clipsy.panel.ProfileSetting;
import com.subhamkumar.clipsy.panel.editor;
import com.subhamkumar.clipsy.panel.profile_result;
import com.subhamkumar.clipsy.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_profile extends android.support.v4.app.Fragment {


    // start of clips
    private ArrayList<Clip> clipList;

    private void showClipsFromResponse(ArrayList<Clip> clipApiResonse) {
        clipList.clear();

        for (int i = 0; i < clipApiResonse.size(); i++) {
            clipApiResonse.get(i).viewer_id = id;
        }

        clipList.addAll(clipApiResonse);
        clip_adapter.notifyDataSetChanged();

    }


    private void addClickListener(View V) {
        V.findViewById(R.id.rl_clip_author).setOnClickListener(v -> gotoProfileResult(V));
        V.findViewById(R.id.rl_clip_profile_pic).setOnClickListener(v -> gotoProfileResult(V));
        V.findViewById(R.id.rl_clip_menu).setOnClickListener(v -> clipMenuClickedDialog(V));
        V.findViewById(R.id.rl_clip_comment).setOnClickListener(v -> clipShowComments(V));
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
            fetchProfile();
            delete_clip_confirmation.dismiss();
        });

        closeAction.setOnClickListener(v -> {
            delete_clip_confirmation.dismiss();
        });

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
                    showClipsFromResponse(clipList);
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
    }


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

    private void clipShowComments(View V) {

        // show dialog

        final Dialog commentsDialog = new Dialog(context);
        commentsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        commentsDialog.setContentView(R.layout.dialog_clip_comments);

        showLoadingContainer(((ShimmerFrameLayout) commentsDialog.findViewById(R.id.rl_comment_placeholder)));
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

    private comment_adapter comment_adapter;
    private LinearLayoutManager commentlinearLayoutManager;
    private List<Comment> comments;
    private RecyclerView commentsRecyclerView;

    private void initializeCommentsDialogRecyclerView(Dialog commentDialog, String clipId) {


        commentsRecyclerView = commentDialog.findViewById(R.id.clip_comments);
        commentlinearLayoutManager = new LinearLayoutManager(context);
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

    private void addCommentDialogClickListeners(Dialog commentDialog, String clipId) {
        commentDialog.findViewById(R.id.commentCloseButton).setOnClickListener(v -> {
            fetchProfile();
            commentDialog.dismiss();
        });

        commentDialog.findViewById(R.id.commentSubmit).setOnClickListener(v -> {

            EditText commentEditText = (EditText) commentDialog.findViewById(R.id.commentContent);
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
        hideLoadingContainer(((ShimmerFrameLayout) commentDialog.findViewById(R.id.rl_comment_placeholder)));

        CommentApiResonse commentApiResonse = new Gson().fromJson(response, CommentApiResonse.class);

        for (int i = 0; i < commentApiResonse.data.size(); i++) {
            commentApiResonse.data.get(i).viewer_id = id;
        }

        comments.addAll(commentApiResonse.data);
        comment_adapter.notifyDataSetChanged();
        commentsRecyclerView.scrollToPosition(comment_adapter.getItemCount() - 1);

    }
    // end of clips

    public void fetchProfile() {
        StringRequest fetch = new StringRequest(Request.Method.GET, String.format(getString(R.string.request_user_user_profile_matrix), searched_id),
                response -> {

                    getProfileAndClips(response);
                    showProfileAndClipUIAndHideLoading();
                },
                error -> {
                    showNetworkRetryDialog();
                }
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
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                return stringStringHashMap;
            }

            ;

        };

        Volley.newRequestQueue(getActivity()).add(fetch);

    }

    protected void handle_error_response(VolleyError error) {
        showNetworkRetryDialog();
    }


    protected void showNetworkRetryDialog() {
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


    private void showProfileAndClipUIAndHideLoading() {
        profileLoadingContainer.stopShimmer();
        profileLoadingContainer.setVisibility(View.GONE);

        clipLoadingContainer.stopShimmer();
        clipLoadingContainer.setVisibility(View.GONE);

        V.findViewById(R.id.fragment_profile_contet_card).setVisibility(View.VISIBLE);
        V.findViewById(R.id.clip_fragment_recycleview).setVisibility(View.VISIBLE);
    }

    private void hideProfileAndClipUIAndShowLoading() {
        V.findViewById(R.id.fragment_profile_contet_card).setVisibility(View.GONE);
        V.findViewById(R.id.clip_fragment_recycleview).setVisibility(View.GONE);

        profileLoadingContainer.setVisibility(View.VISIBLE);
        profileLoadingContainer.startShimmer();

        clipLoadingContainer.setVisibility(View.VISIBLE);
        clipLoadingContainer.startShimmer();
    }

    String viewedProfileName;

    private String getViewedProfileName() {
        return viewedProfileName;
    }

    private void setViewedProfileName(String name) {
        viewedProfileName = name;
    }


    private void getProfileAndClips(String response) {
        Log.i("invalid json", response);
        Gson gson = new Gson();
        ProfileMatrixApiResponse profileMatrixApiResponse = gson.fromJson(response, ProfileMatrixApiResponse.class);
        setProfileElements(profileMatrixApiResponse.data.profile);
        showClipsFromResponse(profileMatrixApiResponse.data.clips);
        setViewedProfileName(profileMatrixApiResponse.data.profile.name);
        relationshipButton.setText(profileMatrixApiResponse.message.equals("Following") ? "Unfollow" : profileMatrixApiResponse.message);
        addRelationShipClickAction(profileMatrixApiResponse.message, searched_id);
    }


    public fragment_profile() {
    }

    private void addProfileClickActions(final String user_viewed) {
        followersCounterContainer.setOnClickListener(view -> showPeopleDialog(user_viewed, id, token, "followers"));
        followingCounterContainer.setOnClickListener(view -> showPeopleDialog(user_viewed, id, token, "following"));
        profile_edit.setOnClickListener(view -> {
            // TODO goto profile settings.
            toProfileImage(user_viewed);
        });
    }


    private RecyclerView rv_profile;
    private LinearLayoutManager linearLayoutManager;
    private com.subhamkumar.clipsy.adapter.profile_adapter profile_adapter;
    private List<Profile> profileList;


    private void showPeopleDialog(String viewedId, String viewerId, String token, String typOfPeople) {

        final Dialog showPeopleDialog = new Dialog(context);
        showPeopleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        showPeopleDialog.setContentView(R.layout.dialog_connected_people);
        initializeShowPeopleDialogVariables(showPeopleDialog);

        ImageView closeButton = showPeopleDialog.findViewById(R.id.connectedPeopleCloseButton);
        closeButton.setOnClickListener(v -> showPeopleDialog.dismiss());

        TextView typeOfConnectedPeople = showPeopleDialog.findViewById(R.id.typeOfConnectedPeople);
        typeOfConnectedPeople.setText(getViewedProfileName().concat(" > ".concat(typOfPeople)));

        getProfiles(showPeopleDialog, typOfPeople, viewedId);
    }

    private void initializeShowPeopleDialogVariables(Dialog dialog) {
        rv_profile = dialog.findViewById(R.id.connected_people);
        linearLayoutManager = new LinearLayoutManager(context);
        profileList = new ArrayList<>();

        profile_adapter = new profile_adapter(profileList) {
            @Override
            protected void addViewClickListeners(View V) {

            }
        };
        rv_profile.setAdapter(profile_adapter);
        rv_profile.setLayoutManager(linearLayoutManager);
    }

    private StringRequest getConnectedUsers;

    private void hideLoadingContainer(ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();
    }

    private void showLoadingContainer(ShimmerFrameLayout profileLoadingContainer) {
        profileLoadingContainer.setVisibility(View.VISIBLE);
        profileLoadingContainer.startShimmer();
    }

    ShimmerFrameLayout clipLoadingContainer, profileLoadingContainer;
    ShimmerFrameLayout connectedProfileContainer;

    private void getProfiles(Dialog dialog, String typeOfPeople, String viewerId) {

        connectedProfileContainer = dialog.findViewById(R.id.rl_fragment_connected_people_loading_container);
        dialog.show();

        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        showLoadingContainer(connectedProfileContainer);

        String profileListUrl = String.format(typeOfPeople.equals("following")
                        ? getString(R.string.request_user_user_following) :
                        getString(R.string.request_user_user_follower),
                viewerId);


        getConnectedUsers = new StringRequest(
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
    }

    private void profileListClickToProfilePage() {

        rv_profile.addOnItemTouchListener(
                new RecyclerItemClickListener(context,
                        (view, position) -> gotToProfileResult(view))
        );
    }

    protected void gotToProfileResult(View view) {
        Intent to_profile_result = new Intent(context, profile_result.class);

        String searchedUserId = ((TextView) view.findViewById(R.id.rl_profile_id)).getText().toString().trim();

        to_profile_result.putExtra(getString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId)
                .putExtra(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                        getString(R.string.bundle_param_caller_activity_fragment_profile_list_to_profile_result))
                .putExtra(getString(R.string.params_token), getTokenFromBundle())
                .putExtra(getString(R.string.params_id), getIdFromToken());


        startActivity(to_profile_result);
    }


    protected void toProfileImage(String user_viewed) {
        startActivity(new Intent(getActivity(), ProfileSetting.class)
                .putExtra("id", id)
                .putExtra("searched_id", user_viewed)
                .putExtra("token", token));
    }

    protected void toFollowing(String user_viewed, Intent to_profiles_list) {
        to_profiles_list.putExtra(getString(R.string.bundle_param_caller_button_to_profile_list),
                getString(R.string.bundle_param_caller_button_following))
                .putExtra(getString(R.string.params_search_id), user_viewed)
                .putExtra(getString(R.string.params_token), token)
                .putExtra(getString(R.string.params_id), id);
        startActivity(to_profiles_list);
    }

    protected void toFollowers(String user_viewed, Intent to_profiles_list) {
        to_profiles_list.putExtra(getString(R.string.bundle_param_caller_button_to_profile_list),
                getString(R.string.bundle_param_caller_button_followers))
                .putExtra(getString(R.string.params_search_id), user_viewed)
                .putExtra(getString(R.string.params_token), token)
                .putExtra(getString(R.string.params_id), id);
        startActivity(to_profiles_list);
    }

    protected String getUrlByResponseMessage(String message, String searched_id) {
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

    protected void addRelationShipClickAction(final String message, final String searched_id) {

        relationshipButton.setOnClickListener(v -> {

            relationShipStringRequest = new StringRequest(
                    Request.Method.POST,
                    getUrlByResponseMessage(message, searched_id),
                    response -> {
                        getProfileAndClips(response);
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

    protected void performRelationShipAction() {
        Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(relationShipStringRequest);
    }

    protected void hideRelationshipButtonIfSameUser(String user_x, String user_y) {

        if (user_x.equals(user_y)) {
            relationshipButton.setVisibility(View.GONE);
        }

    }

    protected void showEditButtonIfSameUser(String user_x, String user_y) {

        if (!user_x.equals(user_y)) {
            profile_edit.setVisibility(View.GONE);
        }

    }


    protected void setProfileElements(Profile profile) {
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

        clip_count.setText(profile.clips);
        following_count.setText(profile.following);
        followers_count.setText(profile.followers);

    }

    String profile_pic = "0";
    protected ImageView _choose_avatar_icon;
    protected String id;
    protected String searched_id;
    protected TextView _name;
    protected TextView _email;
    protected Button relationshipButton;
    protected TextView fragment_profile_followers;
    protected TextView fragment_profile_following;
    protected View V;
    protected String token;
    protected TextView clip_count;
    protected TextView followers_count;
    protected TextView following_count;
    protected Button profile_edit;
    protected LinearLayout clipCounterContainer;
    protected LinearLayout followersCounterContainer;
    protected LinearLayout followingCounterContainer;

    protected Bundle bundle;

    protected String getTokenFromBundle() {
        return bundle.getString(getString(R.string.params_token));
    }

    protected String getIdFromToken() {
        return bundle.getString(getString(R.string.params_id));
    }


    protected Context context;

    protected void findViewByIds() {

        relationshipButton = V.findViewById(R.id.rx);
        _name = V.findViewById(R.id.fragment_profile_name);
        _choose_avatar_icon = V.findViewById(R.id.choose_avatar_icon);
        _email = V.findViewById(R.id.fragment_profile_email);
        fragment_profile_followers = V.findViewById(R.id.fragment_profile_followers);
        fragment_profile_following = V.findViewById(R.id.fragment_profile_following);


        clip_count = V.findViewById(R.id.fragment_profile_clips_count);
        followers_count = V.findViewById(R.id.fragment_profile_followers_count);
        following_count = V.findViewById(R.id.fragment_profile_following_count);

        clipCounterContainer = V.findViewById(R.id.fragment_profile_clips_container);

        followersCounterContainer = V.findViewById(R.id.fragment_profile_followers_container);
        followingCounterContainer = V.findViewById(R.id.fragment_profile_following_container);
        profileLoadingContainer = V.findViewById(R.id.rl_profile_loading_container);
        clipLoadingContainer = V.findViewById(R.id.rl_clip_loading_container);

        profile_edit = V.findViewById(R.id.profile_edit);

    }

    protected void setVariablesFromBundle() {
        id = Objects.requireNonNull(getArguments()).getString(getString(R.string.params_id));
        token = getArguments().getString(getString(R.string.params_token));
        searched_id = setSearchIdIfComingFromFragmentSearchOrCompleteProfileOrProfileList(id);
    }

    protected String setSearchIdIfComingFromFragmentSearchOrCompleteProfileOrProfileList(String id) {
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

    LinearLayoutManager clipLayoutManager;
    ViewGroup fragment_profile;
    Dialog networkRetryDialog;
    RecyclerView rv_clip;
    private com.subhamkumar.clipsy.adapter.clip_adapter clip_adapter;

    protected void initializeClipAdapter(View V) {
        clipLayoutManager = new LinearLayoutManager(getActivity());
        rv_clip = V.findViewById(R.id.clip_fragment_recycleview);

        clip_adapter = new clip_adapter(clipList) {
            @Override
            public void addViewClickListeners(View V) {
                addClickListener(V);
            }
        };

        rv_clip.setLayoutManager(clipLayoutManager);
        rv_clip.setAdapter(clip_adapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        bundle = getArguments();
        networkRetryDialog = new Dialog(context, R.style.CustomDialogTheme);
        clipList = new ArrayList<>();

        V = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeClipAdapter(V);
        fragment_profile = (ViewGroup) V;
        findViewByIds();
        hideProfileAndClipUIAndShowLoading();

        try {
            setVariablesFromBundle();
            hideRelationshipButtonIfSameUser(id, searched_id);
            showEditButtonIfSameUser(id, searched_id);
        } catch (NumberFormatException e) {
            // Log.e("0097", e.getMessage());
        }
        if (getUserVisibleHint()) {
            fetchProfileMatrix();
            addProfileClickActions(searched_id);
        }
        return V;
    }

    protected void fetchProfileMatrix() {
        fetchProfile();
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchProfileMatrix();
        addProfileClickActions(searched_id);
    }
}
