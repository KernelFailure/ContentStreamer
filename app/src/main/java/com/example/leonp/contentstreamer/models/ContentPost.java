package com.example.leonp.contentstreamer.models;

public class ContentPost {

    private String postId;
    private String postTitle;
    private String createdAt;
    private String postSize;

    public ContentPost(String postId, String postTitle, String createdAt, String postSize) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.createdAt = createdAt;
        this.postSize = postSize;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPostSize() {
        return postSize;
    }

    public void setPostSize(String postSize) {
        this.postSize = postSize;
    }

    @Override
    public String toString() {
        return "ContentPost{" +
                "postId='" + postId + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", postSize='" + postSize + '\'' +
                '}';
    }
}
