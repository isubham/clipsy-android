package com.subhamkumar.clipsy.models;

public class Clip {
    /*
    *
    *{
    * "8":{"u_id":"1","u_name":"subham","clip_title":"Gone","clip_content":"Sometimes","timestamp":"2018-09-07 22:36:45","visibility":"1"},
    * "2":{"u_id":"1","u_name":"subham","clip_title":"hello subham","clip_content":"hello yeah","timestamp":"2018-09-05 12:27:22","visibility":"2"},
    * "1":{"u_id":"1","u_name":"subham","clip_title":"Hell","clip_content":"Hell yeah","timestamp":"2018-09-05 12:26:02","visibility":"1"}
    * }
    * `*/
    public String id,
    u_name,
    u_id,
    clip_content,
    clip_time,
    visibility, profile_pic;

    public Clip(String id, String u_name, String u_id, String clip_content, String clip_time, String visibility, String profile_pic) {
        this.id = id;
        this.u_name = u_name;
        this.u_id = u_id;
        this.clip_content = clip_content;
        this.clip_time = clip_time;
        this.visibility = visibility;
        this.profile_pic = profile_pic;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getClip_content() {
        return clip_content;
    }

    public void setClip_content(String clip_content) {
        this.clip_content = clip_content;
    }

    public String getClip_time() {
        return clip_time;
    }

    public void setClip_time(String clip_time) {
        this.clip_time = clip_time;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
