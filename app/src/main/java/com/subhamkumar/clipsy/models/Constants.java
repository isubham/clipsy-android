package com.subhamkumar.clipsy.models;

import com.subhamkumar.clipsy.R;

public  class Constants {
public  static final Integer[] mThumbIds = {
            R.drawable.boy003,
            R.drawable.boy007,
            R.drawable.boy016,
            R.drawable.boy016,
            R.drawable.girl002,
            R.drawable.girl010,
            R.drawable.girl017,
            R.drawable.girl021,
            R.drawable.man001,
            R.drawable.man005,
            R.drawable.man011,
            R.drawable.man014,
            R.drawable.woman004,
            R.drawable.woman006,
            R.drawable.woman012,
            R.drawable.woman013,

    };
    public static final String USER_ID = "user_id";
    public static final String STATUS = "status";
    public static final String CLIP_CREATED = "Clip Created";
    public static final String JSON_EX = "json ex";
    public static final String CREATE_CLIP = "applyChangesToClip";
    public static final String FX = "fx";
    public static final String CLIP_CONTENT = "clip_content";
    public static final String CLIP_TITLE = "clip_title";
    public static final String USER = "user";
    public static final String VISIBILITY = "visibility";
    public static final String FRAGMENT_CLIPS = "fragment_clips";
    public static final String TOKEN = "token";



    public static String OPERATION_SIGN_UP = "sign_up";
    public static String OPERATION_SIGN_IN = "sign_in";
    public static String OPERATION_UPDATE_USER = "update_user";

    public static String OPERATION_CREATE_CLIP = "applyChangesToClip";
    public static String OPERATION_READ_CLIPS = "read_clips";
    public static String OPERATION_UPDATE_CLIP = "update_clip";
    public static String OPERATION_DELETE_CLIP = "delete_clip";

    public static String OPERATION_FOLLOW = "follow";
    public static String OPERATION_UNFOLLOW = "unfollow";
    public static String OPERATION_FOLLOWING = "following";
    public static String OPERATION_FOLLOWERS = "followers";
    public static String OPERATION_FOLLOWING_CLIPS = "following_clips";


    public static String FIELD_USER_EMAIL = "email";
    public static String FIELD_USER_PASSWORD = "password";
    public static String FIELD_USER_NAME = "name";
    public static String FIELD_USER_TYPE = "following_clips";
    public static String FIELD_ID = "id";
    public static String FIELD_USER_ID = "user_id";
    public static String FIELD_TOKEN = "token";



    public static String FIELD_USER_USER_USET_X = "user_x";
    public static String FIELD_USER_USER_USER_Y = "user_y";
    public static String FIELD_USER_USER_TYPE = "type";


    public static String FIELD_CLIP_ID = "id";
    public static String FIELD_CLIP_TITLE  = "clip_title";
    public static String FIELD_CLIP_CONTENT = "clip_content";
    public static String FIELD_CLIP_TIMESTAMP = "timestamp";
    public static String FIELD_CLIP_VISIBILITY = "visibility";
    public static String FIELD_CLIP_USER = "user";

    public static String PUBLIC = "1";
    public static String PRIVATE = "2";

    public static final String request_clip_create = "http://api.pitavya.com/clipsy/create_clip";
    public static final String header_authentication = "Authentication";

   public static final String response_invalid_clip_id = "-1";
    public static final String request_clip_update = "http://api.pitavya.com/clipsy/update_clip/%1$s";
    public static final String request_user_get_user = "http://api.pitavya.com/clipsy/get/%1$s";
    public static String request_user_update_avatar = "http://api.pitavya.com/clipsy/update_avatar/%1$s";
    public static final String request_clip_read = "http://api.pitavya.com/clipsy/read_clip/%1$s";
    public static final String request_clip_delete = "http://api.pitavya.com/clipsy/delete_clip/%1$s";


}
