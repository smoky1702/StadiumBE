package com.example.bookingStadium.service;


import com.example.bookingStadium.Security.SecurityUtils;
import com.example.bookingStadium.dto.request.Evaluation.EvaluationCreationRequest;
import com.example.bookingStadium.dto.request.Evaluation.EvaluationUpdateRequest;
import com.example.bookingStadium.dto.response.EvaluationResponse;
import com.example.bookingStadium.entity.Evaluation;
import com.example.bookingStadium.entity.Booking;
import com.example.bookingStadium.exception.AppException;
import com.example.bookingStadium.exception.ErrorCode;
import com.example.bookingStadium.mapper.EvaluationMapper;
import com.example.bookingStadium.repository.EvaluationRepository;
import com.example.bookingStadium.repository.StadiumRepository;
import com.example.bookingStadium.repository.UserRepository;
import com.example.bookingStadium.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EvaluationService {
    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StadiumRepository stadiumRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private SecurityUtils securityUtils;

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Evaluation createEvaluation(EvaluationCreationRequest request){
        // Kiểm tra user tồn tại
        if(!userRepository.existsById(request.getUserId())){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // Kiểm tra stadium tồn tại
        if (!stadiumRepository.existsById(request.getStadiumId())) {
            throw new AppException(ErrorCode.STADIUM_NOT_EXISTED);
        }
        
        // Đảm bảo người dùng chỉ có thể tạo đánh giá cho chính mình
        if (!securityUtils.isCurrentUser(request.getUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // ✨ LOGIC MỚI: Kiểm tra booking requirements
        if (request.getBookingId() != null) {
            // Kiểm tra booking tồn tại
            Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));
            
            // Kiểm tra booking thuộc về user hiện tại
            if (!booking.getUserId().equals(request.getUserId())) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
            
            // Kiểm tra booking đã completed
            if (!booking.getStatus().name().equals("COMPLETED")) {
                throw new AppException(ErrorCode.BOOKING_NOT_COMPLETED);
            }
            
            // Kiểm tra booking chưa được đánh giá
            if (evaluationRepository.existsByBookingId(request.getBookingId())) {
                throw new AppException(ErrorCode.BOOKING_ALREADY_EVALUATED);
            }
            
            // Kiểm tra thời gian đánh giá (trong vòng 30 ngày sau completion)
            LocalDate bookingDate = booking.getDateOfBooking();
            LocalDate currentDate = LocalDate.now();
            long daysSinceBooking = ChronoUnit.DAYS.between(bookingDate, currentDate);
            
            if (daysSinceBooking > 30) {
                throw new AppException(ErrorCode.EVALUATION_WINDOW_EXPIRED);
            }
        }

        // Tạo evaluation
        Evaluation evaluation = evaluationMapper.toEvaluation(request);
        return evaluationRepository.save(evaluation);
    }

    public List<Evaluation> getEvaluation(){
        return evaluationRepository.findAll();
    }

    public EvaluationResponse findEvaluation(String evaluationId){
        return evaluationMapper.toEvaluationResponse(evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new AppException(ErrorCode.EVALUATION_NOT_EXISTED)));
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public EvaluationResponse updateEvaluation(String evaluationId, EvaluationUpdateRequest request){
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new AppException(ErrorCode.EVALUATION_NOT_EXISTED));
        
        // Kiểm tra người dùng hiện tại có phải là người tạo đánh giá không
        if (!securityUtils.isAdmin() && !securityUtils.isCurrentUser(evaluation.getUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        evaluationMapper.updateEvaluation(evaluation, request);
        return evaluationMapper.toEvaluationResponse(evaluationRepository.save(evaluation));
    }   

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_USER')")
    public void deleteEvaluation(String evaluationId){
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new AppException(ErrorCode.EVALUATION_NOT_EXISTED));
        
        // Kiểm tra người dùng hiện tại có phải là người tạo đánh giá không
        if (!securityUtils.isAdmin() && !securityUtils.isCurrentUser(evaluation.getUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        evaluationRepository.deleteById(evaluationId);
    }
    
    // ✨ NEW METHODS
    
    // Kiểm tra booking có thể đánh giá không
    public boolean canEvaluateBooking(String bookingId, String userId) {
        // Kiểm tra booking tồn tại và thuộc về user
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || !booking.getUserId().equals(userId)) {
            return false;
        }
        
        // Kiểm tra booking đã completed
        if (!booking.getStatus().name().equals("COMPLETED")) {
            return false;
        }
        
        // Kiểm tra chưa có evaluation
        if (evaluationRepository.existsByBookingId(bookingId)) {
            return false;
        }
        
        // Kiểm tra trong thời gian cho phép (30 ngày)
        LocalDate bookingDate = booking.getDateOfBooking();
        LocalDate currentDate = LocalDate.now();
        long daysSinceBooking = ChronoUnit.DAYS.between(bookingDate, currentDate);
        
        return daysSinceBooking <= 30;
    }
    
    // Lấy evaluation theo booking
    public EvaluationResponse getEvaluationByBooking(String bookingId) {
        Evaluation evaluation = evaluationRepository.findByBookingId(bookingId)
            .orElseThrow(() -> new AppException(ErrorCode.EVALUATION_NOT_EXISTED));
        return evaluationMapper.toEvaluationResponse(evaluation);
    }
}

















