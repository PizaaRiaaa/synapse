package com.example.synapse.screen.util;

public class ReadWriteUserDetails {
    public String fullName;
    public String email;
    public String mobileNumber;
    public String password;
    public String dob;
    public String address;
    public String gender;
    public String userType;
    public String imageURL;
    public String token;

    public ReadWriteUserDetails() { }

    public ReadWriteUserDetails(String textFullName, String textEmail, String textMobileNumber, String textPassword,
                                String textDOB, String textAddress, String textGender, String userType, String imageURL, String token){

        this.fullName = textFullName;
        this.email = textEmail;
        this.mobileNumber = textMobileNumber;
        this.password = textPassword;
        this.dob = textDOB;
        this.address = textAddress;
        this.gender = textGender;
        this.userType = userType;
        this.imageURL = imageURL;
        this.token = token;
    }

    public String getFullName(){ return fullName; }
    public void setFullName(String fullName){ this.fullName = fullName; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getMobileNumber(){ return mobileNumber; }
    public void setMobileNumber(String mobileNumber){ this.mobileNumber = mobileNumber; }

    public String getDOB(){ return dob; }
    public void setDob(String dob){ this.dob = dob; }

    public String getAddress(){ return address; }
    public void setAddress(String address){ this.address = address; }

    public String getGender(){ return gender; }
    public void setGender(String gender){ this.gender = gender; }

    public String getUserType(){ return userType; }
    public void setUserType(String userType){ this.userType = userType; }

    public String getImageURL(){ return imageURL; }
    public void setImageURL(String imageURL){ this.imageURL = imageURL; }

    public String getToken(){ return token; }
    public void setToken(String token){ this.token = token; }
}
