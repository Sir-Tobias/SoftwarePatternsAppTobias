package com.example.softwarepatternsapptobias;

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

public class MyOrdersHistory extends AppCompatActivity {

    private ArrayList<Product> products = new ArrayList<>();
    private MainProductsAdapter mAdapter;
    RecyclerView orderRecyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders_history);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        orderRecyclerView = (RecyclerView) findViewById(R.id.myOrdersRecyclerView);
        orderRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager2 = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(myLayoutManager2);

        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("User_OrderHistory");

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if(userSnap.getKey().equals(mUser.getUid())) {
                        GenericTypeIndicator<ArrayList<Order>> t = new GenericTypeIndicator<ArrayList<Order>>() {};
                        ArrayList<Order> orders = userSnap.getValue(t);

                        Log.d("TEST", "12234");

                        for(Order o: orders) {
                            Log.d("ORDER", o.getUserID());
                            products.addAll(o.getProducts());
                        }
                        break;
                    }
                }
                orderRecyclerView.setLayoutManager(new LinearLayoutManager(MyOrdersHistory.this));
                orderRecyclerView.setHasFixedSize(true);
                mAdapter = new MainProductsAdapter(products, MyOrdersHistory.this);
                orderRecyclerView.setAdapter(mAdapter);


                emptyView = findViewById(R.id.emptyMyCustomerHistoryText);

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