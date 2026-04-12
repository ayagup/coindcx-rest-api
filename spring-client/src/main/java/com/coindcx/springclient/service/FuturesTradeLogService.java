package com.coindcx.springclient.service;

import com.coindcx.springclient.entity.FuturesTradeLog;
import com.coindcx.springclient.entity.WebSocketFuturesOrderUpdateData;
import com.coindcx.springclient.entity.WebSocketFuturesPositionUpdateData;
import com.coindcx.springclient.model.ExchangeV1DerivativesFuturesOrdersCreatePostRequest;
import com.coindcx.springclient.model.ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest;
import com.coindcx.springclient.model.ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest;
import com.coindcx.springclient.model.ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss;
import com.coindcx.springclient.model.ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit;
import com.coindcx.springclient.repository.FuturesTradeLogRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists high-value futures trade lifecycle events into a dedicated database log.
 */
@Service
public class FuturesTradeLogService {

    private static final double POSITION_EPSILON = 1e-9;

    private final FuturesTradeLogRepository futuresTradeLogRepository;
    private final Gson gson;

    public FuturesTradeLogService(FuturesTradeLogRepository futuresTradeLogRepository) {
        this.futuresTradeLogRepository = futuresTradeLogRepository;
        this.gson = new Gson();
    }

    @Transactional
    public void logApiOrderSubmission(ExchangeV1DerivativesFuturesOrdersCreatePostRequest request, String responseBody) {
        String orderId = extractOrderId(responseBody);

        appendLog(buildBaseLog("API", "ORDER_SUBMITTED")
                .pair(request.getPair())
                .orderId(orderId)
                .side(enumValue(request.getSide()))
                .status("submitted")
                .orderType(enumValue(request.getOrderType()))
                .quantity(toDouble(request.getTotalQuantity()))
                .price(toDouble(request.getPrice()))
                .triggerPrice(toDouble(request.getStopPrice()))
                .eventTimestamp(request.getTimestamp())
                .message("Futures order submitted via API")
                .rawData(responseBody));

        if (request.getTakeProfitPrice() != null) {
            appendLog(buildBaseLog("API", "TAKE_PROFIT_SET")
                    .pair(request.getPair())
                    .orderId(orderId)
                    .side(enumValue(request.getSide()))
                    .status("submitted")
                    .orderType(enumValue(request.getOrderType()))
                    .quantity(toDouble(request.getTotalQuantity()))
                    .triggerType("TAKE_PROFIT")
                    .triggerPrice(toDouble(request.getTakeProfitPrice()))
                    .eventTimestamp(request.getTimestamp())
                    .message("Take profit attached to order submission")
                    .rawData(responseBody));
        }

        if (request.getStopLossPrice() != null) {
            appendLog(buildBaseLog("API", "STOP_LOSS_SET")
                    .pair(request.getPair())
                    .orderId(orderId)
                    .side(enumValue(request.getSide()))
                    .status("submitted")
                    .orderType(enumValue(request.getOrderType()))
                    .quantity(toDouble(request.getTotalQuantity()))
                    .triggerType("STOP_LOSS")
                    .triggerPrice(toDouble(request.getStopLossPrice()))
                    .eventTimestamp(request.getTimestamp())
                    .message("Stop loss attached to order submission")
                    .rawData(responseBody));
        }
    }

    @Transactional
    public void logApiTpSlRequest(ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest request, String pair) {
        ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestTakeProfit takeProfit = request.getTakeProfit();
        if (takeProfit != null) {
            appendLog(buildBaseLog("API", "TAKE_PROFIT_SET")
                    .pair(pair)
                    .positionId(request.getId())
                    .status("submitted")
                    .orderType(enumValue(takeProfit.getOrderType()))
                    .triggerType("TAKE_PROFIT")
                    .triggerPrice(parseDouble(takeProfit.getStopPrice()))
                    .eventTimestamp(request.getTimestamp())
                    .message("Take profit submitted for position")
                    .rawData(gson.toJson(request)));
        }

        ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequestStopLoss stopLoss = request.getStopLoss();
        if (stopLoss != null) {
            appendLog(buildBaseLog("API", "STOP_LOSS_SET")
                    .pair(pair)
                    .positionId(request.getId())
                    .status("submitted")
                    .orderType(enumValue(stopLoss.getOrderType()))
                    .triggerType("STOP_LOSS")
                    .triggerPrice(parseDouble(stopLoss.getStopPrice()))
                    .eventTimestamp(request.getTimestamp())
                    .message("Stop loss submitted for position")
                    .rawData(gson.toJson(request)));
        }
    }

    @Transactional
    public void logApiPositionCloseRequest(ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest request,
                                           String pair,
                                           Object response) {
        appendLog(buildBaseLog("API", "POSITION_CLOSE_REQUESTED")
                .pair(pair)
                .positionId(request.getId())
                .status("submitted")
                .eventTimestamp(request.getTimestamp())
                .message("Position close submitted via API")
                .rawData(response != null ? gson.toJson(response) : gson.toJson(request)));
    }

    @Transactional
    public void recordPositionLifecycle(WebSocketFuturesPositionUpdateData current,
                                        WebSocketFuturesPositionUpdateData previous) {
        if (current == null || isSamePositionSnapshot(previous, current)) {
            return;
        }

        boolean wasActive = isActivePosition(previous);
        boolean isActive = isActivePosition(current);

        if (!wasActive && isActive) {
            appendLog(buildBaseLog("WEBSOCKET", "POSITION_OPENED")
                    .pair(current.getPair())
                    .positionId(current.getPositionId())
                    .side(current.getSide())
                    .status(current.getStatus())
                    .quantity(current.getQuantity())
                    .price(current.getEntryPrice())
                    .eventTimestamp(current.getUpdateTimestamp())
                    .channelName(current.getChannelName())
                    .message("Position became active")
                    .rawData(current.getRawData()));
        } else if (wasActive && !isActive) {
            appendLog(buildBaseLog("WEBSOCKET", "POSITION_CLOSED")
                    .pair(current.getPair())
                    .positionId(current.getPositionId())
                    .side(previous != null && previous.getSide() != null ? previous.getSide() : current.getSide())
                    .status(current.getStatus())
                    .quantity(previous != null ? previous.getQuantity() : current.getQuantity())
                    .price(current.getCurrentPrice())
                    .eventTimestamp(current.getUpdateTimestamp())
                    .channelName(current.getChannelName())
                    .message("Position is no longer active")
                    .rawData(current.getRawData()));
        }
    }

    @Transactional
    public void recordOrderLifecycle(WebSocketFuturesOrderUpdateData current,
                                     WebSocketFuturesOrderUpdateData previous) {
        if (current == null || isSameOrderSnapshot(previous, current)) {
            return;
        }

        boolean takeProfit = isTakeProfitOrder(current);
        boolean stopLoss = isStopLossOrder(current);
        boolean triggeredNow = isTriggered(current);
        boolean triggeredBefore = isTriggered(previous);

        if (previous == null) {
            if (takeProfit) {
                appendLog(buildBaseLog("WEBSOCKET", "TAKE_PROFIT_SET")
                        .pair(current.getPair())
                        .orderId(current.getOrderId())
                        .groupId(current.getGroupId())
                        .side(current.getSide())
                        .status(current.getStatus())
                        .orderType(current.getOrderType())
                        .triggerType("TAKE_PROFIT")
                        .quantity(current.getTotalQuantity())
                        .price(firstNonNull(current.getPrice(), current.getAvgPrice()))
                        .triggerPrice(firstNonNull(current.getStopPrice(), current.getTakeProfitPrice()))
                        .eventTimestamp(current.getUpdatedAt())
                        .channelName(current.getChannelName())
                        .message(defaultMessage(current, "Take profit order observed on websocket"))
                        .rawData(current.getRawData()));
            } else if (stopLoss) {
                appendLog(buildBaseLog("WEBSOCKET", "STOP_LOSS_SET")
                        .pair(current.getPair())
                        .orderId(current.getOrderId())
                        .groupId(current.getGroupId())
                        .side(current.getSide())
                        .status(current.getStatus())
                        .orderType(current.getOrderType())
                        .triggerType("STOP_LOSS")
                        .quantity(current.getTotalQuantity())
                        .price(firstNonNull(current.getPrice(), current.getAvgPrice()))
                        .triggerPrice(firstNonNull(current.getStopPrice(), current.getStopLossPrice()))
                        .eventTimestamp(current.getUpdatedAt())
                        .channelName(current.getChannelName())
                        .message(defaultMessage(current, "Stop loss order observed on websocket"))
                        .rawData(current.getRawData()));
            }
        }

        if (triggeredNow && !triggeredBefore) {
            appendLog(buildBaseLog("WEBSOCKET", takeProfit ? "TAKE_PROFIT_TRIGGERED" : stopLoss ? "STOP_LOSS_TRIGGERED" : "TRIGGERED_ORDER")
                    .pair(current.getPair())
                    .orderId(current.getOrderId())
                    .groupId(current.getGroupId())
                    .side(current.getSide())
                    .status(current.getStatus())
                    .orderType(current.getOrderType())
                    .triggerType(takeProfit ? "TAKE_PROFIT" : stopLoss ? "STOP_LOSS" : "TRIGGERED")
                    .quantity(current.getFilledQuantity() != null ? current.getFilledQuantity() : current.getTotalQuantity())
                    .price(firstNonNull(current.getAvgPrice(), current.getPrice()))
                    .triggerPrice(current.getStopPrice())
                    .eventTimestamp(current.getUpdatedAt())
                    .channelName(current.getChannelName())
                    .message(defaultMessage(current, "Triggered order update received"))
                    .rawData(current.getRawData()));
            return;
        }

        if (!takeProfit && !stopLoss && isFilled(current) && !isFilled(previous)) {
            appendLog(buildBaseLog("WEBSOCKET", "ORDER_FILLED")
                    .pair(current.getPair())
                    .orderId(current.getOrderId())
                    .groupId(current.getGroupId())
                    .side(current.getSide())
                    .status(current.getStatus())
                    .orderType(current.getOrderType())
                    .quantity(current.getFilledQuantity() != null ? current.getFilledQuantity() : current.getTotalQuantity())
                    .price(firstNonNull(current.getAvgPrice(), current.getPrice()))
                    .eventTimestamp(current.getUpdatedAt())
                    .channelName(current.getChannelName())
                    .message(defaultMessage(current, "Order filled"))
                    .rawData(current.getRawData()));
        }
    }

    private TradeLogBuilder buildBaseLog(String source, String eventType) {
        return new TradeLogBuilder()
                .source(source)
                .eventType(eventType);
    }

    private void appendLog(TradeLogBuilder builder) {
        futuresTradeLogRepository.save(builder.build());
    }

    private boolean isActivePosition(WebSocketFuturesPositionUpdateData position) {
        if (position == null) {
            return false;
        }
        if (position.getQuantity() != null && Math.abs(position.getQuantity()) > POSITION_EPSILON) {
            return true;
        }
        return equalsIgnoreCase(position.getStatus(), "active");
    }

    private boolean isSamePositionSnapshot(WebSocketFuturesPositionUpdateData previous,
                                           WebSocketFuturesPositionUpdateData current) {
        if (previous == null || current == null) {
            return false;
        }
        return safeEquals(previous.getPositionId(), current.getPositionId())
                && safeEquals(previous.getUpdateTimestamp(), current.getUpdateTimestamp())
                && safeEquals(previous.getStatus(), current.getStatus())
                && safeEquals(previous.getQuantity(), current.getQuantity());
    }

    private boolean isSameOrderSnapshot(WebSocketFuturesOrderUpdateData previous,
                                        WebSocketFuturesOrderUpdateData current) {
        if (previous == null || current == null) {
            return false;
        }
        return safeEquals(previous.getOrderId(), current.getOrderId())
                && safeEquals(previous.getUpdatedAt(), current.getUpdatedAt())
                && safeEquals(previous.getStatus(), current.getStatus())
                && safeEquals(previous.getStage(), current.getStage())
                && safeEquals(previous.getGroupStatus(), current.getGroupStatus());
    }

    private boolean isTakeProfitOrder(WebSocketFuturesOrderUpdateData order) {
        return containsIgnoreCase(order != null ? order.getOrderType() : null, "take_profit")
                || containsIgnoreCase(order != null ? order.getOrderCategory() : null, "take_profit")
                || containsIgnoreCase(order != null ? order.getDisplayMessage() : null, "take profit")
                || (order != null && order.getTakeProfitPrice() != null);
    }

    private boolean isStopLossOrder(WebSocketFuturesOrderUpdateData order) {
        if (order == null || isTakeProfitOrder(order)) {
            return false;
        }
        return containsIgnoreCase(order.getOrderType(), "stop")
                || containsIgnoreCase(order.getOrderCategory(), "stop")
                || containsIgnoreCase(order.getDisplayMessage(), "stop loss")
                || containsIgnoreCase(order.getStopTriggerInstruction(), "stop")
                || order.getStopLossPrice() != null
                || order.getStopPrice() != null;
    }

    private boolean isTriggered(WebSocketFuturesOrderUpdateData order) {
        if (order == null) {
            return false;
        }
        return containsIgnoreCase(order.getStatus(), "trigger")
                || containsIgnoreCase(order.getStage(), "trigger")
                || containsIgnoreCase(order.getGroupStatus(), "trigger")
                || containsIgnoreCase(order.getDisplayMessage(), "trigger")
                || ((isTakeProfitOrder(order) || isStopLossOrder(order)) && isFilled(order));
    }

    private boolean isFilled(WebSocketFuturesOrderUpdateData order) {
        if (order == null) {
            return false;
        }
        return equalsIgnoreCase(order.getStatus(), "filled")
                || equalsIgnoreCase(order.getGroupStatus(), "filled")
                || equalsIgnoreCase(order.getStatus(), "close")
                || containsIgnoreCase(order.getDisplayMessage(), "filled");
    }

    private String extractOrderId(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            JsonElement root = JsonParser.parseString(responseBody);
            if (root.isJsonObject()) {
                JsonObject object = root.getAsJsonObject();
                if (object.has("id") && !object.get("id").isJsonNull()) {
                    return object.get("id").getAsString();
                }
                if (object.has("orders") && object.get("orders").isJsonArray()) {
                    JsonArray orders = object.getAsJsonArray("orders");
                    if (!orders.isEmpty()) {
                        JsonObject first = orders.get(0).getAsJsonObject();
                        if (first.has("id") && !first.get("id").isJsonNull()) {
                            return first.get("id").getAsString();
                        }
                    }
                }
                if (object.has("data") && object.get("data").isJsonObject()) {
                    JsonObject data = object.getAsJsonObject("data");
                    if (data.has("id") && !data.get("id").isJsonNull()) {
                        return data.get("id").getAsString();
                    }
                }
            }
        } catch (Exception ignored) {
            // Best-effort extraction only.
        }

        return null;
    }

    private String enumValue(Object enumValue) {
        return enumValue != null ? enumValue.toString() : null;
    }

    private Double parseDouble(String value) {
        try {
            return value != null ? Double.parseDouble(value) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Double toDouble(Number number) {
        return number != null ? number.doubleValue() : null;
    }

    private Double firstNonNull(Double first, Double second) {
        return first != null ? first : second;
    }

    private String defaultMessage(WebSocketFuturesOrderUpdateData order, String fallback) {
        if (order != null && order.getDisplayMessage() != null && !order.getDisplayMessage().isBlank()) {
            return order.getDisplayMessage();
        }
        return fallback;
    }

    private boolean containsIgnoreCase(String value, String needle) {
        return value != null && needle != null && value.toLowerCase().contains(needle.toLowerCase());
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return left != null && right != null && left.equalsIgnoreCase(right);
    }

    private boolean safeEquals(Object left, Object right) {
        return left == null ? right == null : left.equals(right);
    }

    private static class TradeLogBuilder {
        private final FuturesTradeLog log = new FuturesTradeLog();

        TradeLogBuilder source(String source) {
            log.setSource(source);
            return this;
        }

        TradeLogBuilder eventType(String eventType) {
            log.setEventType(eventType);
            return this;
        }

        TradeLogBuilder pair(String pair) {
            log.setPair(pair);
            return this;
        }

        TradeLogBuilder positionId(String positionId) {
            log.setPositionId(positionId);
            return this;
        }

        TradeLogBuilder orderId(String orderId) {
            log.setOrderId(orderId);
            return this;
        }

        TradeLogBuilder groupId(String groupId) {
            log.setGroupId(groupId);
            return this;
        }

        TradeLogBuilder side(String side) {
            log.setSide(side);
            return this;
        }

        TradeLogBuilder status(String status) {
            log.setStatus(status);
            return this;
        }

        TradeLogBuilder orderType(String orderType) {
            log.setOrderType(orderType);
            return this;
        }

        TradeLogBuilder triggerType(String triggerType) {
            log.setTriggerType(triggerType);
            return this;
        }

        TradeLogBuilder quantity(Double quantity) {
            log.setQuantity(quantity);
            return this;
        }

        TradeLogBuilder price(Double price) {
            log.setPrice(price);
            return this;
        }

        TradeLogBuilder triggerPrice(Double triggerPrice) {
            log.setTriggerPrice(triggerPrice);
            return this;
        }

        TradeLogBuilder eventTimestamp(Long eventTimestamp) {
            log.setEventTimestamp(eventTimestamp);
            return this;
        }

        TradeLogBuilder channelName(String channelName) {
            log.setChannelName(channelName);
            return this;
        }

        TradeLogBuilder message(String message) {
            log.setMessage(message);
            return this;
        }

        TradeLogBuilder rawData(String rawData) {
            log.setRawData(rawData);
            return this;
        }

        FuturesTradeLog build() {
            return log;
        }
    }
}
