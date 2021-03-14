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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class UploadRecipeFragment extends Fragment {

    ImageView recipeImage;
    Uri fileUri;
    EditText txtName, txtIngredients,txtDirections, txtDescription, txtDifficulty;
    String imageUrl;
    Button selectImageBtn, uploadRecipeBtn;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload_recipe, container, false);
        getActivity().setTitle("Upload New Recipe");
        setHasOptionsMenu(true);
        recipeImage = (ImageView)view.findViewById(R.id.iv_foodImage);
        txtName = (EditText)view.findViewById(R.id.txtRecipeName);
        txtDescription = (EditText)view.findViewById(R.id.txtDescription);
        txtIngredients = (EditText)view.findViewById(R.id.txtIngredients);
        txtDirections = (EditText)view.findViewById(R.id.txtDirections);
        txtDifficulty = (EditText)view.findViewById(R.id.txtDifficulty);
        selectImageBtn = (Button) view.findViewById(R.id.selectImageButton);
        uploadRecipeBtn = (Button) view.findViewById(R.id.uploadRecipeButton);

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });

        uploadRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        return view;
    }

    public void selectImage(){
        Intent photoPick = new Intent(Intent.ACTION_PICK);
        photoPick.setType("image/*");
        startActivityForResult(photoPick, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            fileUri = data.getData();
            Glide.with(getContext()).load(fileUri).into(recipeImage);
        }
        else Toast.makeText(getActivity(), "Oops! You didn't pick an image!", Toast.LENGTH_SHORT).show();
    }

    public String GetFileExtension(Uri mUri) {

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(mUri)) ;

    }

    public void uploadImage(){
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference().child("RecipeImage/");

        progressDialog = new ProgressDialog(getActivity());

        if (fileUri != null) {

            progressDialog.setTitle("Recipe is Uploading...");
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
                                }
                            });



                         /*   String TempImageName = txtName.getText().toString().trim();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            imageUrl = taskSnapshot.getUploadSessionUri().toString(); */


                            // imageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//
                            //uploadRecipe();
//                            uploadinfo imageUploadInfo = new uploadinfo(TempImageName, taskSnapshot.getUploadSessionUri().toString());
//                            String ImageUploadId = databaseReference.push().getKey();
//                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                        }
                    });
        }
        else {

            Toast.makeText(getActivity(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    /*
        public void buttonUploadRecipe(View view) {
            uploadImage();
            //uploadRecipe();
        }
    */
    public void uploadRecipe(){
        //Toast.makeText(UploadRecipe.this, "upload recipe", Toast.LENGTH_SHORT).show();

        FoodData foodData = new FoodData(
                txtName.getText().toString(), txtIngredients.getText().toString(),
                txtDirections.getText().toString(), txtDescription.getText().toString(),
                txtDifficulty.getText().toString(),
                imageUrl);

        String currentDateTime = DateFormat.getDateTimeInstance()
                .format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("Recipe")
                .child(currentDateTime).setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Recipe uploaded successfully!", Toast.LENGTH_SHORT).show();
                    //finish();
                    progressDialog.dismiss();
                    getActivity().getFragmentManager().popBackStack();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.add(R.id.mainLayout, new HomeFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        recipeImage = (ImageView)findViewById(R.id.iv_foodImage);
        txtName = (EditText)findViewById(R.id.txtRecipeName);
        txtDescription = (EditText)findViewById(R.id.txtDescription);
        txtIngredients = (EditText)findViewById(R.id.txtIngredients);
        txtDirections = (EditText)findViewById(R.id.txtDirections);
        txtDifficulty = (EditText)findViewById(R.id.txtDifficulty);
    }
*/
    /*public void buttonSelectImage(View view) {
        //function for selecting image for recipe when clicking Select Image

        Intent photoPick = new Intent(Intent.ACTION_PICK);
        photoPick.setType("image/*");
        startActivityForResult(photoPick, 1);
    }*/

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null && data.getData() != null){
            fileUri = data.getData();
            recipeImage.setImageURI(fileUri);
//            try{
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                recipeImage.setImageBitmap(bitmap);
//            }catch (IOException e) {
//
//                e.printStackTrace();
//            }
            //recipeImage.setImageURI(uri);
        }
        else Toast.makeText(this, "Oops! You didn't pick an image!", Toast.LENGTH_SHORT).show();
    }
*/

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
            case R.id.backBarBtn: {

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

}