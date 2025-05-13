package com.example.bookingStadium.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "image_id")
    private String imageId;

    @Column(name = "stadium_id")
    private String stadiumId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "date_created", updatable = false)
    private LocalDate dateCreated = LocalDate.now();
}











