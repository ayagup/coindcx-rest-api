package com.coindcx.springclient.service;

import com.coindcx.springclient.api.FuturesApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.client.auth.ApiKeyAuth;
import com.coindcx.springclient.client.auth.HmacSignatureAuth;
import com.coindcx.springclient.config.WebSocketConfig;
import com.coindcx.springclient.model.*;
import okhttp3.Call;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for Futures trading operations
 */
@Service
public class FuturesService {

    private final FuturesApi futuresApi;

    @Autowired
    public FuturesService(WebSocketConfig config) {
        this.futuresApi = new FuturesApi();
        // X-AUTH-APIKEY – static value, set once
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) futuresApi.getApiClient().getAuthentication("ApiKeyAuth");
        apiKeyAuth.setApiKey(config.getApiKey());
        // X-AUTH-SIGNATURE – computed per-request over the actual body bytes
        HmacSignatureAuth sigAuth = (HmacSignatureAuth) futuresApi.getApiClient().getAuthentication("SignatureAuth");
        sigAuth.setApiSecret(config.getApiSecret());
    }

    /** Testing constructor **/
    public FuturesService(FuturesApi futuresApi) {
        this.futuresApi = futuresApi;
    }

    /**
     * Get active futures instruments
     * @param marginCurrencyShortName Futures margin mode
     * @return List of active instruments
     * @throws ApiException if the API call fails
     */
    public List<Object> getActiveInstruments(String marginCurrencyShortName) throws ApiException {
        return futuresApi.exchangeV1DerivativesFuturesDataActiveInstrumentsGet(marginCurrencyShortName);
    }

    /**
     * Create a new futures order.
     *
     * CoinDCX requires the body to be wrapped as:
     *   {"timestamp": <ms>, "order": {side, pair, order_type, total_quantity, ...}}
     * NOT a flat structure.  This matches the MCP reference implementation in
     * coindcx_mcp/client.py::create_futures_order() where payload = {"order": order}.
     *
     * @param request Order creation request (timestamp must be set by caller)
     * @return Raw JSON response body from CoinDCX
     * @throws ApiException if the call cannot be executed or CoinDCX returns an error
     */
    public String createOrder(ExchangeV1DerivativesFuturesOrdersCreatePostRequest request) throws ApiException {
        // Build the inner "order" object — only include non-null optional fields
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("side", request.getSide().getValue());
        orderMap.put("pair", request.getPair());
        orderMap.put("order_type", request.getOrderType().getValue());
        orderMap.put("total_quantity", request.getTotalQuantity());
        orderMap.put("notification",
                request.getNotification() != null
                        ? request.getNotification().getValue()
                        : "no_notification");

        if (request.getLeverage() != null) {
            orderMap.put("leverage", request.getLeverage());
        }
        if (request.getPrice() != null) {
            orderMap.put("price", request.getPrice());
        }
        if (request.getStopPrice() != null) {
            orderMap.put("stop_price", request.getStopPrice());
        }
        // time_in_force is null for market/stop_market/take_profit_market orders — only send when set
        if (request.getTimeInForce() != null) {
            orderMap.put("time_in_force", request.getTimeInForce().getValue());
        }
        if (request.getPositionMarginType() != null) {
            orderMap.put("position_margin_type", request.getPositionMarginType().getValue());
        }
        // margin_currency_short_name: only send when explicitly non-USDT (per MCP reference)
        if (request.getMarginCurrencyShortName() != null
                && !ExchangeV1DerivativesFuturesOrdersCreatePostRequest.MarginCurrencyShortNameEnum.USDT
                        .equals(request.getMarginCurrencyShortName())) {
            orderMap.put("margin_currency_short_name", request.getMarginCurrencyShortName().getValue());
        }
        if (request.getTakeProfitPrice() != null) {
            orderMap.put("take_profit_price", request.getTakeProfitPrice());
        }
        if (request.getStopLossPrice() != null) {
            orderMap.put("stop_loss_price", request.getStopLossPrice());
        }

        // Outer wrapper: timestamp at top level, order fields nested under "order" key
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", request.getTimestamp() != null ? request.getTimestamp() : System.currentTimeMillis());
        body.put("order", orderMap);

        // buildCall() requires explicit Content-Type / Accept headers (generated methods set these automatically)
        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/json");
        headerParams.put("Accept", "application/json");

        Call call = futuresApi.getApiClient().buildCall(
                null,
                "/exchange/v1/derivatives/futures/orders/create",
                "POST",
                new ArrayList<>(),
                new ArrayList<>(),
                body,
                headerParams,
                new HashMap<>(),
                new HashMap<>(),
                new String[]{"ApiKeyAuth", "SignatureAuth"},
                null
        );

        try (Response response = call.execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new ApiException(response.code(),
                        "Order creation failed (HTTP " + response.code() + "): " + responseBody);
            }
            return responseBody.isEmpty() ? "{\"status\":\"ok\"}" : responseBody;
        } catch (IOException e) {
            throw new ApiException("IO error calling create order: " + e.getMessage());
        }
    }

    /**
     * Cancel a futures order
     * @param request Order cancellation request
     * @return Cancellation response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1DerivativesFuturesOrdersCancelPost200Response cancelOrder(ExchangeV1DerivativesFuturesOrdersCancelPostRequest request) throws ApiException {
        return futuresApi.exchangeV1DerivativesFuturesOrdersCancelPost(request);
    }

    /**
     * Get futures orders
     * @param request Orders fetch request
     * @throws ApiException if the API call fails
     */
    public void getOrders(ExchangeV1DerivativesFuturesOrdersPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesOrdersPost(request);
    }

    /**
     * Get futures positions
     * @param request Positions fetch request
     * @throws ApiException if the API call fails
     */
    public void getPositions(ExchangeV1DerivativesFuturesPositionsPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsPost(request);
    }

    /**
     * Add margin to position
     * @param request Add margin request
     * @throws ApiException if the API call fails
     */
    public void addMargin(ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsAddMarginPost(request);
    }

    /**
     * Remove margin from position
     * @param request Remove margin request
     * @throws ApiException if the API call fails
     */
    public void removeMargin(ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsRemoveMarginPost(request);
    }

    /**
     * Update leverage for position
     * @param request Update leverage request
     * @throws ApiException if the API call fails
     */
    public void updateLeverage(ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsUpdateLeveragePost(request);
    }

    /**
     * Exit a futures position
     * @param request Exit position request
     * @return Exit response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1DerivativesFuturesPositionsExitPost200Response exitPosition(ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest request) throws ApiException {
        return futuresApi.exchangeV1DerivativesFuturesPositionsExitPost(request);
    }

    /**
     * Create TP/SL for position
     * @param request TP/SL creation request
     * @throws ApiException if the API call fails
     */
    public void createTpSl(ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsCreateTpslPost(request);
    }

    /**
     * Cancel all open orders for position
     * @param request Cancel request
     * @throws ApiException if the API call fails
     */
    public void cancelAllOpenOrdersForPosition(ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost(request);
    }

    /**
     * Cancel all open orders
     * @param request Cancel request
     * @throws ApiException if the API call fails
     */
    public void cancelAllOpenOrders(ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost(request);
    }

    /**
     * Get position transactions
     * @param request Transactions fetch request
     * @throws ApiException if the API call fails
     */
    public void getPositionTransactions(ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesPositionsTransactionsPost(request);
    }

    /**
     * Get futures trades
     * @param request Trades fetch request
     * @throws ApiException if the API call fails
     */
    public void getTrades(ExchangeV1DerivativesFuturesTradesPostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesTradesPost(request);
    }

    /**
     * Get futures depth/orderbook
     * @param instrument Instrument identifier
     * @param depth Depth level
     * @return Orderbook depth
     * @throws ApiException if the API call fails
     */
    public MarketDataV3OrderbookInstrumentFuturesDepthGet200Response getDepth(String instrument, String depth) throws ApiException {
        return futuresApi.marketDataV3OrderbookInstrumentFuturesDepthGet(instrument, depth);
    }

    /**
     * Get futures wallet balances.
     * Calls POST /exchange/v1/derivatives/futures/wallets with a timestamp body.
     *
     * @return Raw JSON response string from CoinDCX
     * @throws ApiException if the API call fails
     */
    public String getWallets() throws ApiException {
        long timestamp = System.currentTimeMillis();
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", timestamp);

        Call call = futuresApi.getApiClient().buildCall(
                null,
                "/exchange/v1/derivatives/futures/wallets",
                "POST",
                new ArrayList<>(),
                new ArrayList<>(),
                body,
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new String[]{"ApiKeyAuth", "SignatureAuth"},
                null
        );

        try (Response response = call.execute()) {
            String responseBody = response.body() != null ? response.body().string() : "{}";
            if (!response.isSuccessful()) {
                throw new ApiException(response.code(), "Wallets request failed: " + responseBody);
            }
            return responseBody;
        } catch (IOException e) {
            throw new ApiException("IO error calling /exchange/v1/derivatives/futures/wallets: " + e.getMessage());
        }
    }
}
