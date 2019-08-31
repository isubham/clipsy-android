package com.subhamkumar.clipsy.models;

import android.os.Bundle;

import java.util.Objects;

public class Clip extends BaseModel {

    public Profile profile;
    public String clip_id, comment, clip_content, clip_title, clip_time, visibility;

    public Clip(Profile profile, String clip_id, String clip_content, String clip_time, String clip_title) {
        this.profile = profile;
        this.clip_id = clip_id;
        this.clip_content = clip_content;
        this.clip_time = clip_time;
        this.clip_title = clip_title;
    }

    public Clip(Bundle bundle) {
        Objects.requireNonNull(this.profile).id = bundle.getString("id");
        this.profile.name = bundle.getString("name");
        this.profile.profile_pic = bundle.getString("profile_pic");

        this.clip_id = bundle.getString("clip_id");
        this.clip_content = bundle.getString("clip_content");
        this.clip_time = bundle.getString("clip_time");
        this.clip_title = bundle.getString("clip_title");

    }

    public Clip() {
    }

}
