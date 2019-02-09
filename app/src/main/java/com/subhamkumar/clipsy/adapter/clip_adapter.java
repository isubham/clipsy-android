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
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.models.Clip;
import com.subhamkumar.clipsy.utils.CustomTabs;

import java.util.List;

public class clip_adapter extends RecyclerView.Adapter<clip_adapter.Clip_viewholder> {

    public static class Clip_viewholder extends RecyclerView.ViewHolder {
        public TextView id,

        author_name,
                author_id,

                clip_time;
        ImageView profile_pic;
        WebView
                clip_content;

        Clip_viewholder(View V) {
            super(V);

            id = (TextView) V.findViewById(R.id.rl_clip_id);
            author_id = (TextView) V.findViewById(R.id.rl_clip_author_id);
            author_name = (TextView) V.findViewById(R.id.rl_clip_author);
            clip_time = (TextView) V.findViewById(R.id.rl_clip_time);
            clip_content = (WebView) V.findViewById(R.id.rl_clip_content);
            profile_pic = (ImageView) V.findViewById(R.id.rl_clip_profile_pic);

        }
    }

    public List<Clip> clips;
    Context context;

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
        return clip_viewholder;
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
                Log.i("link", span.getURL());
                CustomTabs.openTab(context, span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html)
    {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBindViewHolder(@NonNull Clip_viewholder clip_viewholder, int i) {

        clip_viewholder.id.setText(                 clips.get(i).id);
        clip_viewholder.author_name.setText(        clips.get(i).u_name);
        clip_viewholder.author_id.setText(          clips.get(i).u_id);
        clip_viewholder.clip_content.loadData(clips.get(i).clip_content, "text/html", "UTF-8");
        clip_viewholder.clip_time.setText(          clips.get(i).clip_time);

        try{
            int _profile_pic = Integer.parseInt(clips.get(i).profile_pic);
            int imageResource = CONSTANTS.mThumbIds[_profile_pic];
            clip_viewholder.profile_pic.setImageResource(imageResource);
        }catch (NumberFormatException e) {
            Log.i("002", "nullformatexception"+ e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return clips.size();
    }

}
