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
public class Payment {
    private String id;               // ID thanh toán
    private String orderId;          // ID đơn hàng liên quan
    private String paymentMethod;     // Phương thức thanh toán (ví dụ: "COD", "VNPay")
    private double amount;            // Số tiền thanh toán
    private String transactionId;     // ID giao dịch (nếu có cái này cho VNPay)
}
