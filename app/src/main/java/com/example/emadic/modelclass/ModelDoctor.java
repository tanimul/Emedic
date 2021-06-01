package com.example.emadic.modelclass;

public class ModelDoctor {

    int image;
    String doctorname, doctorqualification,doctorvisitingcharge,doctorvisitingschedule;

    public ModelDoctor(int image, String doctorname, String doctorqualification, String doctorvisitingcharge, String doctorvisitingschedule) {
        this.image = image;
        this.doctorname = doctorname;
        this.doctorqualification = doctorqualification;
        this.doctorvisitingcharge = doctorvisitingcharge;
        this.doctorvisitingschedule = doctorvisitingschedule;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDoctorname() {
        return doctorname;
    }

    public void setDoctorname(String doctorname) {
        this.doctorname = doctorname;
    }

    public String getDoctorqualification() {
        return doctorqualification;
    }

    public void setDoctorqualification(String doctorqualification) {
        this.doctorqualification = doctorqualification;
    }

    public String getDoctorvisitingcharge() {
        return doctorvisitingcharge;
    }

    public void setDoctorvisitingcharge(String doctorvisitingcharge) {
        this.doctorvisitingcharge = doctorvisitingcharge;
    }

    public String getDoctorvisitingschedule() {
        return doctorvisitingschedule;
    }

    public void setDoctorvisitingschedule(String doctorvisitingschedule) {
        this.doctorvisitingschedule = doctorvisitingschedule;
    }
}
