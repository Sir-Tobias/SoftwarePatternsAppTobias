package com.example.softwarepatternsapptobias;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class TProduct implements Serializable, Cloneable {

    private String id;
    private String name;
    private String category;
    private String description;
    private String manufacturer;
    private double price, overallRating;
    private int totalRatings, stockLevel;
    private List<String> images;

    public TProduct() {
    }

    public TProduct(String name, String category, String description,
                    String manufacturer, double price, int stockLevel, List<String> images) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.manufacturer = manufacturer;
        this.price = price;
        this.images = images;
        this.overallRating = 0;
        this.totalRatings = 0;
        this.stockLevel = stockLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(double overallRating) {
        this.overallRating = overallRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void buy(int count){
        Log.d("Method", id + count);

        final DatabaseReference productDB = FirebaseDatabase.getInstance().getReference("Products");

        productDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnap : snapshot.getChildren()) {
                    if (productSnap.getKey().equals(id)) {

                        long stockLevel = (long) productSnap.child("stockLevel").getValue();
                        stockLevel = stockLevel - count;
                        productDB.child(productSnap.getKey()).child("stockLevel").setValue(stockLevel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Object clone() {
        Object clone = null;

        try {
            clone = super.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return clone;
    }
}
