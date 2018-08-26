package com.example.leonp.contentstreamer.models;

public class ContentPost {

    String title;
    String author;
    String createdAt;
    String fileSize;
    String streamType;

    public ContentPost(String title, String author, String createdAt, String fileSize, String streamType) {
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
        this.fileSize = fileSize;
        this.streamType = streamType;
    }

    public ContentPost() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    @Override
    public String toString() {
        return "ContentPost{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", streamType='" + streamType + '\'' +
                '}';
    }
}
