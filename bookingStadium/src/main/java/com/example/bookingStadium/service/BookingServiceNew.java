package com.example.bookingStadium.service;

import com.example.bookingStadium.Security.SecurityUtils;
import com.example.bookingStadium.dto.response.BookingStadiumSummaryResponse;
import com.example.bookingStadium.dto.response.BookingUserResponse;
import com.example.bookingStadium.entity.Booking;
import com.example.bookingStadium.entity.Stadium;
import com.example.bookingStadium.entity.Stadium_Location;
import com.example.bookingStadium.entity.Users;
import com.example.bookingStadium.exception.AppException;
import com.example.bookingStadium.exception.ErrorCode;
import com.example.bookingStadium.repository.BookingRepository;
import com.example.bookingStadium.repository.StadiumLocationRepository;
import com.example.bookingStadium.repository.StadiumRepository;
import com.example.bookingStadium.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingServiceNew {
    
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private StadiumRepository stadiumRepository;

    @Autowired
    private StadiumLocationRepository stadiumLocationRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Lấy danh sách người đã booking của một stadium (chỉ owner mới được xem)
     */
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public BookingStadiumSummaryResponse getBookingUsersByStadium(String stadiumId) {
        // Kiểm tra stadium có tồn tại không
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new AppException(ErrorCode.STADIUM_NOT_EXISTED));

        // Kiểm tra quyền: chỉ owner của stadium mới có thể xem
        Stadium_Location location = stadiumLocationRepository.findById(stadium.getLocationId())
                .orElseThrow(() -> new AppException(ErrorCode.STADIUM_LOCATION_NOT_EXISTED));

        if (!securityUtils.isAdmin() && !securityUtils.isCurrentUser(location.getUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Lấy tất cả booking theo stadium
        List<Booking> bookings = bookingRepository.findByStadiumId(stadiumId);

        // Group theo userId và đếm số lượng booking
        Map<String, Long> userBookingCount = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getUserId, Collectors.counting()));

        // Tạo danh sách BookingUserResponse
        List<BookingUserResponse> users = new ArrayList<>();
        for (Map.Entry<String, Long> entry : userBookingCount.entrySet()) {
            String userId = entry.getKey();
            Long bookingCount = entry.getValue();
            
            // Lấy thông tin user
            Users user = userRepository.findById(userId).orElse(null);
            
            if (user != null) {
                BookingUserResponse userResponse = new BookingUserResponse();
                userResponse.setUserId(userId);
                userResponse.setUserName(user.getFirstname() + " " + user.getLastname());
                userResponse.setEmail(user.getEmail());
                userResponse.setBookingCount(bookingCount.intValue());
                users.add(userResponse);
            }
        }

        // Tạo response
        BookingStadiumSummaryResponse response = new BookingStadiumSummaryResponse();
        response.setStadiumId(stadiumId);
        response.setStadiumName(stadium.getStadiumName());
        response.setTotalUniqueUsers(users.size());
        response.setUsers(users);
        
        return response;
    }
}
