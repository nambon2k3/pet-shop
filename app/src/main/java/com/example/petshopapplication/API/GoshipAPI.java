package com.example.petshopapplication.API;

import com.example.petshopapplication.model.ProvinceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GoshipAPI {
    @GET("cities")
    Call<ProvinceResponse> getProvinces(
            @Header("Authorization") String authorizationToken
    );
}
