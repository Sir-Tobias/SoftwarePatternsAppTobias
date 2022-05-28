package com.example.softwarepatternsapptobias;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ablanco.zoomy.Zoomy;
import com.example.softwarepatternsapptobias.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private static final int REQUEST_CODF_IMAGE = 101;

    private boolean imageUploadSuccess;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference db;
    private TextView counter;
    private EditText editTitle, editManufacturer, editPrice, editCategory, editStockLvl, editDescription;
    private List<Uri> uriList;
    private List<String> images;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        editTitle = findViewById(R.id.editProductTitle);
        editManufacturer = findViewById(R.id.editProductManufacturer);
        editPrice = findViewById(R.id.editProductPrice);
        editCategory = findViewById(R.id.editCategoryText);
        editStockLvl = findViewById(R.id.editItemQuantity);
        editDescription = findViewById(R.id.editReview);
        counter = findViewById(R.id.counter);
        Button addProductButton = findViewById(R.id.addProductButton);

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
        editDescription.addTextChangedListener(mTextEditorWatcher);


        Button chooseImagesButton = findViewById(R.id.chooseImagesButton);

        //Method to upload photo from phone gallery
        chooseImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });


        addProductButton.setOnClickListener(v -> {
            db = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            String uid = mUser.getUid();
            String productTitle = editTitle.getText().toString();
            String productManufact = editManufacturer.getText().toString();
            String productPrice = editPrice.getText().toString();
            double priceDouble = Double.parseDouble(productPrice);
            String productCategory = editCategory.getText().toString();
            String productDescription = editDescription.getText().toString();
            int productStockLvl = Integer.parseInt(editStockLvl.getText().toString());

            if (productTitle.matches("") || productPrice.matches("") ||
            productDescription.matches("") || productManufact.matches("") ||
            editStockLvl.getText().toString().matches("")) {

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(AddProductActivity.this);
                dlgAlert.setMessage("Please fill in required fields(*)");
                dlgAlert.setTitle("Hold Up!");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {

                        });
            } else {

                if (!uploadImages()) {
                    Toast.makeText(AddProductActivity.this, "Cancelled, image upload error. Try again",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Product product = new Product(productTitle, productCategory, productDescription, productManufact,
                            priceDouble, productStockLvl, images);
                    db.child("Products").push().setValue(product)
                            .addOnSuccessListener(aVoid -> Toast.makeText(AddProductActivity.this, "Product added",
                                    Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e -> Toast.makeText(AddProductActivity.this, "Write to db failed", Toast.LENGTH_LONG).show());
                }
            }
        });

        ImageButton backButton = findViewById(R.id.addProductBackButton);
        backButton.setOnClickListener(v -> {
            Intent i = new Intent(AddProductActivity.this, MainActivity2.class);
            finish();
            startActivity(i);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            final ImageView imageView1 = findViewById(R.id.imageView);
            final ImageView imageView2 = findViewById(R.id.imageView2);
            final ImageView imageView3 = findViewById(R.id.imageView3);
            final ImageView imageView4 = findViewById(R.id.imageView4);
            List<ImageView> imageViews = new ArrayList<>();
            imageViews.add(imageView1);
            imageViews.add(imageView2);
            imageViews.add(imageView3);
            imageViews.add(imageView4);
            for (ImageView imageView : imageViews) {
                Zoomy.Builder builder = new Zoomy.Builder(this).target(imageView);
                builder.register();
            }

            final List<Bitmap> bitmaps = new ArrayList<>();
            ClipData clipData = data.getClipData();
            uriList = new ArrayList<>();

            if (clipData != null) {

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    uriList.add(imageUri);
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);

                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                Uri imageUri = data.getData();
                uriList.add(imageUri);
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);

                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmaps.add(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < bitmaps.size(); i++) {
                imageViews.get(i).setImageBitmap(bitmaps.get(i));
            }
        }
    }

    public boolean uploadImages() {
        imageUploadSuccess = true;
        if (uriList != null) {
            images = new ArrayList<>();
            for (Uri uri : uriList) {
                StorageReference ref
                        = storageReference
                        .child("images/" + UUID.randomUUID().toString());

                // Progress Listener for loading
// percentage on the dialog box
                ref.putFile(uri)
                        .addOnFailureListener(e -> {
                            imageUploadSuccess = false;
                            // Error, Image not uploaded
                            Toast
                                    .makeText(AddProductActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        })
                        .addOnProgressListener(
                                taskSnapshot -> {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                });
                Log.i("REF PATH", ref.getPath());
                images.add(ref.getPath());
            }
        }
        return imageUploadSuccess;
    }

}

