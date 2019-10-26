package com.subhamkumar.clipsy.ViewHolders;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Comment;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.Tools;

public class CommentViewHolder  extends RecyclerView.ViewHolder {
    private TextView id;
    private TextView author_id;
    private TextView viewer_id;
    private TextView comment_time;
    private ImageView profile_pic;
    private TextView comment;
    private TextView editComment;
    private TextView deleteComment;

    public TextView getId() {
        return id;
    }

    public void setId(TextView id) {
        this.id = id;
    }

    public TextView getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(TextView author_id) {
        this.author_id = author_id;
    }

    public TextView getViewer_id() {
        return viewer_id;
    }

    public void setViewer_id(TextView viewer_id) {
        this.viewer_id = viewer_id;
    }

    public TextView getComment_time() {
        return comment_time;
    }

    public void setComment_time(TextView comment_time) {
        this.comment_time = comment_time;
    }

    public ImageView getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(ImageView profile_pic) {
        this.profile_pic = profile_pic;
    }

    public TextView getComment() {
        return comment;
    }

    public void setComment(TextView comment) {
        this.comment = comment;
    }

    public TextView getEditComment() {
        return editComment;
    }

    public void setEditComment(TextView editComment) {
        this.editComment = editComment;
    }

    public TextView getDeleteComment() {
        return deleteComment;
    }

    public void setDeleteComment(TextView deleteComment) {
        this.deleteComment = deleteComment;
    }

    public CommentViewHolder(@NonNull View V) {
        super(V);

        id = V.findViewById(R.id.rl_comment_id);
        author_id = V.findViewById(R.id.rl_comment_author_id);
        viewer_id = V.findViewById(R.id.rl_comment_viewer_id);
        comment_time = V.findViewById(R.id.rl_comment_time);
        comment = V.findViewById(R.id.rl_comment_comment);
        profile_pic = V.findViewById(R.id.rl_comment_profile_pic);
        editComment = V.findViewById(R.id.rl_comment_edit);
        deleteComment = V.findViewById(R.id.rl_comment_delete);
    }

    public void BindData(Comment comment, Context context) {

        this.id.setText(comment.comment_id);

        this.author_id.setText(comment.profile.id);
        this.viewer_id.setText(comment.viewer_id);

        if (!comment.profile.id.equals(comment.viewer_id)) {
            this.editComment.setVisibility(View.GONE);
            this.deleteComment.setVisibility(View.GONE);
        }
        else{
            this.editComment.setVisibility(View.VISIBLE);
            this.deleteComment.setVisibility(View.VISIBLE);
        }

        this.comment_time.setText(comment.comment_time);

        setCommentProfileNameAndComment(this, comment);
        Tools.setProfilePic(comment.profile.profile_pic, this.profile_pic, context);
    }


    private void setCommentProfileNameAndComment(@NonNull CommentViewHolder commentViewholder, Comment comment) {
        String profileName = comment.profile.name;
        String profileComment = comment.comment;

        Spannable boldName = new SpannableString(profileName);
        boldName.setSpan(new StyleSpan(Typeface.BOLD), 0, profileName.length(), 0);

        commentViewholder.comment.setText("");
        commentViewholder.comment.append(boldName);
        commentViewholder.comment.append(Constants.DOT + " ");
        commentViewholder.comment.append(profileComment);

    }

}

