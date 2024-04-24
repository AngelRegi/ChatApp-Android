package edu.uncc.hw08;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatSession implements Serializable {
    String senderId, senderName, receiverId, receiverName, creationDate, lastSentMsg, lastSentDate, chatId;
    ArrayList<Object> messages;

    public ChatSession() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastSentMsg() {
        return lastSentMsg;
    }

    public void setLastSentMsg(String lastSentMsg) {
        this.lastSentMsg = lastSentMsg;
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLastSentDate() {
        return lastSentDate;
    }

    public void setLastSentDate(String lastSentDate) {
        this.lastSentDate = lastSentDate;
    }

    public ArrayList<Object> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Object> messages) {
        this.messages = messages;
    }
}
