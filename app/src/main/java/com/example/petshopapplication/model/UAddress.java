package com.example.petshopapplication.model;

import java.io.Serializable;

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
public class UAddress implements Serializable {
    private String addressId;
    private String fullName;
    private String phone;
    private String city;         // Tên thành phố
    private String cityId;      // ID thành phố
    private String district;     // Tên quận
    private String districtId;   // ID quận
    private String ward;// Tên phường
    private String wardId;   // ID quận
    private boolean isDefault;   // Địa chỉ mặc định hay không
    private String userId;      // ID người dùng

}
