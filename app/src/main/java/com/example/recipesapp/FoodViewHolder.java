package com.example.recipesapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

class FoodViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView mTitle, mIngredients,mDirections, mDescription, mDifficulty;
    CardView mCardView;
    ImageButton fav_btn;
//    DatabaseReference favRef;
//    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public FoodViewHolder( View itemView){
        super( itemView);

        imageView = itemView.findViewById(R.id.ivImage);
        mTitle = itemView.findViewById(R.id.tvTitle);
        mDescription = itemView.findViewById(R.id.tvDescription);
        mDifficulty = itemView.findViewById(R.id.tvDifficulty);
        mCardView = itemView.findViewById(R.id.myCardView);
        fav_btn = itemView.findViewById(R.id.buttonFav);

    }
}
