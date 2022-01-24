package com.sushant.anonymoushope.Model;

public class Notification {

    private Boolean isAdmin = false;
    private String postId;
    private String text;
    private String userId;
    private Boolean isDonation = false;
    private String amount;


    public Notification() {
    }

    public Notification(Boolean isAdmin, String postId, String text, String userId, Boolean isDonation, String amount) {
        this.isAdmin = isAdmin;
        this.postId = postId;
        this.text = text;
        this.userId = userId;
        this.isDonation = isDonation;
        this.amount = amount;
    }

    public Notification(Boolean isAdmin, String postId, String text, String userId, Boolean isDonation) {
        this.isAdmin = isAdmin;
        this.postId = postId;
        this.text = text;
        this.userId = userId;
        this.isDonation = isDonation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getDonation() {
        return isDonation;
    }

    public void setDonation(Boolean donation) {
        isDonation = donation;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
