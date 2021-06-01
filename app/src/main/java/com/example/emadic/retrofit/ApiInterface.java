package com.example.emadic.retrofit;

import com.example.emadic.model_direction.DirectionResponse;
import com.example.emadic.model_distancematrix.DistanceResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    //todo check4
    @POST("distancematrix/json")
    Observable<DistanceResponse> getDistanceInfo(@QueryMap Map<String, String> parameters);

    @GET("distancematrix/json")
    Call<DistanceResponse> getDistanceInfos(
            @QueryMap Map<String, String> parameters
    );

    @POST("directions/json")
    Observable<DirectionResponse> getDirectionResponse(@QueryMap Map<String, String> parameters);


    @GET("directions/json")
    Call<DirectionResponse> getDirectionResponses(
            @QueryMap Map<String, String> parameters
    );
}
