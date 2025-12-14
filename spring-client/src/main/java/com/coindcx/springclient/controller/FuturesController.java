package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.service.FuturesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Futures trading operations
 */
@RestController
@RequestMapping("/api/futures")
@Tag(name = "Futures Trading", description = "APIs for futures/derivatives trading operations")
@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "apiSecret")
public class FuturesController {

    private final FuturesService futuresService;

    @Autowired
    public FuturesController(FuturesService futuresService) {
        this.futuresService = futuresService;
    }

    /**
     * Get active futures instruments
     * 
     * @param marginCurrencyShortName Futures margin mode
     * @return List of active instruments
     */
    @GetMapping("/instruments/active")
    public ResponseEntity<List<Object>> getActiveInstruments(
            @RequestParam String marginCurrencyShortName) {
        try {
            List<Object> instruments = futuresService.getActiveInstruments(marginCurrencyShortName);
            return ResponseEntity.ok(instruments);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Create a new futures order
     * 
     * @param request Order creation request
     * @return Success response
     */
    @PostMapping("/orders/create")
    public ResponseEntity<String> createOrder(
            @RequestBody ExchangeV1DerivativesFuturesOrdersCreatePostRequest request) {
        try {
            futuresService.createOrder(request);
            return ResponseEntity.ok("Order created successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Cancel a futures order
     * 
     * @param request Order cancellation request
     * @return Cancellation response
     */
    @PostMapping("/orders/cancel")
    public ResponseEntity<ExchangeV1DerivativesFuturesOrdersCancelPost200Response> cancelOrder(
            @RequestBody ExchangeV1DerivativesFuturesOrdersCancelPostRequest request) {
        try {
            ExchangeV1DerivativesFuturesOrdersCancelPost200Response response = futuresService.cancelOrder(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get futures orders
     * 
     * @param request Orders fetch request
     * @return Success response
     */
    @PostMapping("/orders")
    public ResponseEntity<String> getOrders(
            @RequestBody ExchangeV1DerivativesFuturesOrdersPostRequest request) {
        try {
            futuresService.getOrders(request);
            return ResponseEntity.ok("Orders fetched successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Get futures positions
     * 
     * @param request Positions fetch request
     * @return Success response
     */
    @PostMapping("/positions")
    public ResponseEntity<String> getPositions(
            @RequestBody ExchangeV1DerivativesFuturesPositionsPostRequest request) {
        try {
            futuresService.getPositions(request);
            return ResponseEntity.ok("Positions fetched successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Add margin to position
     * 
     * @param request Add margin request
     * @return Success response
     */
    @PostMapping("/positions/add-margin")
    public ResponseEntity<String> addMargin(
            @RequestBody ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest request) {
        try {
            futuresService.addMargin(request);
            return ResponseEntity.ok("Margin added successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Remove margin from position
     * 
     * @param request Remove margin request
     * @return Success response
     */
    @PostMapping("/positions/remove-margin")
    public ResponseEntity<String> removeMargin(
            @RequestBody ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest request) {
        try {
            futuresService.removeMargin(request);
            return ResponseEntity.ok("Margin removed successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Update leverage for position
     * 
     * @param request Update leverage request
     * @return Success response
     */
    @PostMapping("/positions/update-leverage")
    public ResponseEntity<String> updateLeverage(
            @RequestBody ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest request) {
        try {
            futuresService.updateLeverage(request);
            return ResponseEntity.ok("Leverage updated successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Exit a futures position
     * 
     * @param request Exit position request
     * @return Exit response
     */
    @PostMapping("/positions/exit")
    public ResponseEntity<ExchangeV1DerivativesFuturesPositionsExitPost200Response> exitPosition(
            @RequestBody ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest request) {
        try {
            ExchangeV1DerivativesFuturesPositionsExitPost200Response response = futuresService.exitPosition(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Create TP/SL for position
     * 
     * @param request TP/SL creation request
     * @return Success response
     */
    @PostMapping("/positions/create-tpsl")
    public ResponseEntity<String> createTpSl(
            @RequestBody ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest request) {
        try {
            futuresService.createTpSl(request);
            return ResponseEntity.ok("TP/SL created successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Cancel all open orders for position
     * 
     * @param request Cancel request
     * @return Success response
     */
    @PostMapping("/positions/cancel-all-orders")
    public ResponseEntity<String> cancelAllOpenOrdersForPosition(
            @RequestBody ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest request) {
        try {
            futuresService.cancelAllOpenOrdersForPosition(request);
            return ResponseEntity.ok("All open orders cancelled successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Cancel all open orders
     * 
     * @param request Cancel request
     * @return Success response
     */
    @PostMapping("/orders/cancel-all")
    public ResponseEntity<String> cancelAllOpenOrders(
            @RequestBody ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest request) {
        try {
            futuresService.cancelAllOpenOrders(request);
            return ResponseEntity.ok("All orders cancelled successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Get position transactions
     * 
     * @param request Transactions fetch request
     * @return Success response
     */
    @PostMapping("/positions/transactions")
    public ResponseEntity<String> getPositionTransactions(
            @RequestBody ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest request) {
        try {
            futuresService.getPositionTransactions(request);
            return ResponseEntity.ok("Transactions fetched successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Get futures trades
     * 
     * @param request Trades fetch request
     * @return Success response
     */
    @PostMapping("/trades")
    public ResponseEntity<String> getTrades(
            @RequestBody ExchangeV1DerivativesFuturesTradesPostRequest request) {
        try {
            futuresService.getTrades(request);
            return ResponseEntity.ok("Trades fetched successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        }
    }

    /**
     * Get futures depth/orderbook
     * 
     * @param instrument Instrument identifier
     * @param depth Depth level
     * @return Orderbook depth
     */
    @GetMapping("/depth")
    public ResponseEntity<MarketDataV3OrderbookInstrumentFuturesDepthGet200Response> getDepth(
            @RequestParam String instrument,
            @RequestParam String depth) {
        try {
            MarketDataV3OrderbookInstrumentFuturesDepthGet200Response response = futuresService.getDepth(instrument, depth);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }
}
