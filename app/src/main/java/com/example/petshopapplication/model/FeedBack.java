package com.example.petshopapplication.model;

import java.util.Date;

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
public class FeedBack {
    private String id;
    private String userId;
    private String productId;
    private int rating;
    private String content;
    private String createdAt;
    private boolean isDeleted;
}
