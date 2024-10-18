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
    private int id;
    private int userId;
    private int productId;
    private int rating;
    private String content;
    private Date createdAt;
    private boolean isDeleted;
}
