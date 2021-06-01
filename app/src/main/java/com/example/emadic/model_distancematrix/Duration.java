package com.example.emadic.model_distancematrix;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Duration {
    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("value")
    @Expose
    private Integer value;


    public String getText() {
        return text;
    }


    public Integer getValue() {
        return value;
    }
}
