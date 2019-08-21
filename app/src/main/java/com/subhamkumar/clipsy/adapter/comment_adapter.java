package com.subhamkumar.clipsy.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Comment;
import com.subhamkumar.clipsy.models.Constants;

import java.util.List;
import java.util.Objects;

abstract public class comment_adapter extends RecyclerView.Adapter<comment_adapter.commentViewholder> {


    protected abstract void addViewClickListeners(View V);

    public static class commentViewholder extends RecyclerView.ViewHolder {
        final TextView id;

        final TextView author_id;
        final TextView viewer_id;
        final TextView comment_time;
        final ImageView profile_pic;
        final TextView comment;
        final TextView editComment;
        final TextView deleteComment;




        commentViewholder(View V) {
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
    }

    private final List<Comment> comments;
    private Context context;

    public comment_adapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public commentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View V = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rl_comment, viewGroup, false);
        commentViewholder clip_viewholder = new commentViewholder(V);
        context = viewGroup.getContext();

        addViewClickListeners(V);

        return clip_viewholder;
    }


    @Override
    public void onBindViewHolder(@NonNull commentViewholder commentViewholder, int i) {

        commentViewholder.id.setText(comments.get(i).comment_id);

        commentViewholder.author_id.setText(comments.get(i).profile.id);
        commentViewholder.viewer_id.setText(comments.get(i).viewer_id);

        if (!comments.get(i).profile.id.equals(comments.get(i).viewer_id)) {
            commentViewholder.editComment.setVisibility(View.GONE);
            commentViewholder.deleteComment.setVisibility(View.GONE);
        }
        else{
            commentViewholder.editComment.setVisibility(View.VISIBLE);
            commentViewholder.deleteComment.setVisibility(View.VISIBLE);
        }

        setCommentProfileNameAndComment(commentViewholder, i);

        commentViewholder.comment_time.setText(comments.get(i).comment_time);


        setProfilePic(commentViewholder, i);
    }

    private void setProfilePic(@NonNull commentViewholder commentViewholder, int i) {
        int imageResource, _profile_pic = 0;
        try {
            _profile_pic = Integer.parseInt(comments.get(i).profile.profile_pic);
        } catch (NumberFormatException e) {
            _profile_pic = 0;
        }
        finally {
            imageResource = Constants.mThumbIds[_profile_pic];
            commentViewholder.profile_pic.setImageResource(imageResource);
        }
    }

    private void setCommentProfileNameAndComment(@NonNull commentViewholder commentViewholder, int i) {
        String profileName = comments.get(i).profile.name;
        String profileComment = comments.get(i).comment;

        Spannable boldName = new SpannableString(profileName);
        boldName.setSpan(new StyleSpan(Typeface.BOLD), 0, profileName.length(), 0);

        commentViewholder.comment.setText("");
        commentViewholder.comment.append(boldName);
        commentViewholder.comment.append(Constants.DOT + " ");
        commentViewholder.comment.append(profileComment);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

}
