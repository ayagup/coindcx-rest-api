package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import com.coindcx.springclient.model.metatrader.OpenPositionRequest;
import com.coindcx.springclient.model.metatrader.OpenPositionResponse;
import com.coindcx.springclient.model.metatrader.ClosePositionResponse;
import com.coindcx.springclient.model.metatrader.TpSlRequest;
import com.coindcx.springclient.service.FuturesService;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.MetaTraderSymbolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final MetaTraderService metaTraderService;
    private final MetaTraderSymbolService metaTraderSymbolService;
    private final ObjectMapper objectMapper;

    @Autowired
    public FuturesController(FuturesService futuresService,
                             MetaTraderService metaTraderService,
                             MetaTraderSymbolService metaTraderSymbolService,
                             ObjectMapper objectMapper) {
        this.futuresService = futuresService;
        this.metaTraderService = metaTraderService;
        this.metaTraderSymbolService = metaTraderSymbolService;
        this.objectMapper = objectMapper;
    }

    /**
     * Get merged active futures instruments and MT5 tradable symbols.
     * Always fetches from both CoinDCX futures and MetaTrader 5.
     * Returns: { "futures": [...], "mt5": [...] } with optional error fields if a source fails.
     */
    @GetMapping("/instruments/active")
    @Operation(summary = "Get merged active futures instruments and MT5 symbols",
               description = "Fetches active CoinDCX futures instruments and MetaTrader 5 tradable symbols simultaneously, "
                       + "then merges them into a single response with two keys: 'futures' and 'mt5'. "
                       + "If a source fails, its error is included in 'futures_error' or 'mt5_error' "
                       + "and the other source result is still returned. "
                       + "CoinDCX param: marginCurrencyShortName (default USDT). "
                       + "MT5 param: search (case-insensitive substring filter, e.g. XAU).")
    public ResponseEntity<String> getActiveInstruments(
            @RequestParam(required = false, defaultValue = "USDT") String marginCurrencyShortName,
            @RequestParam(required = false) String search) {
        try {
            // --- CoinDCX futures instruments ---
            List<Object> futuresInstruments = new java.util.ArrayList<>();
            String futuresError = null;
            try {
                List<Object> result = futuresService.getActiveInstruments(marginCurrencyShortName);
                if (result != null) futuresInstruments = result;
            } catch (Exception e) {
                futuresError = e.getMessage();
            }

            // --- MT5 tradable symbols ---
            List<Object> mt5Symbols = new java.util.ArrayList<>();
            String mt5Error = null;
            try {
                List<Map<String, Object>> result = metaTraderService.getSymbols(search);
                if (result != null) mt5Symbols = new java.util.ArrayList<>(result);
            } catch (Exception e) {
                mt5Error = e.getMessage();
            }

            // Build merged response
            Map<String, Object> merged = new java.util.LinkedHashMap<>();
            merged.put("futures", futuresInstruments);
            merged.put("mt5", mt5Symbols);
            if (futuresError != null) merged.put("futures_error", futuresError);
            if (mt5Error != null) merged.put("mt5_error", mt5Error);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(merged));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
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
    @Operation(summary = "Create a futures order or open an MT5 position",
               description = "Creates a CoinDCX futures order by default. "
                       + "Routes to MetaTrader 5 when \"margin_currency_short_name\": \"MT5\" is set, "
                       + "or when the pair/symbol is a registered and enabled MT5 symbol. "
                       + "MT5 fields: pair/market/symbol (required), side/type/direction (buy/sell/long/short), "
                       + "margin_usdt (USDT margin to use; falls back to quantity/total_quantity if absent, must be > 0). "
                       + "leverage is accepted but ignored for MT5 — MT5 manages leverage internally.")
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> body) {
        try {
            // pair: accept pair, market, or symbol
            String pair = body.containsKey("pair") ? (String) body.get("pair")
                    : body.containsKey("market") ? (String) body.get("market")
                    : (String) body.get("symbol");

            String marginMode = (String) body.get("margin_currency_short_name");
            boolean explicitMt5 = "MT5".equalsIgnoreCase(marginMode) || "METATRADER".equalsIgnoreCase(marginMode);

            // -- MT5 routing: explicit mode flag OR pair is an MT5 symbol --
            if (explicitMt5 || (pair != null && !pair.isBlank() && isMt5Pair(pair))) {
                if (pair == null || pair.isBlank()) {
                    return ResponseEntity.badRequest()
                            .header("Content-Type", "application/json")
                            .body("{\"error\":\"MT5 order requires 'pair', 'market', or 'symbol' field\"}");
                }
                // direction: accept side, type, or direction; normalise to long/short
                String raw = body.containsKey("side") ? (String) body.get("side")
                        : body.containsKey("type") ? (String) body.get("type")
                        : (String) body.get("direction");
                String direction = (raw != null && ("sell".equalsIgnoreCase(raw) || "short".equalsIgnoreCase(raw)))
                        ? "short" : "long";
                // margin_usdt: accept margin_usdt directly, or fall back to quantity/total_quantity
                Object marginRaw = body.containsKey("margin_usdt") ? body.get("margin_usdt")
                        : body.containsKey("quantity") ? body.get("quantity")
                        : body.get("total_quantity");
                if (marginRaw == null) {
                    return ResponseEntity.badRequest()
                            .header("Content-Type", "application/json")
                            .body("{\"error\":\"margin_usdt (or quantity) is required for MT5 orders\"}");
                }
                Double marginUsdt = Double.parseDouble(marginRaw.toString());
                if (marginUsdt <= 0) {
                    return ResponseEntity.badRequest()
                            .header("Content-Type", "application/json")
                            .body("{\"error\":\"margin_usdt must be greater than 0\"}");
                }
                OpenPositionResponse mt5Resp = metaTraderService.openPosition(
                        new OpenPositionRequest(pair, direction, marginUsdt));
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(mt5Resp));
            }

            // CoinDCX futures path
            ExchangeV1DerivativesFuturesOrdersCreatePostRequest req =
                    new ExchangeV1DerivativesFuturesOrdersCreatePostRequest();

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
            // Truncate to 3 decimal places (CoinDCX step size = 0.001) — use DOWN so we never exceed intended margin
            Object qtyRaw = body.containsKey("total_quantity") ? body.get("total_quantity") : body.get("quantity");
            if (qtyRaw == null) return ResponseEntity.badRequest().body("Error: 'quantity' or 'total_quantity' is required");
            req.setTotalQuantity(new BigDecimal(qtyRaw.toString()).setScale(3, RoundingMode.DOWN));

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
    @Operation(summary = "Get futures or MT5 pending orders",
               description = "Returns CoinDCX futures orders by default. "
                       + "Set \"margin_currency_short_name\": \"MT5\" to retrieve all MetaTrader 5 pending orders, "
                       + "or include a \"pair\" that is an enabled MT5 symbol to filter by that symbol.")
    public ResponseEntity<String> getOrders(
            @RequestBody Map<String, Object> body) {
        try {
            String pair = body.containsKey("pair") ? (String) body.get("pair") : (String) body.get("market");
            String marginMode = (String) body.get("margin_currency_short_name");

            // -- MT5 routing: triggered by explicit MT5 margin mode OR an MT5 symbol pair --
            if ("MT5".equalsIgnoreCase(marginMode) || "METATRADER".equalsIgnoreCase(marginMode)
                    || (pair != null && !pair.isBlank() && isMt5Pair(pair))) {
                String symbolFilter = (pair != null && !pair.isBlank()) ? pair : null;
                List<Object> mt5Orders = metaTraderService.getOrders(symbolFilter);
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(mt5Orders));
            }
            ExchangeV1DerivativesFuturesOrdersPostRequest request =
                    objectMapper.convertValue(body, ExchangeV1DerivativesFuturesOrdersPostRequest.class);
            futuresService.getOrders(request);
            return ResponseEntity.ok("Orders fetched successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Get futures positions
     * 
     * @param request Positions fetch request
     * @return Success response
     */
    @PostMapping("/positions")
    @Operation(summary = "Get futures or MT5 open positions",
               description = "Returns CoinDCX futures positions by default. "
                       + "Set \"margin_currency_short_name\": \"MT5\" to retrieve all MetaTrader 5 positions, "
                       + "or include a \"pair\" that is an enabled MT5 symbol to filter by that symbol.")
    public ResponseEntity<String> getPositions(
            @RequestBody Map<String, Object> body) {
        try {
            String pair = body.containsKey("pair") ? (String) body.get("pair") : (String) body.get("market");
            String marginMode = (String) body.get("margin_currency_short_name");

            // -- MT5 routing: triggered by explicit MT5 margin mode OR an MT5 symbol pair --
            if ("MT5".equalsIgnoreCase(marginMode) || "METATRADER".equalsIgnoreCase(marginMode)
                    || (pair != null && !pair.isBlank() && isMt5Pair(pair))) {
                String symbolFilter = (pair != null && !pair.isBlank()) ? pair : null;
                List<Object> mt5Positions = metaTraderService.getPositions(symbolFilter);
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(mt5Positions));
            }
            ExchangeV1DerivativesFuturesPositionsPostRequest request =
                    objectMapper.convertValue(body, ExchangeV1DerivativesFuturesPositionsPostRequest.class);
            futuresService.getPositions(request);
            return ResponseEntity.ok("Positions fetched successfully");
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
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
     * Exit a futures position.
     * Accepts frontend-friendly fields:
     *   market      → pair used to resolve position id from the positions API
     *   position_id → direct position UUID (overrides market lookup)
     * timestamp is injected automatically.
     */
    @PostMapping("/positions/exit")
    @Operation(summary = "Exit a futures position or close an MT5 position",
               description = "Closes a CoinDCX futures position by pair/position_id by default. "
                       + "Routes to MetaTrader 5 when a 'ticket' field (long) is present in the body — "
                       + "closes the MT5 position with that ticket and cancels all pending orders on the same symbol. "
                       + "MT5 fields: ticket (required). CoinDCX fields: market/pair, position_id (optional).")
    public ResponseEntity<Object> exitPosition(@RequestBody Map<String, Object> body) {
        try {
            String pair = body.containsKey("market") ? (String) body.get("market")
                    : (String) body.get("pair");

            // -- MT5 routing: close position by ticket (MetaTrader positions use ticket IDs) --
            if (body.containsKey("ticket")) {
                Long ticket = Long.parseLong(body.get("ticket").toString());
                ClosePositionResponse mt5Resp = metaTraderService.closePosition(ticket);
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(mt5Resp));
            }
            if (pair != null && !pair.isBlank() && isMt5Pair(pair)) {
                // Auto-resolve: find the first open MT5 position for this symbol
                @SuppressWarnings("unchecked")
                List<Object> positions = metaTraderService.getPositions(pair);
                if (positions == null || positions.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .header("Content-Type", "application/json")
                            .body("{\"error\":\"No open MT5 position found for symbol: " + pair
                                    + ". Provide the 'ticket' field explicitly to close.\"}" );
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> firstPos = (Map<String, Object>) positions.get(0);
                Object ticketRaw = firstPos.get("ticket");
                if (ticketRaw == null) {
                    return ResponseEntity.badRequest()
                            .header("Content-Type", "application/json")
                            .body("{\"error\":\"MT5 position for " + pair + " has no ticket field\"}");
                }
                Long resolvedTicket = Long.parseLong(ticketRaw.toString());
                ClosePositionResponse mt5Resp = metaTraderService.closePosition(resolvedTicket);
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(mt5Resp));
            }

            // Resolve position id
            String positionId = null;
            if (body.containsKey("position_id") && body.get("position_id") != null) {
                positionId = body.get("position_id").toString();
            } else {
                if (pair != null && !pair.isBlank()) {
                    positionId = futuresService.fetchActivePositionId(pair);
                }
            }
            if (positionId == null || positionId.isBlank()) {
                String pairDesc = body.getOrDefault("market", body.getOrDefault("pair", "unknown")).toString();
                return ResponseEntity.badRequest()
                        .header("Content-Type", "application/json")
                        .body("{\"error\":\"No active position found for " + pairDesc
                                + ". Cannot exit a position that is not open.\"}");
            }

            ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest request =
                    new ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest();
            request.setTimestamp(System.currentTimeMillis());
            request.setId(positionId);

            ExchangeV1DerivativesFuturesPositionsExitPost200Response response = futuresService.exitPosition(request, pair);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode())
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    /**
     * Create TP/SL for position.
     * Accepts frontend-friendly fields:
     *   market       → pair used to resolve position id from WebSocket data
     *   position_id  → direct position id (overrides market lookup)
     *   target_price → mapped to take_profit.stop_price (take_profit_market)
     *   stop_loss    → mapped to stop_loss.stop_price  (stop_market)
     * timestamp is injected automatically.
     */
    @PostMapping("/positions/create-tpsl")
    @Operation(summary = "Set TP/SL on a futures or MT5 position",
               description = "Sets take-profit and/or stop-loss on a CoinDCX futures position by pair/position_id by default. "
                       + "Routes to MetaTrader 5 when a 'ticket' field (long) is present in the body. "
                       + "MT5 fields: ticket (required), target_price/take_profit_price (TP, optional), stop_loss/stop_loss_price (SL, optional). "
                       + "At least one of TP or SL must be provided. CoinDCX fields: market/pair, position_id (optional), target_price, stop_loss.")
    public ResponseEntity<String> createTpSl(@RequestBody Map<String, Object> body) {
        try {
            String pair = body.containsKey("market") ? (String) body.get("market")
                    : (String) body.get("pair");

            // -- MT5 routing: set TP/SL by ticket, or auto-resolve ticket from open position by symbol --
            Object tpRawMt5 = body.containsKey("target_price") ? body.get("target_price") : body.get("take_profit_price");
            Object slRawMt5 = body.containsKey("stop_loss") ? body.get("stop_loss") : body.get("stop_loss_price");

            boolean isMt5Symbol = pair != null && !pair.isBlank() && isMt5Pair(pair);
            boolean hasTicket   = body.containsKey("ticket");

            if (hasTicket || isMt5Symbol) {
                Long ticket;
                if (hasTicket) {
                    ticket = Long.parseLong(body.get("ticket").toString());
                } else {
                    // Auto-resolve: find the first open MT5 position for this symbol
                    List<Object> positions = metaTraderService.getPositions(pair);
                    if (positions == null || positions.isEmpty()) {
                        return ResponseEntity.badRequest()
                                .header("Content-Type", "application/json")
                                .body("{\"error\":\"No open MT5 position found for symbol: " + pair
                                        + ". Cannot set TP/SL on a position that does not exist.\"}");
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, Object> firstPos = (Map<String, Object>) positions.get(0);
                    Object ticketRaw = firstPos.get("ticket");
                    if (ticketRaw == null) {
                        return ResponseEntity.badRequest()
                                .header("Content-Type", "application/json")
                                .body("{\"error\":\"MT5 position for " + pair + " has no ticket field\"}");
                    }
                    ticket = Long.parseLong(ticketRaw.toString());
                }
                if (tpRawMt5 == null && slRawMt5 == null) {
                    return ResponseEntity.badRequest()
                            .header("Content-Type", "application/json")
                            .body("{\"error\":\"At least one of 'target_price'/'take_profit_price' (TP) or 'stop_loss'/'stop_loss_price' (SL) must be provided\"}");
                }
                Map<String, Object> mt5Resp = metaTraderService.setTpSl(new TpSlRequest(
                        ticket,
                        tpRawMt5 != null ? Double.parseDouble(tpRawMt5.toString()) : null,
                        slRawMt5 != null ? Double.parseDouble(slRawMt5.toString()) : null
                ));
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(objectMapper.writeValueAsString(mt5Resp));
            }

            // Resolve position id
            String positionId = null;
            if (body.containsKey("position_id") && body.get("position_id") != null) {
                positionId = body.get("position_id").toString();
            } else {
                if (pair != null && !pair.isBlank()) {
                    positionId = futuresService.fetchActivePositionId(pair);
                }
            }
            if (positionId == null || positionId.isBlank()) {
                return ResponseEntity.badRequest()
                        .header("Content-Type", "application/json")
                        .body("{\"error\":\"No active (filled) position found for " +
                                (body.containsKey("market") ? body.get("market") : body.get("pair")) +
                                ". TP/SL can only be set on a position with non-zero active_pos. " +
                                "Pending limit orders are not eligible.\"}");
            }

            ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest req =
                    new ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest();
            req.setTimestamp(System.currentTimeMillis());
            req.setId(positionId);

            // take_profit from target_price
            Object tpRaw = body.containsKey("target_price") ? body.get("target_price") : body.get("take_profit_price");
            if (tpRaw != null) {
                ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit tp =
                        new ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit();
                tp.setStopPrice(new BigDecimal(tpRaw.toString()).toPlainString());
                tp.setOrderType(ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit.OrderTypeEnum.TAKE_PROFIT_MARKET);
                req.setTakeProfit(tp);
            }

            // stop_loss from stop_loss field
            Object slRaw = body.containsKey("stop_loss") ? body.get("stop_loss") : body.get("stop_loss_price");
            if (slRaw != null) {
                ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss sl =
                        new ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss();
                sl.setStopPrice(new BigDecimal(slRaw.toString()).toPlainString());
                sl.setOrderType(ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss.OrderTypeEnum.STOP_MARKET);
                req.setStopLoss(sl);
            }

            if (req.getTakeProfit() == null && req.getStopLoss() == null) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"At least one of 'target_price' (TP) or 'stop_loss' (SL) must be provided\"}");
            }

            futuresService.createTpSl(req, pair);
            return ResponseEntity.ok("{\"message\":\"TP/SL created successfully\"}");
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
     * Get merged futures position transactions and MT5 closed-position PnL.
     * Always fetches from both CoinDCX futures and MetaTrader 5.
     * Shared params: from (YYYY-MM-DD), to (YYYY-MM-DD), symbol.
     * CoinDCX-only params: stage, page, size, margin_currency_short_name.
     * Returns: { "futures": [...], "mt5": [...] } with optional error fields if a source fails.
     */
    @PostMapping("/positions/transactions")
    @Operation(summary = "Get merged futures position transactions and MT5 PnL history",
               description = "Fetches CoinDCX futures position transactions and MetaTrader 5 closed-position PnL simultaneously, "
                       + "then merges the results into a single response with two keys: 'futures' and 'mt5'. "
                       + "If a source fails, its error is included in 'futures_error' or 'mt5_error' "
                       + "and the other source result is still returned. "
                       + "Shared optional params: from (YYYY-MM-DD), to (YYYY-MM-DD), symbol. "
                       + "CoinDCX-only params: stage (all/default/funding/exit/tpsl_exit/liquidation), page, size.")
    public ResponseEntity<String> getPositionTransactions(
            @RequestBody Map<String, Object> body) {
        try {
            // Shared date / symbol params used by MT5 source
            String from   = body.containsKey("from")   ? (String) body.get("from")   : null;
            String to     = body.containsKey("to")     ? (String) body.get("to")     : null;
            String symbol = body.containsKey("symbol") ? (String) body.get("symbol") : null;

            // --- CoinDCX futures position transactions ---
            List<Object> futuresTxns = new java.util.ArrayList<>();
            String futuresError = null;
            try {
                ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest request =
                        objectMapper.convertValue(body, ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest.class);
                if (request.getTimestamp() == null) request.setTimestamp(System.currentTimeMillis());
                String rawFutures = futuresService.getPositionTransactions(request);
                try {
                    List<?> parsed = objectMapper.readValue(rawFutures, List.class);
                    futuresTxns = new java.util.ArrayList<>(parsed);
                } catch (Exception ignored) {
                    // Non-array body — leave futures empty
                }
            } catch (Exception e) {
                futuresError = e.getMessage();
            }

            // --- MT5 closed-position PnL ---
            List<Object> mt5Pnl = new java.util.ArrayList<>();
            String mt5Error = null;
            try {
                List<Object> result = metaTraderService.getHistoryPnl(from, to, symbol);
                if (result != null) mt5Pnl = result;
            } catch (Exception e) {
                mt5Error = e.getMessage();
            }

            // Build merged response
            Map<String, Object> merged = new java.util.LinkedHashMap<>();
            merged.put("futures", futuresTxns);
            merged.put("mt5", mt5Pnl);
            if (futuresError != null) merged.put("futures_error", futuresError);
            if (mt5Error != null) merged.put("mt5_error", mt5Error);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(merged));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    /**
     * Get merged futures and MT5 trade history.
     * Always fetches from both CoinDCX futures and MetaTrader 5.
     * Shared params: from (YYYY-MM-DD), to (YYYY-MM-DD), symbol/pair.
     * CoinDCX-only params: order_id, page, size, margin_currency_short_name.
     * Returns: { "futures": [...], "mt5": [...] } with optional error fields if a source fails.
     */
    @PostMapping("/trades")
    @Operation(summary = "Get merged futures and MT5 trade history",
               description = "Fetches trade history from both CoinDCX futures and MetaTrader 5 simultaneously, "
                       + "then merges the results into a single response with two keys: 'futures' and 'mt5'. "
                       + "If a source fails, its error is included in 'futures_error' or 'mt5_error' "
                       + "and the other source result is still returned. "
                       + "Shared optional params: from (YYYY-MM-DD), to (YYYY-MM-DD), symbol/pair. "
                       + "CoinDCX-only params: order_id, page, size.")
    public ResponseEntity<String> getTrades(
            @RequestBody Map<String, Object> body) {
        try {
            // Shared date / symbol params used by both sources
            String from   = body.containsKey("from")   ? (String) body.get("from")   : null;
            String to     = body.containsKey("to")     ? (String) body.get("to")     : null;
            String symbol = body.containsKey("symbol") ? (String) body.get("symbol")
                          : body.containsKey("pair")   ? (String) body.get("pair")   : null;

            // --- CoinDCX futures trades ---
            List<Object> futuresTrades = new java.util.ArrayList<>();
            String futuresError = null;
            try {
                ExchangeV1DerivativesFuturesTradesPostRequest request =
                        objectMapper.convertValue(body, ExchangeV1DerivativesFuturesTradesPostRequest.class);
                if (request.getTimestamp() == null) request.setTimestamp(System.currentTimeMillis());
                String rawFutures = futuresService.getTrades(request);
                try {
                    List<?> parsed = objectMapper.readValue(rawFutures, List.class);
                    futuresTrades = new java.util.ArrayList<>(parsed);
                } catch (Exception ignored) {
                    // Non-array body — leave futures empty
                }
            } catch (Exception e) {
                futuresError = e.getMessage();
            }

            // --- MT5 history trades ---
            List<Object> mt5Trades = new java.util.ArrayList<>();
            String mt5Error = null;
            try {
                List<Object> result = metaTraderService.getHistoryTrades(from, to, symbol);
                if (result != null) mt5Trades = result;
            } catch (Exception e) {
                mt5Error = e.getMessage();
            }

            // Build merged response
            Map<String, Object> merged = new java.util.LinkedHashMap<>();
            merged.put("futures", futuresTrades);
            merged.put("mt5", mt5Trades);
            if (futuresError != null) merged.put("futures_error", futuresError);
            if (mt5Error != null) merged.put("mt5_error", mt5Error);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(merged));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    /**
     * Get merged futures and MT5 account leverage.
     * CoinDCX does not expose a standalone account-level leverage endpoint;
     * leverage on CoinDCX is configured per-position via the update-leverage endpoint.
     * MT5 leverage is returned from the MetaTrader 5 account info.
     * Returns: { "futures": { "note": "..." }, "mt5": { "leverage": ..., "display": "..." } }
     * with optional error fields if a source fails.
     */
    @GetMapping("/leverage")
    @Operation(summary = "Get futures and MT5 account leverage",
               description = "Returns MetaTrader 5 account leverage and a note about CoinDCX leverage. "
                       + "CoinDCX does not have a standalone account leverage endpoint — leverage is configured "
                       + "per-position via POST /api/futures/positions/update-leverage. "
                       + "MT5 returns leverage as an integer and display string e.g. '1:500'. "
                       + "If MT5 fails, 'mt5_error' is included and 'futures' note is still returned.")
    public ResponseEntity<String> getLeverage() {
        try {
            // --- CoinDCX: leverage is per-position, no dedicated GET endpoint ---
            Map<String, Object> futuresInfo = new java.util.LinkedHashMap<>();
            futuresInfo.put("note", "CoinDCX leverage is configured per-position. "
                    + "Use POST /api/futures/positions/update-leverage to set it, "
                    + "or retrieve current leverage from POST /api/futures/positions.");

            // --- MT5 account leverage ---
            Object mt5Leverage = null;
            String mt5Error = null;
            try {
                mt5Leverage = metaTraderService.getLeverage();
            } catch (Exception e) {
                mt5Error = e.getMessage();
            }

            // Build merged response
            Map<String, Object> merged = new java.util.LinkedHashMap<>();
            merged.put("futures", futuresInfo);
            merged.put("mt5", mt5Leverage != null ? mt5Leverage : new java.util.LinkedHashMap<>());
            if (mt5Error != null) merged.put("mt5_error", mt5Error);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(merged));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    /**
     * Get merged futures and MT5 candlestick OHLCV data.
     * CoinDCX params: pair (required), from (epoch ms), to (epoch ms), resolution (e.g. 1, 5, 15, 60, 1D).
     * MT5 params are inferred: resolution → timeframe, from/to epoch ms → YYYY-MM-DDTHH:MM:SS date strings.
     * Returns: { "futures": [...], "mt5": [...] } with optional error fields if a source fails.
     */
    @GetMapping("/candles")
    @Operation(summary = "Get merged futures and MT5 candlestick data",
               description = "Fetches OHLCV candlestick bars from both CoinDCX futures and MetaTrader 5 simultaneously. "
                       + "CoinDCX params: pair (required), from (epoch ms), to (epoch ms), "
                       + "resolution (e.g. 1, 5, 15, 30, 60, 1D; default 1D). "
                       + "MT5 timeframe and date range are automatically inferred from these same parameters. "
                       + "Resolution mapping: 1→M1, 5→M5, 15→M15, 30→M30, 60→H1, 240→H4, 1D→D1, 1W→W1. "
                       + "If a source fails, its error is in 'futures_error' or 'mt5_error'.")
    public ResponseEntity<String> getCandlesticks(
            @Parameter(description = "Trading pair / symbol, e.g. BTCUSDT or XAUUSD", required = true)
            @RequestParam String pair,
            @Parameter(description = "Start timestamp (epoch ms)")
            @RequestParam(required = false) Long from,
            @Parameter(description = "End timestamp (epoch ms)")
            @RequestParam(required = false) Long to,
            @Parameter(description = "Candle resolution, e.g. 1, 5, 15, 30, 60, 1D (default 1D)")
            @RequestParam(required = false, defaultValue = "1D") String resolution) {
        try {
            // Infer MT5 timeframe from CoinDCX resolution
            String mt5Timeframe = resolutionToMt5Timeframe(resolution);

            // Infer MT5 date strings from epoch ms (YYYY-MM-DDTHH:MM:SS)
            String mt5From = from != null ? epochMsToMt5Date(from) : null;
            String mt5To   = to   != null ? epochMsToMt5Date(to)   : null;

            // When no date range, default count=100; MT5 handles the rest when dates are present
            Integer mt5Count = (mt5From == null && mt5To == null) ? 100 : null;

            // --- CoinDCX futures candlesticks ---
            List<Object> futuresCandles = new java.util.ArrayList<>();
            String futuresError = null;
            try {
                List<Object> result = futuresService.getCandlesticks(pair, from, to, resolution);
                if (result != null) futuresCandles = result;
            } catch (Exception e) {
                futuresError = e.getMessage();
            }

            // --- MT5 candlesticks (inferred params) ---
            List<Object> mt5Candles = new java.util.ArrayList<>();
            String mt5Error = null;
            try {
                List<Object> result = metaTraderService.getCandlesticks(pair, mt5Timeframe, mt5Count, mt5From, mt5To);
                if (result != null) mt5Candles = result;
            } catch (Exception e) {
                mt5Error = e.getMessage();
            }

            // Build merged response
            Map<String, Object> merged = new java.util.LinkedHashMap<>();
            merged.put("futures", futuresCandles);
            merged.put("mt5", mt5Candles);
            if (futuresError != null) merged.put("futures_error", futuresError);
            if (mt5Error != null) merged.put("mt5_error", mt5Error);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(merged));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    /** Maps a CoinDCX resolution string to an MT5 timeframe string. */
    private static String resolutionToMt5Timeframe(String resolution) {
        if (resolution == null) return "D1";
        switch (resolution.toUpperCase()) {
            case "1":   return "M1";
            case "2":   return "M2";
            case "3":   return "M3";
            case "4":   return "M4";
            case "5":   return "M5";
            case "6":   return "M6";
            case "10":  return "M10";
            case "12":  return "M12";
            case "15":  return "M15";
            case "20":  return "M20";
            case "30":  return "M30";
            case "60":  case "1H":  return "H1";
            case "120": case "2H":  return "H2";
            case "180": case "3H":  return "H3";
            case "240": case "4H":  return "H4";
            case "360": case "6H":  return "H6";
            case "480": case "8H":  return "H8";
            case "720": case "12H": return "H12";
            case "1D":  case "D":   case "D1":  return "D1";
            case "1W":  case "W":   case "W1":  return "W1";
            case "1M":  case "MN1": return "MN1";
            default:    return "D1";
        }
    }

    /** Converts epoch milliseconds to MT5 date string YYYY-MM-DDTHH:MM:SS. */
    private static String epochMsToMt5Date(long epochMs) {
        java.time.Instant instant = java.time.Instant.ofEpochMilli(epochMs);
        java.time.LocalDateTime ldt = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC);
        return ldt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
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
     * Get merged futures wallet balances and MT5 wallet balance.
     * Always fetches from both CoinDCX futures wallets and MetaTrader 5.
     * Returns: { "futures": {...}, "mt5": {...} } with optional error fields if a source fails.
     */
    @GetMapping("/wallets")
    @Operation(summary = "Get merged futures and MT5 wallet balances",
               description = "Fetches CoinDCX USDT/INR futures wallet balances and MetaTrader 5 wallet balance simultaneously, "
                       + "then merges them into { \"futures\": {...}, \"mt5\": {...} }. "
                       + "If a source fails, its error is included in 'futures_error' or 'mt5_error' "
                       + "and the other source result is still returned.")
    public ResponseEntity<String> getWallets() {
        try {
            // --- CoinDCX futures wallets ---
            Object futuresWallets = null;
            String futuresError = null;
            try {
                String raw = futuresService.getWallets();
                futuresWallets = objectMapper.readValue(raw, Object.class);
            } catch (Exception e) {
                futuresError = e.getMessage();
            }

            // --- MT5 wallet balance ---
            Object mt5Balance = null;
            String mt5Error = null;
            try {
                mt5Balance = metaTraderService.getBalance();
            } catch (Exception e) {
                mt5Error = e.getMessage();
            }

            // Build merged response
            java.util.Map<String, Object> merged = new java.util.LinkedHashMap<>();
            merged.put("futures", futuresWallets != null ? futuresWallets : new java.util.LinkedHashMap<>());
            merged.put("mt5", mt5Balance != null ? mt5Balance : new java.util.LinkedHashMap<>());
            if (futuresError != null) merged.put("futures_error", futuresError);
            if (mt5Error != null) merged.put("mt5_error", mt5Error);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(merged));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    /**
     * Returns true when {@code pair} should be routed to MetaTrader 5.
     * Triggered when the symbol is registered & enabled in the local registry,
     * OR when it clearly is not a CoinDCX pair (CoinDCX futures pairs always
     * contain an underscore, e.g. "B-BTC_USDT"; MT5 symbols like "XAUUSD" do not).
     */
    private boolean isMt5Pair(String pair) {
        if (pair == null || pair.isBlank()) return false;
        if (metaTraderSymbolService.isSymbolEnabled(pair)) return true;
        // CoinDCX pairs always contain '_'; no underscore → treat as MT5
        return !pair.contains("_");
    }
}
