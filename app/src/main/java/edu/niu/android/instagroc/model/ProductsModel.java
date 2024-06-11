package edu.niu.android.instagroc.model;

import java.io.Serializable;

public class ProductsModel implements Serializable {
    int id, shopId, categoryId;
    String name, originalPrice, offerPrice, quantity, description, imageUri;

    public ProductsModel() {
    }

    public ProductsModel(int shopId, int categoryId, String name, String originalPrice, String offerPrice, String quantity, String description, String imageUri) {
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.name = name;
        this.originalPrice = originalPrice;
        this.offerPrice = offerPrice;
        this.quantity = quantity;
        this.description = description;
        this.imageUri = imageUri;
    }

    public ProductsModel(int id, int shopId, int categoryId, String name, String originalPrice, String offerPrice, String quantity, String description, String imageUri) {
        this.id = id;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.name = name;
        this.originalPrice = originalPrice;
        this.offerPrice = offerPrice;
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

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
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
