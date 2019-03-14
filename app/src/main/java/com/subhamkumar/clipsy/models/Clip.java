package com.subhamkumar.clipsy.models;

public class Clip {

    public Profile profile;
    public String clip_id, clip_content, clip_time;

    public Clip(Profile profile, String clip_id, String clip_content, String clip_time) {
        this.profile =  profile;
        this.clip_id = clip_id;
        this.clip_content = clip_content;
        this.clip_time = clip_time;
    }


}
