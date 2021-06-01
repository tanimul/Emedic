package com.example.emadic.modelclass;

public class ModelHospHospital {

    int Image;
    String hospital_name, contactNumber, openStatus;

    public ModelHospHospital(int image, String hospital_name, String contactNumber, String openStatus) {
        Image = image;
        this.hospital_name = hospital_name;
        this.contactNumber = contactNumber;
        this.openStatus = openStatus;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }
}
