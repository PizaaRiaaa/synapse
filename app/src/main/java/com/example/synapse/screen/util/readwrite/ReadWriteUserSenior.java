package com.example.synapse.screen.util.readwrite;

public class ReadWriteUserSenior {
    public String barangay;
    public String city;
    public String dob;
    public String firstName;
    public String imageURL;
    public String lastName;
    public String middle;
    public String seniorID;

    public ReadWriteUserSenior(){}

    public ReadWriteUserSenior(String textBarangay, String textCity, String textDOB, String textFirstName, String textImage,
                               String textLastName, String textMiddle, String textSeniorID){

        this.barangay = textBarangay;
        this.city = textCity;
        this.dob = textDOB;
        this.firstName = textFirstName;
        this.imageURL = textImage;
        this.lastName = textLastName;
        this.middle = textMiddle;
        this.seniorID = textSeniorID;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getSeniorID() {
        return seniorID;
    }

    public void setSeniorID(String seniorID) {
        this.seniorID = seniorID;
    }
}
