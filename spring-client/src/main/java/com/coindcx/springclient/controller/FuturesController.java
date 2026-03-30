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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * Create a new futures order.
     * Accepts frontend-friendly field names:
     *   market     → pair
     *   quantity   → total_quantity
     *   order_type → order_type (market | limit_order)
     *   side       → side (buy | sell)
     *   leverage   → leverage (optional)
     *   price      → price (required for limit orders)
     * timestamp is injected automatically.
     */
    @PostMapping("/orders/create")
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> body) {
        try {
            ExchangeV1DerivativesFuturesOrdersCreatePostRequest req =
                    new ExchangeV1DerivativesFuturesOrdersCreatePostRequest();

            // pair: accept both "market" (frontend) and "pair" (direct)
            String pair = body.containsKey("pair") ? (String) body.get("pair")
                    : (String) body.get("market");
            if (pair == null || pair.isBlank()) {
                return ResponseEntity.badRequest().body("Error: 'market' or 'pair' is required");
            }
            req.setPair(pair);

            // side
            String sideStr = (String) body.get("side");
            if (sideStr == null) return ResponseEntity.badRequest().body("Error: 'side' is required");
            req.setSide(ExchangeV1DerivativesFuturesOrdersCreatePostRequest.SideEnum.fromValue(sideStr.toLowerCase()));

            // order_type
            String orderTypeStr = (String) body.get("order_type");
            if (orderTypeStr == null) return ResponseEntity.badRequest().body("Error: 'order_type' is required");
            req.setOrderType(ExchangeV1DerivativesFuturesOrdersCreatePostRequest.OrderTypeEnum.fromValue(orderTypeStr.toLowerCase()));

            // total_quantity: accept both "quantity" (frontend) and "total_quantity" (direct)
            Object qtyRaw = body.containsKey("total_quantity") ? body.get("total_quantity") : body.get("quantity");
            if (qtyRaw == null) return ResponseEntity.badRequest().body("Error: 'quantity' or 'total_quantity' is required");
            req.setTotalQuantity(new BigDecimal(qtyRaw.toString()));

            // leverage (optional)
            Object leverageRaw = body.get("leverage");
            if (leverageRaw != null) {
                req.setLeverage(new BigDecimal(leverageRaw.toString()));
            }

            // price (required for limit orders)
            Object priceRaw = body.get("price");
            if (priceRaw != null) {
                req.setPrice(new BigDecimal(priceRaw.toString()));
            }

            // stop_price (optional)
            Object stopPriceRaw = body.containsKey("stop_price") ? body.get("stop_price") : body.get("stopPrice");
            if (stopPriceRaw != null) {
                req.setStopPrice(new BigDecimal(stopPriceRaw.toString()));
            }

            // notification default
            req.setNotification(ExchangeV1DerivativesFuturesOrdersCreatePostRequest.NotificationEnum.NO_NOTIFICATION);

            // For market/stop_market/take_profit_market orders, time_in_force must not be sent
            ExchangeV1DerivativesFuturesOrdersCreatePostRequest.OrderTypeEnum orderTypeEnum = req.getOrderType();
            if (orderTypeEnum == ExchangeV1DerivativesFuturesOrdersCreatePostRequest.OrderTypeEnum.MARKET
                    || orderTypeEnum == ExchangeV1DerivativesFuturesOrdersCreatePostRequest.OrderTypeEnum.STOP_MARKET
                    || orderTypeEnum == ExchangeV1DerivativesFuturesOrdersCreatePostRequest.OrderTypeEnum.TAKE_PROFIT_MARKET) {
                req.setTimeInForce(null);
            }

            // margin_currency_short_name: only set if explicitly provided, otherwise strip default
            if (body.containsKey("margin_currency_short_name")) {
                String mcName = (String) body.get("margin_currency_short_name");
                if (mcName != null) {
                    req.setMarginCurrencyShortName(
                        ExchangeV1DerivativesFuturesOrdersCreatePostRequest.MarginCurrencyShortNameEnum.fromValue(mcName));
                } else {
                    req.setMarginCurrencyShortName(null);
                }
            } else {
                req.setMarginCurrencyShortName(null);
            }

            // timestamp (always injected server-side)
            req.setTimestamp(System.currentTimeMillis());

            String result = futuresService.createOrder(req);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(result);
        } catch (ApiException e) {
            int status = e.getCode() > 0 ? e.getCode() : 500;
            return ResponseEntity.status(status)
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
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

    /**
     * Get futures wallet balances (USDT and INR wallets).
     *
     * @return Raw wallet balance JSON from CoinDCX
     */
    @GetMapping("/wallets")
    @Operation(summary = "Get futures wallet balances", description = "Returns USDT and INR futures wallet balances")
    public ResponseEntity<String> getWallets() {
        try {
            String result = futuresService.getWallets();
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(result);
        } catch (ApiException e) {
            int status = e.getCode() > 0 ? e.getCode() : 500;
            return ResponseEntity.status(status)
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
