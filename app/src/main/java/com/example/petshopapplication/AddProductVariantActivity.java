package com.example.petshopapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.ColorAdapter;
import com.example.petshopapplication.Adapter.SizeAdapter;
import com.example.petshopapplication.databinding.ActivityAddProductVariantBinding;
import com.example.petshopapplication.databinding.ActivityHomeBinding;
import com.example.petshopapplication.databinding.PopUpAddVariant1Binding;
import com.example.petshopapplication.model.Category;
import com.example.petshopapplication.model.Color;
import com.example.petshopapplication.model.ObjectPrinter;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Size;
import com.example.petshopapplication.model.Variant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;


public class AddProductVariantActivity extends AppCompatActivity implements  SizeAdapter.OnSizeClickEventListener,ColorAdapter.OnColorClickEventListener
{

    Button btnShowAddSize;
    ActivityAddProductVariantBinding binding;
    PopUpAddVariant1Binding binding2;
    FirebaseDatabase database;
    DatabaseReference reference;
    Dialog dialog;
    Dialog dialog2;
    List<Variant> variants = new ArrayList<>();
    String productName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product_variant);

        binding = ActivityAddProductVariantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        database = FirebaseDatabase.getInstance();

        Product model = (Product) getIntent().getSerializableExtra("product");
        if(model == null)
        {
        //    finish();
        }
//        binding.addPvName.setText(model.getName().toString());
        binding.addPvButtonAddSizeColor.setOnClickListener(view -> showAddSize());


    }

    private void showAddSize(){
        dialog = new Dialog(AddProductVariantActivity.this);
        dialog.setContentView(R.layout.pop_up_add_variant1);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

// Create binding BEFORE showing the dialog
        binding2 = PopUpAddVariant1Binding.inflate(getLayoutInflater());
        dialog.setContentView(binding2.getRoot());

        dialog.show();
        initSize(dialog);
        initColor(dialog);
        binding2.btnSubmit.setOnClickListener(vieww->
        {
            if(currentColor == null || currentSize == null)
                Toast.makeText(AddProductVariantActivity.this, "Failed to submit.", Toast.LENGTH_SHORT).show();
            List<Variant> found = variants.stream().filter(x->x.getSize().getName().equals("")).collect(Collectors.toList());

            if(found.size() == 0)
            {
                Variant newva = new Variant();
                newva.setSize(currentSize);
                newva.setDeleted(false);
                newva.setDimension(null);
                newva.setStock(Integer.parseInt(binding2.editTextText.getText().toString()));
                currentColor.setStock(Integer.parseInt(binding2.editTextText.getText().toString()));

                newva.setListColor(List.of(currentColor));
                newva.setPrice(0);
                newva.setDeliveringQuantity(0);
                variants.add(newva);
            }
            else
            {
                Variant updateVar = found.get(0);
                List<Color> colors = updateVar.getListColor();

                colors.add(currentColor);
                updateVar.setListColor(colors);
                currentColor.setStock(Integer.parseInt(binding2.editTextText.getText().toString()));
                updateVar.setStock(updateVar.getStock() + currentColor.getStock());
                int index = variants.indexOf(updateVar);
                if (index != -1) {
                    variants.set(index, updateVar);
                }
            }

        });
        binding2.btnAddSize.setOnClickListener(view -> {
            dialog2 = new Dialog(AddProductVariantActivity.this);
            dialog2.setContentView(R.layout.pop_up_add_variant2);
            dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog2.show();
            Button btnConfirm = dialog2.findViewById(R.id.btn_submit_name);
            btnConfirm.setOnClickListener(view2->
            {
                EditText txtName = dialog2.findViewById(R.id.edit_size);
                if(!txtName.getText().equals("")) {
                    DatabaseReference ref = database.getReference("Size");

                    String id = "size-" + ref.push().getKey(); // Generate a unique ID
                    Size size = new Size();
                    size.setId(id);
                    size.setName(txtName.getText().toString());
                    ref.child(id).setValue(size)
                            .addOnSuccessListener(aVoid ->
                                    {
                                        dialog2.dismiss();
                                        initSize(dialog);

                                        Toast.makeText(AddProductVariantActivity.this, "Size submitted successfully!", Toast.LENGTH_SHORT).show();

                                    })
                            .addOnFailureListener(e ->
                                    {
                                        dialog2.dismiss();
                                        initSize(dialog);

                                        Toast.makeText(AddProductVariantActivity.this, "Failed to submit size.", Toast.LENGTH_SHORT).show();
                                    });

                }
                else
                {
                    dialog2.dismiss();
                    initSize(dialog);
                }
            });

        });
    }
    RecyclerView colorCartRecyclerView, sizeCartRecyclerView;

    private void initSize(Dialog dialog)
    {
        List<Size> sizeItems = new ArrayList<>();
        reference =  database.getReference("Size");
        Query query = reference.orderByChild("id");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        sizeItems.add(dataSnapshot.getValue(Size.class));
                        System.out.println(sizeItems.size());
                    }
                    SizeAdapter sizeAdapter = new SizeAdapter(sizeItems, AddProductVariantActivity.this);
                    sizeCartRecyclerView = dialog.findViewById(R.id.rcv_size);
                    sizeCartRecyclerView.setLayoutManager(new GridLayoutManager(AddProductVariantActivity.this, 2));
                    sizeCartRecyclerView.setAdapter(sizeAdapter);

            }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    Size currentSize = null;
    @Override
    public void onSizeClickEvent(Size size) {
        currentSize = size;
    }

    private void initColor(Dialog dialog)
    {
        List<Color> colorItems = new ArrayList<>();
        Color color = new Color();
        color.setId("1");
        color.setName("Tuna and Salmon");
        color.setImageUrl("https://firebasestorage.googleapis.com/v0/b/pet-shop-4a349.appspot.com/o/per-food-4.png?alt=media&token=2672b45a-80c6-4011-b28a-6fbd97806498");
        colorItems.add(color);
        color.setId("2");
        color.setName("Tuna and Salmon2");
        color.setImageUrl("https://firebasestorage.googleapis.com/v0/b/pet-shop-4a349.appspot.com/o/per-food-4.png?alt=media&token=2672b45a-80c6-4011-b28a-6fbd97806498");
        colorItems.add(color);

        ColorAdapter colorAdapter = new ColorAdapter(colorItems,AddProductVariantActivity.this);
        colorCartRecyclerView = dialog.findViewById(R.id.rcv_color);
        colorCartRecyclerView.setLayoutManager(new GridLayoutManager(AddProductVariantActivity.this, 1));
        colorCartRecyclerView.setAdapter(colorAdapter);
    }
    private Color currentColor = null;
    @Override
    public void onColorClick(Color color) {
        currentColor = color;
        if(currentSize !=null)
        {
            List<Variant> variantss = variants.stream().filter(x->x.getSize().getName().equals(currentSize.getName())).collect(Collectors.toList());
            if(!variantss.isEmpty()) {
                List<Color> colors = variantss.get(0).getListColor();
                if(colors.stream().filter(x->x.getId() == currentColor.getId()).count() > 0)
                binding2.editTextText.setText(colors.stream().filter(x->x.getId() == currentColor.getId()).findFirst().get().getStock());
            }
            }
    }
}