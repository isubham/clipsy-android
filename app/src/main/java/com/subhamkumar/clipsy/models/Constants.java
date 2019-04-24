package com.subhamkumar.clipsy.models;

import com.subhamkumar.clipsy.R;

public class Constants {
    public static final Integer[] mThumbIds = {
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
    public static final String CLIP_CONTENT = "clip_content";
    public static final String TOKEN = "token";

    public static final String request_clip_create = "http://api.pitavya.com/clipsy/create_clip";
    public static final String header_authentication = "Authentication";

    public static final String response_invalid_clip_id = "-1";
    public static final String request_clip_update = "http://api.pitavya.com/clipsy/update_clip/%1$s";
    public static final String request_user_get_user = "http://api.pitavya.com/clipsy/get/%1$s";
    public static String request_user_update_avatar = "http://api.pitavya.com/clipsy/update_avatar/%1$s";
    public static final String request_clip_read = "http://api.pitavya.com/clipsy/read_clip/%1$s";
    public static final String request_clip_delete = "http://api.pitavya.com/clipsy/delete_clip/%1$s";
    public static final String request_user_update_password = "http://api.pitavya.com/clipsy/update_password";
    public static final String request_report_send = "http://api.pitavya.com/clipsy/report/";
    public static final String request_signup = "http://api.pitavya.com/clipsy/signup/";
    public static final String request_user_forgot_password = "http://api.pitavya.com/clipsy/forgot_password";
    public static final String request_user_sign_in = "http://api.pitavya.com/clipsy/signin";
    public static final String request_user_verify_email = "http://api.pitavya.com/clipsy/verify_email_verification";

    public static final String request_user_user_following = "http://api.pitavya.com/clipsy/following/%1$s";
    public static final String request_user_user_follower = "http://api.pitavya.com/clipsy/follower/%1$s";

    // status
    public static final String status_success = "1";
    public static final String status_failed = "0";

    public static final String message_signup_account_created = "Account Created";

    public static final String SIGNOUT = "sign_out";
    public static final String CLOSE = "close";

    // param


    public static final String param_id = "id";
    public static final String param_email = "email";
    public static final String param_verify_token = "verify_token";
    public static final String param_password = "password";
    public static final String param_type = "type";
    public static final String param_name = "name";
    public static final String param_value_public = "2";
    public static final String param_token = "token";
    public static final String param_search_id = "search_id";

    public static final String bundle_param_caller_activity_to_email_verification = "callback";
    public static final String bundle_param_caller_activity_to_fragment_clips = "where_to_fragment_clips";
    public static final String bundle_param_caller_activity_panel = "panel";
    public static final String bundle_param_caller_button_to_profile_list = "profile_list";
    public static final String bundle_param_caller_button_following = "following";
    public static final String bundle_param_profile_result_searched_user_id = "searched_id";
    public static final String bundle_param_caller_activity_fragment_profile_list_to_profile_result = "profile_list_to_complete_profile";



}
