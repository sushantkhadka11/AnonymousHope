package com.sushant.anonymoushope.Model;

public class Posts {
    String postId;
    String content;
    String media;
    String userId;
    String type;
    Boolean accepted;

    public Posts(String postId, String content, String media, String userId, String type, Boolean accepted) {
        this.postId = postId;
        this.content = content;
        this.media = media;
        this.userId = userId;
        this.type = type;
        this.accepted = accepted;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Posts() {
    }

    public String getPostId() {
        return postId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
