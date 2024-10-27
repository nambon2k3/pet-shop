package com.example.petshopapplication.API;

import com.example.petshopapplication.API_model.CityResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GoshipAPI {
    @GET("/api/v2/cities")
    Call<CityResponse> getCities(@Header("Accept") String accept,
                                 @Header("Content-Type") String contentType,
                                 @Header("Authorization") String authToken);
}
