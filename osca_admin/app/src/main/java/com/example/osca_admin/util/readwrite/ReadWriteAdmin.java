package com.example.osca_admin.util.readwrite;

public class ReadWriteAdmin {

    public String email;
    public String userName;
    public String imageURL;
    public String firstName;
    public String lastName;
    public String password;
    public String dateCreated;

    public ReadWriteAdmin(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    public ReadWriteAdmin(String textEmail, String textUsername, String textImageURL,
                          String textFirstName, String textLastName, String textPassword,
                          String textDateCreated){

       this.email = textEmail;
       this.userName = textUsername;
       this.imageURL = textImageURL;
       this.firstName = textFirstName;
       this.lastName = textLastName;
       this.password = textPassword;
       this.dateCreated = textDateCreated;
    }
}
