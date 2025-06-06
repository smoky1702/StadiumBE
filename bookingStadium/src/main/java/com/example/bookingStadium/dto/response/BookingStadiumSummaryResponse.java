package com.example.bookingStadium.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStadiumSummaryResponse {
    private String stadiumId;
    private String stadiumName;
    private int totalUniqueUsers;
    private List<BookingUserResponse> users;
}
