package com.subhamkumar.clipsy.models;

import java.util.Dictionary;
import java.util.HashMap;

public class Notification {
    public String Id, UserId, Message, NotificationType, ActionId, _read;

    public String getNotificationTitle(String notificationType) {
        return getNotificationType().get(notificationType);
    }

    private HashMap<String, String> getNotificationType() {
        HashMap<String, String> notificationTitle = new HashMap<>();

        notificationTitle.put("following", "New follower");
        notificationTitle.put("followingUserNewclip", "New Clip");
        notificationTitle.put("clipNewComment", "New Comment");

        return notificationTitle;
    }


}
