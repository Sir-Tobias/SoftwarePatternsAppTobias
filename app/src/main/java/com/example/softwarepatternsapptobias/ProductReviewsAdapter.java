package com.example.softwarepatternsapptobias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsapptobias.Review;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductReviewsAdapter extends RecyclerView.Adapter<ProductReviewsAdapter.MyViewHolder> {
    private ArrayList<Review> mylistvalues;
    private DatabaseReference userDB;
    private StorageReference storageReference;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView, descriptionView;
        private RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.reviewRowNameText);
            descriptionView = itemView.findViewById(R.id.reviewRowDecriptionText);
            ratingBar = itemView.findViewById(R.id.reviewRowRatingBar);
        }
    }

    public ProductReviewsAdapter(ArrayList<Review> myDataset, Context context) {
        mylistvalues = myDataset;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.review_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final Review review = mylistvalues.get(position);
        holder.nameView.setText(review.getUserName());
        holder.descriptionView.setText(review.getDescription());
        holder.ratingBar.setRating(review.getRating());


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, CustomerOrderHistoryActivity.class);
////                intent.putExtra("USER_INTENT", user);
////                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }
}
