package com.subhamkumar.clipsy.models;

import com.subhamkumar.clipsy.R;

public class Constants {
    public static final Integer[] mThumbIds = {
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar,

            R.drawable.boy,
            R.drawable.boy_1,
            R.drawable.boy_2,
            R.drawable.boy_3,
            R.drawable.boy_4,
            R.drawable.boy_5,
            R.drawable.punk,
            R.drawable.punk_1,

            R.drawable.hindu,
            R.drawable.heisenberg,
            R.drawable.hood,
            R.drawable.japanese,
            R.drawable.knight,
            R.drawable.kung_fu,
            R.drawable.napoleon,
            R.drawable.robocop,
            R.drawable.samurai,
            R.drawable.santa_claus,
            R.drawable.wood_cutter,

            R.drawable.man,
            R.drawable.man_1,
            R.drawable.man_2,
            R.drawable.man_3,
            R.drawable.man_4,
            R.drawable.man_5,

            R.drawable.woman,
            R.drawable.woman_1,
            R.drawable.woman_2,
            R.drawable.woman_3,
            R.drawable.woman_4,
            R.drawable.woman_5,
            R.drawable.woman_6,
            R.drawable.woman_7,
            R.drawable.woman_8,
            R.drawable.woman_9,
            R.drawable.woman_10,
            R.drawable.woman_11,
            R.drawable.woman_12,
            R.drawable.woman_13,
            R.drawable.woman_14,
            R.drawable.woman_15,
            R.drawable.woman_16,
            R.drawable.woman_17,
            R.drawable.woman_18,
            R.drawable.woman_19,
            R.drawable.hindu_1,


    };
    public static final String CLIP_CONTENT = "clip_content";
    public static final String TOKEN = "token";

    public static final String request_clip_create = "https://api.pitavya.com/clipsy/create_clip";
    public static final String header_authentication = "Authentication";

    public static final String response_invalid_clip_id = "-1";
    public static final String request_clip_update = "https://api.pitavya.com/clipsy/update_clip/%1$s";
    public static final String request_user_get_user = "https://api.pitavya.com/clipsy/get/%1$s";
    public static String request_user_update_avatar = "https://api.pitavya.com/clipsy/update_user/%1$s";
    public static final String request_clip_read = "https://api.pitavya.com/clipsy/read_clip/%1$s";
    public static final String request_clip_delete = "https://api.pitavya.com/clipsy/delete_clip/%1$s";
    public static final String request_user_update_password = "https://api.pitavya.com/clipsy/update_password";
    public static final String request_report_send = "https://api.pitavya.com/clipsy/report/";
    public static final String request_signup = "https://api.pitavya.com/clipsy/signup/";
    public static final String request_user_forgot_password = "https://api.pitavya.com/clipsy/forgot_password";
    public static final String request_user_sign_in = "https://api.pitavya.com/clipsy/signin";
    public static final String request_user_verify_email = "https://api.pitavya.com/clipsy/verify_email_verification";

    public static final String request_user_user_following = "https://api.pitavya.com/clipsy/following/%1$s";
    public static final String request_user_user_follower = "https://api.pitavya.com/clipsy/follower/%1$s";

    public static final String request_clip_get_comment = "https://api.pitavya.com/clipsy/comment/%1$s";
    // status
    public static final String status_success = "1";
    public static final String status_failed = "0";

    public static final String message_signup_account_created = "Account Created";

    public static final String SIGNOUT = "sign_out";
    public static final String CLOSE = "close";
    public static final String DOT = "·";


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


    public static final String myFile = "theAwesomeDataInMain";
    public static String myKey = "52521";

    public static final String param_visibility = "visibility";
    public static final String visibility_public = "1";
    public static final String visibility_private = "2";


    // report of clips

    public static final String report_spam = "1";
    public static final String report_inappropriate = "2";



    public static int profilePicChangeRequest = 23323;



}
