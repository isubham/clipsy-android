package com.subhamkumar.clipsy.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.ProfileDetail;

public class ProfileDetailViewHolder extends RecyclerView.ViewHolder {
    private final ImageView _choose_avatar_icon;
    private final TextView _name;
    private final TextView _email;
    private final Button relationshipButton;
    private final TextView fragment_profile_followers;
    private final TextView fragment_profile_following;
    private final TextView clip_count;
    private final TextView followers_count;
    private final TextView following_count;
    private final TextView viewer_id;
    private final TextView id;
    private final Button profile_edit;
    private final LinearLayout clipCounterContainer;
    private final LinearLayout followersCounterContainer;
    private final LinearLayout followingCounterContainer;

    public ProfileDetailViewHolder(@NonNull View V) {
        super(V);

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

        profile_edit = V.findViewById(R.id.profile_edit);
        viewer_id = V.findViewById(R.id.profile_landing_viewer_id);
        id = V.findViewById(R.id.profile_landing_id);


    }

    public void BindData(ProfileDetail profileDetail) {
        this._email.setText(profileDetail.email);
        this._name.setText(profileDetail.name);
        int _profile_pic;
        try {
            _profile_pic = Integer.parseInt(profileDetail.profile_pic);
        } catch (NumberFormatException e) {
            _profile_pic = 0;
        }
        int imageResource = Constants.mThumbIds[_profile_pic];
        this._choose_avatar_icon.setImageResource(imageResource);

        this.clip_count.setText(profileDetail.clips);
        this.following_count.setText(profileDetail.following);
        this.followers_count.setText(profileDetail.followers);

        this.relationshipButton.setText(profileDetail.message.equals("Following") ? "Unfollow" : profileDetail.message);

        this.viewer_id.setText(profileDetail.viewer_id);
        this.id.setText(profileDetail.id);

        hideRelationshipButtonIfSameUser(profileDetail.viewer_id, profileDetail.id);
        showEditButtonIfSameUser(profileDetail.viewer_id, profileDetail.id);

    }


    private void hideRelationshipButtonIfSameUser(String user_x, String user_y) {

        if (user_x.equals(user_y)) {
            relationshipButton.setVisibility(View.GONE);
        }

    }

    private void showEditButtonIfSameUser(String user_x, String user_y) {

        if (!user_x.equals(user_y)) {
            profile_edit.setVisibility(View.GONE);
        }

    }

}
