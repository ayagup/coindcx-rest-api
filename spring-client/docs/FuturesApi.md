# FuturesApi

All URIs are relative to *https://api.coindcx.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exchangeV1DerivativesFuturesDataActiveInstrumentsGet**](FuturesApi.md#exchangeV1DerivativesFuturesDataActiveInstrumentsGet) | **GET** /exchange/v1/derivatives/futures/data/active_instruments | Get Active Futures Instruments |
| [**exchangeV1DerivativesFuturesDataInstrumentGet**](FuturesApi.md#exchangeV1DerivativesFuturesDataInstrumentGet) | **GET** /exchange/v1/derivatives/futures/data/instrument | Get Futures Instrument Details |
| [**exchangeV1DerivativesFuturesDataTradesGet**](FuturesApi.md#exchangeV1DerivativesFuturesDataTradesGet) | **GET** /exchange/v1/derivatives/futures/data/trades | Get Futures Trade History |
| [**exchangeV1DerivativesFuturesOrdersCancelPost**](FuturesApi.md#exchangeV1DerivativesFuturesOrdersCancelPost) | **POST** /exchange/v1/derivatives/futures/orders/cancel | Cancel Futures Order |
| [**exchangeV1DerivativesFuturesOrdersCreatePost**](FuturesApi.md#exchangeV1DerivativesFuturesOrdersCreatePost) | **POST** /exchange/v1/derivatives/futures/orders/create | Create Futures Order |
| [**exchangeV1DerivativesFuturesOrdersPost**](FuturesApi.md#exchangeV1DerivativesFuturesOrdersPost) | **POST** /exchange/v1/derivatives/futures/orders | List Futures Orders |
| [**exchangeV1DerivativesFuturesPositionsAddMarginPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsAddMarginPost) | **POST** /exchange/v1/derivatives/futures/positions/add_margin | Add Margin to Position |
| [**exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost) | **POST** /exchange/v1/derivatives/futures/positions/cancel_all_open_orders_for_position | Cancel All Open Orders for Position |
| [**exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost) | **POST** /exchange/v1/derivatives/futures/positions/cancel_all_open_orders | Cancel All Open Orders |
| [**exchangeV1DerivativesFuturesPositionsCreateTpslPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsCreateTpslPost) | **POST** /exchange/v1/derivatives/futures/positions/create_tpsl | Create Take Profit and Stop Loss Orders |
| [**exchangeV1DerivativesFuturesPositionsExitPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsExitPost) | **POST** /exchange/v1/derivatives/futures/positions/exit | Exit Futures Position |
| [**exchangeV1DerivativesFuturesPositionsPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsPost) | **POST** /exchange/v1/derivatives/futures/positions | List Futures Positions |
| [**exchangeV1DerivativesFuturesPositionsRemoveMarginPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsRemoveMarginPost) | **POST** /exchange/v1/derivatives/futures/positions/remove_margin | Remove Margin from Position |
| [**exchangeV1DerivativesFuturesPositionsTransactionsPost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsTransactionsPost) | **POST** /exchange/v1/derivatives/futures/positions/transactions | Get Position Transactions |
| [**exchangeV1DerivativesFuturesPositionsUpdateLeveragePost**](FuturesApi.md#exchangeV1DerivativesFuturesPositionsUpdateLeveragePost) | **POST** /exchange/v1/derivatives/futures/positions/update_leverage | Update Position Leverage |
| [**exchangeV1DerivativesFuturesTradesPost**](FuturesApi.md#exchangeV1DerivativesFuturesTradesPost) | **POST** /exchange/v1/derivatives/futures/trades | Get Futures Trades |
| [**marketDataCandlesticksGet**](FuturesApi.md#marketDataCandlesticksGet) | **GET** /market_data/candlesticks | Get Futures Candlesticks |
| [**marketDataV3OrderbookInstrumentFuturesDepthGet**](FuturesApi.md#marketDataV3OrderbookInstrumentFuturesDepthGet) | **GET** /market_data/v3/orderbook/{instrument}-futures/{depth} | Get Futures Orderbook |


<a id="exchangeV1DerivativesFuturesDataActiveInstrumentsGet"></a>
# **exchangeV1DerivativesFuturesDataActiveInstrumentsGet**
> List&lt;Object&gt; exchangeV1DerivativesFuturesDataActiveInstrumentsGet(marginCurrencyShortName)

Get Active Futures Instruments

Retrieve a list of active futures instruments for a specified margin currency mode

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    String marginCurrencyShortName = "INR"; // String | Futures margin mode
    try {
      List<Object> result = apiInstance.exchangeV1DerivativesFuturesDataActiveInstrumentsGet(marginCurrencyShortName);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesDataActiveInstrumentsGet");
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
| **marginCurrencyShortName** | **String**| Futures margin mode | [default to USDT] [enum: INR, USDT] |

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
| **200** | Successful response |  -  |

<a id="exchangeV1DerivativesFuturesDataInstrumentGet"></a>
# **exchangeV1DerivativesFuturesDataInstrumentGet**
> Object exchangeV1DerivativesFuturesDataInstrumentGet(pair, marginCurrencyShortName)

Get Futures Instrument Details

Get detailed information about a specific futures instrument

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    String pair = "B-ETH_USDT"; // String | Instrument pair
    String marginCurrencyShortName = "INR"; // String | Futures margin mode
    try {
      Object result = apiInstance.exchangeV1DerivativesFuturesDataInstrumentGet(pair, marginCurrencyShortName);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesDataInstrumentGet");
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
| **pair** | **String**| Instrument pair | |
| **marginCurrencyShortName** | **String**| Futures margin mode | [enum: INR, USDT] |

### Return type

**Object**

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with detailed instrument information |  -  |

<a id="exchangeV1DerivativesFuturesDataTradesGet"></a>
# **exchangeV1DerivativesFuturesDataTradesGet**
> List&lt;Object&gt; exchangeV1DerivativesFuturesDataTradesGet(pair)

Get Futures Trade History

Retrieve trade history for a specific futures pair

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    String pair = "B-ETH_USDT"; // String | Instrument pair
    try {
      List<Object> result = apiInstance.exchangeV1DerivativesFuturesDataTradesGet(pair);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesDataTradesGet");
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
| **pair** | **String**| Instrument pair | |

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
| **200** | Successful response |  -  |

<a id="exchangeV1DerivativesFuturesOrdersCancelPost"></a>
# **exchangeV1DerivativesFuturesOrdersCancelPost**
> ExchangeV1DerivativesFuturesOrdersCancelPost200Response exchangeV1DerivativesFuturesOrdersCancelPost(exchangeV1DerivativesFuturesOrdersCancelPostRequest)

Cancel Futures Order

Cancel an existing futures order by ID

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesOrdersCancelPostRequest exchangeV1DerivativesFuturesOrdersCancelPostRequest = new ExchangeV1DerivativesFuturesOrdersCancelPostRequest(); // ExchangeV1DerivativesFuturesOrdersCancelPostRequest | 
    try {
      ExchangeV1DerivativesFuturesOrdersCancelPost200Response result = apiInstance.exchangeV1DerivativesFuturesOrdersCancelPost(exchangeV1DerivativesFuturesOrdersCancelPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesOrdersCancelPost");
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
| **exchangeV1DerivativesFuturesOrdersCancelPostRequest** | [**ExchangeV1DerivativesFuturesOrdersCancelPostRequest**](ExchangeV1DerivativesFuturesOrdersCancelPostRequest.md)|  | |

### Return type

[**ExchangeV1DerivativesFuturesOrdersCancelPost200Response**](ExchangeV1DerivativesFuturesOrdersCancelPost200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Order cancelled successfully |  -  |

<a id="exchangeV1DerivativesFuturesOrdersCreatePost"></a>
# **exchangeV1DerivativesFuturesOrdersCreatePost**
> exchangeV1DerivativesFuturesOrdersCreatePost(exchangeV1DerivativesFuturesOrdersCreatePostRequest)

Create Futures Order

Place a new futures order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesOrdersCreatePostRequest exchangeV1DerivativesFuturesOrdersCreatePostRequest = new ExchangeV1DerivativesFuturesOrdersCreatePostRequest(); // ExchangeV1DerivativesFuturesOrdersCreatePostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesOrdersCreatePost(exchangeV1DerivativesFuturesOrdersCreatePostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesOrdersCreatePost");
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
| **exchangeV1DerivativesFuturesOrdersCreatePostRequest** | [**ExchangeV1DerivativesFuturesOrdersCreatePostRequest**](ExchangeV1DerivativesFuturesOrdersCreatePostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Order created successfully |  -  |
| **400** | Bad request (insufficient funds, invalid price, etc.) |  -  |
| **422** | Validation error |  -  |

<a id="exchangeV1DerivativesFuturesOrdersPost"></a>
# **exchangeV1DerivativesFuturesOrdersPost**
> exchangeV1DerivativesFuturesOrdersPost(exchangeV1DerivativesFuturesOrdersPostRequest)

List Futures Orders

Retrieve a list of futures orders with pagination

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesOrdersPostRequest exchangeV1DerivativesFuturesOrdersPostRequest = new ExchangeV1DerivativesFuturesOrdersPostRequest(); // ExchangeV1DerivativesFuturesOrdersPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesOrdersPost(exchangeV1DerivativesFuturesOrdersPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesOrdersPost");
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
| **exchangeV1DerivativesFuturesOrdersPostRequest** | [**ExchangeV1DerivativesFuturesOrdersPostRequest**](ExchangeV1DerivativesFuturesOrdersPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with list of orders |  -  |

<a id="exchangeV1DerivativesFuturesPositionsAddMarginPost"></a>
# **exchangeV1DerivativesFuturesPositionsAddMarginPost**
> exchangeV1DerivativesFuturesPositionsAddMarginPost(exchangeV1DerivativesFuturesPositionsAddMarginPostRequest)

Add Margin to Position

Add margin to an existing futures position

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest exchangeV1DerivativesFuturesPositionsAddMarginPostRequest = new ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest(); // ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsAddMarginPost(exchangeV1DerivativesFuturesPositionsAddMarginPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsAddMarginPost");
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
| **exchangeV1DerivativesFuturesPositionsAddMarginPostRequest** | [**ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest**](ExchangeV1DerivativesFuturesPositionsAddMarginPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Margin added successfully |  -  |

<a id="exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost"></a>
# **exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost**
> exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost(exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest)

Cancel All Open Orders for Position

Cancel all open orders for a specific position

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest = new ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest(); // ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost(exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPost");
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
| **exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest** | [**ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest**](ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | All position orders cancelled successfully |  -  |

<a id="exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost"></a>
# **exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost**
> exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost(exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest)

Cancel All Open Orders

Cancel all open futures orders

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest = new ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest(); // ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost(exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPost");
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
| **exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest** | [**ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest**](ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | All orders cancelled successfully |  -  |

<a id="exchangeV1DerivativesFuturesPositionsCreateTpslPost"></a>
# **exchangeV1DerivativesFuturesPositionsCreateTpslPost**
> exchangeV1DerivativesFuturesPositionsCreateTpslPost(exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest)

Create Take Profit and Stop Loss Orders

Create take profit and/or stop loss orders for a position

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest = new ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest(); // ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsCreateTpslPost(exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsCreateTpslPost");
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
| **exchangeV1DerivativesFuturesPositionsCreateTpslPostRequest** | [**ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest**](ExchangeV1DerivativesFuturesPositionsCreateTpslPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | TP/SL orders created successfully |  -  |

<a id="exchangeV1DerivativesFuturesPositionsExitPost"></a>
# **exchangeV1DerivativesFuturesPositionsExitPost**
> ExchangeV1DerivativesFuturesPositionsExitPost200Response exchangeV1DerivativesFuturesPositionsExitPost(exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest)

Exit Futures Position

Exit (close) a futures position

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest = new ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest(); // ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest | 
    try {
      ExchangeV1DerivativesFuturesPositionsExitPost200Response result = apiInstance.exchangeV1DerivativesFuturesPositionsExitPost(exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsExitPost");
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
| **exchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest** | [**ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest**](ExchangeV1DerivativesFuturesPositionsCancelAllOpenOrdersForPositionPostRequest.md)|  | |

### Return type

[**ExchangeV1DerivativesFuturesPositionsExitPost200Response**](ExchangeV1DerivativesFuturesPositionsExitPost200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Position exit initiated successfully |  -  |

<a id="exchangeV1DerivativesFuturesPositionsPost"></a>
# **exchangeV1DerivativesFuturesPositionsPost**
> exchangeV1DerivativesFuturesPositionsPost(exchangeV1DerivativesFuturesPositionsPostRequest)

List Futures Positions

Retrieve futures positions with pagination

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsPostRequest exchangeV1DerivativesFuturesPositionsPostRequest = new ExchangeV1DerivativesFuturesPositionsPostRequest(); // ExchangeV1DerivativesFuturesPositionsPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsPost(exchangeV1DerivativesFuturesPositionsPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsPost");
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
| **exchangeV1DerivativesFuturesPositionsPostRequest** | [**ExchangeV1DerivativesFuturesPositionsPostRequest**](ExchangeV1DerivativesFuturesPositionsPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with positions list |  -  |

<a id="exchangeV1DerivativesFuturesPositionsRemoveMarginPost"></a>
# **exchangeV1DerivativesFuturesPositionsRemoveMarginPost**
> exchangeV1DerivativesFuturesPositionsRemoveMarginPost(exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest)

Remove Margin from Position

Remove margin from an existing futures position

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest = new ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest(); // ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsRemoveMarginPost(exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsRemoveMarginPost");
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
| **exchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest** | [**ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest**](ExchangeV1DerivativesFuturesPositionsRemoveMarginPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Margin removed successfully |  -  |
| **400** | Insufficient funds |  -  |
| **422** | Cannot remove margin (inactive position, liquidation risk, etc.) |  -  |

<a id="exchangeV1DerivativesFuturesPositionsTransactionsPost"></a>
# **exchangeV1DerivativesFuturesPositionsTransactionsPost**
> exchangeV1DerivativesFuturesPositionsTransactionsPost(exchangeV1DerivativesFuturesPositionsTransactionsPostRequest)

Get Position Transactions

Retrieve transaction history for futures positions

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest exchangeV1DerivativesFuturesPositionsTransactionsPostRequest = new ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest(); // ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsTransactionsPost(exchangeV1DerivativesFuturesPositionsTransactionsPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsTransactionsPost");
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
| **exchangeV1DerivativesFuturesPositionsTransactionsPostRequest** | [**ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest**](ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with transaction list |  -  |

<a id="exchangeV1DerivativesFuturesPositionsUpdateLeveragePost"></a>
# **exchangeV1DerivativesFuturesPositionsUpdateLeveragePost**
> exchangeV1DerivativesFuturesPositionsUpdateLeveragePost(exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest)

Update Position Leverage

Update the leverage for a futures position by pair or position ID

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest = new ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest(); // ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesPositionsUpdateLeveragePost(exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesPositionsUpdateLeveragePost");
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
| **exchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest** | [**ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest**](ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Leverage updated successfully |  -  |
| **400** | Validation error (leverage &lt; 1x, insufficient funds, etc.) |  -  |
| **422** | Leverage exceeds maximum or liquidation risk |  -  |

<a id="exchangeV1DerivativesFuturesTradesPost"></a>
# **exchangeV1DerivativesFuturesTradesPost**
> exchangeV1DerivativesFuturesTradesPost(exchangeV1DerivativesFuturesTradesPostRequest)

Get Futures Trades

Retrieve trade information for futures orders

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    ExchangeV1DerivativesFuturesTradesPostRequest exchangeV1DerivativesFuturesTradesPostRequest = new ExchangeV1DerivativesFuturesTradesPostRequest(); // ExchangeV1DerivativesFuturesTradesPostRequest | 
    try {
      apiInstance.exchangeV1DerivativesFuturesTradesPost(exchangeV1DerivativesFuturesTradesPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#exchangeV1DerivativesFuturesTradesPost");
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
| **exchangeV1DerivativesFuturesTradesPostRequest** | [**ExchangeV1DerivativesFuturesTradesPostRequest**](ExchangeV1DerivativesFuturesTradesPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with trades list |  -  |

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
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    String pair = "BTCUSDT"; // String | Trading pair
    Long from = 56L; // Long | Start timestamp
    Long to = 56L; // Long | End timestamp
    String resolution = "resolution_example"; // String | Candle resolution
    String pcode = "f"; // String | Product code (f for futures, s for spot)
    try {
      List<Object> result = apiInstance.marketDataCandlesticksGet(pair, from, to, resolution, pcode);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#marketDataCandlesticksGet");
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
import com.mycompany.api.FuturesApi;

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

    FuturesApi apiInstance = new FuturesApi(defaultClient);
    String instrument = "BTCUSDT"; // String | Futures instrument name
    String depth = "10"; // String | Orderbook depth
    try {
      MarketDataV3OrderbookInstrumentFuturesDepthGet200Response result = apiInstance.marketDataV3OrderbookInstrumentFuturesDepthGet(instrument, depth);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling FuturesApi#marketDataV3OrderbookInstrumentFuturesDepthGet");
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

