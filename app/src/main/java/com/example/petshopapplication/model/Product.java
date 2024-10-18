package com.example.petshopapplication.model;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private int id;
    private String name;
    private String description;
    private int categoryId;
    private String status;
    private boolean isDeleted;
    private Date createdAt;
    private int createdBy;
}
