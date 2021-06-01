package com.example.emadic;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.emadic.model_direction.DirectionResponse;
import com.example.emadic.model_distancematrix.DistanceResponse;
import com.example.emadic.repository.RepositoryDirectionApi;

import java.util.Map;

import retrofit2.http.QueryMap;

public class ViewmodelDirectionApi extends AndroidViewModel {
    private static RepositoryDirectionApi repositoryDirectionApi;
    public ViewmodelDirectionApi(@NonNull Application application) {
        super(application);
        if (repositoryDirectionApi == null) {
            repositoryDirectionApi = new RepositoryDirectionApi();
        }
    }

    public LiveData<DirectionResponse> getDirectionResponse(@QueryMap Map<String, String> parameters){
        return repositoryDirectionApi.getDirectionResponse(parameters);
    }

    //todo check2
    public LiveData<DistanceResponse> getDistanceResponse(@QueryMap Map<String, String> parameters ){
        return repositoryDirectionApi.getDistanceResponse(parameters);
    }
}
