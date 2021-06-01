package com.example.emadic.modelclass;

public class ArroundHosAddress_info {
    private String address,distance,time;
    private Double latitude, longitude;

    public ArroundHosAddress_info(String address, Double latitude, Double longitude,String distance,String time) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance=distance;
        this.time=time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
