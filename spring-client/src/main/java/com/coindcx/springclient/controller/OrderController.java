package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Order management operations
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for creating, managing, and querying orders")
@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "apiSecret")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get count of active orders
     * 
     * @param request Active orders request
     * @return Active orders count
     */
    @PostMapping("/active/count")
    public ResponseEntity<ExchangeV1OrdersActiveOrdersCountPost200Response> getActiveOrdersCount(
            @RequestBody ExchangeV1OrdersActiveOrdersPostRequest request) {
        try {
            ExchangeV1OrdersActiveOrdersCountPost200Response count = orderService.getActiveOrdersCount(request);
            return ResponseEntity.ok(count);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get active orders
     * 
     * @param request Active orders request
     * @return List of active orders
     */
    @PostMapping("/active")
    public ResponseEntity<List<Object>> getActiveOrders(
            @RequestBody ExchangeV1OrdersActiveOrdersPostRequest request) {
        try {
            List<Object> orders = orderService.getActiveOrders(request);
            return ResponseEntity.ok(orders);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Cancel all orders
     * 
     * @param request Cancel all request
     * @return Success response
     */
    @PostMapping("/cancel-all")
    public ResponseEntity<String> cancelAllOrders(
            @RequestBody ExchangeV1OrdersCancelAllPostRequest request) {
        try {
            orderService.cancelAllOrders(request);
            return ResponseEntity.ok("All orders cancelled successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Cancel orders by IDs
     * 
     * @param request Cancel by IDs request
     * @return Success response
     */
    @PostMapping("/cancel-by-ids")
    public ResponseEntity<String> cancelOrdersByIds(
            @RequestBody ExchangeV1OrdersCancelByIdsPostRequest request) {
        try {
            orderService.cancelOrdersByIds(request);
            return ResponseEntity.ok("Orders cancelled by IDs successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Cancel a single order
     * 
     * @param request Cancel order request
     * @return Success response
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelOrder(
            @RequestBody ExchangeV1OrdersCancelPostRequest request) {
        try {
            orderService.cancelOrder(request);
            return ResponseEntity.ok("Order cancelled successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Create multiple orders
     * 
     * @param request Multiple orders creation request
     * @return Multiple orders response
     */
    @PostMapping("/create-multiple")
    public ResponseEntity<ExchangeV1OrdersCreateMultiplePost200Response> createMultipleOrders(
            @RequestBody ExchangeV1OrdersCreateMultiplePostRequest request) {
        try {
            ExchangeV1OrdersCreateMultiplePost200Response response = orderService.createMultipleOrders(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Create a single order
     * 
     * @param request Order creation request
     * @return Order creation response
     */
    @PostMapping("/create")
    public ResponseEntity<ExchangeV1OrdersCreatePost200Response> createOrder(
            @RequestBody ExchangeV1OrdersCreatePostRequest request) {
        try {
            ExchangeV1OrdersCreatePost200Response response = orderService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Edit an existing order
     * 
     * @param request Edit order request
     * @return Edit response
     */
    @PostMapping("/edit")
    public ResponseEntity<Object> editOrder(
            @RequestBody ExchangeV1OrdersEditPostRequest request) {
        try {
            Object response = orderService.editOrder(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get status of multiple orders
     * 
     * @param request Status request for multiple orders
     * @return List of order statuses
     */
    @PostMapping("/status/multiple")
    public ResponseEntity<List<Object>> getMultipleOrdersStatus(
            @RequestBody ExchangeV1OrdersStatusMultiplePostRequest request) {
        try {
            List<Object> statuses = orderService.getMultipleOrdersStatus(request);
            return ResponseEntity.ok(statuses);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get status of a single order
     * 
     * @param request Status request
     * @return Order status
     */
    @PostMapping("/status")
    public ResponseEntity<ExchangeV1OrdersCreatePost200ResponseOrdersInner> getOrderStatus(
            @RequestBody ExchangeV1OrdersStatusPostRequest request) {
        try {
            ExchangeV1OrdersCreatePost200ResponseOrdersInner status = orderService.getOrderStatus(request);
            return ResponseEntity.ok(status);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get trade history
     * 
     * @param request Trade history request
     * @return List of trades
     */
    @PostMapping("/trade-history")
    public ResponseEntity<List<Object>> getTradeHistory(
            @RequestBody ExchangeV1OrdersTradeHistoryPostRequest request) {
        try {
            List<Object> trades = orderService.getTradeHistory(request);
            return ResponseEntity.ok(trades);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }
}
