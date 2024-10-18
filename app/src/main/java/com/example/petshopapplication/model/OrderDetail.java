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
public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int productDetailId;
    private double price;
    private int quantity;
    private Date createdAt;
}
