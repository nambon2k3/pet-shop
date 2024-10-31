package com.example.petshopapplication;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import lombok.NonNull;

public class LoginActivity extends AppCompatActivity {

    EditText edt_email, edt_password;
    Button btn_login;
    TextView tv_registerRedirect;

    FirebaseDatabase database;
    DatabaseReference reference;

    //Authentication with firebase
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //Binding views
        edt_email = findViewById(R.id.edt_login_email);
        edt_password = findViewById(R.id.edt_login_password);
        btn_login = findViewById(R.id.btn_login);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(getString(R.string.tbl_user_name));
        firebaseAuth = FirebaseAuth.getInstance();


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
        //Retrieve user input data
        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();

        //Verify user with firebase
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if(task.isSuccessful()) {
                //User is authenticated, go to home activity
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }

        });

    }


}