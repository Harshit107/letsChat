package com.harshit.letschat.model;

public class UniversalMessageList {

    String message;
    String key;
    String time;
    String senderName;
    String senderImage;
    String senderId;

    public UniversalMessageList(String message) {
        this.message = message;
    }

    public UniversalMessageList(String message, String key, String time, String senderName, String senderImage, String senderId) {
        this.message = message;
        this.key = key;
        this.time = time;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
