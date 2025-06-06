package com.example.bookingStadium.repository;

import com.example.bookingStadium.entity.Bill;
import com.example.bookingStadium.dto.request.Bill.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, String> {
    List<Bill> findByUserId(String userId);
    
    /**
     * Tìm tất cả hóa đơn theo stadiumId
     * @param stadiumId ID của sân cần tìm hóa đơn
     * @return Danh sách các hóa đơn của sân
     */
    @Query("SELECT b FROM Bill b JOIN StadiumBookingDetail sbd ON b.stadiumBookingId = sbd.bookingId WHERE sbd.stadiumId = :stadiumId")
    List<Bill> findByStadiumId(@Param("stadiumId") String stadiumId);
    
    /**
     * Tìm hóa đơn theo danh sách bookingId
     * @param bookingIds Danh sách các bookingId
     * @return Danh sách hóa đơn
     */
    List<Bill> findByStadiumBookingIdIn(List<String> bookingIds);
    
    /**
     * Tìm hóa đơn theo danh sách bookingId và trạng thái
     * @param bookingIds Danh sách các bookingId
     * @param status Trạng thái hóa đơn
     * @return Danh sách hóa đơn
     */
    List<Bill> findByStadiumBookingIdInAndStatus(List<String> bookingIds, BillStatus status);
}
