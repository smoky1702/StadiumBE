package com.example.bookingStadium.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookingStadium.Security.SecurityUtils;
import com.example.bookingStadium.dto.request.Evaluation.EvaluationCreationRequest;
import com.example.bookingStadium.dto.request.Evaluation.EvaluationUpdateRequest;
import com.example.bookingStadium.dto.response.ApiResponse;
import com.example.bookingStadium.dto.response.EvaluationResponse;
import com.example.bookingStadium.dto.response.EvaluationWithUserResponse;
import com.example.bookingStadium.entity.Evaluation;
import com.example.bookingStadium.service.EvaluationService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationService evaluationService;
    
    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping
    ApiResponse<Evaluation> createEvaluation(@RequestBody @Valid EvaluationCreationRequest request){
        ApiResponse<Evaluation> apiResponse = new ApiResponse<>();
        apiResponse.setResult(evaluationService.createEvaluation(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<Evaluation>> getEvaluation(){
        List<Evaluation> evaluationList = evaluationService.getEvaluation();
        ApiResponse<List<Evaluation>> apiResponse = new ApiResponse<>(evaluationList);
        return apiResponse;
    }

    @GetMapping("/{evaluationId}")
    ApiResponse<EvaluationResponse> findEvaluation(@PathVariable("evaluationId") String evaluationId){
        ApiResponse<EvaluationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(evaluationService.findEvaluation(evaluationId));
        return apiResponse;
    }

    @PutMapping("/{evaluationId}")
    ApiResponse<EvaluationResponse> updateEvaluation(@PathVariable("evaluationId") String evaluationId
            , @RequestBody EvaluationUpdateRequest request){
        ApiResponse<EvaluationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(evaluationService.updateEvaluation(evaluationId, request));
        return apiResponse;
    }

    @DeleteMapping("/{evaluationId}")
    ApiResponse<String> deleteEvaluation(@PathVariable("evaluationId") String evaluationId){
        evaluationService.deleteEvaluation(evaluationId);
        return ApiResponse.<String>builder()
                .code(200)
                .result("Evaluation has been deleted")
                .build();
    }
    
    // ✨ NEW ENDPOINTS
    
    // Kiểm tra booking có thể đánh giá không
    @GetMapping("/can-evaluate/{bookingId}")
    ApiResponse<Boolean> canEvaluateBooking(@PathVariable("bookingId") String bookingId){
        String currentUserId = securityUtils.getCurrentUserId();
        boolean canEvaluate = evaluationService.canEvaluateBooking(bookingId, currentUserId);
        
        return ApiResponse.<Boolean>builder()
                .code(200)
                .result(canEvaluate)
                .build();
    }
    
    // Lấy evaluation theo booking
    @GetMapping("/booking/{bookingId}")
    ApiResponse<EvaluationResponse> getEvaluationByBooking(@PathVariable("bookingId") String bookingId){
        ApiResponse<EvaluationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(evaluationService.getEvaluationByBooking(bookingId));
        return apiResponse;
    }
    

    @GetMapping("/stadium/{stadiumId}")
    public ApiResponse<List<EvaluationWithUserResponse>> getEvaluationsByStadium(@PathVariable("stadiumId") String stadiumId) {
        List<EvaluationWithUserResponse> evaluations = evaluationService.getEvaluationsByStadiumId(stadiumId);
        return new ApiResponse<>(evaluations);
    }
}




















