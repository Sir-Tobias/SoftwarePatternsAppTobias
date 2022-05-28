package com.example.softwarepatternsapptobias;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    //implements NavigationView.OnNavigationItemSelectedListener
    private DrawerLayout drawerLayout;
    private List<String> sortSpinnerList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private boolean isAdmin;
    private ArrayList<Product> myDataset = new ArrayList<Product>();
    private MainProductsAdapter mAdapter;
    RecyclerView myRecyclerView;

    private static final int REQUEST_CODF_IMAGE = 101;


    private Uri localFileUri, serverFileUri;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;

    NavigationView nav;
    View header;
    ActionBarDrawerToggle toggle;

    Uri imageUri;

    //ConstraintSet.Layout InfoLayout;
    Layout InfoLayout;

    private DatabaseReference Dataref, Uref, data, chatdata;

    private StorageReference Storageref;
    private FirebaseStorage storage;

    EditText fullnameUpdate, phonenoUpdate;

    //MENU VALUE REFERENCES
    TextView viewNew, mName, mDescription, aDescription;
    ImageView mImage, cImage, aImage, profileImage, menuProfileImage;
    boolean isProfileAdded = false;

    //SHARED PREFERENCES
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //checkIfAdmin();
        nav=(NavigationView)findViewById(R.id.navimenu);

        //GETTING THE HEADER VIEW FROM MY NAVIGATION MENU
        header = nav.getHeaderView(0);

        myRecyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        mName=(TextView)header.findViewById(R.id.userNameMenu);
        mDescription=(TextView)header.findViewById(R.id.userDescription);
        aDescription=(TextView)header.findViewById(R.id.adminDescription);

        sortSpinnerList = new ArrayList<>();
        sortSpinnerList.add("Sort By...");
        sortSpinnerList.add("Title Ascending");
        sortSpinnerList.add("Title Descending");
        sortSpinnerList.add("Price Ascending");
        sortSpinnerList.add("Price Descending");
        sortSpinnerList.add("Manufacturer Ascending");
        sortSpinnerList.add("Manufacturer Descending");

        //GETTING THE TEXT VALUES OF THE NAV MENU
        mName=(TextView)header.findViewById(R.id.userNameMenu);

        cImage=(ImageView)header.findViewById(R.id.customerImage);


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(MainActivity2.this,
                android.R.layout.simple_spinner_item, sortSpinnerList) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        Spinner sortSpinner = findViewById(R.id.sortSpinner);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(categoryAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (position == 1) {
                        Collections.sort(myDataset, Comparator.comparing(Product::getTitle));
                    }
                    else if (position == 2) {
                        Collections.sort(myDataset, Comparator.comparing(Product::getTitle).reversed());
                    }
                    else if (position == 3) {
                        Log.d("Case", String.valueOf(position));
                        myDataset.sort(Comparator.comparing(Product::getPrice));
                    }
                    else if (position == 4) {
                        Log.d("Case", String.valueOf(position));
                        myDataset.sort(Comparator.comparing(Product::getPrice).reversed());
                    }
                    else if (position == 5) {
                        Collections.sort(myDataset, Comparator.comparing(Product::getManufacturer));
                    }
                    else if (position == 6) {
                        Collections.sort(myDataset, Comparator.comparing(Product::getManufacturer).reversed());
                    }

                    mAdapter.filteredList(myDataset);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        EditText searchTitleEdit = findViewById(R.id.nameSearch);
        EditText searchCatEdit = findViewById(R.id.categorySearch);
        EditText searchManuEdit = findViewById(R.id.manufactSearch);
        sortSpinner = findViewById(R.id.sortSpinner);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.navimenu);
//        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        DatabaseReference productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String name = productSnapshot.child("title").getValue().toString();
                    String price = productSnapshot.child("price").getValue().toString();
                    String description = productSnapshot.child("description").getValue().toString();
                    String manufact = productSnapshot.child("manufacturer").getValue().toString();
                    List<String> images = (List<String>) productSnapshot.child("images").getValue();
                    String productID = productSnapshot.getKey();
                    Product product = new Product();
                    product.setId(productID);
                    product.setTitle(name);
                    product.setPrice(Double.parseDouble(price));
                    product.setImages(images);
                    product.setDescription(description);
                    product.setManufacturer(manufact);
                    product.setCategory(productSnapshot.child("category").getValue().toString());
                    long stock = (long) productSnapshot.child("stockLevel").getValue();
                    product.setStockLevel((int) stock);
                    myDataset.add(product);
                }
                myRecyclerView.setLayoutManager(new LinearLayoutManager((MainActivity2.this)));
                myRecyclerView.setHasFixedSize(true);
                mAdapter = new MainProductsAdapter(myDataset, MainActivity2.this);
                myRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent i;
                switch (id) {
                    case R.id.nav_my_cart:
                        i = new Intent(MainActivity2.this, CartActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_profile:
                        i = new Intent(MainActivity2.this, shopUser.class);
                        startActivity(i);
                        break;
                    case R.id.nav_my_orders:
                        i = new Intent(MainActivity2.this, MyOrdersHistory.class);
                        startActivity(i);
                        break;
                    case R.id.nav_add_product:
                        i = new Intent(MainActivity2.this, AddProductActivity.class);
                        startActivity(i);
                        break;
                    case R.id.nav_update_stock:
                        i = new Intent(MainActivity2.this, UpdateStockActivity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_customer_details:
                        i = new Intent(MainActivity2.this, CustomerDetailsActivity.class);
                        startActivity(i);
                        break;

                    case R.id.nav_logout:
                        mAuth.signOut();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity2.this, "User signed out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        searchTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString(), 1);
            }
        });

        searchCatEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString(), 2);
            }
        });

        searchManuEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString(), 3);
            }
        });


        //LOAD INFORMATION METHOD WILL GIVE USERS ACCESS TO DIFFERENT CONTROLS IN THE NAVIGATION
        loadInformation();
    }

    public void filter(String text, int i) {
        ArrayList<Product> products = new ArrayList<>();

        switch (i) {
            case (1):
                for (Product product : myDataset) {
                    if (product.getTitle().toLowerCase().contains(text.toLowerCase())) {
                        products.add(product);
                    }
                }
            case (2):
                for (Product product : myDataset) {
                    if (product.getCategory().toLowerCase().contains(text.toLowerCase())) {
                        products.add(product);
                    }
                }
            case (3):
                for (Product product : myDataset) {
                    if (product.getManufacturer().toLowerCase().contains(text.toLowerCase())) {
                        products.add(product);
                    }
                }
        }
        mAdapter.filteredList(products);
    }

    private void loadInformation() {

        Uref = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        System.out.println("THIS IS THE USER ID VALUE BELOW");
        System.out.println(Uref.getKey());
        String userID = Uref.getKey();

        chatdata = FirebaseDatabase.getInstance().getReference("Chat").child("ChatProfiles");

        Dataref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserDetails");
        //Dataref = FirebaseDatabase.getInstance().getReference("Users").child("Users").child(mAuth.getInstance().getCurrentUser().getUid()).child("UserDetails");

        data = FirebaseDatabase.getInstance().getReference().child("PostDetails");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    System.out.println("THIS IS SUPPOSE TO BE NAME" + postSnapshot.child("NameOfDesigner").getValue().toString());
                    //retrieveIDONE(currentID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //User post = snapshot.getValue(User.class);
                //DataSnapshot post = snapshot.child("userType");
                String post = snapshot.child("fullname").getValue().toString();
//                viewNew.setText("Welcome back "+ post);
                System.out.println("This is working hello "+ post);

//                //SETTING THE NAME OF THE USER IN THE MENU
                mName.setText(post);
//
//                //SETTIING THE TEXTVIEW OF THE UPDATING PROFILE TO FULLNAME
//                fullnameUpdate.setHint(post);
//
//                //SETTIING THE TEXTVIEW OF THE UPDATING PROFILE TO PHONENO
//                String phoneDetails = snapshot.child("phoneNo").getValue().toString();
//                phonenoUpdate.setHint(phoneDetails);
//
//                String newEmail = snapshot.child("email").getValue().toString();
//

                //IF PROFILE PIC EXIST ALREADY IN DATABASE RUN THIS CODE
                if(snapshot.hasChild("profilePic")) {
                    //SETTING THE PROFILE PICTURE FOR THE MENU AND USER PAGE
                    //String link = snapshot.getValue(String.class);
                    //System.out.println("THERE IS NO PROFILE");
                    String link = snapshot.child("profilePic").getValue().toString();
                    Picasso.get().load(link).into(profileImage);
                    Picasso.get().load(link).into(menuProfileImage);

                }

                //CHECKING THE USER TYPE THAT IS LOGGED IN
                String uType = snapshot.child("userType").getValue().toString();
                if (uType.equalsIgnoreCase("customer")) {

                    System.out.println("Updating the menu works");

                    cImage.setVisibility(View.VISIBLE);

                    //SETTING THE DESCRIPTION TO DESIGNER
                    mDescription.setText(uType);

                    nav.getMenu().getItem(4).setVisible(false);
                    nav.getMenu().getItem(5).setVisible(false);
                    nav.getMenu().getItem(6).setVisible(false);

                } else if(uType.equalsIgnoreCase("admin")) {

//                    //SETTING ICON AS ADMIN IF USER TYPE IS ADMIN
//                    mImage.setVisibility(View.GONE);
//                    cImage.setVisibility(View.GONE);
//                    aImage.setVisibility(View.VISIBLE);
                    nav.getMenu().getItem(2).setVisible(false);
                    nav.getMenu().getItem(3).setVisible(false);

                    //SETTING THE DESCRIPTION TO ADMIN
                    aDescription.setText(uType);

                    //Checking to see if chat profile already exists if not it will be created
                    //checkForChatProfile(post, newEmail, userID);
                }
            }


            //I CREATED THIS METHOD TO INHERIT THE VALUES OF THE USER PROFILE RATHER THAN CALLING METHODS DIRECTLY TO IT
            private void chatProfile(String post, String newEmail, String userID) {
                //chatdata = FirebaseDatabase.getInstance().getReference("Chat").child("ChatProfiles");


                final String key = chatdata.push().getKey();

                HashMap hashMap = new HashMap();
                hashMap.put("ChatName", post);
                hashMap.put("ChatEmail", newEmail);
                hashMap.put("ChatID", userID);
                hashMap.put("ChatKey", key);

                chatdata.child(userID).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(teditsUser.this,"T-Chats is now online", Toast.LENGTH_LONG).show();
                        //completedAdding();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity2.this, error.getMessage(), Toast.LENGTH_LONG);

            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void checkIfAdmin() {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (mUser.getUid().equals(snapshot.getKey())) {
                        String admin = snapshot.child("userType").getValue().toString();

                        isAdmin = admin.equals("admin");

                        Log.d("Admin = ", String.valueOf(isAdmin));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}