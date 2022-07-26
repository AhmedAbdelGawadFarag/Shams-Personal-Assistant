package com.example.marcello.models;

import java.util.HashMap;

public class Message {


    // message sender
    public static final int MESSAGE_SENDER_USER = 1;
    public static final int MESSAGE_SENDER_BOT = 2;


    private int messageType;
    private int messageSender;
    private String messageText;
    private HashMap<Object, Object> data;

    public Message(String messageText, int messageSender, int messageType){
        this.messageText = messageText;
        this.messageSender = messageSender;
        this.messageType = messageType;
    }
    public Message(){

    }
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(int messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public HashMap<Object, Object> getData() {
        return data;
    }

    public void setData(HashMap<Object, Object> data) {
        this.data = data;
    }
}
