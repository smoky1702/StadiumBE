package com.example.bookingStadium.repository;

import com.example.bookingStadium.entity.StadiumTimeBoundaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StadiumTimeBoundariesRepository extends JpaRepository<StadiumTimeBoundaries, String> {
    
    /**
     * Find active time boundaries for a stadium
     */
    Optional<StadiumTimeBoundaries> findByStadiumIdAndIsActive(String stadiumId, Boolean isActive);
    
    /**
     * Find active time boundaries for a stadium (default method)
     */
    default Optional<StadiumTimeBoundaries> findActiveByStadiumId(String stadiumId) {
        return findByStadiumIdAndIsActive(stadiumId, true);
    }
    
    /**
     * Check if time boundaries exist for stadium
     */
    boolean existsByStadiumIdAndIsActive(String stadiumId, Boolean isActive);
    
    /**
     * Deactivate existing time boundaries for stadium (for updates)
     */
    @Modifying
    @Transactional
    @Query("UPDATE StadiumTimeBoundaries stb SET stb.isActive = false WHERE stb.stadiumId = :stadiumId")
    void deactivateByStadiumId(@Param("stadiumId") String stadiumId);
}
