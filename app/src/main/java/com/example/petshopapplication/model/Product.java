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
public class Product {
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private String status;
    private boolean isDeleted;
    private String createdAt;
    private int createdBy;
}
