package com.subhamkumar.clipsy.adapter;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.ViewHolders.ClipViewHolder;
import com.subhamkumar.clipsy.ViewHolders.CommentViewHolder;
import com.subhamkumar.clipsy.ViewHolders.ProfileDetailViewHolder;
import com.subhamkumar.clipsy.ViewHolders.ProfileShortViewHolder;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.Comment;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.models.ProfileDetail;

import java.util.ArrayList;


public abstract class Unidapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int PROFILE_DETAIL = 0;
    private final int CLIP = 1;
    private final int PROFILE_SEARCH = 2;
    private final int COMMENT = 3;
    private final ArrayList<Object> items;
    private Context context;

    protected abstract void addViewClickListeners(View V);

    private Context getContext() {
        return context;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public Unidapter(ArrayList<Object> data) {
        this.items = data;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Clip) {
            return CLIP;
        } else if (item instanceof ProfileDetail) {
            return PROFILE_DETAIL;
        } else if (item instanceof Comment) {
            return COMMENT;
        } else if (item instanceof Profile) {
            return PROFILE_SEARCH;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        setContext(viewGroup.getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case PROFILE_DETAIL:
                View profileDetail = layoutInflater.inflate(R.layout.profile_landing_card, viewGroup, false);
                viewHolder = new ProfileDetailViewHolder(profileDetail);
                addViewClickListeners(profileDetail);

                break;
            case CLIP:
                View clip = layoutInflater.inflate(R.layout.rl_clip, viewGroup, false);
                viewHolder = new ClipViewHolder(clip);
                addViewClickListeners(clip);
                break;
            case COMMENT:
                View comment = layoutInflater.inflate(R.layout.rl_comment, viewGroup, false);
                viewHolder = new CommentViewHolder(comment);
                addViewClickListeners(comment);
                break;
            case PROFILE_SEARCH:
                View profile = layoutInflater.inflate(R.layout.rl_profile, viewGroup, false);
                viewHolder = new ProfileShortViewHolder(profile);
                addViewClickListeners(profile);
                break;
            default:
                View clip1 = layoutInflater.inflate(R.layout.rl_clip, viewGroup, false);
                addViewClickListeners(clip1);
                viewHolder = new ClipViewHolder(clip1);

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case PROFILE_DETAIL:
                ProfileDetailViewHolder profileDetailViewHolder = (ProfileDetailViewHolder) viewHolder;
                configureProfileDetail(profileDetailViewHolder , position);
                break;
            case PROFILE_SEARCH:
                ProfileShortViewHolder profileShortViewHolder = (ProfileShortViewHolder) viewHolder;
                configureProfileSearch(profileShortViewHolder, position);
                break;
            case CLIP:
                ClipViewHolder clipViewHolder = (ClipViewHolder) viewHolder;
                configureClip(clipViewHolder, position);
                break;
            case COMMENT:
                CommentViewHolder commentViewHolder = (CommentViewHolder) viewHolder;
                configureComment(commentViewHolder, position);
                break;
            default:
                ClipViewHolder clipViewHolderDef = (ClipViewHolder) viewHolder;
                configureClip(clipViewHolderDef, position);
        }

    }

    private void configureComment(CommentViewHolder commentViewHolder, int position) {
        Comment comment = (Comment) items.get(position);
        commentViewHolder.BindData(comment);
    }

    private void configureProfileSearch(ProfileShortViewHolder profileShortViewHolder, int position) {
        Profile profile = (Profile) items.get(position);
        profileShortViewHolder.BindData(profile);
    }

    private void configureProfileDetail(ProfileDetailViewHolder profileDetailViewHolder, int position) {
        ProfileDetail profileDetail = (ProfileDetail) items.get(position);
        profileDetailViewHolder.BindData(profileDetail);
    }

    private void configureClip(ClipViewHolder clipViewHolder, int position) {
        Clip clip = (Clip) items.get(position);
        clipViewHolder.BindData(clip, getContext());
    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }

}
