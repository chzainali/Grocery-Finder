package edu.niu.android.instagroc.model;

import java.io.Serializable;

public class OrderModel implements Serializable {
    int id, userId, categoryId, productId;
    String name, price,time, quantity, description, imageUri;

    public OrderModel() {
    }

    public OrderModel(int userId, int categoryId, int productId, String name, String price, String time, String quantity, String description, String imageUri) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.time = time;
        this.quantity = quantity;
        this.description = description;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
