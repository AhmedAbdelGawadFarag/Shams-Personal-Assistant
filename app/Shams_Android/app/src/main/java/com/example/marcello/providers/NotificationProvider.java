package com.example.marcello.providers;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class NotificationProvider {
    private static final String TAG = "NotificationProvider";

    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS =
            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String WA_PACKAGE = "com.whatsapp";

    private static NotificationProvider instance = new NotificationProvider();
    public static synchronized NotificationProvider getInstance(){
        return instance;
    }
    private NotificationProvider(){}
    public void showNotifications(Context context) {

        if (isNotificationServiceEnabled(context)) {
            Log.i(TAG, "Notification enabled -- trying to fetch it");
            getNotifications();
        } else {
            Log.i(TAG, "Notification disabled -- Opening settings");
            context.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }

    public void getNotifications() {
        StringBuilder st = new StringBuilder();
        Log.i(TAG, "Waiting for MyNotificationService");
        NotificationService myNotificationService = NotificationService.get();
        if(myNotificationService == null){
            Log.d(TAG, "getNotifications: This is a null");
            return ;
        }
        Log.i(TAG, "Active Notifications: [");
        st.append("Active Notifications: ").append("\r\n");
        for (StatusBarNotification notification :
                myNotificationService.getActiveNotifications()) {
            if (notification.getPackageName().startsWith("com.android")
                    || notification.getPackageName().startsWith("com.samsung.android")) continue;
            if (notification.getPackageName().startsWith("com.estrongs")) continue;
            if (notification.getPackageName().startsWith("com.motorola")) continue;
            if (notification.getPackageName().startsWith("Screenshot")) continue;
            Notification notification1 = notification.getNotification();
            Bundle bun = notification1.extras;


            if (bun.getString(Notification.EXTRA_BIG_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_BIG_TEXT)
                        .toString()).append("\n");
            if (bun.getString(Notification.EXTRA_SUMMARY_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_SUMMARY_TEXT)
                        .toString()).append("\n");
            if (bun.getString(Notification.EXTRA_INFO_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_INFO_TEXT)
                        .toString()).append("\n");

            if (bun.getString(Notification.EXTRA_SUB_TEXT) != null)
                st.append(bun.getString(Notification.EXTRA_SUB_TEXT)
                        .toString()).append("\n");
            if (bun.getString(Notification.EXTRA_TITLE_BIG) != null)
                st.append(bun.getString(Notification.EXTRA_TITLE_BIG)
                        .toString()).append("\n");
            CharSequence[] lines = bun.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            if (lines != null) {
                for (CharSequence line : lines) {
                    st.append(line.toString()).append("  \n");
                }
            }

            //String from = bundle.getString(NotificationCompat.EXTRA_TITLE);
            //String message = bundle.getString(NotificationCompat.EXTRA_TEXT);

            //st.append("From: ").append(from);
            //st.append(message).append("\r\n");

            //SimpleDateFormat format = new SimpleDateFormat("DD-kk:mm:ss:SSS");
            //Long ptime = notification.getPostTime();
            //st.append("Post time: ").append(format.format(ptime)).append("\n");
            //Long nottime = notification.when;
            //st.append("When: ").append(format.format(nottime)).append("\n");
            //Log.i(TAG, "From: " + from);
            //Log.i(TAG, "Message: " + message);

        }
        st.append("]");
        Log.d(TAG, "getNotifications: " + st.toString());
    }
    private boolean isNotificationServiceEnabled(Context context){
        Log.d(TAG, "isNotificationServiceEnabled: checking service enabled");
        String pkgName = context.getPackageName();
        final String allNames = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (allNames != null && !allNames.isEmpty()) {
            for (String name : allNames.split(":")) {
                if (context.getPackageName().equals(
                        ComponentName.unflattenFromString(name).getPackageName())) {
                    return true;
                }
            }
        }
        Log.d(TAG, "isNotificationServiceEnabled: it's enabled service enabled");

        return false;
    }
}
