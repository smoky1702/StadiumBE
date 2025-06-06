package com.example.bookingStadium.service;

import com.example.bookingStadium.dto.request.Stadium.PricingPreviewRequest;
import com.example.bookingStadium.dto.request.Stadium.StadiumPricingRequest;
import com.example.bookingStadium.dto.request.Stadium.CustomTimeBoundariesRequest;
import com.example.bookingStadium.dto.response.Stadium.PricingPreviewResponse;
import com.example.bookingStadium.dto.response.Stadium.StadiumPricingResponse;
import com.example.bookingStadium.entity.Stadium;
import com.example.bookingStadium.entity.StadiumPricingConfig;
import com.example.bookingStadium.entity.StadiumTimeBoundaries;
import com.example.bookingStadium.repository.StadiumRepository;
import com.example.bookingStadium.repository.StadiumPricingConfigRepository;
import com.example.bookingStadium.repository.StadiumTimeBoundariesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.Duration;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
public class PricingService {

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private StadiumPricingConfigRepository pricingConfigRepository;
    
    @Autowired
    private StadiumTimeBoundariesRepository timeBoundariesRepository;

    // Default time slot definitions
    private static final LocalTime DEFAULT_MORNING_START = LocalTime.of(6, 0);
    private static final LocalTime DEFAULT_AFTERNOON_START = LocalTime.of(12, 0);
    private static final LocalTime DEFAULT_EVENING_START = LocalTime.of(18, 0);
    private static final LocalTime DEFAULT_NIGHT_START = LocalTime.of(22, 0);    /**
     * Set pricing multipliers for a stadium
     */
    @Transactional
    public StadiumPricingResponse setPricingMultipliers(StadiumPricingRequest request) {
        Stadium stadium = stadiumRepository.findById(request.getStadiumId())
            .orElseThrow(() -> new RuntimeException("Stadium not found"));

        // Check if pricing config already exists
        Optional<StadiumPricingConfig> existingConfig =
            pricingConfigRepository.findActiveByStadiumId(request.getStadiumId());

        StadiumPricingConfig config;
        if (existingConfig.isPresent()) {
            // Update existing config
            config = existingConfig.get();
            config.setMorningMultiplier(request.getMorningMultiplier());
            config.setAfternoonMultiplier(request.getAfternoonMultiplier());
            config.setEveningMultiplier(request.getEveningMultiplier());
            config.setNightMultiplier(request.getNightMultiplier());
        } else {
            // Create new config
            config = new StadiumPricingConfig();
            config.setStadiumId(request.getStadiumId());
            config.setMorningMultiplier(request.getMorningMultiplier());
            config.setAfternoonMultiplier(request.getAfternoonMultiplier());
            config.setEveningMultiplier(request.getEveningMultiplier());
            config.setNightMultiplier(request.getNightMultiplier());
        }

        pricingConfigRepository.save(config);

        return new StadiumPricingResponse(
            stadium.getStadiumId(),
            stadium.getStadiumName(),
            stadium.getPrice(),
            config.getMorningMultiplier(),
            config.getAfternoonMultiplier(),
            config.getEveningMultiplier(),
            config.getNightMultiplier()
        );
    }

    /**
     * Get pricing multipliers for a stadium
     */
    public StadiumPricingResponse getPricingMultipliers(String stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId)
            .orElseThrow(() -> new RuntimeException("Stadium not found"));

        Optional<StadiumPricingConfig> config =
            pricingConfigRepository.findActiveByStadiumId(stadiumId);

        if (config.isPresent()) {
            StadiumPricingConfig pricingConfig = config.get();
            return new StadiumPricingResponse(
                stadium.getStadiumId(),
                stadium.getStadiumName(),
                stadium.getPrice(),
                pricingConfig.getMorningMultiplier(),
                pricingConfig.getAfternoonMultiplier(),
                pricingConfig.getEveningMultiplier(),
                pricingConfig.getNightMultiplier()
            );
        } else {
            // Return default multipliers if no config exists
            return new StadiumPricingResponse(
                stadium.getStadiumId(),
                stadium.getStadiumName(),
                stadium.getPrice(),
                1.0, 1.0, 1.0, 1.0
            );
        }
    }

    /**
     * Calculate dynamic price based on time slot
     */
    public double calculateDynamicPrice(String stadiumId, LocalTime startTime, LocalTime endTime, double totalHours) {
        Stadium stadium = stadiumRepository.findById(stadiumId)
            .orElseThrow(() -> new RuntimeException("Stadium not found"));

        double basePrice = stadium.getPrice();

        // Get pricing config from database
        Optional<StadiumPricingConfig> config =
            pricingConfigRepository.findActiveByStadiumId(stadiumId);

        if (config.isPresent()) {
            StadiumPricingConfig pricingConfig = config.get();
            double averageMultiplier = calculateAverageMultiplier(stadiumId, startTime, endTime, pricingConfig);
            return basePrice * totalHours * averageMultiplier;
        } else {
            // No custom pricing, use base price
            return basePrice * totalHours;
        }
    }

    /**
     * Calculate average multiplier for a booking time period using custom boundaries
     */
    private double calculateAverageMultiplier(String stadiumId, LocalTime startTime, LocalTime endTime, StadiumPricingConfig config) {
        // Get custom time boundaries for this stadium, or use defaults
        StadiumTimeBoundaries boundaries = getTimeBoundariesForStadium(stadiumId);
        
        // If the booking is within a single day and doesn't cross midnight
        if (startTime.isBefore(endTime) || startTime.equals(endTime)) {
            return calculateDayTimeMultiplier(startTime, endTime, config, boundaries);
        } else {
            // Booking crosses midnight - split into two parts
            double firstPartMultiplier = calculateDayTimeMultiplier(startTime, LocalTime.of(23, 59), config, boundaries);
            double secondPartMultiplier = calculateDayTimeMultiplier(LocalTime.of(0, 0), endTime, config, boundaries);

            // Calculate weighted average based on time portions
            long firstPartMinutes = Duration.between(startTime, LocalTime.of(23, 59)).toMinutes() + 1;
            long secondPartMinutes = Duration.between(LocalTime.of(0, 0), endTime).toMinutes();
            long totalMinutes = firstPartMinutes + secondPartMinutes;

            return (firstPartMultiplier * firstPartMinutes + secondPartMultiplier * secondPartMinutes) / totalMinutes;
        }
    }

    /**
     * Calculate multiplier for a single day time period using custom boundaries
     */
    private double calculateDayTimeMultiplier(LocalTime startTime, LocalTime endTime, StadiumPricingConfig config, StadiumTimeBoundaries boundaries) {
        double totalWeightedMultiplier = 0.0;
        long totalMinutes = 0;

        // Define time slots using custom boundaries
        LocalTime[] slotStarts = {
            boundaries.getMorningStart(),
            boundaries.getAfternoonStart(),
            boundaries.getEveningStart(),
            boundaries.getNightStart()
        };
        LocalTime[] slotEnds = {
            boundaries.getAfternoonStart(),
            boundaries.getEveningStart(),
            boundaries.getNightStart(),
            boundaries.getMorningStart()
        };
        double[] slotMultipliers = {
            config.getMorningMultiplier(),
            config.getAfternoonMultiplier(),
            config.getEveningMultiplier(),
            config.getNightMultiplier()
        };

        for (int i = 0; i < slotStarts.length; i++) {
            LocalTime slotStart = slotStarts[i];
            LocalTime slotEnd = slotEnds[i];

            // Handle night slot (crosses midnight)
            if (i == 3) {
                // Night slot from night start to 24:00
                LocalTime overlapStart = maxTime(startTime, slotStart);
                LocalTime overlapEnd = minTime(endTime, LocalTime.of(23, 59));

                if (overlapStart.isBefore(overlapEnd) || overlapStart.equals(overlapEnd)) {
                    long minutes = Duration.between(overlapStart, overlapEnd).toMinutes() + 1;
                    totalWeightedMultiplier += slotMultipliers[i] * minutes;
                    totalMinutes += minutes;
                }

                // Night slot from 0:00 to morning start
                overlapStart = maxTime(startTime, LocalTime.of(0, 0));
                overlapEnd = minTime(endTime, slotEnd);

                if (overlapStart.isBefore(overlapEnd) || overlapStart.equals(overlapEnd)) {
                    long minutes = Duration.between(overlapStart, overlapEnd).toMinutes() + 1;
                    totalWeightedMultiplier += slotMultipliers[i] * minutes;
                    totalMinutes += minutes;
                }
            } else {
                // Regular slots
                LocalTime overlapStart = maxTime(startTime, slotStart);
                LocalTime overlapEnd = minTime(endTime, slotEnd);

                if (overlapStart.isBefore(overlapEnd)) {
                    long minutes = Duration.between(overlapStart, overlapEnd).toMinutes();
                    totalWeightedMultiplier += slotMultipliers[i] * minutes;
                    totalMinutes += minutes;
                }
            }
        }

        return totalMinutes > 0 ? totalWeightedMultiplier / totalMinutes : 1.0;
    }

    /**
     * Get time boundaries for a stadium (custom or default)
     */
    private StadiumTimeBoundaries getTimeBoundariesForStadium(String stadiumId) {
        Optional<StadiumTimeBoundaries> customBoundaries = 
            timeBoundariesRepository.findByStadiumIdAndIsActive(stadiumId, true);
        
        if (customBoundaries.isPresent()) {
            return customBoundaries.get();
        } else {
            // Return default boundaries
            StadiumTimeBoundaries defaultBoundaries = new StadiumTimeBoundaries();
            defaultBoundaries.setStadiumId(stadiumId);
            defaultBoundaries.setMorningStart(DEFAULT_MORNING_START);
            defaultBoundaries.setAfternoonStart(DEFAULT_AFTERNOON_START);
            defaultBoundaries.setEveningStart(DEFAULT_EVENING_START);
            defaultBoundaries.setNightStart(DEFAULT_NIGHT_START);
            return defaultBoundaries;
        }
    }

    // Helper methods for time comparisons
    private LocalTime maxTime(LocalTime a, LocalTime b) {
        return a.isAfter(b) ? a : b;
    }

    private LocalTime minTime(LocalTime a, LocalTime b) {
        return a.isBefore(b) ? a : b;
    }

    /**
     * Get pricing preview for a booking
     */
    public PricingPreviewResponse getPricingPreview(PricingPreviewRequest request) {
        Stadium stadium = stadiumRepository.findById(request.getStadiumId())
            .orElseThrow(() -> new RuntimeException("Stadium not found"));

        double basePrice = stadium.getPrice();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        
        // Calculate total hours
        double totalHours;
        if (startTime.isBefore(endTime) || startTime.equals(endTime)) {
            totalHours = Duration.between(startTime, endTime).toMinutes() / 60.0;
        } else {
            // Crosses midnight
            totalHours = (Duration.between(startTime, LocalTime.of(23, 59)).toMinutes() + 1 +
                         Duration.between(LocalTime.of(0, 0), endTime).toMinutes()) / 60.0;
        }

        // Get pricing config from database
        Optional<StadiumPricingConfig> config =
            pricingConfigRepository.findActiveByStadiumId(request.getStadiumId());

        double averageMultiplier = 1.0;
        double totalPrice;
        
        if (config.isPresent()) {
            StadiumPricingConfig pricingConfig = config.get();
            averageMultiplier = calculateAverageMultiplier(request.getStadiumId(), startTime, endTime, pricingConfig);
            totalPrice = basePrice * totalHours * averageMultiplier;
        } else {
            totalPrice = basePrice * totalHours;
        }

        // Create hourly breakdown for preview
        List<PricingPreviewResponse.HourlyPriceBreakdown> hourlyBreakdown = 
            createHourlyBreakdown(request.getStadiumId(), startTime, endTime, basePrice, config.orElse(null));

        return new PricingPreviewResponse(
            stadium.getStadiumId(),
            stadium.getStadiumName(),
            basePrice,
            startTime,
            endTime,
            totalHours,
            totalPrice,
            averageMultiplier,
            hourlyBreakdown
        );
    }
    
    /**
     * Create hourly breakdown for pricing preview using custom boundaries
     */
    private List<PricingPreviewResponse.HourlyPriceBreakdown> createHourlyBreakdown(
            String stadiumId, LocalTime startTime, LocalTime endTime, double basePrice, StadiumPricingConfig config) {
        
        List<PricingPreviewResponse.HourlyPriceBreakdown> breakdown = new ArrayList<>();
        StadiumTimeBoundaries boundaries = getTimeBoundariesForStadium(stadiumId);
        
        LocalTime currentTime = startTime;
        boolean crossesMidnight = endTime.isBefore(startTime);
        
        while (true) {
            LocalTime nextHour = currentTime.plusHours(1);
            
            // Determine time slot and multiplier
            String timeSlot = getTimeSlotName(currentTime, boundaries);
            double multiplier = getMultiplierForTime(currentTime, config, boundaries);
            double pricePerHour = basePrice * multiplier;
            
            // Calculate minutes in this hour segment
            double minutes;
            if (crossesMidnight && currentTime.equals(LocalTime.of(23, 0))) {
                // Last hour before midnight
                minutes = Duration.between(currentTime, LocalTime.of(23, 59)).toMinutes() + 1;
            } else if (crossesMidnight && currentTime.equals(LocalTime.of(0, 0))) {
                // First hour after midnight
                minutes = Duration.between(currentTime, endTime.isAfter(nextHour) ? nextHour : endTime).toMinutes();
            } else if (endTime.isBefore(nextHour) || endTime.equals(nextHour)) {
                // Last segment
                minutes = Duration.between(currentTime, endTime).toMinutes();
            } else {
                // Full hour
                minutes = 60.0;
            }
            
            breakdown.add(new PricingPreviewResponse.HourlyPriceBreakdown(
                currentTime.getHour(),
                timeSlot,
                multiplier,
                pricePerHour,
                minutes
            ));
            
            // Check if we've reached the end
            if (!crossesMidnight && (nextHour.isAfter(endTime) || nextHour.equals(endTime))) {
                break;
            } else if (crossesMidnight && currentTime.getHour() >= endTime.getHour() && nextHour.isAfter(endTime)) {
                break;
            }
            
            currentTime = nextHour;
            
            // Handle crossing midnight
            if (crossesMidnight && currentTime.equals(LocalTime.of(0, 0))) {
                // Continue from midnight
            }
        }
        
        return breakdown;
    }
    
    /**
     * Get time slot name for display using custom boundaries
     */
    private String getTimeSlotName(LocalTime time, StadiumTimeBoundaries boundaries) {
        if (time.isBefore(boundaries.getAfternoonStart())) {
            return "Morning";
        } else if (time.isBefore(boundaries.getEveningStart())) {
            return "Afternoon";
        } else if (time.isBefore(boundaries.getNightStart())) {
            return "Evening";
        } else {
            return "Night";
        }
    }
    
    /**
     * Get multiplier for specific time using custom boundaries
     */
    private double getMultiplierForTime(LocalTime time, StadiumPricingConfig config, StadiumTimeBoundaries boundaries) {
        if (config == null) {
            return 1.0;
        }
        
        if (time.isBefore(boundaries.getAfternoonStart())) {
            return config.getMorningMultiplier();
        } else if (time.isBefore(boundaries.getEveningStart())) {
            return config.getAfternoonMultiplier();
        } else if (time.isBefore(boundaries.getNightStart())) {
            return config.getEveningMultiplier();
        } else {
            return config.getNightMultiplier();
        }
    }

    /**
     * Set custom time boundaries for a stadium
     */    @Transactional
    public StadiumTimeBoundaries setCustomTimeBoundaries(CustomTimeBoundariesRequest request) {
        // Debug logging
        System.out.println("=== DEBUG: Received request ===");
        System.out.println("Stadium ID: " + request.getStadiumId());
        System.out.println("Morning Start: " + request.getMorningStart());
        System.out.println("Morning End: " + request.getMorningEnd());
        System.out.println("Afternoon Start: " + request.getAfternoonStart());
        System.out.println("Afternoon End: " + request.getAfternoonEnd());
        System.out.println("Evening Start: " + request.getEveningStart());
        System.out.println("Evening End: " + request.getEveningEnd());
        System.out.println("Night Start: " + request.getNightStart());
        System.out.println("Night End: " + request.getNightEnd());
        
        // Validate the stadium exists
        Stadium stadium = stadiumRepository.findById(request.getStadiumId())
            .orElseThrow(() -> new RuntimeException("Stadium not found"));

        // Validate time boundaries
        validateTimeBoundaries(request);

        // Deactivate existing boundaries
        timeBoundariesRepository.deactivateByStadiumId(request.getStadiumId());        // Create new boundaries
        StadiumTimeBoundaries boundaries = new StadiumTimeBoundaries();
        boundaries.setStadiumId(request.getStadiumId());
        boundaries.setMorningStart(request.getMorningStart());
        boundaries.setMorningEnd(request.getMorningEnd());
        boundaries.setAfternoonStart(request.getAfternoonStart());
        boundaries.setAfternoonEnd(request.getAfternoonEnd());
        boundaries.setEveningStart(request.getEveningStart());
        boundaries.setEveningEnd(request.getEveningEnd());
        boundaries.setNightStart(request.getNightStart());
        boundaries.setNightEnd(request.getNightEnd());
        boundaries.setIsActive(true);

        // Debug logging before save
        System.out.println("=== DEBUG: Entity before save ===");
        System.out.println("Entity Afternoon End: " + boundaries.getAfternoonEnd());
        System.out.println("Entity Evening End: " + boundaries.getEveningEnd());
        System.out.println("Entity Night End: " + boundaries.getNightEnd());

        return timeBoundariesRepository.save(boundaries);
    }

    /**
     * Update custom time boundaries for a stadium
     */
    @Transactional
    public StadiumTimeBoundaries updateCustomTimeBoundaries(CustomTimeBoundariesRequest request) {
        // Validate the stadium exists
        Stadium stadium = stadiumRepository.findById(request.getStadiumId())
            .orElseThrow(() -> new RuntimeException("Stadium not found"));

        // Find existing boundaries
        Optional<StadiumTimeBoundaries> existingBoundaries =
            timeBoundariesRepository.findActiveByStadiumId(request.getStadiumId());

        if (existingBoundaries.isEmpty()) {
            // If no existing boundaries, create new ones (same as setCustomTimeBoundaries)
            return setCustomTimeBoundaries(request);
        }

        // Validate time boundaries
        validateTimeBoundaries(request);

        // Update existing boundaries
        StadiumTimeBoundaries boundaries = existingBoundaries.get();
        boundaries.setMorningStart(request.getMorningStart());
        boundaries.setMorningEnd(request.getMorningEnd());
        boundaries.setAfternoonStart(request.getAfternoonStart());
        boundaries.setAfternoonEnd(request.getAfternoonEnd());
        boundaries.setEveningStart(request.getEveningStart());
        boundaries.setEveningEnd(request.getEveningEnd());
        boundaries.setNightStart(request.getNightStart());
        boundaries.setNightEnd(request.getNightEnd());

        // Save the updated boundaries
        return timeBoundariesRepository.save(boundaries);
    }

    /**
     * Get custom time boundaries for a stadium
     * If no custom boundaries exist, returns default boundaries
     */
    public StadiumTimeBoundaries getCustomTimeBoundaries(String stadiumId) {
        // Validate the stadium exists
        Stadium stadium = stadiumRepository.findById(stadiumId)
            .orElseThrow(() -> new RuntimeException("Stadium not found"));

        // Find existing boundaries
        Optional<StadiumTimeBoundaries> existingBoundaries =
            timeBoundariesRepository.findActiveByStadiumId(stadiumId);

        if (existingBoundaries.isPresent()) {
            return existingBoundaries.get();
        } else {
            // Return default boundaries
            StadiumTimeBoundaries defaultBoundaries = new StadiumTimeBoundaries();
            defaultBoundaries.setStadiumId(stadiumId);
            defaultBoundaries.setMorningStart(DEFAULT_MORNING_START);
            defaultBoundaries.setMorningEnd(DEFAULT_AFTERNOON_START);
            defaultBoundaries.setAfternoonStart(DEFAULT_AFTERNOON_START);
            defaultBoundaries.setAfternoonEnd(DEFAULT_EVENING_START);
            defaultBoundaries.setEveningStart(DEFAULT_EVENING_START);
            defaultBoundaries.setEveningEnd(DEFAULT_NIGHT_START);
            defaultBoundaries.setNightStart(DEFAULT_NIGHT_START);
            defaultBoundaries.setNightEnd(DEFAULT_MORNING_START);
            defaultBoundaries.setIsActive(false); // Not saved to database
            return defaultBoundaries;
        }
    }

    /**
     * Validate time boundaries to ensure they form a continuous 24-hour cycle
     * and don't overlap
     */
    private void validateTimeBoundaries(CustomTimeBoundariesRequest request) {
        // Validate morning starts before afternoon
        if (!request.getMorningStart().isBefore(request.getAfternoonStart())) {
            throw new IllegalArgumentException("Morning must start before afternoon");
        }

        // Validate afternoon starts before evening
        if (!request.getAfternoonStart().isBefore(request.getEveningStart())) {
            throw new IllegalArgumentException("Afternoon must start before evening");
        }

        // Validate evening starts before night
        if (!request.getEveningStart().isBefore(request.getNightStart())) {
            throw new IllegalArgumentException("Evening must start before night");
        }

        // Validate morning end equals afternoon start
        if (!request.getMorningEnd().equals(request.getAfternoonStart())) {
            throw new IllegalArgumentException("Morning end must equal afternoon start");
        }

        // Validate afternoon end equals evening start
        if (!request.getAfternoonEnd().equals(request.getEveningStart())) {
            throw new IllegalArgumentException("Afternoon end must equal evening start");
        }

        // Validate evening end equals night start
        if (!request.getEveningEnd().equals(request.getNightStart())) {
            throw new IllegalArgumentException("Evening end must equal night start");
        }

        // Validate night end equals morning start (completing the 24-hour cycle)
        if (!request.getNightEnd().equals(request.getMorningStart())) {
            throw new IllegalArgumentException("Night end must equal morning start to complete the 24-hour cycle");
        }
    }
}
