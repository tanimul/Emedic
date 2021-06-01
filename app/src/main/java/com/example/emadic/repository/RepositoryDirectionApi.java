package com.example.emadic.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.emadic.model_direction.DirectionResponse;
import com.example.emadic.model_distancematrix.DistanceResponse;
import com.example.emadic.retrofit.ApiClient;
import com.example.emadic.retrofit.ApiInterface;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.QueryMap;

public class RepositoryDirectionApi {
    private ApiInterface apiRequest;

    public RepositoryDirectionApi()
    {
        apiRequest= ApiClient.getApiInterface();
    }

    public LiveData<DistanceResponse> getDistanceResponse(@QueryMap Map<String, String> parameters){
        final MutableLiveData<DistanceResponse> response=new MutableLiveData<>();


        apiRequest.getDistanceInfo(parameters).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DistanceResponse>() {
                    @Override
                    public void accept(DistanceResponse distanceResponse) throws Exception {
                        response.postValue(distanceResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ddddddd", "getDistanceResponse: error:"+throwable.getMessage());
                    }
                });

        return response;
    }


    public LiveData<DirectionResponse> getDirectionResponse(@QueryMap Map<String, String> parameters){
        final MutableLiveData<DirectionResponse> response=new MutableLiveData<>();
        apiRequest.getDirectionResponse(parameters).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DirectionResponse>() {
                    @Override
                    public void accept(DirectionResponse directionResponse) throws Exception {
                        response.postValue(directionResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("dddd", "accept: error"+throwable.getMessage());
                    }
                });
        return  response;
    }



}
