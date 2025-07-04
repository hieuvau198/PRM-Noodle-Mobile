package com.example.prm_noodle_mobile.customer.home;

public class ChatMessageLocal {
    private String message;
    private boolean isBot;

    public ChatMessageLocal(String message, boolean isBot) {
        this.message = message;
        this.isBot = isBot;
    }

    public String getMessage() { return message; }
    public boolean isBot() { return isBot; }
} 