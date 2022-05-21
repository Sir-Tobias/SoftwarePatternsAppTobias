package com.example.softwarepatternsapptobias;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsapptobias.EditProductActivity;
import com.example.softwarepatternsapptobias.MyOrdersHistory;
import com.example.softwarepatternsapptobias.ProductDetailsActivity;
import com.example.softwarepatternsapptobias.UpdateStockActivity;
import com.example.softwarepatternsapptobias.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainProductsAdapter extends RecyclerView.Adapter<MainProductsAdapter.MyViewHolder> {
    private ArrayList<Product> mylistvalues;
    private DatabaseReference userDB;
    private StorageReference storageReference;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView priceView, titleView;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.productNameFavourite);
            priceView = itemView.findViewById(R.id.productPriceFavourite);
            imageView = itemView.findViewById(R.id.imageViewFavourite);
        }
    }

    public MainProductsAdapter(ArrayList<Product> myDataset, Context context) {
        mylistvalues = myDataset;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.product_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final Product product = mylistvalues.get(position);
        holder.titleView.setText(product.getTitle());
        holder.priceView.setText("€" + product.getPrice());

        GlideApp.with(context)
                .load(storageReference.child(product.getImages().get(0)))
                .into(holder.imageView);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);

                if(context.getClass() == MyOrdersHistory.class) {
                    intent.putExtra("PRODUCT_INTENT", product);
                    intent.putExtra("DETAILS_INTENT", "BOUGHT");
                    context.startActivity(intent);
                }
                else if(context.getClass() == UpdateStockActivity.class) {
                    intent = new Intent(context, EditProductActivity.class);
                    intent.putExtra("PRODUCT_INTENT", product);
                    context.startActivity(intent);
                }
                else {
                    intent.putExtra("PRODUCT_INTENT", product);
                    intent.putExtra("DETAILS_INTENT", "NOT BOUGHT");
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }

    public void filteredList(ArrayList<Product> products) {
        mylistvalues = products;
        notifyDataSetChanged();
    }
}
