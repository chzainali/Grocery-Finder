package edu.niu.android.instagroc.model;

import java.io.Serializable;

public class ShopModel implements Serializable {
    int id;
    String name, location, image;

    public ShopModel() {
    }

    public ShopModel(String name, String location, String image) {
        this.name = name;
        this.location = location;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
