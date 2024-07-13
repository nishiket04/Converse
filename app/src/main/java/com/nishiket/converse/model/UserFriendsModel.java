package com.nishiket.converse.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class UserFriendsModel {
    @DocumentId
    private String documentId;
    @PropertyName("userId")
    private String userId;
    @PropertyName("lastMessage")
    private String lastMessage;
    @PropertyName("room")
    private String room;

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
