package com.subhamkumar.clipsy.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Notification;
import com.subhamkumar.clipsy.panel.panel;
import com.subhamkumar.clipsy.panel.profile_result;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

import static com.subhamkumar.clipsy.models.Constants.notificationTypeFollowing;
import static com.subhamkumar.clipsy.models.Constants.notificationTypeNewClip;
import static com.subhamkumar.clipsy.models.Constants.notificationTypeNewComment;
import static com.subhamkumar.clipsy.utils.Message.fragmentSearchToProfileResult;

public class NotificationHelper {

    public static String channelId = "ChannelId", _description = "Description", _name = "ChannelName", title = "Clipsy";
    public static int relationShipNotificationId = 1;
    public static void createNotification(Context context, String contentText) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logotrans)
                .setContentTitle(title)
                .setContentText(contentText);

    }

    private static int getTimeSeconds() {
        // return new Random().nextInt();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM HH:mm:ss");
        Date date = new Date();
        String dateTimeString = formatter.format(date);
        String dateString = dateTimeString.split(" ")[0];
        dateString = TrimZeroInData(dateString);
        String timeString = dateTimeString.split(" ")[1];
        return new Random().nextInt()*Integer.parseInt(getIntegerString(dateString, "/").concat(getIntegerString(timeString, ":")));
    }

    @NonNull
    private static String TrimZeroInData(String dateString) {
        if (dateString.charAt(0) == '0') {
            dateString = dateString.substring(1);
        }
        return dateString;
    }

    private static String getIntegerString(String sequence, String delimiter)  {
        String[] intPieces = sequence.split(delimiter);
        StringBuilder intString = new StringBuilder();
        for (String intPiece :
                intPieces) {
            intString.append(intPiece);
        }
        return intString.toString();
    }


    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = _name;
            String description = _description;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public static void notificationClickAction(Context context, Notification notification) {
        // Create an explicit intent for an Activity in your app
        Notification notificationObj = new Notification();
        String notificationTitle = notificationObj.getNotificationTitle(notification.NotificationType);

        Intent intent = new Intent(); // = new Intent(context, panel.class);

        if (notification.NotificationType.equals(notificationTypeFollowing)) {
            // TODO open that person profile
            intent = new Intent(context, profile_result.class);
            Bundle toProfileResult = fragmentSearchToProfileResult(getToken(context), notification.UserId, notification.ActionId);
            intent.putExtras(toProfileResult);
        }
        else if (notification.NotificationType.equals(notificationTypeNewClip)) {
            // TODO open clip
            intent = new Intent(context, panel.class);
        }
        else if (notification.NotificationType.equals(notificationTypeNewComment)) {
            // TODO open clip later on open the clip and scroll to that comment
            intent = new Intent(context, panel.class);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(notificationTitle)
                .setContentText(notification.Message)
                .setPriority(NotificationCompat.FLAG_NO_CLEAR)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        createNotificationChannel(context);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getTimeSeconds(), builder.build());

    }


    private static String getToken(Context context) {
        return new LoginDb(context).getLoginDetails().TOKEN;
    }
}
