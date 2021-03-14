package com.example.recipesapp.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.recipesapp.Activities.LogInActivity;
import com.example.recipesapp.Activities.MainActivity;
import com.example.recipesapp.Classes.FoodData;
import com.example.recipesapp.MyRecipeAdapter;
import com.example.recipesapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView mRecyclerView;
    List<FoodData> myFoodList, faveFoodList;
    FoodData mFoodData;
    MyRecipeAdapter myAdapter;
    DatabaseReference databaseReference, favRef, usersRef;
    ProgressDialog progressDialog;
    EditText txtSearch;
    FloatingActionButton uploadRecipeBtn;
    Toolbar toolbar;
    boolean searchPressed = true;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);
        //toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Log.d("4","on create Main frag");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        String userName = FirebaseDatabase.getInstance().getReference("User").child(currentUserId).
                child("username").getKey();
        //getActivity().setTitle("Hi " + userName );

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        myFoodList = new ArrayList<>();
        myAdapter = new MyRecipeAdapter(getContext(), myFoodList);
        mRecyclerView.setAdapter(myAdapter);

        txtSearch = (EditText) view.findViewById(R.id.txtSearch);
        uploadRecipeBtn = (FloatingActionButton) view.findViewById(R.id.uploadBtn);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Items....");

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("User");
        String id = mAuth.getCurrentUser().getUid();
        DatabaseReference username = usersRef.child(id).child("username");
        username.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue().toString();
                getActivity().setTitle("Hi " + username );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");
        favRef = FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("Favourites");

        progressDialog.show();

        ValueEventListener eventListenerReg = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myFoodList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    FoodData foodData = itemSnapshot.getValue(FoodData.class);
                    foodData.setKey(itemSnapshot.getKey());
                    myFoodList.add(foodData);
                }

                myAdapter.notifyDataSetChanged();
                Log.d("1","main frag on datachange");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();

            }
        });
        myAdapter.notifyDataSetChanged();

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });

        uploadRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity1 mainActivity1 = (MainActivity1) getActivity();
                //mainActivity1.loadUploadFrag();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.mainLayout, new UploadRecipeFragment(), "1");
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.backBarBtn);
        item.setVisible(false);
        item = menu.findItem(R.id.updateBarBtn);
        item.setVisible(false);
        item = menu.findItem(R.id.deleteBarBtn);
        item.setVisible(false);
        item = menu.findItem(R.id.serachBarBtn);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.serachBarBtn : {
//                if(searchPressed){
//                    txtSearch.setVisibility(View.VISIBLE);
//                    Log.d("search home","searchPressed open");
//                    searchPressed = false;
//                }
//                else {
//                    txtSearch.setVisibility(View.GONE);
//                    Log.d("search home","searchPressed close");
//                    searchPressed = true;
//                }
//                return true;
//            }

            case R.id.favouriteshBarBtn : {
                getActivity().getFragmentManager().popBackStack();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.mainLayout, new FavouritesFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
            case R.id.signOutBarBtn : {

                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.signOut();
                startActivity(new Intent(getActivity(), LogInActivity.class));
            }
        }
        return false;
    }

   /* @Override
    public void onPause() {
        super.onPause();
        Log.d("3","onPause");
        myAdapter = null;
        mRecyclerView.setAdapter(myAdapter);
        //mRecyclerView = null;
    }*/
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainFragment.this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        txtSearch = (EditText)findViewById(R.id.txtSearch);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Items....");

        myFoodList = new ArrayList<>();
        faveFoodList = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); /////
        String currentUserId = user.getUid(); //////

        myAdapter = new MyRecipeAdapter(MainFragment.this, myFoodList);
        mRecyclerView.setAdapter(myAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");
        favRef = FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("Favourites");

        progressDialog.show();

        ValueEventListener eventListenerReg = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myFoodList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    FoodData foodData = itemSnapshot.getValue(FoodData.class);
                    foodData.setKey(itemSnapshot.getKey());
                    myFoodList.add(foodData);
                }

                myAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();

            }
        });

        ValueEventListener eventListenerFave = favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });
    }*/

    private void filter(String text) {

        ArrayList<FoodData> filterList = new ArrayList<>();
        for(FoodData item: myFoodList){
            if(item.getItemName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getItemIngredients().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }
        myAdapter.filteredList(filterList);
    }

    /*
    public void buttonUploadActivity(View view) {
        startActivity(new Intent(this, UploadRecipe.class));
    }
    */

}