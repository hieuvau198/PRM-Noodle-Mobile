package com.example.prm_v3.utils;

import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Map<Integer, Integer> productQuantities = new HashMap<>();
    private Map<Integer, Integer> comboQuantities = new HashMap<>();
    private Map<Integer, Boolean> selectedToppings = new HashMap<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Product quantities
    public void setProductQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            productQuantities.remove(productId);
        } else {
            productQuantities.put(productId, quantity);
        }
    }

    public int getProductQuantity(int productId) {
        return productQuantities.getOrDefault(productId, 0);
    }

    // Combo quantities
    public void setComboQuantity(int comboId, int quantity) {
        if (quantity <= 0) {
            comboQuantities.remove(comboId);
        } else {
            comboQuantities.put(comboId, quantity);
        }
    }

    public int getComboQuantity(int comboId) {
        return comboQuantities.getOrDefault(comboId, 0);
    }

    // Topping selection
    public void setToppingSelected(int toppingId, boolean selected) {
        if (selected) {
            selectedToppings.put(toppingId, true);
        } else {
            selectedToppings.remove(toppingId);
        }
    }

    public boolean isToppingSelected(int toppingId) {
        return selectedToppings.getOrDefault(toppingId, false);
    }

    // Get all selected items
    public Map<Integer, Integer> getProductQuantities() {
        return new HashMap<>(productQuantities);
    }

    public Map<Integer, Integer> getComboQuantities() {
        return new HashMap<>(comboQuantities);
    }

    public Map<Integer, Boolean> getSelectedToppings() {
        return new HashMap<>(selectedToppings);
    }

    // Utility methods
    public int getTotalItems() {
        int total = 0;
        for (int quantity : productQuantities.values()) {
            total += quantity;
        }
        for (int quantity : comboQuantities.values()) {
            total += quantity;
        }
        return total;
    }

    public boolean isEmpty() {
        return productQuantities.isEmpty() && comboQuantities.isEmpty();
    }

    public void clear() {
        productQuantities.clear();
        comboQuantities.clear();
        selectedToppings.clear();
    }
}