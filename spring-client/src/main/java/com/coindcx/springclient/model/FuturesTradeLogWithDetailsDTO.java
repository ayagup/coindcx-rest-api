package com.coindcx.springclient.model;

import com.coindcx.springclient.entity.FuturesTradeLog;
import com.coindcx.springclient.entity.WebSocketFuturesOrderUpdateData;
import com.coindcx.springclient.entity.WebSocketFuturesPositionUpdateData;

/**
 * Response DTO that wraps a FuturesTradeLog entry together with the latest
 * known order details and position details for that log row.
 *
 * {@code orderDetails} is populated when {@code orderId} is non-null and a
 * matching row exists in {@code websocket_futures_order_update_data}.
 *
 * {@code positionDetails} is populated when {@code positionId} is non-null
 * and a matching row exists in {@code websocket_futures_position_update_data}.
 */
public class FuturesTradeLogWithDetailsDTO {

    private FuturesTradeLog log;

    /** Latest order state from WebSocket order-update channel. May be null. */
    private WebSocketFuturesOrderUpdateData orderDetails;

    /** Latest position state from WebSocket position-update channel. May be null. */
    private WebSocketFuturesPositionUpdateData positionDetails;

    public FuturesTradeLogWithDetailsDTO() {}

    public FuturesTradeLogWithDetailsDTO(FuturesTradeLog log,
                                         WebSocketFuturesOrderUpdateData orderDetails,
                                         WebSocketFuturesPositionUpdateData positionDetails) {
        this.log = log;
        this.orderDetails = orderDetails;
        this.positionDetails = positionDetails;
    }

    public FuturesTradeLog getLog() {
        return log;
    }

    public void setLog(FuturesTradeLog log) {
        this.log = log;
    }

    public WebSocketFuturesOrderUpdateData getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(WebSocketFuturesOrderUpdateData orderDetails) {
        this.orderDetails = orderDetails;
    }

    public WebSocketFuturesPositionUpdateData getPositionDetails() {
        return positionDetails;
    }

    public void setPositionDetails(WebSocketFuturesPositionUpdateData positionDetails) {
        this.positionDetails = positionDetails;
    }
}
