package com.example.recipesapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.recipesapp.Fragments.HomeFragment;
import com.example.recipesapp.Fragments.UploadRecipeFragment;
import com.example.recipesapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment fragment;
    private Toolbar toolbar;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.mainLayout);
        fragment = new HomeFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.mainLayout, fragment, "0").commit();
/*
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.mainLayout);
        fragment = new HomeFragment();
        transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.mainLayout, fragment, "0").commit();
        */


//        if (fragment == null) {
//            fragment = new MainFragment();
//           transaction = fragmentManager.beginTransaction();
//
//            transaction.add(R.id.mainLayout, fragment, "0").commit();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //checkUserStatus();
    }




    private void checkUserStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        Boolean counter = sharedPreferences.getBoolean("loginCounter",
                Boolean.valueOf(String.valueOf(MODE_PRIVATE)));
        String email = sharedPreferences.getString("userEmail", String.valueOf(MODE_PRIVATE));
        if(counter){
            fragmentManager = getSupportFragmentManager();
            fragment = fragmentManager.findFragmentById(R.id.mainLayout);
            fragment = new HomeFragment();
            transaction = fragmentManager.beginTransaction();

            transaction.add(R.id.mainLayout, fragment, "0").commit();

        }

    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu, menu);
//        return true;
//    }

    public void signOut() {
        mAuth.signOut();
        SharedPreferences preferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        finish();

}
//        mAuth.signOut();
//        SharedPreferences sharedPreferences = getSharedPreferences("",MODE_PRIVATE);
//        sharedPreferences.edit().clear().commit();
//        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
//        finish();
//    }
}