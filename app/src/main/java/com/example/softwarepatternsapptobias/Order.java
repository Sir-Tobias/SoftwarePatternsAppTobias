package com.example.softwarepatternsapptobias;

import java.util.ArrayList;

public class Order {

    private String id, userID;
    private ArrayList<Product> Products = new ArrayList<>();

    public Order() {
    }

    public Order(String userID, ArrayList<Product> Products) {
        this.userID = userID;
        this.Products = Products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Product> getProducts() {
        return Products;
    }

    public void setProducts(ArrayList<Product> Products) {
        this.Products = Products;
    }

    public void addToOrder(Product p) {
        Products.add(p);
    }

    public void removeProduct(Product p) {
        Products.remove(p);
    }
}
