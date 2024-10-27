package com.example.petshopapplication.model;

import java.io.Serializable;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart implements Serializable {
    private String cartId;
    private String userId;
    private String productId;
    private String quatity; // Sửa từ 'quatity' thành 'quantity'
    private String selectedColorId;
    private String selectedSizeId;
}
