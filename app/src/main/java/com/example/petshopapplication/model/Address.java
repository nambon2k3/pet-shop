package com.example.petshopapplication.model;

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
public class Address {
    private String id;
    private String fullName;
    private String city;
    private String district;
    private String houseNumber;
    private boolean isDefault;
    private String userId;
}
