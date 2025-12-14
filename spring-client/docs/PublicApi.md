# PublicApi

All URIs are relative to *https://api.coindcx.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exchangeTickerGet**](PublicApi.md#exchangeTickerGet) | **GET** /exchange/ticker | Get ticker information |
| [**exchangeV1MarketsDetailsGet**](PublicApi.md#exchangeV1MarketsDetailsGet) | **GET** /exchange/v1/markets_details | Get market details |
| [**exchangeV1MarketsGet**](PublicApi.md#exchangeV1MarketsGet) | **GET** /exchange/v1/markets | Get active markets |
| [**marketDataCandlesGet**](PublicApi.md#marketDataCandlesGet) | **GET** /market_data/candles | Get candlestick data |
| [**marketDataCandlesticksGet**](PublicApi.md#marketDataCandlesticksGet) | **GET** /market_data/candlesticks | Get Futures Candlesticks |
| [**marketDataOrderbookGet**](PublicApi.md#marketDataOrderbookGet) | **GET** /market_data/orderbook | Get order book |
| [**marketDataTradeHistoryGet**](PublicApi.md#marketDataTradeHistoryGet) | **GET** /market_data/trade_history | Get trade history |
| [**marketDataV3OrderbookInstrumentFuturesDepthGet**](PublicApi.md#marketDataV3OrderbookInstrumentFuturesDepthGet) | **GET** /market_data/v3/orderbook/{instrument}-futures/{depth} | Get Futures Orderbook |


<a id="exchangeTickerGet"></a>
# **exchangeTickerGet**
> List&lt;ExchangeTickerGet200ResponseInner&gt; exchangeTickerGet()

Get ticker information

Returns ticker data for all markets. A ticker is generated once every second.

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    try {
      List<ExchangeTickerGet200ResponseInner> result = apiInstance.exchangeTickerGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#exchangeTickerGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;ExchangeTickerGet200ResponseInner&gt;**](ExchangeTickerGet200ResponseInner.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1MarketsDetailsGet"></a>
# **exchangeV1MarketsDetailsGet**
> List&lt;ExchangeV1MarketsDetailsGet200ResponseInner&gt; exchangeV1MarketsDetailsGet()

Get market details

Returns detailed information about all markets

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    try {
      List<ExchangeV1MarketsDetailsGet200ResponseInner> result = apiInstance.exchangeV1MarketsDetailsGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#exchangeV1MarketsDetailsGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;ExchangeV1MarketsDetailsGet200ResponseInner&gt;**](ExchangeV1MarketsDetailsGet200ResponseInner.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1MarketsGet"></a>
# **exchangeV1MarketsGet**
> List&lt;String&gt; exchangeV1MarketsGet()

Get active markets

Returns an array of strings of currently active markets

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    try {
      List<String> result = apiInstance.exchangeV1MarketsGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#exchangeV1MarketsGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

**List&lt;String&gt;**

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="marketDataCandlesGet"></a>
# **marketDataCandlesGet**
> List&lt;MarketDataCandlesGet200ResponseInner&gt; marketDataCandlesGet(pair, interval, startTime, endTime, limit)

Get candlestick data

Returns candlestick bars for given pair

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    String pair = "B-BTC_USDT"; // String | Market pair from Market Details API
    String interval = "1m"; // String | Candlestick interval
    Long startTime = 56L; // Long | Timestamp in milliseconds
    Long endTime = 56L; // Long | Timestamp in milliseconds
    Integer limit = 500; // Integer | Number of candles to return
    try {
      List<MarketDataCandlesGet200ResponseInner> result = apiInstance.marketDataCandlesGet(pair, interval, startTime, endTime, limit);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#marketDataCandlesGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pair** | **String**| Market pair from Market Details API | |
| **interval** | **String**| Candlestick interval | [enum: 1m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 1d, 3d, 1w, 1M] |
| **startTime** | **Long**| Timestamp in milliseconds | [optional] |
| **endTime** | **Long**| Timestamp in milliseconds | [optional] |
| **limit** | **Integer**| Number of candles to return | [optional] [default to 500] |

### Return type

[**List&lt;MarketDataCandlesGet200ResponseInner&gt;**](MarketDataCandlesGet200ResponseInner.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="marketDataCandlesticksGet"></a>
# **marketDataCandlesticksGet**
> List&lt;Object&gt; marketDataCandlesticksGet(pair, from, to, resolution, pcode)

Get Futures Candlesticks

Returns candlestick data for futures instruments

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    String pair = "BTCUSDT"; // String | Trading pair
    Long from = 56L; // Long | Start timestamp
    Long to = 56L; // Long | End timestamp
    String resolution = "resolution_example"; // String | Candle resolution
    String pcode = "f"; // String | Product code (f for futures, s for spot)
    try {
      List<Object> result = apiInstance.marketDataCandlesticksGet(pair, from, to, resolution, pcode);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#marketDataCandlesticksGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pair** | **String**| Trading pair | |
| **from** | **Long**| Start timestamp | |
| **to** | **Long**| End timestamp | |
| **resolution** | **String**| Candle resolution | |
| **pcode** | **String**| Product code (f for futures, s for spot) | [enum: f, s] |

### Return type

**List&lt;Object&gt;**

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with candlestick data |  -  |

<a id="marketDataOrderbookGet"></a>
# **marketDataOrderbookGet**
> MarketDataOrderbookGet200Response marketDataOrderbookGet(pair)

Get order book

Returns a sorted list of bids and asks

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    String pair = "B-BTC_USDT"; // String | Market pair from Market Details API
    try {
      MarketDataOrderbookGet200Response result = apiInstance.marketDataOrderbookGet(pair);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#marketDataOrderbookGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pair** | **String**| Market pair from Market Details API | |

### Return type

[**MarketDataOrderbookGet200Response**](MarketDataOrderbookGet200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="marketDataTradeHistoryGet"></a>
# **marketDataTradeHistoryGet**
> List&lt;MarketDataTradeHistoryGet200ResponseInner&gt; marketDataTradeHistoryGet(pair, limit)

Get trade history

Returns a sorted list of most recent 30 trades by default

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    String pair = "B-BTC_USDT"; // String | Market pair from Market Details API
    Integer limit = 30; // Integer | Number of trades to return
    try {
      List<MarketDataTradeHistoryGet200ResponseInner> result = apiInstance.marketDataTradeHistoryGet(pair, limit);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#marketDataTradeHistoryGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pair** | **String**| Market pair from Market Details API | |
| **limit** | **Integer**| Number of trades to return | [optional] [default to 30] |

### Return type

[**List&lt;MarketDataTradeHistoryGet200ResponseInner&gt;**](MarketDataTradeHistoryGet200ResponseInner.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="marketDataV3OrderbookInstrumentFuturesDepthGet"></a>
# **marketDataV3OrderbookInstrumentFuturesDepthGet**
> MarketDataV3OrderbookInstrumentFuturesDepthGet200Response marketDataV3OrderbookInstrumentFuturesDepthGet(instrument, depth)

Get Futures Orderbook

Returns orderbook data for a futures instrument

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.PublicApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: SignatureAuth
    ApiKeyAuth SignatureAuth = (ApiKeyAuth) defaultClient.getAuthentication("SignatureAuth");
    SignatureAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //SignatureAuth.setApiKeyPrefix("Token");

    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    PublicApi apiInstance = new PublicApi(defaultClient);
    String instrument = "BTCUSDT"; // String | Futures instrument name
    String depth = "10"; // String | Orderbook depth
    try {
      MarketDataV3OrderbookInstrumentFuturesDepthGet200Response result = apiInstance.marketDataV3OrderbookInstrumentFuturesDepthGet(instrument, depth);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling PublicApi#marketDataV3OrderbookInstrumentFuturesDepthGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **instrument** | **String**| Futures instrument name | |
| **depth** | **String**| Orderbook depth | [enum: 10, 20, 50] |

### Return type

[**MarketDataV3OrderbookInstrumentFuturesDepthGet200Response**](MarketDataV3OrderbookInstrumentFuturesDepthGet200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

