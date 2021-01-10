package com.example.crudstorage.model;

import com.google.firebase.database.Exclude;

public class Upload {
    private String name;
    private String imageUrl;
    private String key;

    public Upload() {
    }

    public Upload(String name, String imageUrl) {
        if (name.trim().equals("")) name = "No Name";

        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
