
package com.example.emadic.modelclass;

public class ModelDoctorAppoinment {

        /*int Image;*/
    String name, problemText;

    public ModelDoctorAppoinment(int image, String name, String problemText) {
       /* Image = image;*/
        this.name = name;
        this.problemText = problemText;
    }

   /* public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProblemText() {
        return problemText;
    }

    public void setProblemText(String problemText) {
        this.problemText = problemText;
    }
}
