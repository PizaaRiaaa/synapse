package com.example.osca_admin.util.readwrite;

public class ReadWriteCarer {

   public String firstName;
   public String lastName;
   public String middle;
   public String email;
   public String address;
   public String imageURL;
   public String dob;
   public String gender;
   public String mobileNumber;
   public String date_created;

   public ReadWriteCarer(){}

   public ReadWriteCarer(String textImageURL, String textFirstName, String textLastName,
                         String textAddress, String textEmail, String textDOB,
                         String textMiddle, String textGender, String textMobileNumber,
                         String textDateCreated){

      this.imageURL = textImageURL;
      this.firstName = textFirstName;
      this.lastName = textLastName;
      this.address = textAddress;
      this.email = textEmail;
      this.dob = textDOB;
      this.middle = textMiddle;
      this.gender = textGender;
      this.mobileNumber = textMobileNumber;
      this.date_created = textDateCreated;
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

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getDob() {
      return dob;
   }

   public void setDob(String dob) {
      this.dob = dob;
   }

   public String getMiddle() {
      return middle;
   }

   public void setMiddle(String middle) {
      this.middle = middle;
   }

   public String getGender() {
      return gender;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

   public String getMobileNumber() {
      return mobileNumber;
   }

   public void setMobileNumber(String mobileNumber) {
      this.mobileNumber = mobileNumber;
   }

   public String getDate_created() {
      return date_created;
   }

   public void setDate_created(String date_created) {
      this.date_created = date_created;
   }
}
