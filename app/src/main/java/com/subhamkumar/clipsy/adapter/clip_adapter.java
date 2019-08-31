package com.subhamkumar.clipsy.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.utils.CustomTabs;

import java.util.List;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.html.HtmlPlugin;

abstract public class clip_adapter extends RecyclerView.Adapter<clip_adapter.Clip_viewholder> {


    protected abstract void addViewClickListeners(View V);

    public static class Clip_viewholder extends RecyclerView.ViewHolder {
        final TextView id;

        final TextView author_name;
        final TextView author_id;
        final TextView viewer_id;
        final TextView clip_time;

        final ImageView profile_pic;
        final TextView clip_content;
        final TextView clip_title;


        final TextView clip_comment;


        final TextView clip_visibility_text;
        final ImageView clip_visibility_image;


        Clip_viewholder(View V) {
            super(V);

            id = V.findViewById(R.id.rl_clip_id);
            author_id = V.findViewById(R.id.rl_clip_author_id);
            viewer_id = V.findViewById(R.id.rl_clip_viewer_id);

            author_name = V.findViewById(R.id.rl_clip_author);
            clip_time = V.findViewById(R.id.rl_clip_time);
            clip_content = V.findViewById(R.id.rl_clip_content);
            clip_title = V.findViewById(R.id.rl_clip_title);


            clip_comment = V.findViewById(R.id.rl_clip_comment);


            profile_pic = V.findViewById(R.id.rl_clip_profile_pic);

            clip_visibility_text = V.findViewById(R.id.rl_clip_visibility_text);
            clip_visibility_image = V.findViewById(R.id.rl_clip_visibility_image);


        }
    }

    private final List<Clip> clips;
    private Context context;

    public clip_adapter(List<Clip> clips) {
        this.clips = clips;
    }

    @NonNull
    @Override
    public Clip_viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View V = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rl_clip, viewGroup, false);
        Clip_viewholder clip_viewholder = new Clip_viewholder(V);
        context = viewGroup.getContext();

        addViewClickListeners(V);

        return clip_viewholder;
    }


    @Override
    public void onBindViewHolder(@NonNull Clip_viewholder clip_viewholder, int i) {

        clip_viewholder.id.setText(clips.get(i).clip_id);
        clip_viewholder.author_name.setText(clips.get(i).profile.name);
        clip_viewholder.author_id.setText(clips.get(i).profile.id);
        clip_viewholder.viewer_id.setText(clips.get(i).viewer_id);
        clip_viewholder.clip_time.setText(clips.get(i).clip_time.concat(" · "));
        clip_viewholder.clip_title.setText(clips.get(i).clip_title);

        setCommentViews(clip_viewholder, i);

        renderHtmlInClipContent(clip_viewholder, i);

        setProfilePic(clip_viewholder, i);
    }

    private void setCommentViews(@NonNull Clip_viewholder clip_viewholder, int i) {
        int commentCount = Integer.parseInt(clips.get(i).comment);
        String commentMessage;
        if (commentCount == 0) {
            commentMessage = "Be first commenter";
        }
        else{
            commentMessage = String.format("View %1$s Comment%2$s", clips.get(i).comment,  commentCount > 1 ? "s" : "");
        }
        clip_viewholder.clip_comment.setText(commentMessage);

        String visibility_text = clips.get(i).visibility.equals(Constants.visibility_private) ? "Private. " : "Public. ";
        clip_viewholder.clip_visibility_text.setText(visibility_text);

        int visibility_img = clips.get(i).visibility.equals(Constants.visibility_private) ? R.drawable.user : R.drawable.globe;
        clip_viewholder.clip_visibility_image.setImageResource(visibility_img);
    }

    private void setProfilePic(@NonNull Clip_viewholder clip_viewholder, int i) {
        int imageResource, _profile_pic = 0;

        try {
            _profile_pic = Integer.parseInt(clips.get(i).profile.profile_pic);
        } catch (NumberFormatException e) {
            _profile_pic = 0;
        } finally {
            imageResource = Constants.mThumbIds[_profile_pic];
            clip_viewholder.profile_pic.setImageResource(imageResource);
        }
    }

    private void renderHtmlInClipContent(@NonNull Clip_viewholder clip_viewholder, int i) {
        String htmlString = clips.get(i).clip_content;
        final Markwon.Builder builder = Markwon.builder(context);
        builder.usePlugin(HtmlPlugin.create());
        builder.usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver((view, link) -> {
                    // Log.i("link", link);
                    CustomTabs.openTab(context, link);
                });
            }
        });

        final Spanned markdown = builder.build().toMarkdown(htmlString);
        clip_viewholder.clip_content.setText(markdown);
        clip_viewholder.clip_content.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public int getItemCount() {
        return clips.size();
    }

}
