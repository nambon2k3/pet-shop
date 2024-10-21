package com.example.petshopapplication.callback;


public interface FirebaseCallback<E extends Object> {
    void onCallback(E object);
}
