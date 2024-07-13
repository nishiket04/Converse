package com.nishiket.converse.model;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatModel {
    @DocumentId
    String documentId;
    @PropertyName("msg")
    String msg;
    @PropertyName("time")
    Timestamp time;
    @PropertyName("from")
    String from;
    @PropertyName("to")
    String to;
    int type;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void determineType(String email) {
        if (this.to.equals(email) && !this.from.equals(email)) {
            this.type = 0;
        } else {
            this.type = 1;
        }
        Log.d("data", "determineType: "+type);
    }

    public String getFormattedTime() {
        if (time != null) {
            Date date = time.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat(" dd/MM hh:mm a", Locale.getDefault());
            return sdf.format(date);
        }
        return null;
    }
}
