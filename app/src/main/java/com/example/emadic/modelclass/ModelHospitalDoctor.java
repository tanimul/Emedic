package com.example.emadic.modelclass;

public class ModelHospitalDoctor {

    int Image;
    String name, contact_number;

    public ModelHospitalDoctor(int image, String name, String contact_number) {
        Image = image;
        this.name = name;
        this.contact_number = contact_number;
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

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }
}
