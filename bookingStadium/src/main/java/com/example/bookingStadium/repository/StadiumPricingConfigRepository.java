package com.example.bookingStadium.repository;

import com.example.bookingStadium.entity.StadiumPricingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StadiumPricingConfigRepository extends JpaRepository<StadiumPricingConfig, String> {
    
    /**
     * Find pricing config for a stadium
     */
    Optional<StadiumPricingConfig> findByStadiumIdAndIsActive(String stadiumId, Boolean isActive);
    
    /**
     * Find active pricing config for a stadium
     */
    default Optional<StadiumPricingConfig> findActiveByStadiumId(String stadiumId) {
        return findByStadiumIdAndIsActive(stadiumId, true);
    }
    
    /**
     * Check if pricing config exists for stadium
     */
    boolean existsByStadiumIdAndIsActive(String stadiumId, Boolean isActive);
    
    /**
     * Deactivate existing configs for stadium (for updates)
     */
    @Modifying
    @Transactional
    @Query("UPDATE StadiumPricingConfig spc SET spc.isActive = false WHERE spc.stadiumId = :stadiumId")
    void deactivateByStadiumId(@Param("stadiumId") String stadiumId);
    
    /**
     * Delete pricing config by stadium ID
     */
    @Modifying
    @Transactional
    void deleteByStadiumId(String stadiumId);
}
