package com.example.recipesapp.Fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.recipesapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsFragment extends Fragment {

    TextView foodTitle, foodDescription, foodIngredients, foodDirections;
    ImageView foodImage;
    Button updateBtn, deleteBtn;
    private String key = "";
    private String imageUrl = "";
    private String recipeTitle, recipeDescription, recipeIngredients, recipeDirections;

    public static DetailsFragment newInstance(String title, String ingredients, String description,
                                              String directions, String keyValue, String imgUrl){
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
       // Log.d("10",title);
        bundle.putString("Ingredients", ingredients);
        bundle.putString("Description", description);
        bundle.putString("Directions", directions);
        bundle.putString("Image",imgUrl);
        bundle.putString("keyValue", keyValue);
        detailsFragment.setArguments(bundle);

        return detailsFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        getActivity().setTitle("Recipe Details");
        setHasOptionsMenu(true);
        //setContentView(R.layout.fragment_details);

        foodTitle = (TextView)view.findViewById(R.id.txtTitle);
        foodDescription = (TextView)view.findViewById(R.id.txtDescription);
        foodIngredients= (TextView)view.findViewById(R.id.txtIngredients);
        foodDirections= (TextView)view.findViewById(R.id.txtDirections);
        foodImage = (ImageView)view.findViewById(R.id.ivImage2);
        updateBtn = (Button)view.findViewById(R.id.buttonUpdate);
        deleteBtn = (Button)view.findViewById(R.id.buttonDelete);

        recipeTitle = getArguments().getString("Title");
        Log.d("10", recipeTitle);
        recipeDescription = getArguments().getString("Description");
        recipeIngredients = getArguments().getString("Ingredients");
        recipeDirections = getArguments().getString("Directions");
        imageUrl = getArguments().getString("Image");
        key = getArguments().getString("keyValue");

        foodTitle.setText(recipeTitle);
        foodDescription.setText(recipeDescription);
        foodDirections.setText(recipeDirections);
        foodIngredients.setText(recipeIngredients);

        Glide.with(this).load(imageUrl).into(foodImage);


        recipeTitle = foodTitle.getText().toString(); //
        Log.d("1000", recipeTitle);//

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipe();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe();
            }
        });

        /*Bundle mBundle = getIntent().getExtras();
        if(mBundle!= null){

            foodTitle.setText(mBundle.getString("Title"));
            foodDescription.setText(mBundle.getString("Description"));
            foodIngredients.setText(mBundle.getString("Ingredients"));
            foodDirections.setText(mBundle.getString("Directions"));
            key = mBundle.getString("keyValue");
            imageUrl = mBundle.getString("Image");
            //foodImage.setImageResource(mBundle.getInt("Image"));

            Glide.with(this).load(mBundle.getString("Image")).into(foodImage);
        }*/

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.serachBarBtn);
        item.setVisible(false);
        item =menu.findItem(R.id.favouriteshBarBtn);
        item.setVisible(false);
        item =menu.findItem(R.id.signOutBarBtn);
        item.setVisible(false);
        item =menu.findItem(R.id.deleteBarBtn);
        item.setVisible(false);
        item =menu.findItem(R.id.updateBarBtn);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.updateBarBtn : {
//
//                Log.d("btn update ",foodTitle.getText().toString());
//                updateRecipe();
//                return true;
//            }
//
//            case R.id.deleteBarBtn : {
//
//                deleteRecipe();
//                return true;
//            }

            case R.id.backBarBtn : {

                getActivity().getFragmentManager().popBackStack();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.mainLayout, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                return true;

            }


        }
        return super.onOptionsItemSelected(item);
    }


    public void updateRecipe(){
//        recipeTitle = foodTitle.getText().toString();
//        recipeIngredients = foodIngredients.getText().toString();
//        recipeDescription = foodDescription.getText().toString();
//        recipeDirections = foodDirections.getText().toString();

        Log.d("title UPDATE REC", foodTitle.getText().toString());

        UpdateRecipeFragment updateRecipeFragment = UpdateRecipeFragment.newInstance(recipeTitle,recipeIngredients,
                recipeDescription, recipeDirections, key, imageUrl);
       // UpdateRecipeFragment updateRecipeFragment = UpdateRecipeFragment.newInstance(title,ingredients,
          //      description, directions, mkey, urlImage);
//        recipeTitle ="";
//        recipeDescription = "";
//        recipeDirections = "";
//        recipeIngredients ="";  key = "" ; imageUrl = "";
        getActivity().getFragmentManager().popBackStack();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.mainLayout, updateRecipeFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void deleteRecipe(){

        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Delete")
                .setMessage("Are you sure you'd like to delete this recipe permanently?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipe");
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                reference.child(key).removeValue();
                                Toast.makeText(getActivity(), "Recipe Deleted", Toast.LENGTH_SHORT).show();
                                getActivity().getFragmentManager().popBackStack();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.add(R.id.mainLayout, new HomeFragment());
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }
                        });
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }
/*
    public void buttonDeleteRecipe(View view) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipe");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                reference.child(key).removeValue();
                Toast.makeText(DetailsFragment.this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainFragment.class));
                finish();
            }
        });

    }

    public void buttonUpdateRecipe(View view) {
        startActivity(new Intent(getApplicationContext(), UpdateRecipeActivity.class)
                .putExtra("recipeNameKey", foodTitle.getText().toString())
        .putExtra("descriptionKey", foodDescription.getText().toString())
        .putExtra("ingredientsKey", foodIngredients.getText().toString())
        .putExtra("directionsKey", foodDirections.getText().toString())
        .putExtra("oldImageUrl", imageUrl).putExtra("key", key));
    }
*/
}