package com.subhamkumar.clipsy.ViewHolders;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.CustomTabs;

import ru.noties.markwon.AbstractMarkwonPlugin;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.html.HtmlPlugin;

public class ClipViewHolder extends RecyclerView.ViewHolder {
    private final TextView id;
    private final TextView author_id;
    private final TextView viewer_id;
    private final TextView clip_time;
    private final ImageView profile_pic;
    private final TextView clip_content;
    private final TextView clip_title;
    private final TextView clip_comment;
    private final TextView author_name;
    private final TextView clip_visibility_text;
    private final ImageView clip_visibility_image;


    public ClipViewHolder(View V) {
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


    public void BindData(Clip clip, Context context) {

        this.id.setText(clip.clip_id);
        this.author_name.setText(clip.profile.name);
        this.author_id.setText(clip.profile.id);
        this.viewer_id.setText(clip.viewer_id);
        this.clip_time.setText(clip.clip_time.concat(" · "));
        if (clip.clip_time.isEmpty()) {
            this.clip_title.setVisibility(View.GONE);
        }
        else{
            this.clip_title.setVisibility(View.VISIBLE);
            this.clip_title.setText(clip.clip_title);
        }

        // Comment
        int commentCount = Integer.parseInt(clip.comment);
        String commentMessage;
        if (commentCount == 0) {
            commentMessage = "Be first commenter";
        }
        else{
            commentMessage = String.format("View %1$s Comment%2$s", clip.comment,  commentCount > 1 ? "s" : "");
        }
        this.clip_comment.setText(commentMessage);

        // visibility icon
        String visibility_text = clip.visibility.equals(Constants.visibility_private) ? "Private. " : "Public. ";
        this.clip_visibility_text.setText(visibility_text);

        int visibility_img = clip.visibility.equals(Constants.visibility_private) ? R.drawable.user : R.drawable.globe;
        this.clip_visibility_image.setImageResource(visibility_img);

        // profile pic
        int imageResource, _profile_pic = 0;

        try {
            _profile_pic = Integer.parseInt(clip.profile.profile_pic);
        } catch (NumberFormatException e) {
            _profile_pic = 0;
        } finally {
            imageResource = Constants.mThumbIds[_profile_pic];
            this.profile_pic.setImageResource(imageResource);
        }

        // Render HTML
        renderHtmlInClipContent(this, clip.clip_content, context);


    }

    private void renderHtmlInClipContent(@NonNull ClipViewHolder clipViewHolder, String content, Context context) {
        final Markwon.Builder builder = Markwon.builder(context);
        builder.usePlugin(HtmlPlugin.create());
        builder.usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                builder.linkResolver((view, link) -> CustomTabs.openTab(context, link));
            }
        });

        final Spanned markdown = builder.build().toMarkdown(content);
        clipViewHolder.clip_content.setText(markdown);
        clipViewHolder.clip_content.setMovementMethod(LinkMovementMethod.getInstance());
    }

}

