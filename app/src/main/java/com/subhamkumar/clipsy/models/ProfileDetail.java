package com.subhamkumar.clipsy.models;

import java.util.ArrayList;

public class ProfileDetail extends BaseModel{
    public String id, email, name, profile_pic, type, token, clips, followers, following,
            showCloseIcon, message;
    public ArrayList<relationshipActions> actions;

    private class relationshipActions {
        public String action;
    }
}


