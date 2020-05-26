package com.singularitycoder.mutliviewlist;

public class ChatItem {

    private int intProfileImage;
    private int intImageMessage;
    private String strTextMessage;
    private String strName;
    private String strDate;
    private String strMessageType;
    private String strUserType;

    // Sender Text Message
    public ChatItem(int intProfileImage, String strTextMessage, String strName, String strDate, String strMessageType, String strUserType) {
        this.intProfileImage = intProfileImage;
        this.strTextMessage = strTextMessage;
        this.strName = strName;
        this.strDate = strDate;
        this.strMessageType = strMessageType;
        this.strUserType = strUserType;
    }

    // Sender Image Message
    public ChatItem(int intProfileImage, int intImageMessage, String strName, String strDate, String strMessageType, String strUserType) {
        this.intProfileImage = intProfileImage;
        this.intImageMessage = intImageMessage;
        this.strName = strName;
        this.strDate = strDate;
        this.strMessageType = strMessageType;
        this.strUserType = strUserType;
    }

    // Receiver Text Message
    public ChatItem(int intProfileImage, String strTextMessage, String strName, String strDate, String strMessageType, String strUserType, String empty) {
        this.intProfileImage = intProfileImage;
        this.strTextMessage = strTextMessage;
        this.strName = strName;
        this.strDate = strDate;
        this.strMessageType = strMessageType;
        this.strUserType = strUserType;
    }

    // Receiver Broken Text Message
    public ChatItem(int intProfileImage, String strTextMessage, String strName, String strDate, String strMessageType, String strUserType, String empty1, String empty2) {
        this.intProfileImage = intProfileImage;
        this.strTextMessage = strTextMessage;
        this.strName = strName;
        this.strDate = strDate;
        this.strMessageType = strMessageType;
        this.strUserType = strUserType;
    }

    public int getIntProfileImage() {
        return intProfileImage;
    }

    public void setIntProfileImage(int intProfileImage) {
        this.intProfileImage = intProfileImage;
    }

    public int getIntImageMessage() {
        return intImageMessage;
    }

    public void setIntImageMessage(int intImageMessage) {
        this.intImageMessage = intImageMessage;
    }

    public String getStrTextMessage() {
        return strTextMessage;
    }

    public void setStrTextMessage(String strTextMessage) {
        this.strTextMessage = strTextMessage;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrMessageType() {
        return strMessageType;
    }

    public void setStrMessageType(String strMessageType) {
        this.strMessageType = strMessageType;
    }

    public String getStrUserType() {
        return strUserType;
    }

    public void setStrUserType(String strUserType) {
        this.strUserType = strUserType;
    }
}
