package com.example.emadic.model_distancematrix;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Element {
    @SerializedName("distance")
    @Expose
    private Distance distance;

    @SerializedName("duration")
    @Expose
    private Duration duration;

    @SerializedName("status")
    @Expose
    private String status;


    public Distance getDistance() {
        return distance;
    }


    public Duration getDuration() {
        return duration;
    }


    public String getStatus() {
        return status;
    }
}
