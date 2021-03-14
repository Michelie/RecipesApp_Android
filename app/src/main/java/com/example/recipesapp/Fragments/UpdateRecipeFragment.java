package com.example.recipesapp.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.recipesapp.Classes.FoodData;
import com.example.recipesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class UpdateRecipeFragment extends Fragment {

    ImageView recipeImage;
    Uri fileUri;
    EditText txtName, txtIngredients,txtDirections, txtDescription, txtDifficulty;
    Button selectImageBtn, updateRecipeBtn;
    String imageUrl;
    String key, oldImageUrl;
    String recipeName, recipeDifficulty, recipeDirections, recipeIngredients, recipeDescription;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    boolean imgChanged = false;

    //String recipeTitle = "", recipeDescription, recipeIngredients, recipeDirections;

    public static UpdateRecipeFragment newInstance(String title, String ingredients, String description,
                                                   String directions, String keyValue, String imgUrl){

        UpdateRecipeFragment updateRecipeFragment = new UpdateRecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("recipeNameKey", title);
        bundle.putString("ingredientsKey", ingredients);
        bundle.putString("descriptionKey", description);
        bundle.putString("directionsKey", directions);
        bundle.putString("oldImageUrl",imgUrl);
        bundle.putString("key", keyValue);
        updateRecipeFragment.setArguments(bundle);

        return updateRecipeFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_recipe, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle("Update Recipe");
        recipeImage = (ImageView)view.findViewById(R.id.iv_foodImage);
        txtName = (EditText)view.findViewById(R.id.txtRecipeTitle);
        txtDescription = (EditText)view.findViewById(R.id.txtDescription);
        txtIngredients = (EditText)view.findViewById(R.id.txtIngredients);
        txtDirections = (EditText)view.findViewById(R.id.txtDirections);
        txtDifficulty = (EditText)view.findViewById(R.id.txtDifficulty);
        selectImageBtn = (Button)view.findViewById(R.id.selectImageButton);
        updateRecipeBtn = (Button)view.findViewById(R.id.updateRecipeButton);

        recipeName = getArguments().getString("recipeNameKey");
        recipeDescription = getArguments().getString("descriptionKey");
        recipeIngredients = getArguments().getString("ingredientsKey");
        recipeDirections = getArguments().getString("directionsKey");
        imageUrl = getArguments().getString("oldImageUrl");
        key = getArguments().getString("key");

        Log.d("title 222222 update", recipeName);
        txtName.setText(recipeName);
        txtDescription.setText(recipeDescription);
        txtDirections.setText(recipeDirections);
        txtIngredients.setText(recipeIngredients);
        Glide.with(getContext()).load(imageUrl).into(recipeImage);
/*
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Glide.with(UpdateRecipeFragment.this).load(bundle.getString("oldImageUrl")).into(recipeImage);
            txtName.setText(bundle.getString("recipeNameKey"));
            txtDescription.setText((bundle.getString("descriptionKey")));
            txtIngredients.setText((bundle.getString("ingredientsKey")));
            txtDirections.setText((bundle.getString("directionsKey")));
            key = bundle.getString("key");
            oldImageUrl = bundle.getString("oldImageUrl");
        }*/

        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe").child(key);
        storageReference = FirebaseStorage.getInstance().getReference().child("RecipeImage/");

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        updateRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipe();
            }
        });


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
        item =menu.findItem(R.id.deleteBarBtn);
        item.setVisible(false);
        item =menu.findItem(R.id.updateBarBtn);
        item.setVisible(false);
        item =menu.findItem(R.id.signOutBarBtn);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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


    public void selectImage() {
        Intent photoPick = new Intent(Intent.ACTION_PICK);
        photoPick.setType("image/*");
        startActivityForResult(photoPick, 1);
    }

    /*public void buttonSelectImage(View view) {
        Intent photoPick = new Intent(Intent.ACTION_PICK);
        photoPick.setType("image/*");
        startActivityForResult(photoPick, 1);

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null && data.getData() != null){
            fileUri = data.getData();
            recipeImage.setImageURI(fileUri);
            imgChanged = true;
        }
        else if(resultCode != RESULT_OK) {
            imgChanged = false;
            Toast.makeText(getActivity(), "Image not changed!", Toast.LENGTH_SHORT).show();
        }

    }


    public  void updateRecipe(){
        recipeName = txtName.getText().toString().trim();
        recipeDescription = txtDescription.getText().toString().trim();
        recipeIngredients = txtIngredients.getText().toString().trim();
        recipeDirections = txtDirections.getText().toString().trim();
        recipeDifficulty = txtDifficulty.getText().toString(); //trim()

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Recipe is Updating...");

        if (imgChanged && fileUri != null) {

            progressDialog.show();
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(fileUri));
            storageReference2.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    uploadRecipe();
                                    Toast.makeText(getActivity(), "Image uploaded ok", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    });
        }
        else {
            //image not updated
            uploadRecipe();

        }

    }
    /*
    public void buttonUpdateRecipe(View view) {

        recipeName = txtName.getText().toString().trim();
        recipeDescription = txtDescription.getText().toString().trim();
        recipeIngredients = txtIngredients.getText().toString().trim();
        recipeDirections = txtDirections.getText().toString().trim();
        recipeDifficulty = txtDifficulty.getText().toString(); //trim()

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        if (fileUri != null) {

            progressDialog.setTitle("Recipe is Updating...");
            progressDialog.show();
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(fileUri));
            storageReference2.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    uploadRecipe();
                                    Toast.makeText(UpdateRecipeFragment.this, "Image uploaded ok", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
        }
        else {

            Toast.makeText(UpdateRecipeFragment.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }*/

    public String GetFileExtension(Uri mUri) {

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(mUri)) ;

    }

    public void uploadRecipe(){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Recipe is Updating...");
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 1000);

        FoodData foodData = new FoodData(
                recipeName, recipeIngredients, recipeDirections, recipeDescription,
                recipeDifficulty, imageUrl);


        databaseReference.setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(imgChanged) {
                    StorageReference storageReference2 = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                    storageReference2.delete(); // deletes old image
                }

                //progressDialog.dismiss();
                Toast.makeText(getActivity(), "Data Updated", Toast.LENGTH_SHORT).show();
                getActivity().getFragmentManager().popBackStack();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.mainLayout, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        /*String currentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("Recipe")
                .child(currentDateTime).setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UpdateRecipeActivity.this, "Recipe uploaded successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateRecipeActivity.this, "Failed " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/


    }
}