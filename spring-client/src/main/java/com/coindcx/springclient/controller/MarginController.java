package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.MarginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Margin trading operations
 */
@RestController
@RequestMapping("/api/margin")
@Tag(name = "Margin Trading", description = "APIs for margin trading operations")
@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "apiSecret")
public class MarginController {

    private final MarginService marginService;

    @Autowired
    public MarginController(MarginService marginService) {
        this.marginService = marginService;
    }

    /**
     * Add margin to position
     * 
     * @param request Add margin request
     * @return Add margin response
     */
    @PostMapping("/add-margin")
    public ResponseEntity<ExchangeV1MarginAddMarginPost200Response> addMargin(
            @RequestBody ExchangeV1MarginAddMarginPostRequest request) {
        try {
            ExchangeV1MarginAddMarginPost200Response response = marginService.addMargin(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Cancel a margin order
     * 
     * @param request Cancel order request
     * @return Success response
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelOrder(
            @RequestBody ExchangeV1MarginCancelPostRequest request) {
        try {
            marginService.cancelOrder(request);
            return ResponseEntity.ok("Margin order cancelled successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Create margin orders
     * 
     * @param request Order creation request
     * @return List of created orders
     */
    @PostMapping("/create")
    public ResponseEntity<List<ExchangeV1MarginCreatePost200ResponseInner>> createOrders(
            @RequestBody ExchangeV1MarginCreatePostRequest request) {
        try {
            List<ExchangeV1MarginCreatePost200ResponseInner> orders = marginService.createOrders(request);
            return ResponseEntity.ok(orders);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Edit price of target order
     * 
     * @param request Edit request
     * @return Success response
     */
    @PostMapping("/edit-target-price")
    public ResponseEntity<String> editPriceOfTargetOrder(
            @RequestBody ExchangeV1MarginEditPriceOfTargetOrderPostRequest request) {
        try {
            marginService.editPriceOfTargetOrder(request);
            return ResponseEntity.ok("Target price updated successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Edit stop loss
     * 
     * @param request Edit SL request
     * @return Success response
     */
    @PostMapping("/edit-sl")
    public ResponseEntity<String> editStopLoss(
            @RequestBody ExchangeV1MarginEditSlPostRequest request) {
        try {
            marginService.editStopLoss(request);
            return ResponseEntity.ok("Stop loss updated successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Edit target
     * 
     * @param request Edit target request
     * @return Success response
     */
    @PostMapping("/edit-target")
    public ResponseEntity<String> editTarget(
            @RequestBody ExchangeV1MarginEditTargetPostRequest request) {
        try {
            marginService.editTarget(request);
            return ResponseEntity.ok("Target updated successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Edit trailing stop loss
     * 
     * @param request Edit trailing SL request
     * @return Success response
     */
    @PostMapping("/edit-trailing-sl")
    public ResponseEntity<String> editTrailingStopLoss(
            @RequestBody ExchangeV1MarginEditTrailingSlPostRequest request) {
        try {
            marginService.editTrailingStopLoss(request);
            return ResponseEntity.ok("Trailing stop loss updated successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Exit a margin position
     * 
     * @param request Exit request
     * @return Success response
     */
    @PostMapping("/exit")
    public ResponseEntity<String> exitPosition(
            @RequestBody ExchangeV1MarginExitPostRequest request) {
        try {
            marginService.exitPosition(request);
            return ResponseEntity.ok("Position exited successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Fetch margin orders
     * 
     * @param request Fetch orders request
     * @return List of margin orders
     */
    @PostMapping("/orders")
    public ResponseEntity<List<ExchangeV1MarginFetchOrdersPost200ResponseInner>> fetchOrders(
            @RequestBody ExchangeV1MarginFetchOrdersPostRequest request) {
        try {
            List<ExchangeV1MarginFetchOrdersPost200ResponseInner> orders = marginService.fetchOrders(request);
            return ResponseEntity.ok(orders);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get margin order status
     * 
     * @param request Order request
     * @return Order status
     */
    @PostMapping("/order-status")
    public ResponseEntity<ExchangeV1MarginFetchOrdersPost200ResponseInner> getOrderStatus(
            @RequestBody ExchangeV1MarginOrderPostRequest request) {
        try {
            ExchangeV1MarginFetchOrdersPost200ResponseInner status = marginService.getOrderStatus(request);
            return ResponseEntity.ok(status);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Remove margin from position
     * 
     * @param request Remove margin request
     * @return Remove margin response
     */
    @PostMapping("/remove-margin")
    public ResponseEntity<ExchangeV1MarginRemoveMarginPost200Response> removeMargin(
            @RequestBody ExchangeV1MarginRemoveMarginPostRequest request) {
        try {
            ExchangeV1MarginRemoveMarginPost200Response response = marginService.removeMargin(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }
}
