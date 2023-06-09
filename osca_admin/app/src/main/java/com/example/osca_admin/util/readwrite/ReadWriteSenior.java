package com.example.osca_admin.util.readwrite;

public class ReadWriteSenior {

   public String imageURL;
   public String firstName;
   public String lastName;
   public String email;
   public String address;

   public ReadWriteSenior(){}

   public ReadWriteSenior(String textImageURL, String textFirstName, String textLastName, String textEmail, String textAddress){
      this.imageURL = textImageURL;
      this.firstName = textFirstName;
      this.lastName = textLastName;
      this.email = textEmail;
      this.address = textAddress;
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

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }
}
