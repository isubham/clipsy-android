package com.subhamkumar.clipsy.models;

public class Profile {
    public String id, email, name, password, profile_pic, type, token, clips, followers, following, showCloseIcon, status;
    public String emailStatus, passwordStatus;


    public Profile() {

    }

    public Profile(String id, String email, String name, String profile_pic) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.profile_pic = profile_pic;
    }

    public Profile(String email, String password, String status, String emailStatus, String passwordStatus) {
        this.email = email;
        this.password = password;
        this.status = status;
        this.emailStatus = emailStatus;
        this.passwordStatus = passwordStatus;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
