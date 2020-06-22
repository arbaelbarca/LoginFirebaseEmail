package com.arbaelbarca.loginfirebaseemail.FcmFirebase;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import android.util.Log;

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
    String NOTIFICATION_CHANNEL_ID, getData, getUserid, action;
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
        Log.d("respon", "Action notif " + remoteMessage.getData().get("action"));
        Log.d("respon", "Action " + params.get("name"));
        Log.d("respon", "uuid list" + params.get("product_id"));

        getTopics();
        getData = params.get("name");
        getUserid = params.get("product_id");
        NOTIFICATION_CHANNEL_ID = "channel_notif";


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d("responToken ", " instance " + instanceIdResult.getToken());
                    }
                });

    }

    void getTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "succces", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}
