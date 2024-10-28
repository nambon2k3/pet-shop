package com.example.petshopapplication.model;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {
    private String cartId;
    private String userId;
    private String productId;
    private int quatity;
    private String selectedColorId;
    private String selectedSizeId;
}
