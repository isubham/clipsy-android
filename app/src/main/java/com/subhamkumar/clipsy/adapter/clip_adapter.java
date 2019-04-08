package com.subhamkumar.clipsy.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.utils.CustomTabs;

import java.util.List;

abstract  public class clip_adapter extends RecyclerView.Adapter<clip_adapter.Clip_viewholder> {


    protected abstract void addViewClickListeners(View V) ;

    public static class Clip_viewholder extends RecyclerView.ViewHolder {
        final TextView id;

        final TextView author_name;
        final TextView author_id;
        final TextView viewer_id;
        final TextView clip_time;

        final ImageView profile_pic;
        final WebView
                clip_content;
        Clip_viewholder(View V) {
            super(V);

            id = V.findViewById(R.id.rl_clip_id);
            author_id = V.findViewById(R.id.rl_clip_author_id);
            viewer_id = V.findViewById(R.id.rl_clip_viewer_id);

            author_name = V.findViewById(R.id.rl_clip_author);
            clip_time = V.findViewById(R.id.rl_clip_time);
            clip_content = V.findViewById(R.id.rl_clip_content);

            WebSettings webSettings = clip_content.getSettings();
            webSettings.setTextSize(WebSettings.TextSize.SMALLER);

            profile_pic = V.findViewById(R.id.rl_clip_profile_pic);
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


    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(@NonNull View view) {
                // Do something with span.getURL() to handle the link click...
                Log.i("link", span.getURL());
                CustomTabs.openTab(context, span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBindViewHolder(@NonNull Clip_viewholder clip_viewholder, int i) {

        clip_viewholder.id.setText(clips.get(i).clip_id);
        clip_viewholder.author_name.setText(clips.get(i).profile.name);
        clip_viewholder.author_id.setText(clips.get(i).profile.id);
        clip_viewholder.viewer_id.setText(clips.get(i).viewer_id);

        clip_viewholder.clip_content.loadData(clips.get(i).clip_content, "text/html", "UTF-8");

        clip_viewholder.clip_time.setText(clips.get(i).clip_time);


        try {
            int _profile_pic = Integer.parseInt(clips.get(i).profile.profile_pic);
            int imageResource = Constants.mThumbIds[_profile_pic];
            clip_viewholder.profile_pic.setImageResource(imageResource);
        } catch (NumberFormatException e) {
            int _profile_pic = 0;
            int imageResource = Constants.mThumbIds[_profile_pic];
        }
    }

    @Override
    public int getItemCount() {
        return clips.size();
    }

}
