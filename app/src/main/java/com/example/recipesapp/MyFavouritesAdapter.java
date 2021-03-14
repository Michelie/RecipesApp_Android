package com.example.recipesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipesapp.Classes.FoodData;
import com.example.recipesapp.Fragments.DetailsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyFavouritesAdapter extends RecyclerView.Adapter<FoodViewHolder> {

    private Context context;
    private List<FoodData> myFoodList;
    private int lastPosition = -1;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference favRef = database.getInstance().getReference("User");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean favChecker = false;


    public MyFavouritesAdapter(Context context, List<FoodData> myFoodList) {
        this.context = context;
        this.myFoodList = myFoodList;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item, parent, false);
        Log.d("2", "my fave adapter on create view...");
        return new FoodViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder holder, int i) {

        Glide.with(context).load(myFoodList.get(i).getItemImage()).into(holder.imageView);
        holder.mTitle.setText(myFoodList.get(i).getItemName());
        holder.mDescription.setText(myFoodList.get(i).getItemDescription());
        holder.mDifficulty.setText(myFoodList.get(i).getItemDifficulty());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = myFoodList.get(holder.getAdapterPosition()).getItemName();
                String ingredients = myFoodList.get(holder.getAdapterPosition()).getItemIngredients();
                String description = myFoodList.get(holder.getAdapterPosition()).getItemDescription();
                String directions = myFoodList.get(holder.getAdapterPosition()).getItemDirections();
                String keyValue = myFoodList.get(holder.getAdapterPosition()).getKey();
                String imgUrl = myFoodList.get(i).getItemImage();

                DetailsFragment detailsFragment = DetailsFragment.newInstance(title, ingredients,
                        description, directions, keyValue, imgUrl);
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.mainLayout, detailsFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }


        });

        FoodData foodD = myFoodList.get(i);
        String currentUserId = user.getUid();

        isFave(foodD.getKey(), holder.fav_btn, currentUserId);

        holder.fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.fav_btn.getTag().equals("unchecked")) {
                    FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("favourites")
                            .child(foodD.getKey()).setValue(foodD);

                } else {
                    Toast.makeText(context, "Recipe was deleted from your Favourites list", Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("favourites")
                            .child(foodD.getKey()).removeValue();
                }
            }
        });

        setAnimation(holder.itemView, i);
    }

    private void isFave(final String postId, final ImageView imageView, String currentUserId) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(currentUserId)
                .child("favourites");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                    imageView.setTag("checked");
                    //addToHisNotifications(""+hisId,""+postId,"Liked Your Post");


                } else {
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    imageView.setTag("unchecked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setButtonImage(ImageButton im, int position) {
        im.setImageResource(R.drawable.ic_baseline_favorite_24);
        im.setTag("checked");
        //favChecker = false;

    }

    public void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return myFoodList.size();
    }

    public void filteredList(ArrayList<FoodData> filterList) {
        myFoodList = filterList;
        notifyDataSetChanged();
    }
}
