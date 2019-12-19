package com.chat.chatroom;

public class ChatTitle {

    private String chatTitleId, fromUserId, fromUser, toUserId, toUser;

    public ChatTitle() {
    }

    public ChatTitle(String chatTitleId, String fromUserId, String fromUser, String toUserId, String toUser) {
        this.chatTitleId = chatTitleId;
        this.fromUserId = fromUserId;
        this.fromUser = fromUser;
        this.toUserId = toUserId;
        this.toUser = toUser;
    }

    public String getChatTitleId() {
        return chatTitleId;
    }

    public void setChatTitleId(String chatTitleId) {
        this.chatTitleId = chatTitleId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
}
