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
    private String quantity;
}
