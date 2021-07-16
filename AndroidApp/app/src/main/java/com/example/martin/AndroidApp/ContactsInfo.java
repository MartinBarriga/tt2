package com.example.martin.AndroidApp;

public class ContactsInfo {
    private Long id;
    private String phoneNumber;
    private String name;
    private Boolean isMessageSelected;
    private Boolean isNotificationSelected;
    private Boolean isUser;
    private String userID;

    public ContactsInfo(Long id, String phoneNumber, String name, Boolean isMessageSelected, Boolean isNotificationSelected, Boolean isUser, String userID) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.isMessageSelected = isMessageSelected;
        this.isNotificationSelected = isNotificationSelected;
        this.isUser = isUser;
        this.userID = userID;
    }

    public String getUserID(){
        return userID;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsMessageSelected() {
        return isMessageSelected;
    }

    public void setIsMessageSelected(Boolean messageSelected) {
        isMessageSelected = messageSelected;
    }

    public Boolean getIsNotificationSelected() {
        return isNotificationSelected;
    }

    public void setIsNotificationSelected(Boolean notificationSelected) {
        isNotificationSelected = notificationSelected;
    }
    public Boolean getIsUser(){
        return isUser;
    }
    public void setIsUser(Boolean isUser){
        this.isUser = isUser;
    }

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
}
