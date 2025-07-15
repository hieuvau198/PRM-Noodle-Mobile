package com.example.prm_noodle_mobile.model;

public class Banner {
    private int imageResource;
    private String title;

    public Banner(int imageResource, String title) {
        this.imageResource = imageResource;
        this.title = title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }
}
