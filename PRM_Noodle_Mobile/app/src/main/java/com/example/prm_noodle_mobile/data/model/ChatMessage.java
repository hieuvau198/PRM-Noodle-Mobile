package com.example.prm_noodle_mobile.data.model;

public class ChatMessage {
    private String message;
    private String sessionId;

    public ChatMessage(String message, String sessionId) {
        this.message = message;
        this.sessionId = sessionId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
} 