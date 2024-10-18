package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petshopapplication.model.Account;
import com.example.petshopapplication.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {


    EditText edt_username, edt_password, edt_phone, edt_address, edt_re_password, edt_fullname;
    Button btn_register;
    TextView tv_login;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        //Binding views
        edt_fullname = findViewById(R.id.edt_register_fullname);
        edt_username = findViewById(R.id.edt_register_username);
        edt_phone = findViewById(R.id.edt_register_phone);
        edt_address = findViewById(R.id.edt_register_address);
        edt_password = findViewById(R.id.edt_register_password);
        edt_re_password = findViewById(R.id.edt_register_re_password);

        //Handling registration button click
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(v -> {

            //Initializing firebase
            database = FirebaseDatabase.getInstance();
            reference = database.getReference();

            //Getting the values from EditText fields
            String fullName = edt_fullname.getText().toString();
            String username = edt_username.getText().toString();
            String phone = edt_phone.getText().toString();
            String address = edt_address.getText().toString();
            String password = edt_password.getText().toString();
            String re_password = edt_re_password.getText().toString();

            //Checking if password and re-password match
            if(password.equals(re_password)){

                //Creating a new user
                User user = User.builder()
                            .fullName(fullName)
                            .username(username)
                            .password(password)
                            .phoneNumber(phone)
                            .address(address)
                            .status(true)
                            .roleId(1)
                            .createdAt(new Date())
                            .build();



                //Adding the user to firebase
                reference.child("users").setValue(user);



                //Clearing EditText fields
                edt_fullname.setText("");
                edt_username.setText("");
                edt_phone.setText("");
                edt_address.setText("");
                edt_password.setText("");
                edt_re_password.setText("");

                //Displaying a success message
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                edt_username.setText("");
                edt_phone.setText("");
                edt_address.setText("");
                edt_password.setText("");
                edt_re_password.setText("");

                //Displaying a success message
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            } else {
                //Displaying a message if passwords don't match
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });


        //Handling login link click
        tv_login = findViewById(R.id.linkLogin);
        tv_login.setOnClickListener(v -> {
            //Starting the login activity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}