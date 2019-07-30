package com.subhamkumar.clipsy.utils;

import android.os.Bundle;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;

import static com.subhamkumar.clipsy.models.Constants.PROFILE_LIST_TO_PROFILE_RESULT;
import static com.subhamkumar.clipsy.models.Constants.TO_HOME;
import static com.subhamkumar.clipsy.models.Constants.fragment_profile;
import static com.subhamkumar.clipsy.models.Constants.fragment_search;
import static com.subhamkumar.clipsy.models.Constants.searched_user_id;
import static com.subhamkumar.clipsy.models.Constants.id;
import static com.subhamkumar.clipsy.models.Constants.token;



public class Message {

    public static Bundle fragmentSearchToProfileResult(String _token, String _id, String _searchedUserId) {
        Bundle message = new Bundle();
        message.putString(token, _token);
        message.putString(id, _id);
        message.putString(searched_user_id , _searchedUserId);
        // message.putString(TO_HOME, fragment_search);
        return message;
    }


    public static Bundle fragmentProfileToProfileResult(String _token, String _id, String _searchedUserId) {
        Bundle message = new Bundle();
        message.putString(searched_user_id, _searchedUserId);
        message.putString(token, _token);
        message.putString(id, _id);
        // message.putString(TO_HOME, PROFILE_LIST_TO_PROFILE_RESULT);
        return message;

    }



    public static Bundle fragmentClipToProfileResult(String _token, String _id, String _searchedUserId) {
        Bundle message = new Bundle();
        message.putString(Constants.token, _token);
        message.putString(Constants.id, _id);
        message.putString(searched_user_id, _searchedUserId);
        // message.putString(Constants.TO_HOME, fragment_profile);
        return message;
    }

    public static String getSearchId_forClips_orSearch_orProfileList(Bundle arguments, String id) {
        if (arguments.containsKey(searched_user_id)) {
            return arguments.getString(searched_user_id);
        }
        return  id;
    }

    public static String getToken(Bundle bundle) {
        return bundle.getString("token");
    }

    public static String getId(Bundle bundle) {
        return bundle.getString("id");
    }



}
