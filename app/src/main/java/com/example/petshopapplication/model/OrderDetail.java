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
public class OrderDetail {
    private String productId; // ID của sản phẩm
    private String variantId; // ID variant đã chọn
    private String colorId;   // ID color đã chọn
    private int quantity;     // Số lượng sản phẩm
    private double purchased;     // Giá cho sản phẩm đã chọn

}
