package com.example.emadic;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.emadic.Maps.DataParser;
import com.example.emadic.Maps.DownloadUrl;
import com.example.emadic.repository.RepositoryDirectionApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class ViewModelEmedic extends AndroidViewModel {
    private static final String TAG = "ViewModelEmedic";
    int PROXIMITY_RADIUS = 10000;
    private static RepositoryDirectionApi repositoryDistanceApi;

    public ViewModelEmedic(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<HashMap<String, String>>> getNearbyPlaces(double lat, double lang, String tag) {
        MutableLiveData<List<HashMap<String, String>>> result = new MutableLiveData<>();

        Executors.newSingleThreadExecutor().execute(() -> {
            String googlePlacesData;
            String url = getUrl(lat, lang, tag);
            DownloadUrl downloadURL = new DownloadUrl();
            DataParser parser = new DataParser();

            List<HashMap<String, String>> nearbyPlaceLis = new ArrayList<>();

            try {
                googlePlacesData = downloadURL.readUrl(url);
                nearbyPlaceLis = parser.parse(googlePlacesData);
                result.postValue(nearbyPlaceLis);
            } catch (Exception e) {
                Log.d(TAG, "getNearbyPlaces: error" + e.getMessage());
            }
        });

        return result;
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyCWzXk-SGxgedHpO8pnTA0h6aYzEiJL_ss");
        Log.d(TAG, "url = " + googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }

//    public LiveData<DistanceResponse> getDistanceResponse(@QueryMap Map<String, String> parameters) {
//        return repositoryDirectionApi.getDistanceResponse(parameters);
//    }
}
