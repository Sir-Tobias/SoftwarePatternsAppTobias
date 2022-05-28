package com.example.softwarepatternsapptobias;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsapptobias.BuyStock;
import com.example.softwarepatternsapptobias.Invoker;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CartActivity extends AppCompatActivity {

    private FirebaseUser mUser;

    private ArrayList<Product> cart, cart2 = new ArrayList<Product>();
    private Order order;
    private MainProductsAdapter mAdapter;
    RecyclerView myRecyclerView;

    private TextView totalTextView;
    double totalPrice = 0;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        myRecyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if (userSnap.getKey().equals(mUser.getUid())) {
                        cart = new ArrayList<>();
                        if (userSnap.child("cart").exists()) {

                            user = userSnap.getValue(User.class);

                            cart = user.getCart();
                            Log.d("IF", "HELLO");
                        }

                        totalPrice = 0;

                        for (Product p : cart) {
                            if(user.isStudent()) {
                                totalPrice += user.calculateDiscount(p.getPrice(), 0.2);
                            }
                            else {
                                totalPrice += p.getPrice();
                            }
                        }

                        totalTextView = findViewById(R.id.shoppingCartTotalText);

                        if (!cart.isEmpty()) {
                            if(user.isStudent()) {
                                totalTextView.setText("Total Price(including 20% discount for students): €" + totalPrice);
                            }
                            else {
                                totalTextView.setText("Total Price: €" + totalPrice);
                            }
                        } else {
                            totalTextView.setText("Cart is empty");
                        }

                        myRecyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                        myRecyclerView.setHasFixedSize(true);
                        mAdapter = new MainProductsAdapter(cart, CartActivity.this);
                        new ItemTouchHelper(itemTouch).attachToRecyclerView(myRecyclerView);
                        myRecyclerView.setAdapter(mAdapter);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button payNowButton = findViewById(R.id.payNowButton);
        payNowButton.setOnClickListener(v -> {
            if (!cart.isEmpty()) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(CartActivity.this);

                List<String> productIDs = new ArrayList<>();
                HashMap<String, Integer> hashMap = new HashMap<>();

                for (Product p : cart) {
                    productIDs.add(p.getId());
                }

                for (String productID : productIDs) {
                    int occurrences = Collections.frequency(productIDs, productID);
                    hashMap.put(productID, occurrences);
                }

                int count = 0;

                Iterator it = hashMap.entrySet().iterator();

                while (it.hasNext()) {

                    Map.Entry pair = (Map.Entry) it.next();

                    BuyStock buyStockOrder = new BuyStock(cart.get(count), (Integer) pair.getValue());

                    Invoker invoker = Invoker.getInstance();

                    invoker.takeOrder(buyStockOrder);

                    invoker.placeOrders();

                    count ++;
                }

                DatabaseReference orderDetails = FirebaseDatabase.getInstance().getReference("User_OrderHistory");
                orderDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        if (snapshot1.hasChild(mUser.getUid())) {
                            final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");

                            userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                                        if(userSnap.getKey().equals(mUser.getUid())) {
                                            User user = userSnap.getValue(User.class);

                                            cart2 = user.getCart();
                                        }
                                    }
                                    GenericTypeIndicator<ArrayList<Order>> t = new GenericTypeIndicator<ArrayList<Order>>() {};
                                    ArrayList<Order> orders = snapshot1.child(mUser.getUid()).getValue(t);
                                    Order order = new Order(mUser.getUid(), cart2);
                                    orders.add(order);
                                    orderDetails.child(mUser.getUid()).setValue(orders);

                                    DatabaseReference userDB1 = FirebaseDatabase.getInstance().getReference("Users");
                                    userDB1.child(mUser.getUid()).child("cart").setValue(new ArrayList<Product>());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");

                            userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                                        if(userSnap.getKey().equals(mUser.getUid())) {
                                            User user = userSnap.getValue(User.class);

                                            cart2 = user.getCart();
                                        }
                                    }
                                    ArrayList<Order> orders = new ArrayList<>();
                                    order = new Order(mUser.getUid(), cart2);
                                    orders.add(order);
                                    orderDetails.child(mUser.getUid()).setValue(orders);
                                    Log.d("TAGG", "FALSE");

                                    DatabaseReference userDB1 = FirebaseDatabase.getInstance().getReference("Users");
                                    userDB1.child(mUser.getUid()).child("cart").setValue(new ArrayList<Product>());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                cart.clear();
                mAdapter.notifyDataSetChanged();

                totalTextView.setText("Cart is empty");

                dlgAlert.setMessage("Thank you for your purchase. " +
                        "Your order will be dispatched within 2 working days.");
                dlgAlert.setTitle("Transaction Approved");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                        });
            } else {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(CartActivity.this);
                dlgAlert.setMessage("Shopping cart is empty.");
                dlgAlert.setTitle("Error");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                        });
            }
        });
    }


    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    //.addBackgroundColor(ContextCompat.getColor(UserProductsActivity.this, R.color.grey))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Toast.makeText(CartActivity.this, "Removed from cart", Toast.LENGTH_SHORT).show();
            final Product product = cart.get(viewHolder.getAdapterPosition());
            Log.d("PRODUCT ID", product.getId());
            cart.remove(viewHolder.getAdapterPosition());

            final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");

            userDB.child(mUser.getUid()).child("cart").setValue(cart);
            //myDataset.clear();
            mAdapter.notifyDataSetChanged();

            totalPrice = totalPrice - product.getPrice();
            totalTextView.setText("Total Price: €" + totalPrice);
        }
    };
}