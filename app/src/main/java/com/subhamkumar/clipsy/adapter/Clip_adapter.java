package com.subhamkumar.clipsy.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Clip;

import java.util.List;

public class Clip_adapter extends RecyclerView.Adapter<Clip_adapter.Clip_viewholder> {

    public static class Clip_viewholder extends RecyclerView.ViewHolder {
        public TextView id,

        author_name,
                author_id,

        clip_title,
                clip_content,
                clip_time;

        Clip_viewholder(View V) {
            super(V);

            id = (TextView) V.findViewById(R.id.rl_clip_id);
            author_id = (TextView) V.findViewById(R.id.rl_clip_author_id);
            author_name = (TextView) V.findViewById(R.id.rl_clip_author);
            clip_time = (TextView) V.findViewById(R.id.rl_clip_time);
            clip_title = (TextView) V.findViewById(R.id.rl_clip_title);
            clip_content = (TextView) V.findViewById(R.id.rl_clip_content);
        }
    }

    public List<Clip> clips;

    public
    Clip_adapter(List<Clip> clips) {
        this.clips = clips;
    }

    @NonNull
    @Override
    public Clip_viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View V = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rl_clip, viewGroup, false);
        Clip_viewholder clip_viewholder = new Clip_viewholder(V);
        return clip_viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull Clip_viewholder clip_viewholder, int i) {

        clip_viewholder.id.setText(                 clips.get(i).id);
        clip_viewholder.author_name.setText(        clips.get(i).u_name);
        clip_viewholder.author_id.setText(          clips.get(i).u_id);
        clip_viewholder.clip_title.setText(         clips.get(i).clip_title);
        clip_viewholder.clip_content.setText(       clips.get(i).clip_content);
        clip_viewholder.clip_time.setText(          clips.get(i).clip_time);

    }

    @Override
    public int getItemCount() {
        return clips.size();
    }

}
