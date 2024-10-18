package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import lombok.NonNull;

public class LoginActivity extends AppCompatActivity {

    EditText edt_username, edt_password;
    Button btn_login;
    TextView tv_registerRedirect;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //Binding views
        edt_username = findViewById(R.id.edt_login_username);
        edt_password = findViewById(R.id.edt_login_password);
        btn_login = findViewById(R.id.btn_login);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");


        //Handling login button click
        btn_login.setOnClickListener(v -> {
            //CONTENT: Implement login logic
            checkUser();

        });

        //Handling register link click
        tv_registerRedirect = findViewById(R.id.linkRegister);
        tv_registerRedirect.setOnClickListener(v -> {
            // CONTENT: Implement register redirect logic
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }


    public void checkUser() {
        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();

        Query checkUserExisted = reference.orderByChild("username").equalTo("username");

        checkUserExisted.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //User exists, continue with login
                    //CONTENT: Implement login logic
                    edt_username.setError(null);
                    String passwordFromDatabase = dataSnapshot.child(username).child("password").getValue(String.class);
                    if (password.equals(passwordFromDatabase)) {
                        edt_username.setError(null);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        edt_password.setError("Invalid Credentials");
                        edt_password.requestFocus();
                    }


                } else {
                    //User does not exist, notify user and ask to register
                    //CONTENT: Implement notify user and ask to register logic
                    edt_username.setError("User does not exist");
                    edt_password.requestFocus();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }

        });


    }


}