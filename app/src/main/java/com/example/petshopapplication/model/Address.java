package com.example.petshopapplication.model;

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
public class Address {
    private String id;
    private String fullName;
    private String phone;
    private String city;
    private String district;
    private String houseNumber;
    private boolean isDefault; // Kiểu dữ liệu boolean
    private String userId;
}
