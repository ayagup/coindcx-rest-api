# OrderApi

All URIs are relative to *https://api.coindcx.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exchangeV1OrdersActiveOrdersCountPost**](OrderApi.md#exchangeV1OrdersActiveOrdersCountPost) | **POST** /exchange/v1/orders/active_orders_count | Get active orders count |
| [**exchangeV1OrdersActiveOrdersPost**](OrderApi.md#exchangeV1OrdersActiveOrdersPost) | **POST** /exchange/v1/orders/active_orders | Get active orders |
| [**exchangeV1OrdersCancelAllPost**](OrderApi.md#exchangeV1OrdersCancelAllPost) | **POST** /exchange/v1/orders/cancel_all | Cancel all orders |
| [**exchangeV1OrdersCancelByIdsPost**](OrderApi.md#exchangeV1OrdersCancelByIdsPost) | **POST** /exchange/v1/orders/cancel_by_ids | Cancel multiple orders by IDs |
| [**exchangeV1OrdersCancelPost**](OrderApi.md#exchangeV1OrdersCancelPost) | **POST** /exchange/v1/orders/cancel | Cancel order |
| [**exchangeV1OrdersCreateMultiplePost**](OrderApi.md#exchangeV1OrdersCreateMultiplePost) | **POST** /exchange/v1/orders/create_multiple | Create multiple orders |
| [**exchangeV1OrdersCreatePost**](OrderApi.md#exchangeV1OrdersCreatePost) | **POST** /exchange/v1/orders/create | Create new order |
| [**exchangeV1OrdersEditPost**](OrderApi.md#exchangeV1OrdersEditPost) | **POST** /exchange/v1/orders/edit | Edit order price |
| [**exchangeV1OrdersStatusMultiplePost**](OrderApi.md#exchangeV1OrdersStatusMultiplePost) | **POST** /exchange/v1/orders/status_multiple | Get multiple order status |
| [**exchangeV1OrdersStatusPost**](OrderApi.md#exchangeV1OrdersStatusPost) | **POST** /exchange/v1/orders/status | Get order status |
| [**exchangeV1OrdersTradeHistoryPost**](OrderApi.md#exchangeV1OrdersTradeHistoryPost) | **POST** /exchange/v1/orders/trade_history | Get account trade history |


<a id="exchangeV1OrdersActiveOrdersCountPost"></a>
# **exchangeV1OrdersActiveOrdersCountPost**
> ExchangeV1OrdersActiveOrdersCountPost200Response exchangeV1OrdersActiveOrdersCountPost(exchangeV1OrdersActiveOrdersPostRequest)

Get active orders count

Fetch count of active orders

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersActiveOrdersPostRequest exchangeV1OrdersActiveOrdersPostRequest = new ExchangeV1OrdersActiveOrdersPostRequest(); // ExchangeV1OrdersActiveOrdersPostRequest | 
    try {
      ExchangeV1OrdersActiveOrdersCountPost200Response result = apiInstance.exchangeV1OrdersActiveOrdersCountPost(exchangeV1OrdersActiveOrdersPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersActiveOrdersCountPost");
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
| **exchangeV1OrdersActiveOrdersPostRequest** | [**ExchangeV1OrdersActiveOrdersPostRequest**](ExchangeV1OrdersActiveOrdersPostRequest.md)|  | |

### Return type

[**ExchangeV1OrdersActiveOrdersCountPost200Response**](ExchangeV1OrdersActiveOrdersCountPost200Response.md)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1OrdersActiveOrdersPost"></a>
# **exchangeV1OrdersActiveOrdersPost**
> List&lt;Object&gt; exchangeV1OrdersActiveOrdersPost(exchangeV1OrdersActiveOrdersPostRequest)

Get active orders

Fetch active orders

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersActiveOrdersPostRequest exchangeV1OrdersActiveOrdersPostRequest = new ExchangeV1OrdersActiveOrdersPostRequest(); // ExchangeV1OrdersActiveOrdersPostRequest | 
    try {
      List<Object> result = apiInstance.exchangeV1OrdersActiveOrdersPost(exchangeV1OrdersActiveOrdersPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersActiveOrdersPost");
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
| **exchangeV1OrdersActiveOrdersPostRequest** | [**ExchangeV1OrdersActiveOrdersPostRequest**](ExchangeV1OrdersActiveOrdersPostRequest.md)|  | |

### Return type

**List&lt;Object&gt;**

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1OrdersCancelAllPost"></a>
# **exchangeV1OrdersCancelAllPost**
> exchangeV1OrdersCancelAllPost(exchangeV1OrdersCancelAllPostRequest)

Cancel all orders

Cancel multiple active orders in a single API call

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersCancelAllPostRequest exchangeV1OrdersCancelAllPostRequest = new ExchangeV1OrdersCancelAllPostRequest(); // ExchangeV1OrdersCancelAllPostRequest | 
    try {
      apiInstance.exchangeV1OrdersCancelAllPost(exchangeV1OrdersCancelAllPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersCancelAllPost");
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
| **exchangeV1OrdersCancelAllPostRequest** | [**ExchangeV1OrdersCancelAllPostRequest**](ExchangeV1OrdersCancelAllPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Orders cancelled successfully |  -  |

<a id="exchangeV1OrdersCancelByIdsPost"></a>
# **exchangeV1OrdersCancelByIdsPost**
> exchangeV1OrdersCancelByIdsPost(exchangeV1OrdersCancelByIdsPostRequest)

Cancel multiple orders by IDs

Cancel multiple active orders by their IDs

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersCancelByIdsPostRequest exchangeV1OrdersCancelByIdsPostRequest = new ExchangeV1OrdersCancelByIdsPostRequest(); // ExchangeV1OrdersCancelByIdsPostRequest | 
    try {
      apiInstance.exchangeV1OrdersCancelByIdsPost(exchangeV1OrdersCancelByIdsPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersCancelByIdsPost");
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
| **exchangeV1OrdersCancelByIdsPostRequest** | [**ExchangeV1OrdersCancelByIdsPostRequest**](ExchangeV1OrdersCancelByIdsPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Orders cancelled successfully |  -  |

<a id="exchangeV1OrdersCancelPost"></a>
# **exchangeV1OrdersCancelPost**
> exchangeV1OrdersCancelPost(exchangeV1OrdersCancelPostRequest)

Cancel order

Cancel an active order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersCancelPostRequest exchangeV1OrdersCancelPostRequest = new ExchangeV1OrdersCancelPostRequest(); // ExchangeV1OrdersCancelPostRequest | 
    try {
      apiInstance.exchangeV1OrdersCancelPost(exchangeV1OrdersCancelPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersCancelPost");
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
| **exchangeV1OrdersCancelPostRequest** | [**ExchangeV1OrdersCancelPostRequest**](ExchangeV1OrdersCancelPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Order cancelled successfully |  -  |

<a id="exchangeV1OrdersCreateMultiplePost"></a>
# **exchangeV1OrdersCreateMultiplePost**
> ExchangeV1OrdersCreateMultiplePost200Response exchangeV1OrdersCreateMultiplePost(exchangeV1OrdersCreateMultiplePostRequest)

Create multiple orders

Place multiple orders on the exchange (only supported for CoinDCX INR markets, ecode&#x3D;I)

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersCreateMultiplePostRequest exchangeV1OrdersCreateMultiplePostRequest = new ExchangeV1OrdersCreateMultiplePostRequest(); // ExchangeV1OrdersCreateMultiplePostRequest | 
    try {
      ExchangeV1OrdersCreateMultiplePost200Response result = apiInstance.exchangeV1OrdersCreateMultiplePost(exchangeV1OrdersCreateMultiplePostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersCreateMultiplePost");
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
| **exchangeV1OrdersCreateMultiplePostRequest** | [**ExchangeV1OrdersCreateMultiplePostRequest**](ExchangeV1OrdersCreateMultiplePostRequest.md)|  | |

### Return type

[**ExchangeV1OrdersCreateMultiplePost200Response**](ExchangeV1OrdersCreateMultiplePost200Response.md)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Orders created successfully |  -  |

<a id="exchangeV1OrdersCreatePost"></a>
# **exchangeV1OrdersCreatePost**
> ExchangeV1OrdersCreatePost200Response exchangeV1OrdersCreatePost(exchangeV1OrdersCreatePostRequest)

Create new order

Place a new order on the exchange. Maximum of 25 open orders at a time for one specific market.

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersCreatePostRequest exchangeV1OrdersCreatePostRequest = new ExchangeV1OrdersCreatePostRequest(); // ExchangeV1OrdersCreatePostRequest | 
    try {
      ExchangeV1OrdersCreatePost200Response result = apiInstance.exchangeV1OrdersCreatePost(exchangeV1OrdersCreatePostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersCreatePost");
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
| **exchangeV1OrdersCreatePostRequest** | [**ExchangeV1OrdersCreatePostRequest**](ExchangeV1OrdersCreatePostRequest.md)|  | |

### Return type

[**ExchangeV1OrdersCreatePost200Response**](ExchangeV1OrdersCreatePost200Response.md)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Order created successfully |  -  |

<a id="exchangeV1OrdersEditPost"></a>
# **exchangeV1OrdersEditPost**
> Object exchangeV1OrdersEditPost(exchangeV1OrdersEditPostRequest)

Edit order price

Edit the price of an active order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersEditPostRequest exchangeV1OrdersEditPostRequest = new ExchangeV1OrdersEditPostRequest(); // ExchangeV1OrdersEditPostRequest | 
    try {
      Object result = apiInstance.exchangeV1OrdersEditPost(exchangeV1OrdersEditPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersEditPost");
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
| **exchangeV1OrdersEditPostRequest** | [**ExchangeV1OrdersEditPostRequest**](ExchangeV1OrdersEditPostRequest.md)|  | |

### Return type

**Object**

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Order edited successfully |  -  |

<a id="exchangeV1OrdersStatusMultiplePost"></a>
# **exchangeV1OrdersStatusMultiplePost**
> List&lt;Object&gt; exchangeV1OrdersStatusMultiplePost(exchangeV1OrdersStatusMultiplePostRequest)

Get multiple order status

Fetch status of multiple orders

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersStatusMultiplePostRequest exchangeV1OrdersStatusMultiplePostRequest = new ExchangeV1OrdersStatusMultiplePostRequest(); // ExchangeV1OrdersStatusMultiplePostRequest | 
    try {
      List<Object> result = apiInstance.exchangeV1OrdersStatusMultiplePost(exchangeV1OrdersStatusMultiplePostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersStatusMultiplePost");
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
| **exchangeV1OrdersStatusMultiplePostRequest** | [**ExchangeV1OrdersStatusMultiplePostRequest**](ExchangeV1OrdersStatusMultiplePostRequest.md)|  | |

### Return type

**List&lt;Object&gt;**

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1OrdersStatusPost"></a>
# **exchangeV1OrdersStatusPost**
> ExchangeV1OrdersCreatePost200ResponseOrdersInner exchangeV1OrdersStatusPost(exchangeV1OrdersStatusPostRequest)

Get order status

Fetch status of any order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersStatusPostRequest exchangeV1OrdersStatusPostRequest = new ExchangeV1OrdersStatusPostRequest(); // ExchangeV1OrdersStatusPostRequest | 
    try {
      ExchangeV1OrdersCreatePost200ResponseOrdersInner result = apiInstance.exchangeV1OrdersStatusPost(exchangeV1OrdersStatusPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersStatusPost");
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
| **exchangeV1OrdersStatusPostRequest** | [**ExchangeV1OrdersStatusPostRequest**](ExchangeV1OrdersStatusPostRequest.md)|  | |

### Return type

[**ExchangeV1OrdersCreatePost200ResponseOrdersInner**](ExchangeV1OrdersCreatePost200ResponseOrdersInner.md)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1OrdersTradeHistoryPost"></a>
# **exchangeV1OrdersTradeHistoryPost**
> List&lt;Object&gt; exchangeV1OrdersTradeHistoryPost(exchangeV1OrdersTradeHistoryPostRequest)

Get account trade history

Fetch trades associated with your account

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.OrderApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    OrderApi apiInstance = new OrderApi(defaultClient);
    ExchangeV1OrdersTradeHistoryPostRequest exchangeV1OrdersTradeHistoryPostRequest = new ExchangeV1OrdersTradeHistoryPostRequest(); // ExchangeV1OrdersTradeHistoryPostRequest | 
    try {
      List<Object> result = apiInstance.exchangeV1OrdersTradeHistoryPost(exchangeV1OrdersTradeHistoryPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OrderApi#exchangeV1OrdersTradeHistoryPost");
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
| **exchangeV1OrdersTradeHistoryPostRequest** | [**ExchangeV1OrdersTradeHistoryPostRequest**](ExchangeV1OrdersTradeHistoryPostRequest.md)|  | |

### Return type

**List&lt;Object&gt;**

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

