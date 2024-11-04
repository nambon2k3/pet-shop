package com.example.petshopapplication.model;


import com.google.firebase.database.PropertyName;

import java.io.Serializable;
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
public class Product  implements Serializable {
    private String id;
    private String categoryId;
    private String name;
    private String description;
    private int discount;
    private String baseImageURL;
    private double basePrice;
    private List<Variant> listVariant;
    private String createdAt;;
    @PropertyName("isDeleted")

    private boolean isDeleted;
    @PropertyName("isDeleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    @PropertyName("isDeleted")
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
