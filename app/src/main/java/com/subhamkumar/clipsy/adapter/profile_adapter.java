package com.subhamkumar.clipsy.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Profile;

import java.util.List;
import java.util.Objects;

abstract public class profile_adapter extends RecyclerView.Adapter<profile_adapter.Profile_viewholder> {

    protected abstract void addViewClickListeners(View V);

    public static class Profile_viewholder extends RecyclerView.ViewHolder {
        final TextView id;
        final TextView name;
        final TextView email;
        final ImageView profile_pic;
        final ImageView crossIcon;

        Profile_viewholder(View V) {
            super(V);
            id = V.findViewById(R.id.rl_profile_id);
            email = V.findViewById(R.id.rl_profile_email);
            name = V.findViewById(R.id.rl_profile_name);
            profile_pic = V.findViewById(R.id.rl_profile_profile_pic);
            crossIcon = V.findViewById(R.id.rl_profile_close);

        }
    }

    private final List<Profile> profiles;

    public profile_adapter(List<Profile> profiles) {
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public Profile_viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View V = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rl_profile, viewGroup, false);
        Profile_viewholder clip_viewholder = new Profile_viewholder(V);
        Context context = viewGroup.getContext();
        addViewClickListeners(V);
        return clip_viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Profile_viewholder profileViewholder, int i) {

        profileViewholder.id.setText(profiles.get(i).id);
        profileViewholder.name.setText(profiles.get(i).name);
        profileViewholder.email.setText(profiles.get(i).email);

        setCrossIcon(profileViewholder, i);

        setProfilePic(profileViewholder, i);

    }

    private void setCrossIcon(@NonNull Profile_viewholder profileViewholder, int i) {
        if (!Objects.equals(profiles.get(i).showCloseIcon, "1")) {
            profileViewholder.crossIcon.setVisibility(View.GONE);
        } else {
            profileViewholder.crossIcon.setVisibility(View.VISIBLE);
        }
    }

    private void setProfilePic(@NonNull Profile_viewholder profileViewholder, int i) {
        try {
            int _profile_pic;
            // TODO set default when not set.
            if (profiles.get(i).profile_pic.equals("")) {
                _profile_pic = 0;
            } else {

                _profile_pic = Integer.parseInt(profiles.get(i).profile_pic);
            }

            int imageResource = Constants.mThumbIds[_profile_pic];
            profileViewholder.profile_pic.setImageResource(imageResource);
        } catch (NumberFormatException e) {
            // Log.i("002", "nullformatexception" + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

}
