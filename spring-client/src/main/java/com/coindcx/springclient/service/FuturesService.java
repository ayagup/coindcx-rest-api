package com.coindcx.springclient.service;

import com.coindcx.springclient.api.FuturesApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Futures trading operations
 */
@Service
public class FuturesService {

    private final FuturesApi futuresApi;

    public FuturesService() {
        this.futuresApi = new FuturesApi();
    }

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
     * Create a new futures order
     * @param request Order creation request
     * @throws ApiException if the API call fails
     */
    public void createOrder(ExchangeV1DerivativesFuturesOrdersCreatePostRequest request) throws ApiException {
        futuresApi.exchangeV1DerivativesFuturesOrdersCreatePost(request);
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
}
