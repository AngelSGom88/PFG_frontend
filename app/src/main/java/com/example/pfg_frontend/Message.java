package com.example.pfg_frontend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    private String text;
    private boolean isFromUser; // true = usuario, false = bot
    private long timestamp;
    private String url;

    public Message(String text, boolean isFromUser, long timestamp) {
        this.text = text;
        this.isFromUser = isFromUser;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public boolean isFromUser() {
        return isFromUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
