package com.example.recipesapp.Classes;

public class FoodData {

    private String itemName;
    private String itemIngredients, itemDirections;
    private String itemDescription;
    private String itemDifficulty;
    private String itemImage;
    private String key;

    public FoodData(){};
    public FoodData(String itemName,String itemIngredients, String itemDirections, String itemDescription, String itemDifficulty, String itemImage) {
        this.itemName = itemName;
        this.itemIngredients = itemIngredients;
        this.itemDirections = itemDirections;
        this.itemDescription = itemDescription;
        this.itemDifficulty = itemDifficulty;
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemIngredients() {
        return itemIngredients;
    }

    public String getItemDirections() {
        return itemDirections;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemDifficulty() {
        return itemDifficulty;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
