package com.example.bookingStadium.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingUserResponse {
    private String userId;
    private String userName;
    private String email;
    private int bookingCount;
}
