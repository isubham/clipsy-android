package com.subhamkumar.clipsy.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;
import com.subhamkumar.clipsy.utils.Tools;

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

    public void BindData(Profile profile, Context context) {
        this.id.setText(profile.id);
        this.name.setText(profile.name);
        this.email.setText(profile.email);
        setCrossIcon(this, profile.showCloseIcon);
        Tools.setProfilePic(profile.profile_pic, this.profile_pic, context);
    }

    private void setCrossIcon(@NonNull ProfileShortViewHolder profileViewholder, String showCloseIcon) {
        if (!showCloseIcon.equals("1")) {
            profileViewholder.crossIcon.setVisibility(View.GONE);
        } else {
            profileViewholder.crossIcon.setVisibility(View.VISIBLE);
        }
    }

}
