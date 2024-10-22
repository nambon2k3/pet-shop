package com.example.petshopapplication.model;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDetail {
    private String productDetailId;
    private String productId;
    private double price;
    private String imageUrl;
    private int stock;
    private int discount;
    private double importPrice;
    private int deliveringQuantity;
    private boolean isDeleted;
    private int createdBy;
    private String createdAt;
    private int height;
    private int weight;
}
