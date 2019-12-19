package com.chat.chatroom;

public class Chat {

    private String messageId, message, toUserId, toName, fromUserId, fromName, messageDate;
    int type;

    public Chat() {
    }

    public Chat(String messageId, String message, String toUserId, String toName, String fromUserId, String fromName, String messageDate, int type) {
        this.messageId = messageId;
        this.message = message;
        this.toUserId = toUserId;
        this.toName = toName;
        this.fromUserId = fromUserId;
        this.fromName = fromName;
        this.messageDate = messageDate;
        this.type = type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
