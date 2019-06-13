package com.subhamkumar.clipsy.models;

import android.os.Bundle;

public class Clip {

    public Profile profile;
    public String clip_id, comment, clip_content, clip_time, viewer_id, visibility;

    public Clip(Profile profile, String clip_id, String clip_content, String clip_time) {
        this.profile = profile;
        this.clip_id = clip_id;
        this.clip_content = clip_content;
        this.clip_time = clip_time;
    }

    public Clip(Bundle bundle) {
        this.profile.id = bundle.getString("id");
        this.profile.name = bundle.getString("name");
        this.profile.profile_pic = bundle.getString("profile_pic");

        this.clip_id = bundle.getString("clip_id");
        this.clip_content = bundle.getString("clip_content");
        this.clip_time = bundle.getString("clip_time");

    }

    public Clip() {
    }

}
