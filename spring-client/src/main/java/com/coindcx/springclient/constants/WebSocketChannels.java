package com.coindcx.springclient.constants;

/**
 * Constants for CoinDCX WebSocket channels and events
 */
public class WebSocketChannels {
    
    // Private Channels (require authentication)
    public static final String CHANNEL_PRIVATE_COINDCX = "coindcx";
    public static final String CHANNEL_PRIVATE_FUTURES_BTC_PRICE_1M = "B-BTC_USDT_1m-futures";
    public static final String CHANNEL_PRIVATE_FUTURES_BTC_NEW_TRADES = "B-BTC_USDT@trades-futures";
    public static final String CHANNEL_PRIVATE_FUTURES_BTC_DEPTH_UPDATE = "B-BTC_USDT@orderbook@20-futures";
    public static final String CHANNEL_PRIVATE_FUTURES_CURRENT_PRICES = "currentPrices@futures@rt";
    
    // Public Channel Patterns
    public static final String CHANNEL_CANDLESTICK_PATTERN = "%s_%s"; // pair_interval (e.g., B-BTC_USDT_1m)
    public static final String CHANNEL_ORDERBOOK_PATTERN = "%s@orderbook@%d"; // pair@orderbook@depth (e.g., B-BTC_USDT@orderbook@20)
    public static final String CHANNEL_TRADES_PATTERN = "%s@trades"; // pair@trades (e.g., B-BTC_USDT@trades)
    public static final String CHANNEL_PRICES_PATTERN = "%s@prices"; // pair@prices (e.g., B-BTC_USDT@prices)
    public static final String CHANNEL_CURRENT_PRICES = "currentPrices@spot@%s"; // interval (1s or 10s)
    public static final String CHANNEL_PRICE_STATS = "priceStats@spot@%s"; // interval (60s)
    
    // Events - Private
    public static final String EVENT_BALANCE_UPDATE = "balance-update";
    public static final String EVENT_ORDER_UPDATE = "order-update";
    public static final String EVENT_TRADE_UPDATE = "trade-update";
    public static final String EVENT_FUTURES_POSITION_UPDATE = "df-position-update";
    public static final String EVENT_FUTURES_ORDER_UPDATE = "df-order-update";
    public static final String EVENT_FUTURES_BALANCE_UPDATE = "balance-update";
    
    // Events - Public
    public static final String EVENT_CANDLESTICK = "candlestick";
    public static final String EVENT_DEPTH_SNAPSHOT = "depth-snapshot";
    public static final String EVENT_DEPTH_UPDATE = "depth-update";
    public static final String EVENT_NEW_TRADE = "new-trade";
    public static final String EVENT_PRICE_CHANGE = "price-change";
    public static final String EVENT_CURRENT_PRICES_UPDATE = "currentPrices@spot#update";
    public static final String EVENT_PRICE_STATS_UPDATE = "priceStats@spot#update";
    public static final String EVENT_FUTURES_CURRENT_PRICES_UPDATE = "currentPrices@futures#update";
    
    // Socket Events
    public static final String EVENT_CONNECT = "connect";
    public static final String EVENT_CONNECT_ERROR = "connect_error";
    public static final String EVENT_DISCONNECT = "disconnect";
    
    // Actions
    public static final String ACTION_JOIN = "join";
    public static final String ACTION_LEAVE = "leave";
    
    // Candlestick Intervals
    public static final String[] CANDLESTICK_INTERVALS = {
        "1m", "5m", "15m", "30m", "1h", "4h", "8h", "1d", "3d", "1w", "1M"
    };
    
    // Orderbook Depths
    public static final int[] ORDERBOOK_DEPTHS = {10, 20, 50};
    
    // Price Update Intervals
    public static final String[] PRICE_INTERVALS = {"1s", "10s"};
    public static final String STATS_INTERVAL = "60s";
    
    /**
     * Build a candlestick channel name
     * @param pair The trading pair (e.g., "B-BTC_USDT")
     * @param interval The time interval (e.g., "1m", "5m")
     * @return Channel name
     */
    public static String buildCandlestickChannel(String pair, String interval) {
        return String.format(CHANNEL_CANDLESTICK_PATTERN, pair, interval);
    }
    
    /**
     * Build an orderbook channel name
     * @param pair The trading pair (e.g., "B-BTC_USDT")
     * @param depth The orderbook depth (10, 20, or 50)
     * @return Channel name
     */
    public static String buildOrderbookChannel(String pair, int depth) {
        return String.format(CHANNEL_ORDERBOOK_PATTERN, pair, depth);
    }
    
    /**
     * Build a trades channel name
     * @param pair The trading pair (e.g., "B-BTC_USDT")
     * @return Channel name
     */
    public static String buildTradesChannel(String pair) {
        return String.format(CHANNEL_TRADES_PATTERN, pair);
    }
    
    /**
     * Build a prices channel name
     * @param pair The trading pair (e.g., "B-BTC_USDT")
     * @return Channel name
     */
    public static String buildPricesChannel(String pair) {
        return String.format(CHANNEL_PRICES_PATTERN, pair);
    }
    
    /**
     * Build a current prices channel name
     * @param interval The update interval ("1s" or "10s")
     * @return Channel name
     */
    public static String buildCurrentPricesChannel(String interval) {
        return String.format(CHANNEL_CURRENT_PRICES, interval);
    }
    
    /**
     * Build a price stats channel name
     * @param interval The update interval (typically "60s")
     * @return Channel name
     */
    public static String buildPriceStatsChannel(String interval) {
        return String.format(CHANNEL_PRICE_STATS, interval);
    }
}
