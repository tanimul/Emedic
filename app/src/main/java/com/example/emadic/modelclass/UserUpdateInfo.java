package com.example.emadic.modelclass;

public class UserUpdateInfo {
    String name, blood_grp, medical_condition;

    public UserUpdateInfo() {
    }

    public UserUpdateInfo(String name, String blood_grp, String medical_condition) {

        this.name = name;
        this.blood_grp = blood_grp;
        this.medical_condition = medical_condition;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlood_grp() {
        return blood_grp;
    }

    public void setBlood_grp(String blood_grp) {
        this.blood_grp = blood_grp;
    }

    public String getMedical_condition() {
        return medical_condition;
    }

    public void setMedical_condition(String medical_condition) {
        this.medical_condition = medical_condition;
    }
}
