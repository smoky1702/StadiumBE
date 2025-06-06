package com.example.bookingStadium.controller;

import com.example.bookingStadium.dto.request.Stadium.StadiumPricingRequest;
import com.example.bookingStadium.dto.request.Stadium.PricingPreviewRequest;
import com.example.bookingStadium.dto.request.Stadium.CustomTimeBoundariesRequest;
import com.example.bookingStadium.dto.response.Stadium.StadiumPricingResponse;
import com.example.bookingStadium.dto.response.Stadium.PricingPreviewResponse;
import com.example.bookingStadium.entity.StadiumTimeBoundaries;
import com.example.bookingStadium.service.PricingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing")
@CrossOrigin(origins = "*")
public class StadiumPricingController {
    
    @Autowired
    private PricingService pricingService;
    
    /**
     * Set pricing multipliers for a stadium
     */
    @PostMapping("/{stadiumId}/set")
    public ResponseEntity<StadiumPricingResponse> setPricingMultipliers(
            @PathVariable String stadiumId,
            @Valid @RequestBody StadiumPricingRequest request) {
        try {
            // Validate stadiumId from path parameter
            if (stadiumId == null || stadiumId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Set stadiumId from path parameter
            request.setStadiumId(stadiumId);
            StadiumPricingResponse response = pricingService.setPricingMultipliers(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get pricing multipliers for a stadium
     */
    @GetMapping("/{stadiumId}")
    public ResponseEntity<StadiumPricingResponse> getPricingMultipliers(
            @PathVariable String stadiumId) {
        try {
            StadiumPricingResponse response = pricingService.getPricingMultipliers(stadiumId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update pricing multipliers for a stadium
     */
    @PutMapping("/{stadiumId}/update")
    public ResponseEntity<StadiumPricingResponse> updatePricingMultipliers(
            @PathVariable String stadiumId,
            @Valid @RequestBody StadiumPricingRequest request) {
        try {
            // Validate stadiumId from path parameter
            if (stadiumId == null || stadiumId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Set stadiumId from path parameter
            request.setStadiumId(stadiumId);
            StadiumPricingResponse response = pricingService.setPricingMultipliers(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get pricing preview for a specific time range
     */
    @PostMapping("/{stadiumId}/preview")
    public ResponseEntity<PricingPreviewResponse> getPricingPreview(
            @PathVariable String stadiumId,
            @Valid @RequestBody PricingPreviewRequest request) {
        try {
            // Validate stadiumId from path parameter
            if (stadiumId == null || stadiumId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Set stadiumId from path parameter
            request.setStadiumId(stadiumId);
            PricingPreviewResponse response = pricingService.getPricingPreview(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Set custom time boundaries for a stadium
     */
    @PostMapping("/{stadiumId}/time")
    public ResponseEntity<StadiumTimeBoundaries> setCustomTimeBoundaries(
            @PathVariable String stadiumId,
            @Valid @RequestBody CustomTimeBoundariesRequest request) {
        try {
            // Validate stadiumId from path parameter
            if (stadiumId == null || stadiumId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Set stadiumId from path parameter
            request.setStadiumId(stadiumId);
            StadiumTimeBoundaries response = pricingService.setCustomTimeBoundaries(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update custom time boundaries for a stadium
     */
    @PutMapping("/{stadiumId}/time")
    public ResponseEntity<StadiumTimeBoundaries> updateCustomTimeBoundaries(
            @PathVariable String stadiumId,
            @Valid @RequestBody CustomTimeBoundariesRequest request) {
        try {
            // Validate stadiumId from path parameter
            if (stadiumId == null || stadiumId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Set stadiumId from path parameter
            request.setStadiumId(stadiumId);
            StadiumTimeBoundaries response = pricingService.updateCustomTimeBoundaries(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get custom time boundaries for a stadium
     */
    @GetMapping("/{stadiumId}/time")
    public ResponseEntity<StadiumTimeBoundaries> getCustomTimeBoundaries(@PathVariable String stadiumId) {
        try {
            StadiumTimeBoundaries response = pricingService.getCustomTimeBoundaries(stadiumId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
