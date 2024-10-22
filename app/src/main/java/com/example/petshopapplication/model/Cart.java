package com.example.petshopapplication.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
    private String productId;        // ID sản phẩm
    private String productName;      // Tên sản phẩm
    private String productDescription; // Mô tả sản phẩm
    private String imageUrl;         // URL hình ảnh
    private double price;            // Giá sản phẩm
    private int stock;               // Số lượng tồn kho
    private int quantity;            // Số lượng sản phẩm trong giỏ
    private boolean isDeleted;       // Trạng thái đã xóa
}
