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
    visibility;

    public Clip(String id, String u_name, String u_id, String clip_content, String clip_time, String visibility) {
        this.id = id;
        this.u_name = u_name;
        this.u_id = u_id;
        this.clip_content = clip_content;
        this.clip_time = clip_time;
        this.visibility = visibility;
    }
}
