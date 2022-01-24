package com.sushant.anonymoushope.Model;

public class User {
    String uid;
    String FirstName;
    String LastName;
    String Email;
    String PhoneNumber;
    String Gender;
    String BIO;
    String bioMetrics;
    String bankName;
    String accountName;
    String accountNumber;
    String Password;
    String profileImage;
    String donated;
    String received;
    Boolean accepted;
    Boolean AdminAccepted;

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Boolean getAdminAccepted() {
        return AdminAccepted;
    }

    public void setAdminAccepted(Boolean adminAccepted) {
        AdminAccepted = adminAccepted;
    }

    public User(String uid, String firstName, String lastName, String email, String phoneNumber, String gender, String BIO, String bioMetrics, String bankName, String accountName, String accountNumber, String password, String profileImage, String donated, String received, Boolean accepted, Boolean adminAccepted) {
        this.uid = uid;
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        PhoneNumber = phoneNumber;
        Gender = gender;
        this.BIO = BIO;
        this.bioMetrics = bioMetrics;
        this.bankName = bankName;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        Password = password;
        this.profileImage = profileImage;
        this.donated = donated;
        this.received = received;
        this.accepted = accepted;
        AdminAccepted = adminAccepted;
    }

    public String getBioMetrics() {
        return bioMetrics;
    }

    public void setBioMetrics(String bioMetrics) {
        this.bioMetrics = bioMetrics;
    }

    public String getDonated() {
        return donated;
    }

    public void setDonated(String donated) {
        this.donated = donated;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getGender() {
        return Gender;
    }



    public void setGender(String gender) {
        Gender = gender;
    }

    public User() {
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getBIO() {
        return BIO;
    }

    public void setBIO(String BIO) {
        this.BIO = BIO;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
