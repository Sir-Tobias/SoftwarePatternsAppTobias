package com.example.softwarepatternsapptobias;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.WithHint;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class shopUser extends AppCompatActivity {

    private static final int REQUEST_CODF_IMAGE = 101;


    private Uri localFileUri, serverFileUri;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;

    NavigationView nav;
    View header;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

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

    FirebaseAuth mAuth;
    boolean isProfileAdded = false;

    //SHARED PREFERENCES
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_user);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //InfoLayout=(Layout) findViewById(R.layout.navheader);
        nav=(NavigationView)findViewById(R.id.navimenu);

        //GETTING THE HEADER VIEW FROM MY NAVIGATION MENU
        header = nav.getHeaderView(0);

        //EDITTEXTVIEW OF UPDATING USER
        fullnameUpdate=(EditText)findViewById(R.id.uFullname);
        phonenoUpdate=(EditText)findViewById(R.id.uPhoneNo);

        //GETTING THE TEXT VALUES OF THE NAV MENU
        mName=(TextView)header.findViewById(R.id.userNameMenu);
        mDescription=(TextView)header.findViewById(R.id.userDescription);
        aDescription=(TextView)header.findViewById(R.id.adminDescription);

        cImage=(ImageView)header.findViewById(R.id.customerImage);

        storage = FirebaseStorage.getInstance();
        Storageref = storage.getReference().child("TeditsPost");


        profileImage=(ImageView)findViewById(R.id.profile_image);
        menuProfileImage=(ImageView)header.findViewById(R.id.profile_image);


        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        viewNew=(TextView)findViewById(R.id.banner2);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //INSTANTIATING MY SHARED PREFERENCE
        sp = getSharedPreferences("newAnswer", Context.MODE_PRIVATE);



        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                //loadInformation();

                switch (menuItem.getItemId())
                {

                    case R.id.nav_profile :
                        Toast.makeText(getApplicationContext(),"Profile is open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_user_catalogue :
                        Toast.makeText(getApplicationContext(),"Profile is open",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(shopUser.this, MainActivity2.class));
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_logout :
                        Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(shopUser.this, MainActivity.class));
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODF_IMAGE);

            }
        });

        //LOAD INFORMATION METHOD WILL GIVE USERS ACCESS TO DIFFERENT CONTROLS IN THE NAVIGATION
        loadInformation();
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
                viewNew.setText("Welcome back "+ post);
                System.out.println("This is working hello "+ post);

                //SETTING THE NAME OF THE USER IN THE MENU
                mName.setText(post);

                //SETTIING THE TEXTVIEW OF THE UPDATING PROFILE TO FULLNAME
                fullnameUpdate.setHint(post);

                //SETTIING THE TEXTVIEW OF THE UPDATING PROFILE TO PHONENO
                String phoneDetails = snapshot.child("phoneNo").getValue().toString();
                phonenoUpdate.setHint(phoneDetails);

                String newEmail = snapshot.child("email").getValue().toString();

                //Checking to see if chat profile already exists if not it will be created
                //checkForChatProfile(post, newEmail, userID);


                chatProfile(post, newEmail, userID);

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
                    mName.setText(post);
                    //SETTING THE DESCRIPTION TO DESIGNER
                    mDescription.setText(uType);


                    //ADDING THE USERNAME TO MY SHARED PREFERENCE
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", post);
                    editor.commit();

                    //Checking to see if chat profile already exists if not it will be created
                    //checkForChatProfile(post, newEmail, userID);

                } else if(uType.equalsIgnoreCase("Admin")) {


                    mName.setText(post);
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
                        completedAdding();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(shopUser.this, error.getMessage(), Toast.LENGTH_LONG);

            }
        });
    }

    private void completedAdding() {
        System.out.println("THE FUNCTION IS COMPLETE");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODF_IMAGE && data!=null)
        {
            imageUri=data.getData();
            isProfileAdded=true;
            profileImage.setImageURI(imageUri);
        }
    }

    public void submitUpdate(View view) {
        String fullname = fullnameUpdate.getText().toString();
        String phoneno = phonenoUpdate.getText().toString();


        //IF THE TEXTBOX ARE NOT EMPTY START THE DETAILS ONLY METHOD
        if(!fullname.isEmpty() && !phoneno.isEmpty() && isProfileAdded!=true) {
            updateDetailsOnly(fullname, phoneno);
        }
        //IF THE IMAGE IS SECTION IS POPULATED AND THE OTHER FIELDS ARE EMPTY WHEN SUBMIT IS PRESSED
        else if(isProfileAdded!=false && fullname.isEmpty() && phoneno.isEmpty()) {
            updateImageOnly();
            return;
        }
        //IF TEXTB0X AND IMAGE ARE NOT EMPTY START UPDATEDETAILSANDPHOTO METHOD
        else if(isProfileAdded!=false  && !fullname.isEmpty() && !phoneno.isEmpty()) {
            updateDetailsAndPhoto(fullname, phoneno);
        }

        else if(fullname.isEmpty()) {
            fullnameUpdate.setError("Enter name");
            fullnameUpdate.requestFocus();
            return;
        }
        else if(fullname.length()<8){
            fullnameUpdate.setError("Full name must contain at least 8 letters");
            fullnameUpdate.requestFocus();
        }
        else if(phoneno.isEmpty()) {
            phonenoUpdate.setError("Enter your phone Number");
            phonenoUpdate.requestFocus();
            return;
        }
        else if(phoneno.length()<8) {
            phonenoUpdate.setError("Phone number must contain at least 8 numbers");
        }
        else {
            Toast.makeText(shopUser.this, "Nothing to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImageOnly() {

        Dataref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Data reference key
        final String key = Dataref.push().getKey();

        //UPDATING THE USER IMAGE DETAIL
        Storageref.child(key+ ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Storageref.child(key+ ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //HashMap hashMap = new HashMap();

                        //PASSING THE PROFILE PICTURE INTO THE USER DETAILS
                        Dataref.child("UserDetails").child("profilePic").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //HashMap hashMap = new HashMap();
                                //hashMap.put("ProfilerImageUri", uri.toString());


                            }
                        });
                    }
                });
            }
        });

    }

    private void updateDetailsOnly(String fullname, String phoneno) {
        Dataref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        Dataref.child("UserDetails").child("fullname").setValue(fullname).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(shopUser.this,"Details successfully updated", Toast.LENGTH_LONG).show();
            }
        });
        Dataref.child("UserDetails").child("phoneNo").setValue(phoneno).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(shopUser.this,"Details successfully updated Here", Toast.LENGTH_LONG).show();
                startActivity(new Intent(shopUser.this, shopUser.class));
            }
        });
    }

    private void updateDetailsAndPhoto(String fullname, String phoneno) {
        Dataref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Data reference key
        final String key = Dataref.push().getKey();

        //UPDATING THE USER DETAIL VALUES
        Dataref.child("UserDetails").child("fullname").setValue(fullname).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(shopUser.this,"Details successfully updated", Toast.LENGTH_LONG).show();
            }
        });
        Dataref.child("UserDetails").child("phoneNo").setValue(phoneno).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(shopUser.this,"Details successfully updated Here", Toast.LENGTH_LONG).show();
                startActivity(new Intent(shopUser.this, shopUser.class));
            }
        });

        //UPDATING THE USER IMAGE DETAIL
        Storageref.child(key+ ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Storageref.child(key+ ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //HashMap hashMap = new HashMap();

                        //PASSING THE PROFILE PICTURE INTO THE USER DETAILS
                        Dataref.child("UserDetails").child("profilePic").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //HashMap hashMap = new HashMap();
                                //hashMap.put("ProfilerImageUri", uri.toString());

                            }
                        });
                    }
                });
            }
        });


    }
}