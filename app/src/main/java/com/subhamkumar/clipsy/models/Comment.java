package com.subhamkumar.clipsy.models;

import android.os.Bundle;

public class Comment {

    public Profile profile;
    public String comment_id, comment, comment_time, viewer_id, visibility;

    public Comment(Profile profile, String comment_id, String comment_content, String comment_time) {
        this.profile = profile;
        this.comment_id = comment_id;
        this.comment= comment_content;
        this.comment_time = comment_time;
    }

    public Comment(Bundle bundle) {
        this.profile.id = bundle.getString("id");
        this.profile.name = bundle.getString("name");
        this.profile.profile_pic = bundle.getString("profile_pic");

        this.comment_id = bundle.getString("comment_id");
        this.comment= bundle.getString("comment_content");
        this.comment_time = bundle.getString("comment_time");

    }

    public Comment() {
    }

}
