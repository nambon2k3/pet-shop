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
    private String orderDetailId;
    private String orderId;
    private String productDetailId;
    private double price;
    private int quantity;
    private Date createdAt;

    // private String productId;
    // private double purchasedMoney;
    // private String shipment;
    // voucher old
    // thanh toan: private String payment;
}
