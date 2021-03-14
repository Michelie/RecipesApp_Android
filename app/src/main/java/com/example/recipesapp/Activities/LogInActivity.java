package com.example.recipesapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipesapp.Fragments.HomeFragment;
import com.example.recipesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    private String email, password;
    private FirebaseAuth firebaseAuth;
    Toolbar toolbarLog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor loginPrefsEditor;

    private Boolean saveLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        toolbarLog = findViewById(R.id.myToolbar);
        toolbarLog.setTitle("Hello!");

        txtEmail = (EditText)findViewById(R.id.text_email);
        txtPassword = (EditText)findViewById(R.id.text_password);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences =getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = sharedPreferences.edit();
        saveLogin = sharedPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            txtEmail.setText(sharedPreferences.getString("email", ""));
            txtPassword.setText(sharedPreferences.getString("password", ""));
            buttonLoginFunction(findViewById(R.id.logInButton));
        }
//        if(sharedPreferences.getString("keyEmail",null)!= null)
//        {
//            txtEmail.setText(sharedPreferences.getString("keyEmail", null));
//            txtPassword.setText(sharedPreferences.getString("keyPass", null));
//            buttonLoginFunction(findViewById(R.id.logInButton));
//
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void buttonRegFunction(View view) {
        //new users
        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }

    public void buttonLoginFunction(View view) {
        //Existing users
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            automaticSignIn();
                            //saveData(email);
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            //Toast.makeText(LogInActivity.this, "Logged in", Toast.LENGTH_SHORT).show();


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LogInActivity.this, "Login failed or user not available", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
//
//    private void saveData(String email) {
//        loginPrefsEditor.putBoolean("saveLogin", true);
//        loginPrefsEditor.putString("email", email);
//        loginPrefsEditor.putString("password", password);
//        loginPrefsEditor.commit();
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        Toast.makeText(LogInActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
//
//    }


    private void automaticSignIn(){
        loginPrefsEditor.putBoolean("saveLogin", true);
        loginPrefsEditor.putString("email", email);
        loginPrefsEditor.putString("password", password);
        loginPrefsEditor.commit();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        Toast.makeText(LogInActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
    }
}