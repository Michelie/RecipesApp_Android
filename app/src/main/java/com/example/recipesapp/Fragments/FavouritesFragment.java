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
import android.widget.TextView;

import com.example.recipesapp.Classes.FoodData;
import com.example.recipesapp.MyFavouritesAdapter;
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

public class FavouritesFragment extends Fragment {

    RecyclerView fRecyclerView;
    List<FoodData> faveFoodList;

    FoodData mFoodData;
    MyFavouritesAdapter myFaveAdapter;
    DatabaseReference databaseReference, favRef, fav_ListRef;
    ProgressDialog progressDialog;
    EditText txtSearch2;
    FloatingActionButton uploadRecipeBtn;
    Toolbar toolbar;
    boolean searchPressed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        getActivity().setTitle("Favourite Recipes");
        setHasOptionsMenu(true);

        fRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView2);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        fRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        faveFoodList = new ArrayList<>();
        myFaveAdapter = new MyFavouritesAdapter(getContext(), faveFoodList);
        fRecyclerView.setAdapter(myFaveAdapter);

        txtSearch2 = (EditText) view.findViewById(R.id.txtSearch2);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Items....");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); /////
        String currentUserId = user.getUid(); //////

        //databaseReference = FirebaseDatabase.getInstance().getReference("Recipe");

        favRef = FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("favourites");
        progressDialog.show();

        ValueEventListener eventListenerReg = favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                faveFoodList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    FoodData foodData = itemSnapshot.getValue(FoodData.class);
                    foodData.setKey(itemSnapshot.getKey());
                    faveFoodList.add(foodData);
                    Log.d("1","fave frag inside for datachange");
                }
                myFaveAdapter.notifyDataSetChanged();
                Log.d("1","fave frag on datachange");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();

            }
        });

        txtSearch2.addTextChangedListener(new TextWatcher() {
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

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.favouriteshBarBtn);
        item.setVisible(false);
        item = menu.findItem(R.id.updateBarBtn);
        item.setVisible(false);
        item = menu.findItem(R.id.deleteBarBtn);
        item.setVisible(false);
        item =menu.findItem(R.id.signOutBarBtn);
        item.setVisible(false);
        item = menu.findItem(R.id.serachBarBtn);
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

//            case R.id.serachBarBtn: {
//                if(searchPressed){
//                    txtSearch2.setVisibility(View.GONE);
//                    searchPressed = false;
//                }else{
//                    txtSearch2.setVisibility(View.VISIBLE);
//                    searchPressed = true;
//                }
//
//                return true;
//            }
        }
        return false;
    }



    private void filter(String text) {

        ArrayList<FoodData> filterList = new ArrayList<>();
        for (FoodData item : faveFoodList) {
            if (item.getItemName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getItemIngredients().toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }
        myFaveAdapter.filteredList(filterList);
    }

}