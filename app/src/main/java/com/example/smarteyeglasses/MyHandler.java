package com.example.smarteyeglasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.example.smarteyeglasses.Activities.HomeScreenActivity;
import com.google.android.gms.games.internal.constants.NotificationChannel;
import com.microsoft.windowsazure.notifications.NotificationsHandler;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class MyHandler extends NotificationsHandler {
    public static final String NOTIFICATION_CHANNEL_ID = "nh-demo-channel-id";
    public static final String NOTIFICATION_CHANNEL_NAME = "Notification Hubs Demo Channel";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Notification Hubs Demo Channel";

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        sendNotification(nhMessage);
        if (HomeScreenActivity.isVisible) {
            HomeScreenActivity.homeScreenActivity.ToastNotify(nhMessage);
        }
    }

    private void sendNotification(String msg) {

        Intent intent = new Intent(ctx, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(
                ctx)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(android.R.drawable.ic_popup_reminder);

        notificationBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void createChannelAndHandleNotifications(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        NotificationsManager.handleNotifications(context, "", MyHandler.class);
    }
}