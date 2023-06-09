package com.example.synapse.screen.util.readwrite;

public class ReadWriteUserSenior {
    public String barangay;
    public String address;
    public String city;
    public String dob;
    public String firstName;
    public String image;
    public String lastName;
    public String middle;
    public String heartrate;
    public String status;
    public String stepcounts;
    public String seniorID;
    public String email;
    public String mobileNumber;
    public String gender;

    public ReadWriteUserSenior(){}

    public ReadWriteUserSenior(String textBarangay, String textCity, String textDOB, String textFirstName, String textImage,
                               String textLastName, String textMiddle, String textSeniorID, String textHeartRate, String textStatus,
                               String textStepCounts, String textEmail, String textMobileNumber, String textGender, String textAddress){

        this.barangay = textBarangay;
        this.city = textCity;
        this.dob = textDOB;
        this.firstName = textFirstName;
        this.image = textImage;
        this.lastName = textLastName;
        this.middle = textMiddle;
        this.heartrate = textHeartRate;
        this.status = textStatus;
        this.stepcounts = textStepCounts;
        this.seniorID = textSeniorID;
        this.email = textEmail;
        this.mobileNumber = textMobileNumber;
        this.gender = textGender;
        this.address = textAddress;

    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public void setHeartrate(String heartrate) { this.heartrate = heartrate;}

    public String getHeartrate() { return heartrate;}

    public void setStatus(String status) { this.status = status;}

    public String getStatus() { return status;}

    public void setStepcounts(String stepcounts) { this.stepcounts = stepcounts;}

    public String getStepcounts() { return stepcounts;}

    public String getSeniorID() {
        return seniorID;
    }

    public void setSeniorID(String seniorID) {
        this.seniorID = seniorID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
