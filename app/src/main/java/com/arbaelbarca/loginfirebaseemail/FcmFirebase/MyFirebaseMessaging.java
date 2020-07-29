package com.arbaelbarca.loginfirebaseemail.FcmFirebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    NotificationManager mNotificationManager;
    String NOTIFICATION_CHANNEL_ID, getUUID, getUserid, action;
    String title, body;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            Log.d("respon", "From: " + remoteMessage.getFrom());
            Log.d("respon", "Body: " + body);
            Log.d("respon", "Title " + title);
            Log.d("respon", "data " + remoteMessage.getData());

        }

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        action = params.get("action");

        Log.e("JSON_OBJECT", object.toString());
        Log.d("respon", "Action " + params.get("uuid"));

        getTopics();
        getUUID = params.get("uuid");
        NOTIFICATION_CHANNEL_ID = "channel_notif";


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(instanceIdResult -> Log.d("responToken ", " instance " + instanceIdResult.getToken()));

        intentNotif(remoteMessage);


    }

    private void intentNotif(RemoteMessage remoteMessage) {
        long[] pattern = {0, 1000, 500, 1000};
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Intent qIntent = new Intent(this, MainActivity.class);
        qIntent.putExtra("uuidUser", getUUID);
        Log.d("responActionQuestions", "diterima");

        PendingIntent contentIntentQuest = PendingIntent.getActivity(this, 0,
                qIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTitle(remoteMessage.getNotification().getBody())
                .setContentText(remoteMessage.getNotification().getTitle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_new)
                .setAutoCancel(true);

        notificationBuilder.setContentIntent(contentIntentQuest);
        mNotificationManager.notify(1000, notificationBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Your Notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }
    }

    void getTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("event")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "succces", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> {

        });
    }


}
