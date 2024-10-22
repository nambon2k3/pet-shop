package com.example.petshopapplication.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
    private String productId;
    private String productName;
    private String productDescription;
    private String imageUrl;
    private double price;
    private int stock;
    private int quantity;
    private boolean isDeleted;
}
