package com.example.petshopapplication.utils;

import android.content.Context;

import com.example.petshopapplication.R;

public class Validate {

    public static boolean isPhoneNumberValid(String phoneNumber) {
        // Regex pattern for phone number validation
        return phoneNumber.matches("^(\\+84-|0)([35789][0-9]{8})$");
    }

    public static boolean isPasswordValid(String password) {
        // Regex pattern for password validation
        return password.matches("^.{8,}$");
    }

    public static boolean isFullNameValid(String fullName) {
        // Regex pattern for fullName validation
        return fullName.matches("^[a-zA-Z\\s]+$");
    }

    public static String isRegisterValid(Context context, String phoneNumber, String password, String fullName) {
        if(!isFullNameValid(fullName)) {
            return context.getString(R.string.valid_fullName_fail);
        }
        else if (!isPhoneNumberValid(phoneNumber)) {
            return context.getString(R.string.valid_phone_fail);
        }
        if (!isPasswordValid(password)) {
            return context.getString(R.string.valid_password_fail);
        }
        return null;
    }

}
