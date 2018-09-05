package com.subhamkumar.clipsy.models;

public class Clip {
    public String id,

    author_name,
    author_id,

    clip_title,
    clip_content,
    clip_time;

    public Clip(String id, String author_name, String author_id, String clip_title, String clip_content, String clip_time) {
        this.id = id;
        this.author_name = author_name;
        this.author_id = author_id;
        this.clip_title = clip_title;
        this.clip_content = clip_content;
        this.clip_time = clip_time;
    }
}
