package com.example.bookingStadium.service;


import com.example.bookingStadium.Security.SecurityUtils;
import com.example.bookingStadium.dto.request.Bill.BillCreationRequest;
import com.example.bookingStadium.dto.request.Bill.BillPaidRequest;
import com.example.bookingStadium.dto.request.Bill.BillStatus;
import com.example.bookingStadium.dto.request.Bill.BillUpdateRequest;
import com.example.bookingStadium.dto.response.BillResponse;
import com.example.bookingStadium.dto.response.RevenueResponse;
import com.example.bookingStadium.entity.Bill;
import com.example.bookingStadium.entity.Booking;
import com.example.bookingStadium.entity.Stadium;
import com.example.bookingStadium.entity.StadiumBookingDetail;
import com.example.bookingStadium.entity.Stadium_Location;
import com.example.bookingStadium.exception.AppException;
import com.example.bookingStadium.exception.ErrorCode;
import com.example.bookingStadium.mapper.BillMapper;
import com.example.bookingStadium.repository.BillRepository;
import com.example.bookingStadium.repository.BookingRepository;
import com.example.bookingStadium.repository.StadiumBookingDetailRepository;
import com.example.bookingStadium.repository.StadiumLocationRepository;
import com.example.bookingStadium.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BillMapper billMapper;
    @Autowired
    private StadiumBookingDetailRepository stadiumBookingDetailRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private StadiumRepository stadiumRepository;
    @Autowired
    private StadiumLocationRepository stadiumLocationRepository;

    public Bill createBill(BillCreationRequest request){
       StadiumBookingDetail stadiumBookingDetail = stadiumBookingDetailRepository
               .findByBookingId(request.getStadiumBookingId());
       double price = stadiumBookingDetail.getPrice();

       Booking booking = bookingRepository.findById(request.getStadiumBookingId())
               .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_EXISTED));

       String userId = booking.getUserId();
       
       // Kiểm tra người dùng hiện tại có phải là người tạo booking hoặc OWNER
       if (!securityUtils.isOwner() && !securityUtils.isCurrentUser(userId)) {
           throw new AppException(ErrorCode.FORBIDDEN);
       }
       
       Bill bill = billMapper.toBill(request);
       bill.setFinalPrice(price);
       bill.setUserId(userId);
       return billRepository.save(bill);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OWNER')")
    public List<Bill> getBill(){
        return billRepository.findAll();
    }

    public BillResponse findBill(String billId){
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_EXISTED));
        
        // Chỉ cho phép người sở hữu bill, admin hoặc owner xem chi tiết bill
        if (!securityUtils.isAdmin() && !securityUtils.isOwner() 
                && !securityUtils.isCurrentUser(bill.getUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        return billMapper.toBillResponse(bill);
    }

    public BillResponse updateBill(String billId, BillUpdateRequest request){
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_EXISTED));

        // Chỉ cho phép chủ hóa đơn, admin hoặc owner cập nhật bill
        if (!securityUtils.isAdmin() && !securityUtils.isOwner() && !securityUtils.isCurrentUser(bill.getUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        String stadiumBookingId = bill.getStadiumBookingId();

        StadiumBookingDetail stadiumBookingDetail =
                stadiumBookingDetailRepository.findByBookingId(stadiumBookingId);

        double finalPrice = stadiumBookingDetail.getPrice();

        bill.setPaymentMethodId(request.getPaymentMethodId());
        bill.setFinalPrice(finalPrice);

        return billMapper.toBillResponse(billRepository.save(bill));
    }

    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public BillResponse billPaid(String billId, BillPaidRequest request){
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_EXISTED));
        billMapper.paidBill(bill, request);
        return billMapper.toBillResponse(billRepository.save(bill));
    }

    public void deleteBill(String billId){
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_NOT_EXISTED));
                
        // Chỉ cho phép admin hoặc owner xóa bill
        if (!securityUtils.isAdmin() && !securityUtils.isOwner()) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        billRepository.deleteById(billId);
    }
     /* Lấy danh sách hóa đơn user cụ thể*/
    public List<BillResponse> getUserBills(String userId) {
        // chỉ admin, owner hoặc chính người dùng đó mới có quyền xem
        if (!securityUtils.isAdmin() && !securityUtils.isOwner() && !securityUtils.isCurrentUser(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Lấy danh sách bill theo userId
        List<Bill> bills = billRepository.findByUserId(userId);

        // Chuyển đổi sang DTO response
        return bills.stream()
                .map(billMapper::toBillResponse)
                .toList();
    }
    
    /**
     * Lấy danh sách hóa đơn của một sân cụ thể
     * Chỉ owner của sân (kiểm tra qua location) hoặc admin mới có quyền xem
     * 
     * @param stadiumId ID của sân cần lấy hóa đơn
     * @return Danh sách các hóa đơn của sân
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_ADMIN')")
    public List<BillResponse> getStadiumBills(String stadiumId) {
        // Kiểm tra sân có tồn tại không
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new AppException(ErrorCode.STADIUM_NOT_EXISTED));
        
        // Kiểm tra owner có quyền truy cập sân này không
        if (securityUtils.isOwner()) {
            String locationId = stadium.getLocationId();
            Stadium_Location location = stadiumLocationRepository.findById(locationId)
                    .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));
            
            // Nếu không phải là owner của location này và không phải admin
            if (!securityUtils.isCurrentUser(location.getUserId()) && !securityUtils.isAdmin()) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }
        
        // Lấy danh sách booking của sân này
        List<Booking> bookings = bookingRepository.findByStadiumId(stadiumId);
        
        // Lấy các bookingId
        List<String> bookingIds = bookings.stream()
                .map(Booking::getBookingId)
                .toList();
        
        // Lấy bills dựa trên các bookingId
        List<Bill> bills = billRepository.findByStadiumBookingIdIn(bookingIds);
        
        // Map sang response
        return bills.stream()
                .map(billMapper::toBillResponse)
                .toList();
    }
    
    /**
     * Tính tổng doanh thu của một sân
     * @param stadiumId ID của sân cần tính doanh thu
     * @return Tổng doanh thu của sân (từ các hóa đơn đã thanh toán)
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_ADMIN')")
    public RevenueResponse getStadiumRevenue(String stadiumId) {
        // Kiểm tra sân có tồn tại không
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new AppException(ErrorCode.STADIUM_NOT_EXISTED));
        
        // Kiểm tra owner có quyền truy cập sân này không
        if (securityUtils.isOwner()) {
            String locationId = stadium.getLocationId();
            Stadium_Location location = stadiumLocationRepository.findById(locationId)
                    .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));
            
            // Nếu không phải là owner của location này và không phải admin
            if (!securityUtils.isCurrentUser(location.getUserId()) && !securityUtils.isAdmin()) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }
        
        // Lấy danh sách booking của sân này
        List<Booking> bookings = bookingRepository.findByStadiumId(stadiumId);
        
        // Lấy các bookingId
        List<String> bookingIds = bookings.stream()
                .map(Booking::getBookingId)
                .toList();
        
        // Lấy bills đã thanh toán dựa trên các bookingId
        List<Bill> paidBills = billRepository.findByStadiumBookingIdInAndStatus(
                bookingIds, 
                BillStatus.PAID
        );
        
        // Tính tổng doanh thu
        double totalRevenue = paidBills.stream()
                .mapToDouble(Bill::getFinalPrice)
                .sum();
        
        // Tạo response
        return RevenueResponse.builder()
                .id(stadiumId)
                .name(stadium.getStadiumName())
                .totalRevenue(totalRevenue)
                .totalBills(paidBills.size())
                .build();
    }
    
    /**
     * Tính tổng doanh thu của một địa điểm
     * @param locationId ID của địa điểm cần tính doanh thu
     * @return Tổng doanh thu của địa điểm (từ tất cả các sân thuộc địa điểm)
     */
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_ADMIN')")
    public RevenueResponse getLocationRevenue(String locationId) {
        // Kiểm tra location có tồn tại không
        Stadium_Location location = stadiumLocationRepository.findById(locationId)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_EXISTED));
        
        // Kiểm tra owner có quyền truy cập location này không
        if (securityUtils.isOwner()) {
            // Nếu không phải là owner của location này và không phải admin
            if (!securityUtils.isCurrentUser(location.getUserId()) && !securityUtils.isAdmin()) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }
        
        // Lấy tất cả sân thuộc địa điểm này
        List<Stadium> stadiums = stadiumRepository.findByLocationId(locationId);
        
        // Lấy danh sách ID sân
        List<String> stadiumIds = stadiums.stream()
                .map(Stadium::getStadiumId)
                .toList();
        
        // Lấy tất cả booking của các sân này
        List<Booking> bookings = bookingRepository.findByStadiumIdIn(stadiumIds);
        
        // Lấy các bookingId
        List<String> bookingIds = bookings.stream()
                .map(Booking::getBookingId)
                .toList();
        
        // Lấy bills đã thanh toán dựa trên các bookingId
        List<Bill> paidBills = billRepository.findByStadiumBookingIdInAndStatus(
                bookingIds, 
                BillStatus.PAID
        );
        
        // Tính tổng doanh thu
        double totalRevenue = paidBills.stream()
                .mapToDouble(Bill::getFinalPrice)
                .sum();
        
        // Tạo response
        return RevenueResponse.builder()
                .id(locationId)
                .name(location.getLocationName())
                .totalRevenue(totalRevenue)
                .totalBills(paidBills.size())
                .build();
    }
}   
















