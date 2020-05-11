package com.example.hscheduler;

public class Doctor {
    String Nume,Specializare,image,Prenume,userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNume() {
        return Nume;
    }

    public void setNume(String Nume) {
        this.Nume = Nume;
    }

    public String getPrenume() {
        return Prenume;
    }

    public void setPrenume(String Prenume) {
        this.Prenume = Prenume;
    }

    public String getSpecializare() {
        return Specializare;
    }

    public void setSpecializare(String Specializare) {
        this.Specializare = Specializare;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername(){

        return getNume() + " " + getPrenume();
    }

    public Doctor(String Nume, String Specializare, String image, String Prenume,String userID) {
        this.Nume = Nume;
        this.Specializare = Specializare;
        this.image = image;
        this.Prenume = Prenume;
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "Nume='" + Nume + '\'' +
                ", Specializare='" + Specializare + '\'' +
                ", image='" + image + '\'' +
                ", Prenume='" + Prenume + '\'' +
                '}';
    }

    public Doctor(){

    }
}
