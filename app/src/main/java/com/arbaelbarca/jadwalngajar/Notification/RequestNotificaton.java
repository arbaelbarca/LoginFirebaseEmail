package com.arbaelbarca.jadwalngajar.Notification;

import com.google.gson.annotations.SerializedName;

public class RequestNotificaton {
    @SerializedName("to") //  "to" changed to token
    private String token;

    @SerializedName("content_available") //  "to" changed to token
    private boolean content_available;

    @SerializedName("notification")
    private SendNotificationModel sendNotificationModel;

    @SerializedName("data")
    private SendNotificationModel data;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isContent_available() {
        return content_available;
    }

    public void setContent_available(boolean content_available) {
        this.content_available = content_available;
    }

    public SendNotificationModel getSendNotificationModel() {
        return sendNotificationModel;
    }

    public void setSendNotificationModel(SendNotificationModel sendNotificationModel) {
        this.sendNotificationModel = sendNotificationModel;
    }

    public SendNotificationModel getData() {
        return data;
    }

    public void setData(SendNotificationModel data) {
        this.data = data;
    }
}
