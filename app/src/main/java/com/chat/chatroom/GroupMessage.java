package com.chat.chatroom;

public class GroupMessage {

    private String messageId, userId, userName, message;
    int type;

    public GroupMessage() {
    }

    public GroupMessage(String messageId, String userId, String userName, String message,int type) {
        this.messageId = messageId;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.type=type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
