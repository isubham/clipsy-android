package com.subhamkumar.clipsy.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.Profile;

import java.util.List;

public class Profile_adapter extends RecyclerView.Adapter<Profile_adapter.Profile_viewholder> {

    public static class Profile_viewholder extends RecyclerView.ViewHolder {
        public TextView id, name, email; ImageView profile_pic;

        Profile_viewholder(View V) {
            super(V);
            id = (TextView) V.findViewById(R.id.rl_profile_id);
            email = (TextView) V.findViewById(R.id.rl_profile_email);
            name = (TextView) V.findViewById(R.id.rl_profile_name);
            profile_pic = (ImageView) V.findViewById(R.id.rl_profile_profile_pic);
        }
    }

    public List<Profile> profiles;

    public Profile_adapter(List<Profile> profiles) {
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public Profile_viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View V = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rl_profile, viewGroup, false);
        Profile_viewholder clip_viewholder = new Profile_viewholder(V);
        return clip_viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Profile_viewholder clip_viewholder, int i) {

        clip_viewholder.id.setText(                 profiles.get(i).id);
        clip_viewholder.name.setText(          profiles.get(i).name);
        clip_viewholder.email.setText(          profiles.get(i).email);

        try{
            int _profile_pic;
            // TODO set default when not set.
            if(profiles.get(i).profile_pic.equals("")) {
                _profile_pic = 0;
            }
            _profile_pic = Integer.parseInt(profiles.get(i).profile_pic);
            int imageResource = CONSTANTS.mThumbIds[_profile_pic];

            clip_viewholder.profile_pic.setImageResource(imageResource);
        }catch (NumberFormatException e) {
            Log.i("002", "nullformatexception" + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

}
