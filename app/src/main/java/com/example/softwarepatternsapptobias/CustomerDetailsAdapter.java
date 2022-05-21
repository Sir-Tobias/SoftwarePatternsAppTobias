package com.example.softwarepatternsapptobias;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomerDetailsAdapter extends RecyclerView.Adapter<CustomerDetailsAdapter.MyViewHolder> {
    private ArrayList<User> mylistvalues;
    private DatabaseReference userDB;
    private StorageReference storageReference;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView, emailView, addressView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameDetailsRow);
            emailView = itemView.findViewById(R.id.emailDetailsRow);
            addressView = itemView.findViewById(R.id.addressDetailsRow);
        }
    }

    public CustomerDetailsAdapter(ArrayList<User> myDataset, Context context) {
        mylistvalues = myDataset;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.customerdetails_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final User user = mylistvalues.get(position);
        holder.nameView.setText("Name: " + user.getFullname());
        holder.emailView.setText("Email: " + user.getEmail());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomerOrderHistoryActivity.class);
                intent.putExtra("USER_INTENT", user);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }
}
