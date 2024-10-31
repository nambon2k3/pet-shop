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
    private int quantity;
    private String selectedVariantId;
    private String selectedColorId;
    private boolean isChecked;

}
