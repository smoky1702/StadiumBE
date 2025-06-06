package com.example.bookingStadium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {
    private String id; // stadiumId or locationId
    private String name; // stadium name or location name
    private double totalRevenue; // total sum of paid bills
    private long totalBills; // count of paid bills
}
