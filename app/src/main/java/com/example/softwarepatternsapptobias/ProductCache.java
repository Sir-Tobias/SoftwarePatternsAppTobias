package com.example.softwarepatternsapptobias;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Hashtable;

public class ProductCache {

    private static Hashtable<String, Product> productMap  = new Hashtable<>();

    public static Product getProduct(String productId) {
        Product cachedProduct = productMap.get(productId);
        return (Product) cachedProduct.clone();
    }

    public static void loadCache() {
        DatabaseReference productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    product.setId(productSnapshot.getKey());

                    productMap.put(product.getId(), product);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
