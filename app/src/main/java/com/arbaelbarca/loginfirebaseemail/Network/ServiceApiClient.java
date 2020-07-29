package com.arbaelbarca.loginfirebaseemail.Network;

import android.content.Context;

import com.arbaelbarca.loginfirebaseemail.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceApiClient {

    Retrofit retrofit;
    private ApiServices apiServices;
    private static ServiceApiClient serviceApiClient;

    private ServiceApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_MAPS)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiServices = retrofit.create(ApiServices.class);
    }


    public static ApiServices getApiNotif() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_NOTIF)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiServices.class);


    }

    public static Retrofit getApiCuacaHome(Context context) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_CUACA_HOME)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    private static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(getLoggingInterceptor())
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    private static HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }


    public ApiServices getApiServices() {
        return apiServices;
    }

    public static ServiceApiClient getInstance() {
        if (serviceApiClient == null)
            serviceApiClient = new ServiceApiClient();
        return serviceApiClient;
    }
}
