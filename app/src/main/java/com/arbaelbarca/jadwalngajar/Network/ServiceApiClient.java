package com.arbaelbarca.jadwalngajar.Network;

import android.content.Context;

import com.arbaelbarca.jadwalngajar.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceApiClient {

    Retrofit retrofit;
    ApiServices apiServices;

    public static Retrofit getApiMaps(Context context) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_MAPS)
                .client(provideOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    public static Retrofit getApi(Context context) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_CUACA)
                .client(provideOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    public static Retrofit getApiCuacaHome(Context context) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_CUACA_HOME)
                .client(provideOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }

    private static OkHttpClient provideOkHttpClient(Context context) {
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
}
