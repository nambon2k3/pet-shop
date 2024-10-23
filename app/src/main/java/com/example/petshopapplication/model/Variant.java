package com.example.petshopapplication.model;


import java.util.List;

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
public class Variant {
    private String id;
    private Size size;
    private List<Color> listColor;
    private Dimension dimension;
    private double price;
    private int stock;
    private int deliveringQuantity;
    private double importPrice;
}
