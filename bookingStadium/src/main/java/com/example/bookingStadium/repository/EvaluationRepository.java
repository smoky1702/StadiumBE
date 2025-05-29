package com.example.bookingStadium.repository;

import com.example.bookingStadium.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, String> {
    
    // Tìm evaluation theo booking_id
    Optional<Evaluation> findByBookingId(String bookingId);
    
    // Kiểm tra booking đã có evaluation chưa
    boolean existsByBookingId(String bookingId);
    
    // Lấy tất cả evaluations của user
    List<Evaluation> findByUserId(String userId);
    
    // Lấy tất cả evaluations của stadium
    List<Evaluation> findByStadiumId(String stadiumId);
    
    // Lấy evaluations theo user và stadium (để backward compatibility)
    List<Evaluation> findByUserIdAndStadiumId(String userId, String stadiumId);
}
