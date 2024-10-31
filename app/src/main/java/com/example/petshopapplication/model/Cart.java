package com.example.petshopapplication.model;

import java.io.Serializable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Cart implements Serializable {
    private String cartId;
    private String userId;
    private String productId;
    private String quantity;
    private String selectedColorId;
    private String selectedVariantId;
}
