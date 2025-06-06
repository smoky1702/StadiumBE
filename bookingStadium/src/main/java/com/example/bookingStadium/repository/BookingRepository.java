package com.example.bookingStadium.repository;

import com.example.bookingStadium.dto.request.Booking.BookingStatus;
import com.example.bookingStadium.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByDateOfBookingAndLocationId(LocalDate dateOfBooking, String locationId);
    List<Booking> findByUserId(String userId);
    
    /**
     * Tìm tất cả booking theo stadium ID
     * @param stadiumId ID của sân
     * @return Danh sách booking
     */
    @Query("SELECT b FROM Booking b JOIN StadiumBookingDetail sbd ON b.bookingId = sbd.bookingId WHERE sbd.stadiumId = :stadiumId")
    List<Booking> findByStadiumId(@Param("stadiumId") String stadiumId);
    
    /**
     * Tìm tất cả booking theo danh sách stadium ID
     * @param stadiumIds Danh sách ID của sân
     * @return Danh sách booking
     */
    @Query("SELECT b FROM Booking b JOIN StadiumBookingDetail sbd ON b.bookingId = sbd.bookingId WHERE sbd.stadiumId IN :stadiumIds")
    List<Booking> findByStadiumIdIn(@Param("stadiumIds") List<String> stadiumIds);
    
    // Tìm các booking đã xác nhận đã qua thời gian sử dụng để chuyển sang COMPLETED
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND " +
           "((b.dateOfBooking < :currentDate) OR " +
           "(b.dateOfBooking = :currentDate AND b.endTime < :currentTime))")
    List<Booking> findBookingsToComplete(
            @Param("status") BookingStatus status,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") Time currentTime);
    
    // Tìm tất cả booking PENDING đã quá hạn ngày
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.dateOfBooking < :currentDate")
    List<Booking> findPastPendingBookings(
            @Param("status") BookingStatus status,
            @Param("currentDate") LocalDate currentDate);
    
    // Tìm các booking PENDING của ngày hiện tại đã quá 15 phút sau giờ bắt đầu
    @Query(value = "SELECT * FROM stadium_booking b WHERE b.status = ?1 " +
            "AND b.date_of_booking = ?2 " +
            "AND TIMESTAMPDIFF(MINUTE, CONCAT(b.date_of_booking, ' ', b.start_time), ?3) > 15", 
            nativeQuery = true)
    List<Booking> findDelayedPendingBookingsToday(
            String status,
            LocalDate currentDate,
            LocalDateTime currentTimestamp);
}