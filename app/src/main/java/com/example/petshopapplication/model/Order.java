package com.example.petshopapplication.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.text.SimpleDateFormat;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Order implements Serializable {
    private String id;
    private String userId;
    private String shipmentId;
    private String rateId;
    private String carrierLogo;
    private String carrierName;
    private String districtId;
    private String district;
    private String cityId;
    private String city;
    private String wardId;
    private String ward;
    private double totalAmount;
    private List<OrderDetail> orderDetails;
    private Date orderDate;
    private String paymentId;
    private String status;
    private boolean isShipmentCreated = false;

}
