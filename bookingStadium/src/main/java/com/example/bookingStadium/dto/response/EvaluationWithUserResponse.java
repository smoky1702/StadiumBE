package com.example.bookingStadium.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationWithUserResponse {
    @JsonProperty("evaluation_id")
    private String evaluationId;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("user_name")
    private String userName;  // Tên người đánh giá (firstname + lastname)
    
    @JsonProperty("stadium_id")
    private String stadiumId;
    
    @JsonProperty("booking_id")
    private String bookingId;
    
    @JsonProperty("rating_score")
    private BigDecimal ratingScore;
    
    @JsonProperty("comment")
    private String comment;
    
    @JsonProperty("date_created")
    private LocalDate dateCreated;
}
