package com.arbaelbarca.loginfirebaseemail.Network;

import com.arbaelbarca.loginfirebaseemail.Notification.RequestNotificaton;
import com.arbaelbarca.loginfirebaseemail.model.modeldirectionmaps.ResponseDirectionMaps;
import com.arbaelbarca.loginfirebaseemail.model.modelnotif.ResponsePushNotif;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServices {

    @Headers({"Authorization: key=AAAAXi9Ojn4:APA91bHIwfrBHDGs2xQ2oNvMlMOThJrJ_ALUdxzRrIeHGnpxJK1mSHnAWmgZLdlmXexUipxe7RMaAq4X16KxjupXmmplj1MXl1dVxUL9jgvYdL3KE-UXb8gonImqwy5R6S_-sLfi_QoE",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponsePushNotif> sendChatNotification(@Body RequestNotificaton requestNotificaton);


    @GET("bmkg/")
    Call<okhttp3.ResponseBody> getListLokasiCuaca(
            @Query("menu") String menu,
            @Query("wilayah") String wilayah
    );


    @POST("directions/json")
    Call<ResponseDirectionMaps> requestDirectionMaps(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String apiKey

    );
}
