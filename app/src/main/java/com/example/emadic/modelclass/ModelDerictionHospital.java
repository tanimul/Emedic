package com.example.emadic.modelclass;

public class ModelDerictionHospital {

    int Image;
    String name, distance, timeDistance, openingHour;

    public ModelDerictionHospital(int image, String name, String distance, String timeDistance, String openingHour) {
        Image = image;
        this.name = name;
        this.distance = distance;
        this.timeDistance = timeDistance;
        this.openingHour = openingHour;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTimeDistance() {
        return timeDistance;
    }

    public void setTimeDistance(String timeDistance) {
        this.timeDistance = timeDistance;
    }

    public String getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }
}
