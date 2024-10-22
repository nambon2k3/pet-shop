package com.example.petshopapplication.model;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {
    private int imageUrl;
    private String name;
    private String price;
    private String quatity;
    private boolean isChecked;
}
