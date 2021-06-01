package com.example.emadic.modelclass;

public class ModelAppointment {

    int Image;
    String doctorname,appointment_date;

    public ModelAppointment(int image, String doctorname, String appointment_date) {
        Image = image;
        this.doctorname = doctorname;
        this.appointment_date = appointment_date;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getName() {
        return doctorname;
    }

    public void setName(String name) {
        this.doctorname = name;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }
}
