package com.example.softwarepatternsapptobias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EditProductActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference db;
    private EditText editTitle, editManufacturer, editPrice, editCategory, editStockLvl, editDescription;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent i = getIntent();
        product = (Product) i.getSerializableExtra("PRODUCT_INTENT");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        editTitle = findViewById(R.id.updateProductName);
        editPrice = findViewById(R.id.updateProductPrice);
        editDescription = findViewById(R.id.updateDescription);
        editCategory = findViewById(R.id.updateCategoryText);
        editManufacturer = findViewById(R.id.updateManufactText);
        editStockLvl = findViewById(R.id.updateStockLevelText);

        editTitle.setText(product.getTitle());
        editPrice.setText(String.valueOf(product.getPrice()));
        editDescription.setText(product.getDescription());
        editCategory.setText(product.getCategory());
        editManufacturer.setText(product.getManufacturer());
        editStockLvl.setText(String.valueOf(product.getStockLevel()));

        Button addProductButton = findViewById(R.id.updateProductButton);

        addProductButton.setOnClickListener(v -> {
            db = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            String uid = mUser.getUid();
            String productName = editTitle.getText().toString();
            String productPrice = editPrice.getText().toString();
            double priceDouble = Double.parseDouble(productPrice);
            String productDescription = editDescription.getText().toString();
            String productManufact = editManufacturer.getText().toString();
            String productCategory = editCategory.getText().toString();
            int productStockLvl = Integer.parseInt(editStockLvl.getText().toString());

            if (productName.matches("") || productPrice.matches("") ||
                    productDescription.matches("") || productManufact.matches("") ||
                    editStockLvl.getText().toString().matches("")) {

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(EditProductActivity.this);
                dlgAlert.setMessage("Please fill in required fields(*)");
                dlgAlert.setTitle("Hold Up!");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            } else {
                String id = product.getId();
                double rating = product.getOverallRating();
                int totalRatings = product.getTotalRatings();
                List<String> images = product.getImages();
                product = new Product(productName, productCategory, productDescription, productManufact,
                        priceDouble, productStockLvl, images);
                product.setTotalRatings(totalRatings);
                product.setOverallRating(rating);
                db.child("Products").child(id).setValue(product)
                        .addOnSuccessListener(aVoid -> Toast.makeText(EditProductActivity.this, "Product updated",
                                Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Write to db failed", Toast.LENGTH_LONG).show());
            }
        });

        ImageButton backButton = findViewById(R.id.updateProductBackButton);
        backButton.setOnClickListener(v -> {
            Intent i1 = new Intent(EditProductActivity.this, UpdateStockActivity.class);
            finish();
            startActivity(i1);
        });
    }
}