package com.example.leonp.contentstreamer.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "contentstreamer-mobilehub-1377632671-posts2")

public class Posts2DO {
    private String _userId;
    private String _postId;
    private String _author;
    private String _createdAt;
    private String _imagePath;
    private String _streamType;
    private String _title;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "postId")
    @DynamoDBAttribute(attributeName = "postId")
    public String getPostId() {
        return _postId;
    }

    public void setPostId(final String _postId) {
        this._postId = _postId;
    }
    @DynamoDBAttribute(attributeName = "author")
    public String getAuthor() {
        return _author;
    }

    public void setAuthor(final String _author) {
        this._author = _author;
    }
    @DynamoDBAttribute(attributeName = "createdAt")
    public String getCreatedAt() {
        return _createdAt;
    }

    public void setCreatedAt(final String _createdAt) {
        this._createdAt = _createdAt;
    }
    @DynamoDBAttribute(attributeName = "imagePath")
    public String getImagePath() {
        return _imagePath;
    }

    public void setImagePath(final String _imagePath) {
        this._imagePath = _imagePath;
    }
    @DynamoDBAttribute(attributeName = "streamType")
    public String getStreamType() {
        return _streamType;
    }

    public void setStreamType(final String _streamType) {
        this._streamType = _streamType;
    }
    @DynamoDBAttribute(attributeName = "title")
    public String getTitle() {
        return _title;
    }

    public void setTitle(final String _title) {
        this._title = _title;
    }

}
