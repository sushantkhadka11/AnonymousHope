package com.sushant.anonymoushope.Model;

public class Chat {
    private String message;
    private String msgReceiver;
    private String msgSender;

    public Chat(String message, String msgReceiver, String msgSender) {
        this.message = message;
        this.msgReceiver = msgReceiver;
        this.msgSender = msgSender;
    }


    public Chat() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgReceiver() {
        return msgReceiver;
    }

    public void setMsgReceiver(String msgReceiver) {
        this.msgReceiver = msgReceiver;
    }

    public String getMsgSender() {
        return msgSender;
    }

    public void setMsgSender(String msgSender) {
        this.msgSender = msgSender;
    }
}
