package com.example.emadic.modelclass;

public class ModelAmbulance {

    int Image;
    String name, ambulance_contact_number;

    public ModelAmbulance(int image, String name, String ambulance_contact_number) {
        Image = image;
        this.name = name;
        this.ambulance_contact_number = ambulance_contact_number;
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

    public String getAmbulance_contact_number() {
        return ambulance_contact_number;
    }

    public void setAmbulance_contact_number(String ambulance_contact_number) {
        this.ambulance_contact_number = ambulance_contact_number;
    }
}
