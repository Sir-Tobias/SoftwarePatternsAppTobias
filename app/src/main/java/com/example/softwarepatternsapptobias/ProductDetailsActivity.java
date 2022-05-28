package com.example.softwarepatternsapptobias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsapptobias.Product;
import com.example.softwarepatternsapptobias.Review;
import com.example.softwarepatternsapptobias.GlideApp;
import com.example.softwarepatternsapptobias.ProductReviewsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    Product product;
    private FirebaseUser mUser;
    private List<Product> cart = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();
    private ProductReviewsAdapter reviewsAdapter;
    private RecyclerView myRecyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //Log.d("USERNAME", mUser.getDisplayName());

        Intent i = getIntent();
        product = (Product) i.getSerializableExtra("PRODUCT_INTENT");
        String isBought = (String) i.getSerializableExtra("DETAILS_INTENT");

        Button reviewButton = findViewById(R.id.reviewProductButton);

        if (isBought.equals("NOT BOUGHT"))
            reviewButton.setVisibility(View.GONE);

        TextView productName = findViewById(R.id.productTitle);
        TextView productPrice = findViewById(R.id.productPrice);
        TextView productCategory = findViewById(R.id.productCategory);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productManufacturer = findViewById(R.id.productManufacturer);

        productName.setText(product.getTitle());
        productPrice.setText(String.valueOf(product.getPrice()));
        productCategory.setText(product.getCategory());
        productManufacturer.setText(product.getManufacturer());
        productDescription.setText(product.getDescription());


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        ImageView imageView = findViewById(R.id.productImageViewDetails);

        for (String url : product.getImages()) {

            GlideApp.with(ProductDetailsActivity.this)
                    .load(storageReference.child(url))
                    .into(imageView);

            break;
        }

        Button buyButton = findViewById(R.id.addToCartButton);
        buyButton.setOnClickListener((View.OnClickListener) v -> {
            final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");

            Log.d("STOCK", String.valueOf(product.getStockLevel()));

            if (product.getStockLevel() > 0) {

                userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            if (userSnap.getKey().equals(mUser.getUid())) {
                                if (userSnap.child("cart").exists()) {
                                    cart = (ArrayList<Product>) userSnap.child("cart").getValue();
                                    Log.d("IF", "HELLO");
                                }


                                cart.add(product);
                                userDB.child(userSnap.getKey()).child("cart").setValue(cart);
                                cart.clear();

                                Toast.makeText(ProductDetailsActivity.this, "Added to cart",
                                        Toast.LENGTH_SHORT).show();

                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (product.getStockLevel() <= 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ProductDetailsActivity.this);
                dlgAlert.setMessage("Item is out of stock.");
                dlgAlert.setTitle("Error");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        (DialogInterface.OnClickListener) (dialog, which) -> {
                        });
            }
        });

        reviewButton.setOnClickListener(v -> {
            Intent i1 = new Intent(ProductDetailsActivity.this, ReviewProductActivity.class);
            i1.putExtra("PRODUCT_INTENT", product);

            startActivity(i1);
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.reviewRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        DatabaseReference reviewDB = FirebaseDatabase.getInstance().getReference("Product_Reviews");
        reviewDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot reviewSnap: snapshot.getChildren()) {
                    if(product.getId().equals(reviewSnap.getKey())) {
                       for(DataSnapshot reviewSnap2: reviewSnap.getChildren()) {
                           Review review = reviewSnap2.getValue(Review.class);
                           reviews.add(review);
                       }
                    }
                }
                myRecyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
                myRecyclerView.setHasFixedSize(true);
                reviewsAdapter = new ProductReviewsAdapter(reviews, ProductDetailsActivity.this);
                myRecyclerView.setAdapter(reviewsAdapter);

                emptyView = findViewById(R.id.emptyReviewText);

                if (reviews.isEmpty()) {
                    myRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    myRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}