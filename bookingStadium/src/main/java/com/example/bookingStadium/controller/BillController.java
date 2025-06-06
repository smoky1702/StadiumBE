package com.example.bookingStadium.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookingStadium.dto.request.Bill.BillCreationRequest;
import com.example.bookingStadium.dto.request.Bill.BillPaidRequest;
import com.example.bookingStadium.dto.request.Bill.BillUpdateRequest;
import com.example.bookingStadium.dto.response.ApiResponse;
import com.example.bookingStadium.dto.response.BillResponse;
import com.example.bookingStadium.dto.response.RevenueResponse;
import com.example.bookingStadium.entity.Bill;
import com.example.bookingStadium.service.BillService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bill")
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping
    ApiResponse<Bill> createBill(@RequestBody @Valid BillCreationRequest request){
        ApiResponse<Bill> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.createBill(request));
        return apiResponse;
    }

    @PostMapping("/user")
    ApiResponse<Bill> createUserBill(@RequestBody @Valid BillCreationRequest request){
        ApiResponse<Bill> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.createBill(request));
        return apiResponse;
    }

    @PostMapping("/owner")
    ApiResponse<Bill> createOwnerBill(@RequestBody @Valid BillCreationRequest request){
        ApiResponse<Bill> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.createBill(request));
        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<Bill>> getBill(){
        List<Bill> billList = billService.getBill();
        ApiResponse<List<Bill>> apiResponse = new ApiResponse<>(billList);
        return apiResponse;
    }

    @GetMapping("/{billId}")
    ApiResponse<BillResponse> findBill(@PathVariable("billId") String billId){
        ApiResponse<BillResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.findBill(billId));
        return apiResponse;
    }

    @PutMapping("/update/{billId}")
    ApiResponse<BillResponse> updateBill(@PathVariable("billId") String billId, BillUpdateRequest request){
        ApiResponse<BillResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.updateBill(billId, request));
        return apiResponse;
    }

    @PutMapping("/paid/{billId}")
    ApiResponse<BillResponse> paidBill(@PathVariable("billId") String billId, BillPaidRequest request){
        ApiResponse<BillResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(billService.billPaid(billId, request));
        return apiResponse;
    }
    

    @GetMapping("/stadium/{stadiumId}")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public ApiResponse<List<BillResponse>> getStadiumBills(@PathVariable("stadiumId") String stadiumId){
        List<BillResponse> bills = billService.getStadiumBills(stadiumId);
        return new ApiResponse<>(bills);
    }

    @DeleteMapping("/{billId}")
    ApiResponse<String> deleteBill(@PathVariable("billId") String billId){
        billService.deleteBill(billId);
        return ApiResponse.<String>builder()
                .code(200)
                .result("Bill has been deleted")
                .build();
    }
    
    /**
     * Calculates total revenue for a specific stadium
     * Only owner of the stadium (checked via location) or admin can access this endpoint
     * 
     * @param stadiumId ID of the stadium to calculate revenue for
     * @return Total revenue from paid bills for the stadium
     */
    @GetMapping("/revenue/stadium/{stadiumId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_ADMIN')")
    public ApiResponse<RevenueResponse> getStadiumRevenue(@PathVariable("stadiumId") String stadiumId) {
        RevenueResponse revenue = billService.getStadiumRevenue(stadiumId);
        return new ApiResponse<>(revenue);
    }
    
    /**
     * Calculates total revenue for a specific location (sum of all stadiums in the location)
     * Only owner of the location or admin can access this endpoint
     * 
     * @param locationId ID of the location to calculate revenue for
     * @return Total revenue from paid bills for all stadiums in the location
     */
    @GetMapping("/revenue/location/{locationId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_ADMIN')")
    public ApiResponse<RevenueResponse> getLocationRevenue(@PathVariable("locationId") String locationId) {
        RevenueResponse revenue = billService.getLocationRevenue(locationId);
        return new ApiResponse<>(revenue);
    }
}









