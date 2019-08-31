package com.subhamkumar.clipsy.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;

public class ProfileShortViewHolder extends RecyclerView.ViewHolder {

    private final TextView id;
    private final TextView name;
    private final TextView email;
    private final ImageView profile_pic;
    private final ImageView crossIcon;

    public ProfileShortViewHolder(View V) {
        super(V);
        id = V.findViewById(R.id.rl_profile_id);
        email = V.findViewById(R.id.rl_profile_email);
        name = V.findViewById(R.id.rl_profile_name);
        profile_pic = V.findViewById(R.id.rl_profile_profile_pic);
        crossIcon = V.findViewById(R.id.rl_profile_close);

    }

    public void BindData(Profile profile) {
        this.id.setText(profile.id);
        this.name.setText(profile.name);
        this.email.setText(profile.email);
        setCrossIcon(this, profile.showCloseIcon);
        setProfilePic(this, profile.profile_pic);
    }

    private void setCrossIcon(@NonNull ProfileShortViewHolder profileViewholder, String showCloseIcon) {
        if (!showCloseIcon.equals("1")) {
            profileViewholder.crossIcon.setVisibility(View.GONE);
        } else {
            profileViewholder.crossIcon.setVisibility(View.VISIBLE);
        }
    }

    private void setProfilePic(@NonNull ProfileShortViewHolder profileViewholder, String profile_pic) {
        try {
            int _profile_pic;
            // TODO set default when not set.
            if (profile_pic.equals("")) {
                _profile_pic = 0;
            } else {
                _profile_pic = Integer.parseInt(profile_pic);
            }

            int imageResource = Constants.mThumbIds[_profile_pic];
            profileViewholder.profile_pic.setImageResource(imageResource);
        } catch (NumberFormatException e) {
        }
    }
}
