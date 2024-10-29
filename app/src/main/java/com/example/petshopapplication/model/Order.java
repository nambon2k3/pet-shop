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
public class Order {
    private String id;
    private String userId;
    private Date orderDate;
    private double amount;
    private String status;
    private String address_id;

}
