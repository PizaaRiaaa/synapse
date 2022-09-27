package com.example.synapse.screen.util.readwrite;

public class ReadWriteUserDetails {
    public String firstName;
    public String middle;
    public String lastName;
    public String email;
    public String mobileNumber;
    public String password;
    public String dob;
    public String address;
    public String city;
    public String gender;
    public String userType;
    public String imageURL;
    public String token;
    public String date_created;

    public ReadWriteUserDetails() { }

    public ReadWriteUserDetails(String textFirstName, String textMiddle, String textLastName, String textEmail, String textMobileNumber, String textPassword,
                                String textDOB, String textAddress, String textCity, String textGender,
                                String userType, String imageURL, String token, String date_created){

        this.firstName = textFirstName;
        this.middle = textMiddle;
        this.lastName = textLastName;
        this.email = textEmail;
        this.mobileNumber = textMobileNumber;
        this.password = textPassword;
        this.dob = textDOB;
        this.address = textAddress;
        this.city = textCity;
        this.gender = textGender;
        this.userType = userType;
        this.imageURL = imageURL;
        this.token = token;
        this.date_created = date_created;
    }

    public String getFirstName(){ return firstName; }
    public void setFirstName(String firstName){ this.firstName = firstName; }

    public String getMiddle(){ return middle; }
    public void setMiddle(String middle){ this.middle = middle; }

    public String getLastName(){ return lastName; }
    public void setLastName(String lastName){ this.lastName = lastName; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getMobileNumber(){ return mobileNumber; }
    public void setMobileNumber(String mobileNumber){ this.mobileNumber = mobileNumber; }

    public String getDOB(){ return dob; }
    public void setDob(String dob){ this.dob = dob; }

    public String getAddress(){ return address; }
    public void setAddress(String address){ this.address = address; }

    public String getCity(){ return city; }
    public void setCity(String city){ this.city = city; }

    public String getGender(){ return gender; }
    public void setGender(String gender){ this.gender = gender; }

    public String getUserType(){ return userType; }
    public void setUserType(String userType){ this.userType = userType; }

    public String getImageURL(){ return imageURL; }
    public void setImageURL(String imageURL){ this.imageURL = imageURL; }

    public String getDate_created(){ return date_created; }
    public void setDate_created(String date_created){ this.date_created = date_created; }

    public String getToken(){ return token; }
    public void setToken(String token){ this.token = token; }
}
