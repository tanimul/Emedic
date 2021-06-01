package com.example.emadic.model_direction;

public class UserModel {

    private String imageURL,name,phnNumber,email;

    public UserModel() {
    }

    public UserModel(String imageURL, String name, String phnNumber, String email) {
        this.imageURL = imageURL;
        this.name = name;
        this.phnNumber = phnNumber;
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhnNumber() {
        return phnNumber;
    }

    public void setPhnNumber(String phnNumber) {
        this.phnNumber = phnNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
