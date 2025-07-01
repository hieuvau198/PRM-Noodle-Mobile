package com.example.prm_noodle_mobile.data.model;

public class Topping {
    private int toppingId;
    private String toppingName;
    private int price;
    private String description;
    private boolean isAvailable;

    public Topping(int toppingId, String toppingName, int price, String description, boolean isAvailable) {
        this.toppingId = toppingId;
        this.toppingName = toppingName;
        this.price = price;
        this.description = description;
        this.isAvailable = isAvailable;
    }

    public int getToppingId() { return toppingId; }
    public void setToppingId(int toppingId) { this.toppingId = toppingId; }
    public String getToppingName() { return toppingName; }
    public void setToppingName(String toppingName) { this.toppingName = toppingName; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
} 