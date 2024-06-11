package edu.niu.android.instagroc.model;

import java.io.Serial;
import java.io.Serializable;

public class CategoryModel implements Serializable {
    int id, shopId;
    String name, image;

    public CategoryModel() {
    }

    public CategoryModel(int shopId, String name, String image) {
        this.shopId = shopId;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
