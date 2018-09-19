package com.example.leonp.contentstreamer.models;

import android.graphics.Bitmap;

public class PostPicture {

    private Bitmap bitmap;
    private String fileSize;

    public PostPicture(Bitmap bitmap, String fileSize) {
        this.bitmap = bitmap;
        this.fileSize = fileSize;
    }

    public PostPicture() {

    };

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "PostPicture{" +
                "bitmap=" + bitmap +
                ", fileSize='" + fileSize + '\'' +
                '}';
    }
}
