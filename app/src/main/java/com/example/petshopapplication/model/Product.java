package com.example.petshopapplication.model;


import java.util.Date;
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
public class Product {
    private String id;
    private Category category;
    private String name;
    private String description;
    private int discount;
    private List<Variant> listVariant;
    private boolean isDeleted;
    private String createdAt;
}
