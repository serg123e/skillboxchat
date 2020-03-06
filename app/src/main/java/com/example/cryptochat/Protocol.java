package com.example.cryptochat;

import android.telephony.gsm.GsmCellLocation;

import com.google.gson.Gson;

import java.util.NavigableMap;

public class Protocol {
    public static final int USER_STATUS = 1;
    public static final int MESSAGE = 2;
    public static final int USER_NAME = 3;

    static class User {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User() {}
    }


    static class UserStatus {
        private User user;
        private boolean connected;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public UserStatus() {}
    }

    static class Message {
        private final static int GROUP_CHAT = 1;
        private long sender;
        private long receiver = GROUP_CHAT;
        private String encodedText;

        public long getSender() {
            return sender;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }

        public long getReceiver() {
            return receiver;
        }

        public void setReceiver(long receiver) {
            this.receiver = receiver;
        }

        public String getEncodedText() {
            return encodedText;
        }

        public void setEncodedText(String encodedText) {
            this.encodedText = encodedText;
        }

        public Message(String encodedText) {
            this.encodedText = encodedText;
        }
    }

    public static int getType(String json) {
        if (json == null || json.length() < 3) {
            return -1;
        } else {
            return Integer.valueOf(json.substring(0, 1));
        }
    }

    static class UserName{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UserName(String name) {
            this.name = name;
        }
    }
    public static UserStatus unpackStatus(String json) {
        Gson g = new Gson();
        return g.fromJson(json.substring(1), UserStatus.class);
    }

    public static Message unpackMessage(String json) {
        Gson g = new Gson();
        return g.fromJson(json.substring(1), Message.class);
    }

    public static String packMessage(Message message) {
        Gson g = new Gson();
        return MESSAGE+g.toJson(message);
    }

    public static String packName(UserName name) {
        Gson g = new Gson();
        return USER_NAME+g.toJson(name);
    }

}
