package com.coindcx.springclient.service;

import com.coindcx.springclient.entity.WebSocketSpotBalanceData;
import com.coindcx.springclient.entity.WebSocketSpotOrderUpdateData;
import com.coindcx.springclient.entity.WebSocketSpotTradeUpdateData;
import com.coindcx.springclient.entity.WebSocketSpotCandlestickData;
import com.coindcx.springclient.entity.WebSocketSpotDepthSnapshotData;
import com.coindcx.springclient.entity.WebSocketSpotDepthUpdateData;
import com.coindcx.springclient.entity.WebSocketSpotCurrentPriceData;
import com.coindcx.springclient.entity.WebSocketSpotPriceStatsData;
import com.coindcx.springclient.entity.WebSocketSpotNewTradeData;
import com.coindcx.springclient.entity.WebSocketSpotPriceChangeData;
import com.coindcx.springclient.entity.WebSocketFuturesPositionUpdateData;
import com.coindcx.springclient.entity.WebSocketFuturesOrderUpdateData;
import com.coindcx.springclient.entity.WebSocketFuturesData;
import com.coindcx.springclient.model.WebSocketBalanceUpdateData;
import com.coindcx.springclient.model.WebSocketFuturesCandlestickData;
import com.coindcx.springclient.model.WebSocketFuturesOrderbookData;
import com.coindcx.springclient.model.WebSocketFuturesCurrentPricesData;
import com.coindcx.springclient.model.WebSocketFuturesNewTradeData;
import com.coindcx.springclient.entity.WebSocketSpotData;
import com.coindcx.springclient.repository.WebSocketSpotBalanceDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotOrderUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotTradeUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotCandlestickDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDepthSnapshotDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDepthUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotCurrentPriceDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotPriceStatsDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotNewTradeDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotPriceChangeDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesPositionUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesOrderUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesDataRepository;
import com.coindcx.springclient.repository.WebSocketBalanceUpdateDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesCandlestickDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesOrderbookDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesCurrentPricesDataRepository;
import com.coindcx.springclient.repository.WebSocketFuturesNewTradeDataRepository;
import com.coindcx.springclient.repository.WebSocketSpotDataRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to persist WebSocket data to database
 */
@Service
public class WebSocketDataPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketDataPersistenceService.class);

    private final WebSocketSpotDataRepository spotDataRepository;
    private final WebSocketFuturesDataRepository futuresDataRepository;
    private final WebSocketSpotBalanceDataRepository spotBalanceDataRepository;
    private final WebSocketSpotOrderUpdateDataRepository spotOrderUpdateDataRepository;
    private final WebSocketSpotTradeUpdateDataRepository spotTradeUpdateDataRepository;
    private final WebSocketSpotCandlestickDataRepository spotCandlestickDataRepository;
    private final WebSocketSpotDepthSnapshotDataRepository spotDepthSnapshotDataRepository;
    private final WebSocketSpotDepthUpdateDataRepository spotDepthUpdateDataRepository;
    private final WebSocketSpotCurrentPriceDataRepository spotCurrentPriceDataRepository;
    private final WebSocketSpotPriceStatsDataRepository spotPriceStatsDataRepository;
    private final WebSocketSpotNewTradeDataRepository spotNewTradeDataRepository;
    private final WebSocketSpotPriceChangeDataRepository spotPriceChangeDataRepository;
    private final WebSocketFuturesPositionUpdateDataRepository futuresPositionUpdateDataRepository;
    private final WebSocketFuturesOrderUpdateDataRepository futuresOrderUpdateDataRepository;
    private final WebSocketBalanceUpdateDataRepository balanceUpdateDataRepository;
    private final WebSocketFuturesCandlestickDataRepository futuresCandlestickDataRepository;
    private final WebSocketFuturesOrderbookDataRepository futuresOrderbookDataRepository;
    private final WebSocketFuturesCurrentPricesDataRepository futuresCurrentPricesDataRepository;
    private final WebSocketFuturesNewTradeDataRepository futuresNewTradeDataRepository;
    private final Gson gson;

    public WebSocketDataPersistenceService(
            WebSocketSpotDataRepository spotDataRepository,
            WebSocketFuturesDataRepository futuresDataRepository,
            WebSocketSpotBalanceDataRepository spotBalanceDataRepository,
            WebSocketSpotOrderUpdateDataRepository spotOrderUpdateDataRepository,
            WebSocketSpotTradeUpdateDataRepository spotTradeUpdateDataRepository,
            WebSocketSpotCandlestickDataRepository spotCandlestickDataRepository,
            WebSocketSpotDepthSnapshotDataRepository spotDepthSnapshotDataRepository,
            WebSocketSpotDepthUpdateDataRepository spotDepthUpdateDataRepository,
            WebSocketSpotCurrentPriceDataRepository spotCurrentPriceDataRepository,
            WebSocketSpotPriceStatsDataRepository spotPriceStatsDataRepository,
            WebSocketSpotNewTradeDataRepository spotNewTradeDataRepository,
            WebSocketSpotPriceChangeDataRepository spotPriceChangeDataRepository,
            WebSocketFuturesPositionUpdateDataRepository futuresPositionUpdateDataRepository,
            WebSocketFuturesOrderUpdateDataRepository futuresOrderUpdateDataRepository,
            WebSocketBalanceUpdateDataRepository balanceUpdateDataRepository,
            WebSocketFuturesCandlestickDataRepository futuresCandlestickDataRepository,
            WebSocketFuturesOrderbookDataRepository futuresOrderbookDataRepository,
            WebSocketFuturesCurrentPricesDataRepository futuresCurrentPricesDataRepository,
            WebSocketFuturesNewTradeDataRepository futuresNewTradeDataRepository) {
        this.spotDataRepository = spotDataRepository;
        this.futuresDataRepository = futuresDataRepository;
        this.spotBalanceDataRepository = spotBalanceDataRepository;
        this.spotOrderUpdateDataRepository = spotOrderUpdateDataRepository;
        this.spotTradeUpdateDataRepository = spotTradeUpdateDataRepository;
        this.spotCandlestickDataRepository = spotCandlestickDataRepository;
        this.spotDepthSnapshotDataRepository = spotDepthSnapshotDataRepository;
        this.spotDepthUpdateDataRepository = spotDepthUpdateDataRepository;
        this.spotCurrentPriceDataRepository = spotCurrentPriceDataRepository;
        this.spotPriceStatsDataRepository = spotPriceStatsDataRepository;
        this.spotNewTradeDataRepository = spotNewTradeDataRepository;
        this.spotPriceChangeDataRepository = spotPriceChangeDataRepository;
        this.futuresPositionUpdateDataRepository = futuresPositionUpdateDataRepository;
        this.futuresOrderUpdateDataRepository = futuresOrderUpdateDataRepository;
        this.balanceUpdateDataRepository = balanceUpdateDataRepository;
        this.futuresCandlestickDataRepository = futuresCandlestickDataRepository;
        this.futuresOrderbookDataRepository = futuresOrderbookDataRepository;
        this.futuresCurrentPricesDataRepository = futuresCurrentPricesDataRepository;
        this.futuresNewTradeDataRepository = futuresNewTradeDataRepository;
        this.gson = new Gson();
    }

    /**
     * Save spot market data asynchronously
     */
    @Async
    @Transactional
    public void saveSpotData(String marketPair, String eventType, String channelName, Object data) {
        try {
            WebSocketSpotData spotData = new WebSocketSpotData();
            spotData.setMarketPair(marketPair);
            spotData.setEventType(eventType);
            spotData.setChannelName(channelName);
            spotData.setTimestamp(LocalDateTime.now());

            // Convert data to JSON string
            String jsonData = gson.toJson(data);
            spotData.setRawData(jsonData);

            // Parse and extract fields based on event type
            parseSpotData(spotData, jsonData, eventType);

            spotDataRepository.save(spotData);
            logger.debug("✓ Saved spot data for {} - Event: {}", marketPair, eventType);

        } catch (Exception e) {
            logger.error("Failed to save spot data for {} - Event: {}", marketPair, eventType, e);
        }
    }

    /**
     * Save futures market data asynchronously
     */
    @Async
    @Transactional
    public void saveFuturesData(String contractSymbol, String eventType, String channelName, Object data) {
        try {
            WebSocketFuturesData futuresData = new WebSocketFuturesData();
            futuresData.setContractSymbol(contractSymbol);
            futuresData.setEventType(eventType);
            futuresData.setChannelName(channelName);
            futuresData.setTimestamp(LocalDateTime.now());

            // Convert data to JSON string
            String jsonData = gson.toJson(data);
            futuresData.setRawData(jsonData);

            // Parse and extract fields based on event type
            parseFuturesData(futuresData, jsonData, eventType);

            futuresDataRepository.save(futuresData);
            logger.debug("✓ Saved futures data for {} - Event: {}", contractSymbol, eventType);

        } catch (Exception e) {
            logger.error("Failed to save futures data for {} - Event: {}", contractSymbol, eventType, e);
        }
    }

    /**
     * Parse spot market data and populate entity fields
     */
    private void parseSpotData(WebSocketSpotData entity, String jsonData, String eventType) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Common fields
            if (json.has("T")) {
                entity.setExchangeTimestamp(json.get("T").getAsLong());
            }

            switch (eventType) {
                case "price-change":
                case "currentPrices@spot#update":
                    parsePriceChange(entity, json);
                    break;

                case "new-trade":
                    parseNewTrade(entity, json);
                    break;

                case "depth-update":
                case "depth-snapshot":
                    parseDepthUpdate(entity, json);
                    break;

                case "candlestick":
                    parseCandlestick(entity, json);
                    break;

                default:
                    logger.debug("Unknown spot event type: {}", eventType);
            }

        } catch (Exception e) {
            logger.warn("Failed to parse spot data: {}", e.getMessage());
        }
    }

    /**
     * Parse futures market data and populate entity fields
     */
    private void parseFuturesData(WebSocketFuturesData entity, String jsonData, String eventType) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Common fields
            if (json.has("T")) {
                entity.setExchangeTimestamp(json.get("T").getAsLong());
            }

            switch (eventType) {
                case "price-change":
                case "mark-price-update":
                    parseFuturesPriceChange(entity, json);
                    break;

                case "position-update":
                    parsePositionUpdate(entity, json);
                    break;

                case "order-update":
                    parseOrderUpdate(entity, json);
                    break;

                case "funding-rate-update":
                    parseFundingRateUpdate(entity, json);
                    break;

                case "new-trade":
                    parseFuturesTrade(entity, json);
                    break;

                default:
                    logger.debug("Unknown futures event type: {}", eventType);
            }

        } catch (Exception e) {
            logger.warn("Failed to parse futures data: {}", e.getMessage());
        }
    }

    // Helper methods for parsing specific event types

    private void parsePriceChange(WebSocketSpotData entity, JsonObject json) {
        if (json.has("p")) entity.setPrice(new BigDecimal(json.get("p").getAsString()));
        if (json.has("v")) entity.setVolume(new BigDecimal(json.get("v").getAsString()));
        if (json.has("h")) entity.setHigh(new BigDecimal(json.get("h").getAsString()));
        if (json.has("l")) entity.setLow(new BigDecimal(json.get("l").getAsString()));
        if (json.has("o")) entity.setOpen(new BigDecimal(json.get("o").getAsString()));
        if (json.has("c")) entity.setClose(new BigDecimal(json.get("c").getAsString()));
    }

    private void parseNewTrade(WebSocketSpotData entity, JsonObject json) {
        if (json.has("t")) entity.setTradeId(json.get("t").getAsString());
        if (json.has("p")) entity.setPrice(new BigDecimal(json.get("p").getAsString()));
        if (json.has("q")) entity.setQuantity(new BigDecimal(json.get("q").getAsString()));
        if (json.has("s")) entity.setSide(json.get("s").getAsString());
    }

    private void parseDepthUpdate(WebSocketSpotData entity, JsonObject json) {
        if (json.has("b") && json.get("b").isJsonArray() && json.getAsJsonArray("b").size() > 0) {
            var bid = json.getAsJsonArray("b").get(0).getAsJsonArray();
            entity.setBidPrice(new BigDecimal(bid.get(0).getAsString()));
            entity.setBidQuantity(new BigDecimal(bid.get(1).getAsString()));
        }
        if (json.has("a") && json.get("a").isJsonArray() && json.getAsJsonArray("a").size() > 0) {
            var ask = json.getAsJsonArray("a").get(0).getAsJsonArray();
            entity.setAskPrice(new BigDecimal(ask.get(0).getAsString()));
            entity.setAskQuantity(new BigDecimal(ask.get(1).getAsString()));
        }
    }

    private void parseCandlestick(WebSocketSpotData entity, JsonObject json) {
        if (json.has("o")) entity.setOpen(new BigDecimal(json.get("o").getAsString()));
        if (json.has("h")) entity.setHigh(new BigDecimal(json.get("h").getAsString()));
        if (json.has("l")) entity.setLow(new BigDecimal(json.get("l").getAsString()));
        if (json.has("c")) entity.setClose(new BigDecimal(json.get("c").getAsString()));
        if (json.has("v")) entity.setVolume(new BigDecimal(json.get("v").getAsString()));
    }

    private void parseFuturesPriceChange(WebSocketFuturesData entity, JsonObject json) {
        if (json.has("p")) entity.setLastPrice(new BigDecimal(json.get("p").getAsString()));
        if (json.has("m")) entity.setMarkPrice(new BigDecimal(json.get("m").getAsString()));
        if (json.has("i")) entity.setIndexPrice(new BigDecimal(json.get("i").getAsString()));
        if (json.has("v")) entity.setVolume(new BigDecimal(json.get("v").getAsString()));
        if (json.has("h")) entity.setHigh(new BigDecimal(json.get("h").getAsString()));
        if (json.has("l")) entity.setLow(new BigDecimal(json.get("l").getAsString()));
    }

    private void parsePositionUpdate(WebSocketFuturesData entity, JsonObject json) {
        if (json.has("size")) entity.setPositionSize(new BigDecimal(json.get("size").getAsString()));
        if (json.has("leverage")) entity.setLeverage(new BigDecimal(json.get("leverage").getAsString()));
        if (json.has("upnl")) entity.setUnrealizedPnl(new BigDecimal(json.get("upnl").getAsString()));
        if (json.has("liquidationPrice")) entity.setLiquidationPrice(new BigDecimal(json.get("liquidationPrice").getAsString()));
        if (json.has("side")) entity.setSide(json.get("side").getAsString());
    }

    private void parseOrderUpdate(WebSocketFuturesData entity, JsonObject json) {
        if (json.has("price")) entity.setLastPrice(new BigDecimal(json.get("price").getAsString()));
        if (json.has("quantity")) entity.setQuantity(new BigDecimal(json.get("quantity").getAsString()));
        if (json.has("side")) entity.setSide(json.get("side").getAsString());
    }

    private void parseFundingRateUpdate(WebSocketFuturesData entity, JsonObject json) {
        if (json.has("fundingRate")) entity.setFundingRate(new BigDecimal(json.get("fundingRate").getAsString()));
        if (json.has("nextFundingTime")) {
            long timestamp = json.get("nextFundingTime").getAsLong();
            entity.setNextFundingTime(LocalDateTime.ofEpochSecond(timestamp / 1000, 0, java.time.ZoneOffset.UTC));
        }
    }

    private void parseFuturesTrade(WebSocketFuturesData entity, JsonObject json) {
        if (json.has("t")) entity.setTradeId(json.get("t").getAsString());
        if (json.has("p")) entity.setLastPrice(new BigDecimal(json.get("p").getAsString()));
        if (json.has("q")) entity.setQuantity(new BigDecimal(json.get("q").getAsString()));
        if (json.has("s")) entity.setSide(json.get("s").getAsString());
    }

    /**
     * Save spot balance update data asynchronously
     */
    @Async
    @Transactional
    public void saveSpotBalanceData(Object data) {
        try {
            WebSocketSpotBalanceData spotBalanceData = new WebSocketSpotBalanceData();
            spotBalanceData.setTimestamp(LocalDateTime.now());

            // Convert data to JSON string
            String jsonData = gson.toJson(data);
            spotBalanceData.setRawData(jsonData);

            // Parse and extract spot balance fields
            parseSpotBalanceData(spotBalanceData, jsonData);

            spotBalanceDataRepository.save(spotBalanceData);
            logger.debug("✓ Saved spot balance data for currency: {}", spotBalanceData.getCurrency());

        } catch (Exception e) {
            logger.error("Failed to save spot balance data", e);
        }
    }

    /**
     * Parse spot balance data and populate entity fields
     */
    private void parseSpotBalanceData(WebSocketSpotBalanceData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract common balance fields
            if (json.has("currency")) {
                entity.setCurrency(json.get("currency").getAsString());
            } else if (json.has("c")) {
                entity.setCurrency(json.get("c").getAsString());
            }

            if (json.has("balance")) {
                entity.setBalance(json.get("balance").getAsString());
            } else if (json.has("b")) {
                entity.setBalance(json.get("b").getAsString());
            }

            if (json.has("locked_balance")) {
                entity.setLockedBalance(json.get("locked_balance").getAsString());
            } else if (json.has("l")) {
                entity.setLockedBalance(json.get("l").getAsString());
            }

            if (json.has("available_balance")) {
                entity.setAvailableBalance(json.get("available_balance").getAsString());
            } else if (json.has("a")) {
                entity.setAvailableBalance(json.get("a").getAsString());
            }

            // Calculate total balance if not provided
            if (json.has("total_balance")) {
                entity.setTotalBalance(json.get("total_balance").getAsString());
            } else if (entity.getBalance() != null && entity.getLockedBalance() != null) {
                try {
                    BigDecimal balance = new BigDecimal(entity.getBalance());
                    BigDecimal locked = new BigDecimal(entity.getLockedBalance());
                    entity.setTotalBalance(balance.add(locked).toString());
                } catch (NumberFormatException e) {
                    logger.debug("Could not calculate total balance", e);
                }
            }

            // Extract user ID if available
            if (json.has("user_id")) {
                entity.setUserId(json.get("user_id").getAsString());
            } else if (json.has("u")) {
                entity.setUserId(json.get("u").getAsString());
            }

            // Extract timestamp if available
            if (json.has("timestamp")) {
                entity.setExchangeTimestamp(json.get("timestamp").getAsLong());
            } else if (json.has("t")) {
                entity.setExchangeTimestamp(json.get("t").getAsLong());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot balance data", e);
        }
    }

    /**
     * Save spot order update data asynchronously
     */
    @Async
    @Transactional
    public void saveSpotOrderUpdateData(Object data) {
        try {
            WebSocketSpotOrderUpdateData orderUpdateData = new WebSocketSpotOrderUpdateData();
            orderUpdateData.setTimestamp(LocalDateTime.now());

            // Convert data to JSON string
            String jsonData = gson.toJson(data);
            orderUpdateData.setRawData(jsonData);

            // Parse and extract order update fields
            parseSpotOrderUpdateData(orderUpdateData, jsonData);

            spotOrderUpdateDataRepository.save(orderUpdateData);
            logger.debug("✓ Saved spot order update for order ID: {} status: {}", 
                        orderUpdateData.getOrderId(), orderUpdateData.getStatus());

        } catch (Exception e) {
            logger.error("Failed to save spot order update data", e);
        }
    }

    /**
     * Parse spot order update data and populate entity fields
     */
    private void parseSpotOrderUpdateData(WebSocketSpotOrderUpdateData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract order ID (required)
            if (json.has("id")) {
                entity.setOrderId(json.get("id").getAsString());
            }

            // Extract client order ID
            if (json.has("client_order_id")) {
                entity.setClientOrderId(json.get("client_order_id").getAsString());
            }

            // Extract order type
            if (json.has("order_type")) {
                entity.setOrderType(json.get("order_type").getAsString());
            }

            // Extract side (buy/sell)
            if (json.has("side")) {
                entity.setSide(json.get("side").getAsString());
            }

            // Extract status
            if (json.has("status")) {
                entity.setStatus(json.get("status").getAsString());
            }

            // Extract fee amount
            if (json.has("fee_amount")) {
                entity.setFeeAmount(json.get("fee_amount").getAsString());
            }

            // Extract maker fee
            if (json.has("maker_fee")) {
                entity.setMakerFee(json.get("maker_fee").getAsString());
            }

            // Extract taker fee
            if (json.has("taker_fee")) {
                entity.setTakerFee(json.get("taker_fee").getAsString());
            }

            // Extract total quantity
            if (json.has("total_quantity")) {
                entity.setTotalQuantity(json.get("total_quantity").getAsString());
            }

            // Extract remaining quantity
            if (json.has("remaining_quantity")) {
                entity.setRemainingQuantity(json.get("remaining_quantity").getAsString());
            }

            // Extract average price
            if (json.has("avg_price")) {
                entity.setAvgPrice(json.get("avg_price").getAsString());
            }

            // Extract price per unit
            if (json.has("price_per_unit")) {
                entity.setPricePerUnit(json.get("price_per_unit").getAsString());
            }

            // Extract stop price
            if (json.has("stop_price")) {
                entity.setStopPrice(json.get("stop_price").getAsString());
            }

            // Extract market
            if (json.has("market")) {
                entity.setMarket(json.get("market").getAsString());
            }

            // Extract time in force
            if (json.has("time_in_force")) {
                entity.setTimeInForce(json.get("time_in_force").getAsString());
            }

            // Extract created_at timestamp
            if (json.has("created_at")) {
                entity.setCreatedAt(json.get("created_at").getAsLong());
            }

            // Extract updated_at timestamp
            if (json.has("updated_at")) {
                entity.setUpdatedAt(json.get("updated_at").getAsLong());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot order update data", e);
        }
    }

    /**
     * Save spot trade update data asynchronously
     */
    @Async
    @Transactional
    public void saveSpotTradeUpdateData(Object data) {
        try {
            WebSocketSpotTradeUpdateData tradeUpdateData = new WebSocketSpotTradeUpdateData();
            tradeUpdateData.setTimestamp(LocalDateTime.now());

            // Convert data to JSON string
            String jsonData = gson.toJson(data);
            tradeUpdateData.setRawData(jsonData);

            // Parse and extract trade update fields
            parseSpotTradeUpdateData(tradeUpdateData, jsonData);

            spotTradeUpdateDataRepository.save(tradeUpdateData);
            logger.debug("✓ Saved spot trade update - Trade ID: {} Symbol: {} Price: {} Quantity: {}", 
                        tradeUpdateData.getTradeId(), tradeUpdateData.getSymbol(), 
                        tradeUpdateData.getPrice(), tradeUpdateData.getQuantity());

        } catch (Exception e) {
            logger.error("Failed to save spot trade update data", e);
        }
    }

    /**
     * Parse spot trade update data and populate entity fields
     */
    private void parseSpotTradeUpdateData(WebSocketSpotTradeUpdateData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract order ID (o)
            if (json.has("o")) {
                entity.setOrderId(json.get("o").getAsString());
            }

            // Extract client order ID (c)
            if (json.has("c")) {
                entity.setClientOrderId(json.get("c").getAsString());
            }

            // Extract trade ID (t) - required
            if (json.has("t")) {
                entity.setTradeId(json.get("t").getAsString());
            }

            // Extract symbol (s) - required
            if (json.has("s")) {
                entity.setSymbol(json.get("s").getAsString());
            }

            // Extract price (p)
            if (json.has("p")) {
                entity.setPrice(json.get("p").getAsString());
            }

            // Extract quantity (q)
            if (json.has("q")) {
                entity.setQuantity(json.get("q").getAsString());
            }

            // Extract timestamp (T)
            if (json.has("T")) {
                entity.setExchangeTimestamp(json.get("T").getAsLong());
            }

            // Extract is buyer maker (m)
            if (json.has("m")) {
                entity.setIsBuyerMaker(json.get("m").getAsBoolean());
            }

            // Extract fee amount (f)
            if (json.has("f")) {
                entity.setFeeAmount(json.get("f").getAsString());
            }

            // Extract exchange identifier (e)
            if (json.has("e")) {
                entity.setExchangeIdentifier(json.get("e").getAsString());
            }

            // Extract status (x)
            if (json.has("x")) {
                entity.setStatus(json.get("x").getAsString());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot trade update data", e);
        }
    }

    /**
     * Save spot candlestick data
     */
    @Async
    @Transactional
    public void saveSpotCandlestickData(Object data) {
        try {
            String jsonData = gson.toJson(data);
            
            WebSocketSpotCandlestickData candlestick = new WebSocketSpotCandlestickData();
            candlestick.setRawData(jsonData);
            
            parseSpotCandlestickData(candlestick, jsonData);
            
            spotCandlestickDataRepository.save(candlestick);
            
            logger.debug("Saved spot candlestick data: symbol={}, interval={}, start={}, completed={}", 
                        candlestick.getSymbol(), candlestick.getCandleInterval(), 
                        candlestick.getStartTimestamp(), candlestick.getIsCompleted());
            
        } catch (Exception e) {
            logger.error("Error saving spot candlestick data", e);
        }
    }

    /**
     * Parse spot candlestick data from JSON
     */
    private void parseSpotCandlestickData(WebSocketSpotCandlestickData entity, String jsonData) {
        try {
            JsonObject outerJson = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract the actual candlestick data from nested structure
            // Data format: {"map":{"data":"{...}","event":"candlestick"}}
            String candlestickDataString = jsonData;
            if (outerJson.has("map")) {
                JsonObject mapObject = outerJson.getAsJsonObject("map");
                if (mapObject.has("data") && !mapObject.get("data").isJsonNull()) {
                    candlestickDataString = mapObject.get("data").getAsString();
                }
            }

            JsonObject json = JsonParser.parseString(candlestickDataString).getAsJsonObject();

            // Extract start timestamp (t)
            if (json.has("t")) {
                entity.setStartTimestamp(json.get("t").getAsLong());
            }

            // Extract close timestamp (T)
            if (json.has("T")) {
                entity.setCloseTimestamp(json.get("T").getAsLong());
            }

            // Extract symbol (s)
            if (json.has("s")) {
                entity.setSymbol(json.get("s").getAsString());
            }

            // Extract candle interval (i)
            if (json.has("i")) {
                entity.setCandleInterval(json.get("i").getAsString());
            }

            // Extract first trade ID (f)
            if (json.has("f")) {
                entity.setFirstTradeId(json.get("f").getAsString());
            }

            // Extract last trade ID (L)
            if (json.has("L")) {
                entity.setLastTradeId(json.get("L").getAsString());
            }

            // Extract open price (o)
            if (json.has("o")) {
                entity.setOpenPrice(json.get("o").getAsString());
            }

            // Extract close price (c)
            if (json.has("c")) {
                entity.setClosePrice(json.get("c").getAsString());
            }

            // Extract high price (h)
            if (json.has("h")) {
                entity.setHighPrice(json.get("h").getAsString());
            }

            // Extract low price (l)
            if (json.has("l")) {
                entity.setLowPrice(json.get("l").getAsString());
            }

            // Extract base asset volume (v)
            if (json.has("v")) {
                entity.setBaseAssetVolume(json.get("v").getAsString());
            }

            // Extract number of trades (n)
            if (json.has("n")) {
                entity.setNumberOfTrades(json.get("n").getAsLong());
            }

            // Extract is completed (x)
            if (json.has("x")) {
                String xValue = json.get("x").getAsString();
                entity.setIsCompleted("Y".equalsIgnoreCase(xValue) || "true".equalsIgnoreCase(xValue));
            }

            // Extract quote asset volume (q)
            if (json.has("q")) {
                entity.setQuoteAssetVolume(json.get("q").getAsString());
            }

            // Extract taker buy base asset volume (V)
            if (json.has("V")) {
                entity.setTakerBuyBaseAssetVolume(json.get("V").getAsString());
            }

            // Extract taker buy quote asset volume (Q)
            if (json.has("Q")) {
                entity.setTakerBuyQuoteAssetVolume(json.get("Q").getAsString());
            }

            // Extract first trade ID B (B)
            if (json.has("B")) {
                entity.setFirstTradeIdB(json.get("B").getAsString());
            }

            // Extract exchange code (ecode)
            if (json.has("ecode")) {
                entity.setExchangeCode(json.get("ecode").getAsString());
            }

            // Extract channel name (channel)
            if (json.has("channel")) {
                entity.setChannelName(json.get("channel").getAsString());
            }

            // Extract product (pr)
            if (json.has("pr")) {
                entity.setProduct(json.get("pr").getAsString());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot candlestick data", e);
        }
    }

    /**
     * Get statistics about stored data
     */
    public String getStorageStatistics() {
        long spotCount = spotDataRepository.count();
        long futuresCount = futuresDataRepository.count();
        long spotBalanceCount = spotBalanceDataRepository.count();
        long spotOrderUpdateCount = spotOrderUpdateDataRepository.count();
        long spotTradeUpdateCount = spotTradeUpdateDataRepository.count();
        long spotCandlestickCount = spotCandlestickDataRepository.count();
        long spotDepthSnapshotCount = spotDepthSnapshotDataRepository.count();
        long spotDepthUpdateCount = spotDepthUpdateDataRepository.count();
        long spotCurrentPriceCount = spotCurrentPriceDataRepository.count();
        long spotPriceStatsCount = spotPriceStatsDataRepository.count();
        long spotNewTradeCount = spotNewTradeDataRepository.count();
        long spotPriceChangeCount = spotPriceChangeDataRepository.count();
        long futuresPositionUpdateCount = futuresPositionUpdateDataRepository.count();
        long futuresOrderUpdateCount = futuresOrderUpdateDataRepository.count();
        long balanceUpdateCount = balanceUpdateDataRepository.count();
        long futuresCandlestickCount = futuresCandlestickDataRepository.count();
        long futuresOrderbookCount = futuresOrderbookDataRepository.count();
        long futuresCurrentPricesCount = futuresCurrentPricesDataRepository.count();
        long futuresNewTradeCount = futuresNewTradeDataRepository.count();
        
        return String.format(
            "WebSocket Data Storage Statistics:\n" +
            "  Spot Markets: %d records\n" +
            "  Futures Markets: %d records\n" +
            "  Spot Balance Updates: %d records\n" +
            "  Spot Order Updates: %d records\n" +
            "  Spot Trade Updates: %d records\n" +
            "  Spot Candlesticks: %d records\n" +
            "  Spot Depth Snapshots: %d records\n" +
            "  Spot Depth Updates: %d records\n" +
            "  Spot Current Prices: %d records\n" +
            "  Spot Price Stats: %d records\n" +
            "  Spot New Trades: %d records\n" +
            "  Spot Price Changes: %d records\n" +
            "  Futures Position Updates: %d records\n" +
            "  Futures Order Updates: %d records\n" +
            "  Futures Balance Updates: %d records\n" +
            "  Futures Candlesticks: %d records\n" +
            "  Futures Orderbooks: %d records\n" +
            "  Futures Current Prices: %d records\n" +
            "  Futures New Trades: %d records\n" +
            "  Total: %d records",
            spotCount, futuresCount, spotBalanceCount, spotOrderUpdateCount, spotTradeUpdateCount, spotCandlestickCount, spotDepthSnapshotCount, spotDepthUpdateCount, spotCurrentPriceCount, spotPriceStatsCount, spotNewTradeCount, spotPriceChangeCount, futuresPositionUpdateCount, futuresOrderUpdateCount, balanceUpdateCount, futuresCandlestickCount, futuresOrderbookCount, futuresCurrentPricesCount, futuresNewTradeCount,
            spotCount + futuresCount + spotBalanceCount + spotOrderUpdateCount + spotTradeUpdateCount + spotCandlestickCount + spotDepthSnapshotCount + spotDepthUpdateCount + spotCurrentPriceCount + spotPriceStatsCount + spotNewTradeCount + spotPriceChangeCount + futuresPositionUpdateCount + futuresOrderUpdateCount + balanceUpdateCount + futuresCandlestickCount + futuresOrderbookCount + futuresCurrentPricesCount + futuresNewTradeCount
        );
    }

    /**
     * Save spot depth snapshot data
     */
    @Async
    @Transactional
    public void saveSpotDepthSnapshotData(Object data, String channelName) {
        try {
            String jsonData = gson.toJson(data);
            
            WebSocketSpotDepthSnapshotData snapshot = new WebSocketSpotDepthSnapshotData();
            snapshot.setRawData(jsonData);
            snapshot.setChannelName(channelName);
            
            // Extract depth level from channel name (e.g., "B-BTC_USDT@orderbook@20" -> 20)
            if (channelName != null && channelName.contains("@orderbook@")) {
                String[] parts = channelName.split("@orderbook@");
                if (parts.length > 1) {
                    try {
                        snapshot.setDepthLevel(Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        logger.warn("Could not parse depth level from channel: {}", channelName);
                    }
                }
            }
            
            parseSpotDepthSnapshotData(snapshot, jsonData);
            
            spotDepthSnapshotDataRepository.save(snapshot);
            
            logger.debug("Saved spot depth snapshot: symbol={}, version={}, depthLevel={}", 
                        snapshot.getSymbol(), snapshot.getVersion(), snapshot.getDepthLevel());
            
        } catch (Exception e) {
            logger.error("Error saving spot depth snapshot data", e);
        }
    }

    /**
     * Parse spot depth snapshot data from JSON
     */
    private void parseSpotDepthSnapshotData(WebSocketSpotDepthSnapshotData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract version (vs)
            if (json.has("vs")) {
                entity.setVersion(json.get("vs").getAsLong());
            }

            // Extract timestamp (ts)
            if (json.has("ts")) {
                entity.setSnapshotTimestamp(json.get("ts").getAsLong());
            }

            // Extract product (pr)
            if (json.has("pr")) {
                entity.setProduct(json.get("pr").getAsString());
            }

            // Extract symbol (s)
            if (json.has("s")) {
                entity.setSymbol(json.get("s").getAsString());
            }

            // Extract bids array (store as JSON string)
            if (json.has("bids")) {
                entity.setBids(json.get("bids").toString());
            }

            // Extract asks array (store as JSON string)
            if (json.has("asks")) {
                entity.setAsks(json.get("asks").toString());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot depth snapshot data", e);
        }
    }

    /**
     * Save spot depth update (order book changes) data
     */
    @Async
    @Transactional
    public void saveSpotDepthUpdateData(Object data, String channelName) {
        try {
            WebSocketSpotDepthUpdateData update = new WebSocketSpotDepthUpdateData();
            String jsonData = gson.toJson(data);
            update.setRawData(jsonData);
            update.setChannelName(channelName);

            // Extract depth level from channel name (e.g., "B-BTC_USDT@orderbook@20" -> 20)
            if (channelName != null && channelName.contains("@orderbook@")) {
                String[] parts = channelName.split("@orderbook@");
                if (parts.length > 1) {
                    try {
                        update.setDepthLevel(Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        logger.warn("Could not parse depth level from channel name: {}", channelName);
                    }
                }
            }

            parseSpotDepthUpdateData(update, jsonData);
            spotDepthUpdateDataRepository.save(update);
            logger.debug("Saved spot depth update data for symbol: {}, version: {}, depthLevel: {}", 
                update.getSymbol(), update.getVersion(), update.getDepthLevel());
        } catch (Exception e) {
            logger.error("Error saving spot depth update data", e);
        }
    }

    /**
     * Parse spot depth update data from JSON
     */
    private void parseSpotDepthUpdateData(WebSocketSpotDepthUpdateData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract version (vs)
            if (json.has("vs")) {
                entity.setVersion(json.get("vs").getAsLong());
            }

            // Extract timestamp (ts)
            if (json.has("ts")) {
                entity.setTimestamp(json.get("ts").getAsLong());
            }

            // Extract product (pr)
            if (json.has("pr")) {
                entity.setProduct(json.get("pr").getAsString());
            }

            // Extract event time (E)
            if (json.has("E")) {
                entity.setEventTime(json.get("E").getAsLong());
            }

            // Extract symbol (s)
            if (json.has("s")) {
                entity.setSymbol(json.get("s").getAsString());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot depth update data", e);
        }
    }

    /**
     * Save spot current price data
     */
    @Async
    @Transactional
    public void saveSpotCurrentPriceData(Object data, String channelName) {
        try {
            WebSocketSpotCurrentPriceData priceData = new WebSocketSpotCurrentPriceData();
            String jsonData = gson.toJson(data);
            priceData.setRawData(jsonData);
            priceData.setChannelName(channelName);

            // Extract interval from channel name (e.g., "currentPrices@spot@10s" -> "10s")
            if (channelName != null && channelName.contains("@spot@")) {
                String[] parts = channelName.split("@spot@");
                if (parts.length > 1) {
                    priceData.setPriceInterval(parts[1]);
                }
            }

            parseSpotCurrentPriceData(priceData, jsonData);
            spotCurrentPriceDataRepository.save(priceData);
            logger.debug("Saved spot current price data for interval: {}, version: {}", 
                priceData.getPriceInterval(), priceData.getVersion());
        } catch (Exception e) {
            logger.error("Error saving spot current price data", e);
        }
    }

    /**
     * Parse spot current price data from JSON
     */
    private void parseSpotCurrentPriceData(WebSocketSpotCurrentPriceData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract version (vs)
            if (json.has("vs")) {
                entity.setVersion(json.get("vs").getAsLong());
            }

            // Extract timestamp (ts)
            if (json.has("ts")) {
                entity.setTimestamp(json.get("ts").getAsLong());
            }

            // Extract product (pr)
            if (json.has("pr")) {
                entity.setProduct(json.get("pr").getAsString());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot current price data", e);
        }
    }

    /**
     * Save spot price stats data (24h market statistics)
     */
    @Async
    @Transactional
    public void saveSpotPriceStatsData(Object data, String channelName) {
        try {
            WebSocketSpotPriceStatsData statsData = new WebSocketSpotPriceStatsData();
            String jsonData = gson.toJson(data);
            statsData.setRawData(jsonData);
            statsData.setChannelName(channelName);

            // Extract interval from channel name (e.g., "priceStats@spot@60s" -> "60s")
            if (channelName != null && channelName.contains("@spot@")) {
                String[] parts = channelName.split("@spot@");
                if (parts.length > 1) {
                    statsData.setStatsInterval(parts[1]);
                }
            }

            parseSpotPriceStatsData(statsData, jsonData);
            spotPriceStatsDataRepository.save(statsData);
            logger.debug("Saved spot price stats data for interval: {}, version: {}, priceChange: {}%, volume: {}", 
                statsData.getStatsInterval(), statsData.getVersion(), statsData.getPriceChangePercent(), statsData.getVolume24h());
        } catch (Exception e) {
            logger.error("Error saving spot price stats data", e);
        }
    }

    /**
     * Parse spot price stats data from JSON
     */
    private void parseSpotPriceStatsData(WebSocketSpotPriceStatsData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract version (vs)
            if (json.has("vs")) {
                entity.setVersion(json.get("vs").getAsLong());
            }

            // Extract timestamp (ts)
            if (json.has("ts")) {
                entity.setTimestamp(json.get("ts").getAsLong());
            }

            // Extract product (pr)
            if (json.has("pr")) {
                entity.setProduct(json.get("pr").getAsString());
            }

            // Extract price change percent (pc) - 24h price change percentage
            if (json.has("pc")) {
                entity.setPriceChangePercent(json.get("pc").getAsDouble());
            }

            // Extract volume 24h (v) - 24h trading volume
            if (json.has("v")) {
                entity.setVolume24h(json.get("v").getAsDouble());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot price stats data", e);
        }
    }

    /**
     * Save spot new trade data (latest trade information)
     */
    @Async
    @Transactional
    public void saveSpotNewTradeData(Object data, String channelName) {
        try {
            WebSocketSpotNewTradeData tradeData = new WebSocketSpotNewTradeData();
            String jsonData = gson.toJson(data);
            tradeData.setRawData(jsonData);
            tradeData.setChannelName(channelName);

            // Extract pair from channel name (e.g., "B-BTC_USDT@trades" -> "B-BTC_USDT")
            if (channelName != null && channelName.contains("@trades")) {
                String pair = channelName.split("@trades")[0];
                tradeData.setPair(pair);
            }

            parseSpotNewTradeData(tradeData, jsonData);
            spotNewTradeDataRepository.save(tradeData);
            logger.debug("Saved spot new trade data for symbol: {}, pair: {}, price: {}, quantity: {}", 
                tradeData.getSymbol(), tradeData.getPair(), tradeData.getTradePrice(), tradeData.getQuantity());
        } catch (Exception e) {
            logger.error("Error saving spot new trade data", e);
        }
    }

    /**
     * Parse spot new trade data from JSON
     */
    private void parseSpotNewTradeData(WebSocketSpotNewTradeData entity, String jsonData) {
        try {
            JsonObject outerJson = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract the actual trade data from nested structure
            // Data format: {"map":{"data":"{...}","event":"new-trade"}}
            String tradeDataString = jsonData;
            if (outerJson.has("map")) {
                JsonObject mapObject = outerJson.getAsJsonObject("map");
                if (mapObject.has("data") && !mapObject.get("data").isJsonNull()) {
                    tradeDataString = mapObject.get("data").getAsString();
                }
            }

            JsonObject json = JsonParser.parseString(tradeDataString).getAsJsonObject();

            // Extract isBuyerMarketMaker (m) - whether the buyer is market maker or not
            if (json.has("m")) {
                entity.setIsBuyerMarketMaker(json.get("m").getAsBoolean());
            }

            // Extract tradePrice (p) - trade price
            if (json.has("p")) {
                entity.setTradePrice(json.get("p").getAsDouble());
            }

            // Extract quantity (q) - quantity
            if (json.has("q")) {
                entity.setQuantity(json.get("q").getAsDouble());
            }

            // Extract tradeTimestamp (T) - timestamp of trade
            if (json.has("T")) {
                entity.setTradeTimestamp(json.get("T").getAsLong());
            }

            // Extract symbol (s) - symbol(currency)
            if (json.has("s")) {
                entity.setSymbol(json.get("s").getAsString());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot new trade data", e);
        }
    }

    /**
     * Save spot price change data (price-change event from {pair}@prices channel)
     * Channel format: B-BTC_USDT@prices
     * Event: price-change
     * Response fields: p (trade price), T (timestamp of trade), pr (product)
     */
    @Async
    public void saveSpotPriceChangeData(Object data, String channelName) {
        try {
            WebSocketSpotPriceChangeData priceChangeData = new WebSocketSpotPriceChangeData();
            String jsonData = gson.toJson(data);
            priceChangeData.setRawData(jsonData);
            priceChangeData.setChannelName(channelName);

            // Extract pair from channel name (e.g., "B-BTC_USDT@prices" -> "B-BTC_USDT")
            if (channelName != null && channelName.contains("@prices")) {
                String pair = channelName.split("@prices")[0];
                priceChangeData.setPair(pair);
            }

            parseSpotPriceChangeData(priceChangeData, jsonData);
            spotPriceChangeDataRepository.save(priceChangeData);
            logger.debug("Saved spot price change data for pair: {}, price: {}, timestamp: {}, product: {}", 
                priceChangeData.getPair(), priceChangeData.getTradePrice(), 
                priceChangeData.getTradeTimestamp(), priceChangeData.getProduct());
        } catch (Exception e) {
            logger.error("Error saving spot price change data", e);
        }
    }

    /**
     * Parse spot price change data from JSON
     */
    private void parseSpotPriceChangeData(WebSocketSpotPriceChangeData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract tradePrice (p) - trade price
            if (json.has("p")) {
                entity.setTradePrice(json.get("p").getAsDouble());
            }

            // Extract tradeTimestamp (T) - timestamp of trade
            if (json.has("T")) {
                entity.setTradeTimestamp(json.get("T").getAsLong());
            }

            // Extract product (pr) - product (spot)
            if (json.has("pr")) {
                entity.setProduct(json.get("pr").getAsString());
            }

        } catch (Exception e) {
            logger.error("Error parsing spot price change data", e);
        }
    }

    /**
     * Save futures position update data (df-position-update event from coindcx channel)
     * Channel: coindcx
     * Event: df-position-update
     * Response: Array of position objects with detailed position information
     */
    @Async
    public void saveFuturesPositionUpdateData(Object data, String channelName) {
        try {
            String jsonData = gson.toJson(data);
            
            // The response is an array of position objects
            if (data instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> positions = (List<Object>) data;
                
                for (Object positionObj : positions) {
                    WebSocketFuturesPositionUpdateData positionData = new WebSocketFuturesPositionUpdateData();
                    String positionJson = gson.toJson(positionObj);
                    positionData.setRawData(positionJson);
                    positionData.setChannelName(channelName);
                    
                    parseFuturesPositionUpdateData(positionData, positionJson);
                    futuresPositionUpdateDataRepository.save(positionData);
                    logger.debug("Saved futures position update data for position: {}, pair: {}, side: {}, pnl: {}", 
                        positionData.getPositionId(), positionData.getPair(), 
                        positionData.getSide(), positionData.getTotalPnl());
                }
            }
        } catch (Exception e) {
            logger.error("Error saving futures position update data", e);
        }
    }

    /**
     * Parse futures position update data from JSON
     */
    private void parseFuturesPositionUpdateData(WebSocketFuturesPositionUpdateData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract id - position ID
            if (json.has("id")) {
                entity.setPositionId(json.get("id").getAsString());
            }

            // Extract pair - trading pair
            if (json.has("pair")) {
                entity.setPair(json.get("pair").getAsString());
            }

            // Extract active_pos - active position quantity
            if (json.has("active_pos")) {
                entity.setQuantity(json.get("active_pos").getAsDouble());
            }

            // Extract avg_price - average entry price
            if (json.has("avg_price")) {
                entity.setEntryPrice(json.get("avg_price").getAsDouble());
            }

            // Extract mark_price - current/mark price
            if (json.has("mark_price")) {
                entity.setCurrentPrice(json.get("mark_price").getAsDouble());
            }

            // Extract liquidation_price
            if (json.has("liquidation_price")) {
                entity.setLiquidationPrice(json.get("liquidation_price").getAsDouble());
            }

            // Extract leverage
            if (json.has("leverage")) {
                entity.setLeverage(json.get("leverage").getAsDouble());
            }

            // Extract locked_margin - total margin
            if (json.has("locked_margin")) {
                entity.setMargin(json.get("locked_margin").getAsDouble());
            }

            // Extract locked_user_margin - initial margin
            if (json.has("locked_user_margin")) {
                entity.setInitialMargin(json.get("locked_user_margin").getAsDouble());
            }

            // Extract maintenance_margin
            if (json.has("maintenance_margin")) {
                entity.setMaintenanceMargin(json.get("maintenance_margin").getAsDouble());
            }

            // Extract margin_type (isolated/cross)
            if (json.has("margin_type")) {
                entity.setPositionMarginType(json.get("margin_type").getAsString());
            }

            // Extract margin_currency_short_name
            if (json.has("margin_currency_short_name")) {
                entity.setMarginCurrency(json.get("margin_currency_short_name").getAsString());
            }

            // Extract updated_at - timestamp
            if (json.has("updated_at")) {
                entity.setUpdateTimestamp(json.get("updated_at").getAsLong());
            }

            // Determine side based on position quantities
            double activePos = json.has("active_pos") ? json.get("active_pos").getAsDouble() : 0;
            if (activePos > 0) {
                entity.setSide("long");
            } else if (activePos < 0) {
                entity.setSide("short");
            } else {
                // Check inactive positions to determine side
                double inactiveBuy = json.has("inactive_pos_buy") ? json.get("inactive_pos_buy").getAsDouble() : 0;
                double inactiveSell = json.has("inactive_pos_sell") ? json.get("inactive_pos_sell").getAsDouble() : 0;
                if (inactiveBuy != 0 || inactiveSell != 0) {
                    entity.setSide("closed");
                } else {
                    entity.setSide("none");
                }
            }

            // Determine status
            if (activePos != 0) {
                entity.setStatus("active");
            } else {
                entity.setStatus("inactive");
            }

            // Calculate PnL (this is a simplified calculation, actual PnL would need more data)
            // In a real scenario, you'd need realized_pnl and unrealized_pnl from the API
            if (entity.getQuantity() != null && entity.getEntryPrice() != null && entity.getCurrentPrice() != null) {
                double unrealizedPnl = entity.getQuantity() * (entity.getCurrentPrice() - entity.getEntryPrice());
                entity.setUnrealizedPnl(unrealizedPnl);
                entity.setTotalPnl(unrealizedPnl); // Simplified - would need to add realized PnL
                
                // Calculate ROI
                if (entity.getMargin() != null && entity.getMargin() != 0) {
                    double roi = (unrealizedPnl / entity.getMargin()) * 100;
                    entity.setRoi(roi);
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing futures position update data", e);
        }
    }

    /**
     * Save futures order update data from coindcx channel (df-order-update event)
     * Handles array of order objects
     */
    @Async
    @Transactional
    public void saveFuturesOrderUpdateData(Object data, String channelName) {
        try {
            // The response is an array of order objects
            if (data instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> orders = (List<Object>) data;
                
                for (Object orderObj : orders) {
                    WebSocketFuturesOrderUpdateData orderData = new WebSocketFuturesOrderUpdateData();
                    String orderJson = gson.toJson(orderObj);
                    orderData.setRawData(orderJson);
                    orderData.setChannelName(channelName);
                    
                    parseFuturesOrderUpdateData(orderData, orderJson);
                    futuresOrderUpdateDataRepository.save(orderData);
                    logger.debug("Saved futures order update data for order: {}, pair: {}, status: {}, type: {}", 
                        orderData.getOrderId(), orderData.getPair(), 
                        orderData.getStatus(), orderData.getOrderType());
                }
            }
        } catch (Exception e) {
            logger.error("Error saving futures order update data", e);
        }
    }

    /**
     * Parse futures order update data from JSON
     */
    private void parseFuturesOrderUpdateData(WebSocketFuturesOrderUpdateData entity, String jsonData) {
        try {
            JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract id - order ID
            if (json.has("id")) {
                entity.setOrderId(json.get("id").getAsString());
            }

            // Extract pair - trading pair
            if (json.has("pair")) {
                entity.setPair(json.get("pair").getAsString());
            }

            // Extract side - buy/sell
            if (json.has("side")) {
                entity.setSide(json.get("side").getAsString());
            }

            // Extract status - order status
            if (json.has("status")) {
                entity.setStatus(json.get("status").getAsString());
            }

            // Extract order_type
            if (json.has("order_type")) {
                entity.setOrderType(json.get("order_type").getAsString());
            }

            // Extract stop_trigger_instruction
            if (json.has("stop_trigger_instruction") && !json.get("stop_trigger_instruction").isJsonNull()) {
                entity.setStopTriggerInstruction(json.get("stop_trigger_instruction").getAsString());
            }

            // Extract notification
            if (json.has("notification") && !json.get("notification").isJsonNull()) {
                entity.setNotification(json.get("notification").getAsString());
            }

            // Extract leverage
            if (json.has("leverage")) {
                entity.setLeverage(json.get("leverage").getAsDouble());
            }

            // Extract maker_fee
            if (json.has("maker_fee")) {
                entity.setMakerFee(json.get("maker_fee").getAsDouble());
            }

            // Extract taker_fee
            if (json.has("taker_fee")) {
                entity.setTakerFee(json.get("taker_fee").getAsDouble());
            }

            // Extract fee_amount
            if (json.has("fee_amount")) {
                entity.setFeeAmount(json.get("fee_amount").getAsDouble());
            }

            // Extract price
            if (json.has("price")) {
                entity.setPrice(json.get("price").getAsDouble());
            }

            // Extract stop_price
            if (json.has("stop_price")) {
                entity.setStopPrice(json.get("stop_price").getAsDouble());
            }

            // Extract avg_price
            if (json.has("avg_price")) {
                entity.setAvgPrice(json.get("avg_price").getAsDouble());
            }

            // Extract take_profit_price
            if (json.has("take_profit_price") && !json.get("take_profit_price").isJsonNull()) {
                entity.setTakeProfitPrice(json.get("take_profit_price").getAsDouble());
            }

            // Extract stop_loss_price
            if (json.has("stop_loss_price") && !json.get("stop_loss_price").isJsonNull()) {
                entity.setStopLossPrice(json.get("stop_loss_price").getAsDouble());
            }

            // Extract total_quantity
            if (json.has("total_quantity")) {
                entity.setTotalQuantity(json.get("total_quantity").getAsDouble());
            }

            // Extract remaining_quantity
            if (json.has("remaining_quantity")) {
                entity.setRemainingQuantity(json.get("remaining_quantity").getAsDouble());
            }

            // Extract cancelled_quantity
            if (json.has("cancelled_quantity")) {
                entity.setCancelledQuantity(json.get("cancelled_quantity").getAsDouble());
            }

            // Calculate filled_quantity
            if (entity.getTotalQuantity() != null && entity.getRemainingQuantity() != null) {
                double filledQuantity = entity.getTotalQuantity() - entity.getRemainingQuantity() - 
                    (entity.getCancelledQuantity() != null ? entity.getCancelledQuantity() : 0.0);
                entity.setFilledQuantity(filledQuantity);
            }

            // Extract ideal_margin
            if (json.has("ideal_margin")) {
                entity.setIdealMargin(json.get("ideal_margin").getAsDouble());
            }

            // Extract order_category
            if (json.has("order_category") && !json.get("order_category").isJsonNull()) {
                entity.setOrderCategory(json.get("order_category").getAsString());
            }

            // Extract stage
            if (json.has("stage") && !json.get("stage").isJsonNull()) {
                entity.setStage(json.get("stage").getAsString());
            }

            // Extract created_at timestamp
            if (json.has("created_at")) {
                entity.setCreatedAt(json.get("created_at").getAsLong());
            }

            // Extract updated_at timestamp
            if (json.has("updated_at")) {
                entity.setUpdatedAt(json.get("updated_at").getAsLong());
            }

            // Extract display_message
            if (json.has("display_message") && !json.get("display_message").isJsonNull()) {
                entity.setDisplayMessage(json.get("display_message").getAsString());
            }

            // Extract group_status
            if (json.has("group_status") && !json.get("group_status").isJsonNull()) {
                entity.setGroupStatus(json.get("group_status").getAsString());
            }

            // Extract group_id
            if (json.has("group_id") && !json.get("group_id").isJsonNull()) {
                entity.setGroupId(json.get("group_id").getAsString());
            }

            // Extract metatags
            if (json.has("metatags") && !json.get("metatags").isJsonNull()) {
                entity.setMetatags(json.get("metatags").getAsString());
            }

            // Extract margin_currency_short_name
            if (json.has("margin_currency_short_name") && !json.get("margin_currency_short_name").isJsonNull()) {
                entity.setMarginCurrencyShortName(json.get("margin_currency_short_name").getAsString());
            }

            // Extract settlement_currency_conversion_price
            if (json.has("settlement_currency_conversion_price") && !json.get("settlement_currency_conversion_price").isJsonNull()) {
                entity.setSettlementCurrencyConversionPrice(json.get("settlement_currency_conversion_price").getAsDouble());
            }

            // Extract trades array and count
            if (json.has("trades") && json.get("trades").isJsonArray()) {
                int tradeCount = json.getAsJsonArray("trades").size();
                entity.setTradeCount(tradeCount);
            } else {
                entity.setTradeCount(0);
            }

        } catch (Exception e) {
            logger.error("Error parsing futures order update data", e);
        }
    }

    /**
     * Save balance update data (handles array response)
     */
    @Async
    @Transactional
    public void saveBalanceUpdateData(Object data, String channelName) {
        try {
            String jsonString = gson.toJson(data);
            logger.debug("Received balance update data: {}", jsonString);

            // Parse as JSON array
            com.google.gson.JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
            
            // Process each balance update in the array
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject balanceJson = jsonArray.get(i).getAsJsonObject();
                WebSocketBalanceUpdateData entity = parseBalanceUpdateData(balanceJson, channelName, jsonString);
                if (entity != null) {
                    balanceUpdateDataRepository.save(entity);
                    logger.debug("Saved balance update data for currency: {}", entity.getCurrencyShortName());
                }
            }
            
            logger.info("Saved {} balance update records", jsonArray.size());
        } catch (Exception e) {
            logger.error("Error saving balance update data", e);
        }
    }

    /**
     * Parse balance update data from JSON
     */
    private WebSocketBalanceUpdateData parseBalanceUpdateData(JsonObject json, String channelName, String rawData) {
        try {
            WebSocketBalanceUpdateData entity = new WebSocketBalanceUpdateData();
            entity.setChannelName(channelName);
            entity.setRawData(rawData);

            // Extract id -> balanceId
            if (json.has("id") && !json.get("id").isJsonNull()) {
                entity.setBalanceId(json.get("id").getAsString());
            }

            // Extract balance
            if (json.has("balance") && !json.get("balance").isJsonNull()) {
                entity.setBalance(new BigDecimal(json.get("balance").getAsString()));
            } else {
                entity.setBalance(BigDecimal.ZERO);
            }

            // Extract locked_balance
            if (json.has("locked_balance") && !json.get("locked_balance").isJsonNull()) {
                entity.setLockedBalance(new BigDecimal(json.get("locked_balance").getAsString()));
            } else {
                entity.setLockedBalance(BigDecimal.ZERO);
            }

            // Calculate available balance = balance - locked_balance
            BigDecimal availableBalance = entity.getBalance().subtract(entity.getLockedBalance());
            entity.setAvailableBalance(availableBalance);

            // Extract currency_id
            if (json.has("currency_id") && !json.get("currency_id").isJsonNull()) {
                entity.setCurrencyId(json.get("currency_id").getAsString());
            }

            // Extract currency_short_name
            if (json.has("currency_short_name") && !json.get("currency_short_name").isJsonNull()) {
                entity.setCurrencyShortName(json.get("currency_short_name").getAsString());
            }

            return entity;
        } catch (Exception e) {
            logger.error("Error parsing balance update data", e);
            return null;
        }
    }

    /**
     * Save futures candlestick data (handles response structure with data array)
     */
    @Async
    @Transactional
    public void saveFuturesCandlestickData(Object data, String channelName) {
        try {
            String jsonString = gson.toJson(data);
            logger.debug("Received futures candlestick data: {}", jsonString);

            JsonObject outerJson = JsonParser.parseString(jsonString).getAsJsonObject();

            // Extract the actual candlestick data from nested structure
            // Data format: {"map":{"data":"{...}","event":"candlestick"}}
            String candlestickDataString = jsonString;
            if (outerJson.has("map")) {
                JsonObject mapObject = outerJson.getAsJsonObject("map");
                if (mapObject.has("data") && !mapObject.get("data").isJsonNull()) {
                    candlestickDataString = mapObject.get("data").getAsString();
                }
            }

            JsonObject rootJson = JsonParser.parseString(candlestickDataString).getAsJsonObject();

            // Extract metadata from root level
            Long ets = rootJson.has("Ets") && !rootJson.get("Ets").isJsonNull() ? rootJson.get("Ets").getAsLong() : null;
            String interval = rootJson.has("i") && !rootJson.get("i").isJsonNull() ? rootJson.get("i").getAsString() : null;
            String channel = rootJson.has("channel") && !rootJson.get("channel").isJsonNull() ? rootJson.get("channel").getAsString() : channelName;
            String product = rootJson.has("pr") && !rootJson.get("pr").isJsonNull() ? rootJson.get("pr").getAsString() : "futures";
            
            // Parse data array
            if (rootJson.has("data") && rootJson.get("data").isJsonArray()) {
                com.google.gson.JsonArray dataArray = rootJson.getAsJsonArray("data");
                
                // Process each candlestick in the array
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject candleJson = dataArray.get(i).getAsJsonObject();
                    WebSocketFuturesCandlestickData entity = parseFuturesCandlestickData(candleJson, ets, interval, channel, product, candlestickDataString);
                    if (entity != null) {
                        futuresCandlestickDataRepository.save(entity);
                        logger.debug("Saved futures candlestick data for pair: {} duration: {}", entity.getPair(), entity.getDuration());
                    }
                }
                
                logger.debug("Saved {} futures candlestick records from channel: {}", dataArray.size(), channel);
            } else {
                logger.warn("No data array found in futures candlestick response");
            }
        } catch (Exception e) {
            logger.error("Error saving futures candlestick data", e);
        }
    }

    /**
     * Parse futures candlestick data from JSON
     */
    private WebSocketFuturesCandlestickData parseFuturesCandlestickData(JsonObject json, Long ets, String interval, String channelName, String product, String rawData) {
        try {
            WebSocketFuturesCandlestickData entity = new WebSocketFuturesCandlestickData();
            entity.setChannelName(channelName);
            entity.setProduct(product);
            entity.setRawData(rawData);
            
            // Set metadata from root level
            if (ets != null) {
                entity.setEts(ets);
            }
            if (interval != null) {
                entity.setInterval(interval);
            }

            // Extract OHLC data
            if (json.has("open") && !json.get("open").isJsonNull()) {
                entity.setOpen(new BigDecimal(json.get("open").getAsString()));
            } else {
                entity.setOpen(BigDecimal.ZERO);
            }

            if (json.has("close") && !json.get("close").isJsonNull()) {
                entity.setClose(new BigDecimal(json.get("close").getAsString()));
            } else {
                entity.setClose(BigDecimal.ZERO);
            }

            if (json.has("high") && !json.get("high").isJsonNull()) {
                entity.setHigh(new BigDecimal(json.get("high").getAsString()));
            } else {
                entity.setHigh(BigDecimal.ZERO);
            }

            if (json.has("low") && !json.get("low").isJsonNull()) {
                entity.setLow(new BigDecimal(json.get("low").getAsString()));
            } else {
                entity.setLow(BigDecimal.ZERO);
            }

            // Extract volume data
            if (json.has("volume") && !json.get("volume").isJsonNull()) {
                entity.setVolume(new BigDecimal(json.get("volume").getAsString()));
            } else {
                entity.setVolume(BigDecimal.ZERO);
            }

            if (json.has("quote_volume") && !json.get("quote_volume").isJsonNull()) {
                entity.setQuoteVolume(new BigDecimal(json.get("quote_volume").getAsString()));
            } else {
                entity.setQuoteVolume(BigDecimal.ZERO);
            }

            // Extract timestamps
            if (json.has("open_time") && !json.get("open_time").isJsonNull()) {
                entity.setOpenTime(json.get("open_time").getAsLong());
            }

            if (json.has("close_time") && !json.get("close_time").isJsonNull()) {
                entity.setCloseTime(json.get("close_time").getAsDouble());
            }

            // Extract pair, duration, and symbol
            if (json.has("pair") && !json.get("pair").isJsonNull()) {
                entity.setPair(json.get("pair").getAsString());
            }

            if (json.has("duration") && !json.get("duration").isJsonNull()) {
                entity.setDuration(json.get("duration").getAsString());
            }

            if (json.has("symbol") && !json.get("symbol").isJsonNull()) {
                entity.setSymbol(json.get("symbol").getAsString());
            }

            return entity;
        } catch (Exception e) {
            logger.error("Error parsing futures candlestick data", e);
            return null;
        }
    }

    /**
     * Save futures orderbook data
     * Handles depth-snapshot event
     */
    @Async
    public void saveFuturesOrderbookData(Object data) {
        try {
            String jsonString = gson.toJson(data);
            WebSocketFuturesOrderbookData orderbookData = parseFuturesOrderbookData(jsonString);
            if (orderbookData != null) {
                futuresOrderbookDataRepository.save(orderbookData);
                logger.debug("Saved futures orderbook data: {}", orderbookData.getChannelName());
            }
        } catch (Exception e) {
            logger.error("Error saving futures orderbook data", e);
        }
    }

    /**
     * Parse futures orderbook data
     * Sample response:
     * {
     *   "ts":1705913767265,
     *   "vs":53727235,
     *   "asks":{"2410":"112.442","2409.77":"55.997","2409.78":"5.912"},
     *   "bids":{"2409.76":"12.417","2409.75":"1.516","2409.74":"15.876"},
     *   "pr":"futures"
     * }
     */
    private WebSocketFuturesOrderbookData parseFuturesOrderbookData(String data) {
        try {
            JsonObject outerJson = JsonParser.parseString(data).getAsJsonObject();

            // Extract the actual orderbook data from nested structure
            // Data format: {"map":{"data":"{...}","event":"depth-update"}}
            String orderbookDataString = data;
            if (outerJson.has("map")) {
                JsonObject mapObject = outerJson.getAsJsonObject("map");
                if (mapObject.has("data") && !mapObject.get("data").isJsonNull()) {
                    orderbookDataString = mapObject.get("data").getAsString();
                }
            }

            JsonObject json = JsonParser.parseString(orderbookDataString).getAsJsonObject();
            WebSocketFuturesOrderbookData entity = new WebSocketFuturesOrderbookData();

            // Set raw data
            entity.setRawData(orderbookDataString);

            // Extract timestamp (ts field)
            if (json.has("ts") && !json.get("ts").isJsonNull()) {
                entity.setTimestamp(json.get("ts").getAsLong());
            }

            // Extract version sequence (vs field)
            if (json.has("vs") && !json.get("vs").isJsonNull()) {
                entity.setVersionSequence(json.get("vs").getAsLong());
            }

            // Extract asks as JSON string
            if (json.has("asks") && !json.get("asks").isJsonNull()) {
                entity.setAsks(json.get("asks").toString());
            }

            // Extract bids as JSON string
            if (json.has("bids") && !json.get("bids").isJsonNull()) {
                entity.setBids(json.get("bids").toString());
            }

            // Extract product type (pr field)
            if (json.has("pr") && !json.get("pr").isJsonNull()) {
                entity.setProductType(json.get("pr").getAsString());
            }

            // Extract channel name from metadata (if available)
            if (json.has("channel") && !json.get("channel").isJsonNull()) {
                String channelName = json.get("channel").getAsString();
                entity.setChannelName(channelName);

                // Extract instrument name and depth from channel
                // Channel format: [instrument_name]@orderbook@{depth}-futures
                // Example: B-ID_USDT@orderbook@50-futures
                if (channelName.contains("@orderbook@")) {
                    String[] parts = channelName.split("@orderbook@");
                    if (parts.length >= 1) {
                        entity.setInstrumentName(parts[0]);
                    }
                    if (parts.length >= 2) {
                        String depthPart = parts[1].replace("-futures", "");
                        try {
                            entity.setDepth(Integer.parseInt(depthPart));
                        } catch (NumberFormatException e) {
                            logger.warn("Could not parse depth from channel: {}", channelName);
                        }
                    }
                }
            }

            return entity;
        } catch (Exception e) {
            logger.error("Error parsing futures orderbook data", e);
            return null;
        }
    }

    /**
     * Save futures current prices data
     * Handles currentPrices@futures#update event
     */
    @Async
    public void saveFuturesCurrentPricesData(Object data) {
        try {
            String jsonString = gson.toJson(data);
            WebSocketFuturesCurrentPricesData pricesData = parseFuturesCurrentPricesData(jsonString);
            if (pricesData != null) {
                futuresCurrentPricesDataRepository.save(pricesData);
                logger.debug("Saved futures current prices data");
            }
        } catch (Exception e) {
            logger.error("Error saving futures current prices data", e);
        }
    }

    /**
     * Parse futures current prices data
     * Sample response:
     * {
     *   "vs":29358821,
     *   "ts":1707384027242,
     *   "pr":"futures",
     *   "pST":1707384027230,
     *   "prices":{
     *     "B-UNI_USDT":{"bmST":1707384027000,"cmRT":1707384027149},
     *     "B-LDO_USDT":{"mp":2.87559482,"bmST":1707384027000,"cmRT":1707384027149}
     *   }
     * }
     */
    private WebSocketFuturesCurrentPricesData parseFuturesCurrentPricesData(String data) {
        try {
            JsonObject outerJson = JsonParser.parseString(data).getAsJsonObject();

            // Extract the actual prices data from nested structure
            // Data format: {"map":{"data":"{...}","event":"currentPrices@futures#update"}}
            String pricesDataString = data;
            if (outerJson.has("map")) {
                JsonObject mapObject = outerJson.getAsJsonObject("map");
                if (mapObject.has("data") && !mapObject.get("data").isJsonNull()) {
                    pricesDataString = mapObject.get("data").getAsString();
                }
            }

            JsonObject json = JsonParser.parseString(pricesDataString).getAsJsonObject();
            WebSocketFuturesCurrentPricesData entity = new WebSocketFuturesCurrentPricesData();

            // Set raw data
            entity.setRawData(pricesDataString);

            // Extract timestamp (ts field)
            if (json.has("ts") && !json.get("ts").isJsonNull()) {
                entity.setTimestamp(json.get("ts").getAsLong());
            }

            // Extract version sequence (vs field)
            if (json.has("vs") && !json.get("vs").isJsonNull()) {
                entity.setVersionSequence(json.get("vs").getAsLong());
            }

            // Extract product type (pr field)
            if (json.has("pr") && !json.get("pr").isJsonNull()) {
                entity.setProductType(json.get("pr").getAsString());
            }

            // Extract prices sent timestamp (pST field)
            if (json.has("pST") && !json.get("pST").isJsonNull()) {
                entity.setPricesSentTimestamp(json.get("pST").getAsLong());
            }

            // Extract prices as JSON string
            if (json.has("prices") && !json.get("prices").isJsonNull()) {
                entity.setPrices(json.get("prices").toString());
            }

            return entity;
        } catch (Exception e) {
            logger.error("Error parsing futures current prices data", e);
            return null;
        }
    }

    /**
     * Save futures new trade data
     * Sample response:
     * {
     *   "T": 1705516361108,       // Trade timestamp
     *   "RT": 1705516416271.6133, // Received timestamp
     *   "p": "0.3473",            // Price
     *   "q": "40",                // Quantity
     *   "m": 1,                   // Is buyer maker (1=sell, 0=buy)
     *   "s": "B-ID_USDT",         // Symbol
     *   "pr": "f"                 // Product type (futures)
     * }
     */
    @Async
    @Transactional
    public void saveFuturesNewTradeData(Object data, String channelName) {
        try {
            logger.debug("Received futures new trade data from channel: {}, data type: {}", channelName, data.getClass().getSimpleName());

            String jsonString = gson.toJson(data);
            logger.debug("Converted to JSON string (first 300 chars): {}",
                jsonString.length() > 300 ? jsonString.substring(0, 300) + "..." : jsonString);

            WebSocketFuturesNewTradeData tradeData = parseFuturesNewTradeData(jsonString, channelName);

            if (tradeData != null) {
                // Validate required fields before attempting to save
                if (tradeData.getTradeTimestamp() == null) {
                    logger.error("Cannot save futures new trade data: tradeTimestamp is null");
                    logger.error("Entity state - Symbol: {}, Price: {}, Quantity: {}, Channel: {}",
                        tradeData.getSymbol(), tradeData.getPrice(), tradeData.getQuantity(), tradeData.getChannelName());
                    return;
                }

                futuresNewTradeDataRepository.save(tradeData);
                logger.debug("✅ Saved futures new trade data - Timestamp: {}, Symbol: {}, Price: {}, Quantity: {}",
                    tradeData.getTradeTimestamp(), tradeData.getSymbol(), tradeData.getPrice(), tradeData.getQuantity());
            } else {
                logger.warn("Failed to parse futures new trade data, skipping save");
            }
        } catch (Exception e) {
            logger.error("Error saving futures new trade data", e);
        }
    }

    /**
     * Parse futures new trade data
     */
    private WebSocketFuturesNewTradeData parseFuturesNewTradeData(String data, String channelName) {
        try {
            logger.debug("Parsing futures new trade data. Input data (first 200 chars): {}",
                data.length() > 200 ? data.substring(0, 200) + "..." : data);

            JsonObject outerJson = JsonParser.parseString(data).getAsJsonObject();
            logger.debug("Parsed outer JSON, has 'map' field: {}", outerJson.has("map"));

            // Extract the actual trade data from nested structure
            // Data format: {"map":{"data":"{...}","event":"new-trade"}}
            String tradeDataString = data;
            if (outerJson.has("map")) {
                JsonObject mapObject = outerJson.getAsJsonObject("map");
                logger.debug("Map object keys: {}", mapObject.keySet());

                if (mapObject.has("data") && !mapObject.get("data").isJsonNull()) {
                    tradeDataString = mapObject.get("data").getAsString();
                    logger.debug("Extracted inner trade data string (first 200 chars): {}",
                        tradeDataString.length() > 200 ? tradeDataString.substring(0, 200) + "..." : tradeDataString);
                }
            }

            JsonObject json = JsonParser.parseString(tradeDataString).getAsJsonObject();
            logger.debug("Parsed trade data JSON, keys: {}", json.keySet());
            logger.debug("Has 'T' field: {}, Value: {}", json.has("T"), json.has("T") ? json.get("T") : "N/A");

            WebSocketFuturesNewTradeData entity = new WebSocketFuturesNewTradeData();

            // Set raw data and channel name
            entity.setRawData(tradeDataString);
            entity.setChannelName(channelName);

            // Extract trade timestamp (T field) - REQUIRED FIELD
            if (json.has("T") && !json.get("T").isJsonNull()) {
                long timestamp = json.get("T").getAsLong();
                entity.setTradeTimestamp(timestamp);
                logger.debug("Set tradeTimestamp: {}", timestamp);
            } else {
                logger.error("CRITICAL: 'T' field is missing or null in trade data! Cannot save without timestamp.");
                logger.error("Trade data keys available: {}", json.keySet());
                logger.error("Raw trade data: {}", tradeDataString);
                return null;
            }

            // Extract received timestamp (RT field) as BigDecimal for precision
            if (json.has("RT") && !json.get("RT").isJsonNull()) {
                entity.setReceivedTimestamp(json.get("RT").getAsBigDecimal());
            }

            // Extract price (p field) as BigDecimal
            if (json.has("p") && !json.get("p").isJsonNull()) {
                String priceStr = json.get("p").getAsString();
                entity.setPrice(new BigDecimal(priceStr));
            }

            // Extract quantity (q field) as BigDecimal
            if (json.has("q") && !json.get("q").isJsonNull()) {
                String quantityStr = json.get("q").getAsString();
                entity.setQuantity(new BigDecimal(quantityStr));
            }

            // Extract is buyer maker flag (m field) - 1 = sell/maker, 0 = buy/taker
            if (json.has("m") && !json.get("m").isJsonNull()) {
                entity.setIsBuyerMaker(json.get("m").getAsInt());
            }

            // Extract symbol (s field)
            if (json.has("s") && !json.get("s").isJsonNull()) {
                entity.setSymbol(json.get("s").getAsString());
            }

            // Extract product type (pr field)
            if (json.has("pr") && !json.get("pr").isJsonNull()) {
                entity.setProductType(json.get("pr").getAsString());
            }

            logger.debug("Successfully parsed futures new trade data. TradeTimestamp: {}, Symbol: {}",
                entity.getTradeTimestamp(), entity.getSymbol());

            return entity;
        } catch (Exception e) {
            logger.error("Error parsing futures new trade data: {}", e.getMessage(), e);
            logger.error("Failed to parse data (first 500 chars): {}",
                data.length() > 500 ? data.substring(0, 500) + "..." : data);
            return null;
        }
    }
}
