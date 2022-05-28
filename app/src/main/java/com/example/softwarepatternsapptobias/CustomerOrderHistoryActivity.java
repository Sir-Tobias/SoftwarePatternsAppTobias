package com.example.softwarepatternsapptobias;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsapptobias.Order;
import com.example.softwarepatternsapptobias.Product;
import com.example.softwarepatternsapptobias.User;
import com.example.softwarepatternsapptobias.ProductCache;
import com.example.softwarepatternsapptobias.MainProductsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerOrderHistoryActivity extends AppCompatActivity {

    private ArrayList<Product> products = new ArrayList<>();
    private MainProductsAdapter mAdapter;
    RecyclerView orderRecyclerView;
    private User user;
    private TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_history);

        ProductCache.loadCache();

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("USER_INTENT");
        Log.d("USER INTENT", user.getUid());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        orderRecyclerView = (RecyclerView) findViewById(R.id.customerOrdersRecyclerView);
        orderRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager2 = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(myLayoutManager2);


        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("User_OrderHistory");

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if(userSnap.getKey().equals(user.getUid())) {
                        GenericTypeIndicator<ArrayList<Order>> t = new GenericTypeIndicator<ArrayList<Order>>() {};
                        ArrayList<Order> orders = userSnap.getValue(t);

                        Log.d("TEST", "12234");

                        for(Order o: orders) {
                            for(Product p: o.getProducts()) {

                                products.add(ProductCache.getProduct(p.getId()));

                            }
                        }
                        break;
                    }
                }
                orderRecyclerView.setLayoutManager(new LinearLayoutManager(CustomerOrderHistoryActivity.this));
                orderRecyclerView.setHasFixedSize(true);
                mAdapter = new MainProductsAdapter(products, CustomerOrderHistoryActivity.this);
                orderRecyclerView.setAdapter(mAdapter);


                emptyView = findViewById(R.id.emptyCustomerHistoryText);
                
                if (products.isEmpty()) {
                    orderRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    orderRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}