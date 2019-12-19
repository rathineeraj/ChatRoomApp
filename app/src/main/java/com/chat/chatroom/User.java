package com.chat.chatroom;

public class User {

    private String userId, name, gender;
    int isActive;


    public User() {
    }

    public User(String userId, String name, String gender, int isActive) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.isActive = isActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
