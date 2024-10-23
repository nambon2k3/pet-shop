package com.example.petshopapplication.API;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static Retrofit getRetrofitInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor()) // Thêm Interceptor tại đây
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://sandbox.goship.io/api/v2/")  // Thay bằng URL của bạn
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)  // Thêm client với Interceptor
                .build();
    }
}
