package com.example.softwarepatternsapptobias;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewProductActivity extends AppCompatActivity {

    private EditText editText;
    private RatingBar ratingBar;
    private TextView counter;
    private FirebaseUser mUser;
    private User user;
    private Product product, newProduct;
    private float newRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_product);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if (userSnap.getKey().equals(mUser.getUid())) {
                        user = userSnap.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editText = findViewById(R.id.editReview);
        ratingBar = findViewById(R.id.rating);
        Button button = findViewById(R.id.reviewButton);
        counter = findViewById(R.id.counter);

        Intent i = getIntent();
        product = (Product) i.getSerializableExtra("PRODUCT_INTENT");


        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                counter.setText(String.valueOf(s.length()) + "/250");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        editText.addTextChangedListener(mTextEditorWatcher);

        button.setOnClickListener(v -> {
            newRating = ratingBar.getRating();
            String description = editText.getText().toString();

            DatabaseReference reviewDB = FirebaseDatabase.getInstance().getReference("Product_Reviews");
            reviewDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(product.getId())) {
                        GenericTypeIndicator<ArrayList<Review>> t = new GenericTypeIndicator<ArrayList<Review>>() {
                        };
                        ArrayList<Review> reviews = snapshot.child(product.getId()).getValue(t);
                        Review review = new Review(description, product.getId(), newRating, user.getFullname());
                        reviews.add(review);
                        reviewDB.child(product.getId()).setValue(reviews);
                        Log.d("REVIEW", "TRUE");

                    } else {
                        Log.d("REVIEW", "FALSE");
                        ArrayList<Review> reviews = new ArrayList<>();
                        Review review = new Review(description, product.getId(), newRating, user.getFullname());
                        reviews.add(review);
                        reviewDB.child(product.getId()).setValue(reviews);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            final DatabaseReference productDB = FirebaseDatabase.getInstance().getReference("Products");

            productDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot productSnap : snapshot.getChildren()) {
                        if (productSnap.getKey().equals(product.getId())) {
                            newProduct = productSnap.getValue(Product.class);

                            break;
                        }
                    }

                    int ratings = newProduct.getTotalRatings();
                    double rating = newProduct.getOverallRating();
                    double finalRating = ((rating * ratings) + newRating) / (ratings + 1);

                    newProduct.setTotalRatings(ratings + 1);
                    newProduct.setOverallRating(finalRating);

                    DatabaseReference productDB = FirebaseDatabase.getInstance().getReference("Products");
                    productDB.child(product.getId()).setValue(newProduct);

                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }
}