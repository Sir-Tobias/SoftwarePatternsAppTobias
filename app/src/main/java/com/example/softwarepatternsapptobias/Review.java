package com.example.softwarepatternsapptobias;

public class Review {

    private String id, description, productID, userName;
    private float rating;

    public Review() {
    }

    public Review(String description, String productID, float rating, String userName) {
        this.description = description;
        this.productID = productID;
        this.rating = rating;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
