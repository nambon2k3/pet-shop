package com.example.petshopapplication.model;

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
public class Order {
    private String id;
    private String userId;
    private String shipmentId;  // ID từ đơn vận chuyển
    private String rateId;  // mã vc
    private String carrierLogo;
    private String carrierName;
    private String districtId;   // ID quận
    private String cityId;      // ID thành phố
    private String wardId;   // ID quận
    private double totalAmount; // Tổng số tiền của đơn hàng
    private List<OrderDetail> orderDetails; // Chi tiết các sản phẩm trong đơn hàng
    private Date orderDate;
    private String paymentId; // Thông tin thanh toán
    private String status;
    private boolean isShipmentCreated = false;

}
