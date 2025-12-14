package com.coindcx.springclient.service;

import com.coindcx.springclient.api.PublicApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Public market data operations
 */
@Service
public class PublicService {

    private final PublicApi publicApi;

    public PublicService() {
        this.publicApi = new PublicApi();
    }

    public PublicService(PublicApi publicApi) {
        this.publicApi = publicApi;
    }

    /**
     * Get ticker information for all markets
     * @return List of ticker data
     * @throws ApiException if the API call fails
     */
    public List<ExchangeTickerGet200ResponseInner> getTicker() throws ApiException {
        return publicApi.exchangeTickerGet();
    }

    /**
     * Get market details
     * @return List of market details
     * @throws ApiException if the API call fails
     */
    public List<ExchangeV1MarketsDetailsGet200ResponseInner> getMarketDetails() throws ApiException {
        return publicApi.exchangeV1MarketsDetailsGet();
    }

    /**
     * Get markets information
     * @return List of markets
     * @throws ApiException if the API call fails
     */
    public Object getMarkets() throws ApiException {
        return publicApi.exchangeV1MarketsGet();
    }

    /**
     * Get candlestick data
     * @param pair Trading pair
     * @param interval Time interval
     * @param startTime Start timestamp
     * @param endTime End timestamp
     * @param limit Number of candles
     * @return List of candlestick data
     * @throws ApiException if the API call fails
     */
    public List<MarketDataCandlesGet200ResponseInner> getCandles(String pair, String interval, Long startTime, Long endTime, Integer limit) throws ApiException {
        return publicApi.marketDataCandlesGet(pair, interval, startTime, endTime, limit);
    }

    /**
     * Get orderbook for a market
     * @param pair Trading pair
     * @return Orderbook data
     * @throws ApiException if the API call fails
     */
    public MarketDataOrderbookGet200Response getOrderbook(String pair) throws ApiException {
        return publicApi.marketDataOrderbookGet(pair);
    }

    /**
     * Get trade history for a market
     * @param pair Trading pair
     * @param limit Number of trades
     * @return List of recent trades
     * @throws ApiException if the API call fails
     */
    public List<MarketDataTradeHistoryGet200ResponseInner> getTradeHistory(String pair, Integer limit) throws ApiException {
        return publicApi.marketDataTradeHistoryGet(pair, limit);
    }

    /**
     * Get futures depth/orderbook
     * @param instrument Futures instrument
     * @param depth Depth level
     * @return Futures orderbook depth
     * @throws ApiException if the API call fails
     */
    public MarketDataV3OrderbookInstrumentFuturesDepthGet200Response getFuturesDepth(String instrument, String depth) throws ApiException {
        return publicApi.marketDataV3OrderbookInstrumentFuturesDepthGet(instrument, depth);
    }
}
