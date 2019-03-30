package com.subhamkumar.clipsy.utils;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.subhamkumar.clipsy.R;

public class BundleBetweenActivities extends Fragment {

    private final Bundle message;

    public BundleBetweenActivities() {
        message = new Bundle();
    }

    public Bundle activity_sign_in_to_activity_forgot_password(String email) {
        message.putString("email", email);
        return message;
    }

    public Bundle activity_sign_up_to_activity_email_verification(String email) {
        message.putString("email", email);
        return message;
    }

    public Bundle activity_forgot_password_to_activity_change_password(String email) {
        message.putString("email", email);
        return message;
    }

    public Bundle activity_change_password_to_activity_sign_in(String email) {
        message.putString("email", email);
        return message;
    }

    public Bundle activity_email_verification_to_activity_sign_in(String email) {
        message.putString("email", email);
        return message;
    }

    public Bundle fragment_profile_followers_button_to_activity_profile_list(String user_viewed, String token, String id) {

        message.putString(GetString(R.string.bundle_param_caller_button_to_profile_list),
                GetString(R.string.bundle_param_caller_button_followers));
        message.putString(GetString(R.string.params_search_id), user_viewed);
        message.putString(GetString(R.string.params_token), token);
        message.putString(GetString(R.string.params_id), id);
        return message;
    }


    public Bundle fragment_profile_following_button_to_activity_profile_list(String user_viewed, String token, String id) {

        message.putString(GetString(R.string.bundle_param_caller_button_to_profile_list),
                GetString(R.string.bundle_param_caller_button_following));
        message.putString(GetString(R.string.params_search_id), user_viewed);
        message.putString(GetString(R.string.params_token), token);
        message.putString(GetString(R.string.params_id), id);
        return message;
    }

    public Bundle fragment_profile_choose_avatar_button_to_activity_profile_list(String user_viewed, String token, String id) {

        message.putString(GetString(R.string.params_search_id), user_viewed);
        message.putString(GetString(R.string.params_token), token);
        message.putString(GetString(R.string.params_id), id);
        return message;
    }

    private String GetString(int id) {
        return Resources.getSystem().getString(id);
    }

    public Bundle fragment_search_to_activity_profile_result(String searchedUserId) {
        message.putString(GetString(R.string.bundle_param_profile_result_searched_user_id), searchedUserId);
        message.putString(GetString(R.string.bundle_param_caller_activity_to_fragment_clips),
                GetString(R.string.bundle_param_caller_activity_fragment_search));
        return message;
    }

}
