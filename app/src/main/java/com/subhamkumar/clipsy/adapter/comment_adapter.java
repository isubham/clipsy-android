package com.subhamkumar.clipsy.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    public void onBindViewHolder(@NonNull commentViewholder clip_viewholder, int i) {

        clip_viewholder.id.setText(comments.get(i).comment_id);

        clip_viewholder.author_id.setText(comments.get(i).profile.id);
        clip_viewholder.viewer_id.setText(comments.get(i).viewer_id);

        if (!comments.get(i).profile.id.equals(comments.get(i).viewer_id)) {
            clip_viewholder.editComment.setVisibility(View.GONE);
            clip_viewholder.deleteComment.setVisibility(View.GONE);
        }

        clip_viewholder.comment.setText(comments.get(i).profile.name.concat(" · ".concat(comments.get(i).comment)));
        clip_viewholder.comment_time.setText(comments.get(i).comment_time);


        int imageResource, _profile_pic = 0;
        try {
            _profile_pic = Integer.parseInt(comments.get(i).profile.profile_pic);
        } catch (NumberFormatException e) {
            _profile_pic = 0;
        }
        finally {
            imageResource = Constants.mThumbIds[_profile_pic];
            clip_viewholder.profile_pic.setImageResource(imageResource);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

}
