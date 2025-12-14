package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1FundingFetchOrdersPost200ResponseInner;
import com.coindcx.springclient.model.ExchangeV1FundingFetchOrdersPostRequest;
import com.coindcx.springclient.model.ExchangeV1FundingLendPostRequest;
import com.coindcx.springclient.service.LendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Lending operations
 */
@RestController
@RequestMapping("/api/lending")
@Tag(name = "Lending", description = "APIs for lending and funding operations")
@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "apiSecret")
public class LendController {

    private final LendService lendService;

    @Autowired
    public LendController(LendService lendService) {
        this.lendService = lendService;
    }

    /**
     * Fetch lending orders
     * 
     * @param request Fetch orders request with pagination
     * @return List of lending orders
     */
    @PostMapping("/orders/fetch")
    public ResponseEntity<List<ExchangeV1FundingFetchOrdersPost200ResponseInner>> fetchOrders(
            @RequestBody ExchangeV1FundingFetchOrdersPostRequest request) {
        try {
            List<ExchangeV1FundingFetchOrdersPost200ResponseInner> orders = lendService.fetchOrders(request);
            return ResponseEntity.ok(orders);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Create a new lending order
     * 
     * @param request Lend order request
     * @return Success response
     */
    @PostMapping("/orders/create")
    public ResponseEntity<String> createLendOrder(
            @RequestBody ExchangeV1FundingLendPostRequest request) {
        try {
            lendService.createLendOrder(request);
            return ResponseEntity.ok("Lending order created successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }
}
