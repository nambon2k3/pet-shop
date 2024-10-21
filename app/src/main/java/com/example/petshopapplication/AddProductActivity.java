package com.example.petshopapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

//        Spinner spinner = findViewById(R.id.spinner); // Make sure the ID matches your XML
//
//        // Step 2: Define your categories
//        final String[] categories = {"Category 1", "Category 2", "Category 3"}; // Replace with your actual categories
//
//        // Step 3: Create the ArrayAdapter using the custom layout
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                // Inflate the custom layout if it hasn't been already
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
//                }
//
//                // Set the text for the spinner item
//                TextView textView = convertView.findViewById(R.id.spinner_text);
//                textView.setText(categories[position]);
//
//                return convertView;
//            }
//
//            @Override
//            public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                return getView(position, convertView, parent);
//            }
//        };
//
//        // Step 4: Set the adapter for the Spinner
//        spinner.setAdapter(adapter);
    }
}
