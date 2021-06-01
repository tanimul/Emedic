package com.example.emadic.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//todo check5
public class ApiClient {
    private static Retrofit retrofit;

    private static final String baseURL="https://maps.googleapis.com/maps/api/";
    private static ApiInterface apiInterface;

    public static synchronized ApiInterface getApiInterface(){
        if(retrofit==null){
            retrofit=new retrofit2.Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            apiInterface=retrofit.create(ApiInterface.class);
        }
        return apiInterface;
    }
}
