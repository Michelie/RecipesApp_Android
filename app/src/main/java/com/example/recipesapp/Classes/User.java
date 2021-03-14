package com.example.recipesapp.Classes;

import com.example.recipesapp.Classes.FoodData;

import java.util.ArrayList;

public class User {
    public String fullName, username, email;


    public User(){}

    public User(String fullName, String username, String email) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        //this.favorites = new ArrayList<FoodData>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}
