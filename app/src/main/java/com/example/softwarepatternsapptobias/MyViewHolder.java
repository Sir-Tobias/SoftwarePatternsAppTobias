package com.example.softwarepatternsapptobias;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView viewOne;
    TextView viewTwo;
    TextView viewThree;
    TextView viewFour;
    ImageView imageView;
    //CardView cardView;
    View view;

    public MyViewHolder(View itemView) {
        super(itemView);

        viewOne = itemView.findViewById(R.id.contentOne);
        viewTwo = itemView.findViewById(R.id.contentTwo);
        viewThree = itemView.findViewById(R.id.contentThree);
        viewFour = itemView.findViewById(R.id.contentFour);
        imageView = itemView.findViewById(R.id.image_single_view);
        //cardView=cardView.findViewById(R.id.menupopbutton);

        view = itemView;
    }
}
