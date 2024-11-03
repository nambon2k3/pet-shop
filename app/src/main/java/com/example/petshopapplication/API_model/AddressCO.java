package com.example.petshopapplication.API_model;

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
public class AddressCO {
    private String name;
    private String phone;
    private String street;
    private String ward;
    private String district;
    private String city;
}