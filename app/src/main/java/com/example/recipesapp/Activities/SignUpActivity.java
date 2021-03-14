package com.example.recipesapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipesapp.Classes.User;
import com.example.recipesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword, txtConfirmPassword, txtFullName, txtUsername;
    Button btnRegister;
    private FirebaseAuth firebaseAuth;
    private String email, password, confirmPassword;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private Toolbar toolbarSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        toolbarSign = findViewById(R.id.myToolbar);
        toolbarSign.setTitle("Registration");
        //getSupportActionBar().setTitle("Registration Form");

        txtEmail = (EditText)findViewById(R.id.txt_Email);
        txtPassword = (EditText)findViewById(R.id.txt_Password);
        txtConfirmPassword = (EditText)findViewById(R.id.txt_ConfirmPassword);
        txtFullName = (EditText)findViewById(R.id.txt_FullName);
        txtUsername = (EditText)findViewById(R.id.txt_Username);
        btnRegister = (Button)findViewById(R.id.button_Register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

    }

    public void buttonRegister(View view) {

        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
        confirmPassword = txtConfirmPassword.getText().toString().trim();
        String fullName = txtFullName.getText().toString();
        String username = txtUsername.getText().toString();

        if(password.length()<6){
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.equals(confirmPassword)){

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                User user = new User(fullName, username, email);
                                //can be written in parts - (like calculator app)
                                FirebaseDatabase.getInstance().getReference("User")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SignUpActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                                    }
                                });

                            } else {

                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }
}