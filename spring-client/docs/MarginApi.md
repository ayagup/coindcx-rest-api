# MarginApi

All URIs are relative to *https://api.coindcx.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exchangeV1MarginAddMarginPost**](MarginApi.md#exchangeV1MarginAddMarginPost) | **POST** /exchange/v1/margin/add_margin | Add Margin |
| [**exchangeV1MarginCancelPost**](MarginApi.md#exchangeV1MarginCancelPost) | **POST** /exchange/v1/margin/cancel | Cancel Margin Order |
| [**exchangeV1MarginCreatePost**](MarginApi.md#exchangeV1MarginCreatePost) | **POST** /exchange/v1/margin/create | Place Margin Order |
| [**exchangeV1MarginEditPriceOfTargetOrderPost**](MarginApi.md#exchangeV1MarginEditPriceOfTargetOrderPost) | **POST** /exchange/v1/margin/edit_price_of_target_order | Edit Price of Target Order |
| [**exchangeV1MarginEditSlPost**](MarginApi.md#exchangeV1MarginEditSlPost) | **POST** /exchange/v1/margin/edit_sl | Edit Stop Loss |
| [**exchangeV1MarginEditTargetPost**](MarginApi.md#exchangeV1MarginEditTargetPost) | **POST** /exchange/v1/margin/edit_target | Edit Target Price |
| [**exchangeV1MarginEditTrailingSlPost**](MarginApi.md#exchangeV1MarginEditTrailingSlPost) | **POST** /exchange/v1/margin/edit_trailing_sl | Edit Trailing Stop Loss |
| [**exchangeV1MarginExitPost**](MarginApi.md#exchangeV1MarginExitPost) | **POST** /exchange/v1/margin/exit | Exit Margin Order |
| [**exchangeV1MarginFetchOrdersPost**](MarginApi.md#exchangeV1MarginFetchOrdersPost) | **POST** /exchange/v1/margin/fetch_orders | Fetch Margin Orders |
| [**exchangeV1MarginOrderPost**](MarginApi.md#exchangeV1MarginOrderPost) | **POST** /exchange/v1/margin/order | Query Margin Order |
| [**exchangeV1MarginRemoveMarginPost**](MarginApi.md#exchangeV1MarginRemoveMarginPost) | **POST** /exchange/v1/margin/remove_margin | Remove Margin |


<a id="exchangeV1MarginAddMarginPost"></a>
# **exchangeV1MarginAddMarginPost**
> ExchangeV1MarginAddMarginPost200Response exchangeV1MarginAddMarginPost(exchangeV1MarginAddMarginPostRequest)

Add Margin

Add a particular amount to your margin order, decreasing the effective leverage

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginAddMarginPostRequest exchangeV1MarginAddMarginPostRequest = new ExchangeV1MarginAddMarginPostRequest(); // ExchangeV1MarginAddMarginPostRequest | 
    try {
      ExchangeV1MarginAddMarginPost200Response result = apiInstance.exchangeV1MarginAddMarginPost(exchangeV1MarginAddMarginPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginAddMarginPost");
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
| **exchangeV1MarginAddMarginPostRequest** | [**ExchangeV1MarginAddMarginPostRequest**](ExchangeV1MarginAddMarginPostRequest.md)|  | |

### Return type

[**ExchangeV1MarginAddMarginPost200Response**](ExchangeV1MarginAddMarginPost200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Margin added successfully |  -  |

<a id="exchangeV1MarginCancelPost"></a>
# **exchangeV1MarginCancelPost**
> exchangeV1MarginCancelPost(exchangeV1MarginCancelPostRequest)

Cancel Margin Order

Cancel an existing margin order by its ID

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginCancelPostRequest exchangeV1MarginCancelPostRequest = new ExchangeV1MarginCancelPostRequest(); // ExchangeV1MarginCancelPostRequest | 
    try {
      apiInstance.exchangeV1MarginCancelPost(exchangeV1MarginCancelPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginCancelPost");
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
| **exchangeV1MarginCancelPostRequest** | [**ExchangeV1MarginCancelPostRequest**](ExchangeV1MarginCancelPostRequest.md)|  | |

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
| **200** | Order cancelled successfully |  -  |

<a id="exchangeV1MarginCreatePost"></a>
# **exchangeV1MarginCreatePost**
> List&lt;ExchangeV1MarginCreatePost200ResponseInner&gt; exchangeV1MarginCreatePost(exchangeV1MarginCreatePostRequest)

Place Margin Order

Create a new margin order with leverage for long or short positions.  **Important Notes:** - Maximum of 10 open orders at a time for one specific market - Set ecode parameter to &#39;B&#39; for all margin API calls - Leverage allows borrowing capital to increase potential returns  **Order Types:** market_order, limit_order, stop_limit, take_profit  **Order Status:** init, partial_entry, open, partial_close, close, cancelled, rejected, triggered 

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginCreatePostRequest exchangeV1MarginCreatePostRequest = new ExchangeV1MarginCreatePostRequest(); // ExchangeV1MarginCreatePostRequest | 
    try {
      List<ExchangeV1MarginCreatePost200ResponseInner> result = apiInstance.exchangeV1MarginCreatePost(exchangeV1MarginCreatePostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginCreatePost");
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
| **exchangeV1MarginCreatePostRequest** | [**ExchangeV1MarginCreatePostRequest**](ExchangeV1MarginCreatePostRequest.md)|  | |

### Return type

[**List&lt;ExchangeV1MarginCreatePost200ResponseInner&gt;**](ExchangeV1MarginCreatePost200ResponseInner.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful order placement |  -  |

<a id="exchangeV1MarginEditPriceOfTargetOrderPost"></a>
# **exchangeV1MarginEditPriceOfTargetOrderPost**
> exchangeV1MarginEditPriceOfTargetOrderPost(exchangeV1MarginEditPriceOfTargetOrderPostRequest)

Edit Price of Target Order

Edit the price of the target order in a margin position

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginEditPriceOfTargetOrderPostRequest exchangeV1MarginEditPriceOfTargetOrderPostRequest = new ExchangeV1MarginEditPriceOfTargetOrderPostRequest(); // ExchangeV1MarginEditPriceOfTargetOrderPostRequest | 
    try {
      apiInstance.exchangeV1MarginEditPriceOfTargetOrderPost(exchangeV1MarginEditPriceOfTargetOrderPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginEditPriceOfTargetOrderPost");
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
| **exchangeV1MarginEditPriceOfTargetOrderPostRequest** | [**ExchangeV1MarginEditPriceOfTargetOrderPostRequest**](ExchangeV1MarginEditPriceOfTargetOrderPostRequest.md)|  | |

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
| **200** | Target order price updated successfully |  -  |

<a id="exchangeV1MarginEditSlPost"></a>
# **exchangeV1MarginEditSlPost**
> exchangeV1MarginEditSlPost(exchangeV1MarginEditSlPostRequest)

Edit Stop Loss

Edit the stop loss price of an existing margin order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginEditSlPostRequest exchangeV1MarginEditSlPostRequest = new ExchangeV1MarginEditSlPostRequest(); // ExchangeV1MarginEditSlPostRequest | 
    try {
      apiInstance.exchangeV1MarginEditSlPost(exchangeV1MarginEditSlPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginEditSlPost");
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
| **exchangeV1MarginEditSlPostRequest** | [**ExchangeV1MarginEditSlPostRequest**](ExchangeV1MarginEditSlPostRequest.md)|  | |

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
| **200** | Stop loss updated successfully |  -  |

<a id="exchangeV1MarginEditTargetPost"></a>
# **exchangeV1MarginEditTargetPost**
> exchangeV1MarginEditTargetPost(exchangeV1MarginEditTargetPostRequest)

Edit Target Price

Edit the target price of an existing margin order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginEditTargetPostRequest exchangeV1MarginEditTargetPostRequest = new ExchangeV1MarginEditTargetPostRequest(); // ExchangeV1MarginEditTargetPostRequest | 
    try {
      apiInstance.exchangeV1MarginEditTargetPost(exchangeV1MarginEditTargetPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginEditTargetPost");
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
| **exchangeV1MarginEditTargetPostRequest** | [**ExchangeV1MarginEditTargetPostRequest**](ExchangeV1MarginEditTargetPostRequest.md)|  | |

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
| **200** | Target price updated successfully |  -  |

<a id="exchangeV1MarginEditTrailingSlPost"></a>
# **exchangeV1MarginEditTrailingSlPost**
> exchangeV1MarginEditTrailingSlPost(exchangeV1MarginEditTrailingSlPostRequest)

Edit Trailing Stop Loss

Edit the trailing stop loss configuration of an existing margin order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginEditTrailingSlPostRequest exchangeV1MarginEditTrailingSlPostRequest = new ExchangeV1MarginEditTrailingSlPostRequest(); // ExchangeV1MarginEditTrailingSlPostRequest | 
    try {
      apiInstance.exchangeV1MarginEditTrailingSlPost(exchangeV1MarginEditTrailingSlPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginEditTrailingSlPost");
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
| **exchangeV1MarginEditTrailingSlPostRequest** | [**ExchangeV1MarginEditTrailingSlPostRequest**](ExchangeV1MarginEditTrailingSlPostRequest.md)|  | |

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
| **200** | Trailing stop loss updated successfully |  -  |

<a id="exchangeV1MarginExitPost"></a>
# **exchangeV1MarginExitPost**
> exchangeV1MarginExitPost(exchangeV1MarginExitPostRequest)

Exit Margin Order

Exit an existing margin order position

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginExitPostRequest exchangeV1MarginExitPostRequest = new ExchangeV1MarginExitPostRequest(); // ExchangeV1MarginExitPostRequest | 
    try {
      apiInstance.exchangeV1MarginExitPost(exchangeV1MarginExitPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginExitPost");
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
| **exchangeV1MarginExitPostRequest** | [**ExchangeV1MarginExitPostRequest**](ExchangeV1MarginExitPostRequest.md)|  | |

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
| **200** | Order exited successfully |  -  |

<a id="exchangeV1MarginFetchOrdersPost"></a>
# **exchangeV1MarginFetchOrdersPost**
> List&lt;ExchangeV1MarginFetchOrdersPost200ResponseInner&gt; exchangeV1MarginFetchOrdersPost(exchangeV1MarginFetchOrdersPostRequest)

Fetch Margin Orders

Fetch margin orders and optionally their details which include all buy/sell related orders.  **Note:** This API supports pagination. See Pagination section for more details. 

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginFetchOrdersPostRequest exchangeV1MarginFetchOrdersPostRequest = new ExchangeV1MarginFetchOrdersPostRequest(); // ExchangeV1MarginFetchOrdersPostRequest | 
    try {
      List<ExchangeV1MarginFetchOrdersPost200ResponseInner> result = apiInstance.exchangeV1MarginFetchOrdersPost(exchangeV1MarginFetchOrdersPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginFetchOrdersPost");
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
| **exchangeV1MarginFetchOrdersPostRequest** | [**ExchangeV1MarginFetchOrdersPostRequest**](ExchangeV1MarginFetchOrdersPostRequest.md)|  | |

### Return type

[**List&lt;ExchangeV1MarginFetchOrdersPost200ResponseInner&gt;**](ExchangeV1MarginFetchOrdersPost200ResponseInner.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with margin orders |  -  |

<a id="exchangeV1MarginOrderPost"></a>
# **exchangeV1MarginOrderPost**
> ExchangeV1MarginFetchOrdersPost200ResponseInner exchangeV1MarginOrderPost(exchangeV1MarginOrderPostRequest)

Query Margin Order

Query a specific margin order and optionally its details

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginOrderPostRequest exchangeV1MarginOrderPostRequest = new ExchangeV1MarginOrderPostRequest(); // ExchangeV1MarginOrderPostRequest | 
    try {
      ExchangeV1MarginFetchOrdersPost200ResponseInner result = apiInstance.exchangeV1MarginOrderPost(exchangeV1MarginOrderPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginOrderPost");
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
| **exchangeV1MarginOrderPostRequest** | [**ExchangeV1MarginOrderPostRequest**](ExchangeV1MarginOrderPostRequest.md)|  | |

### Return type

[**ExchangeV1MarginFetchOrdersPost200ResponseInner**](ExchangeV1MarginFetchOrdersPost200ResponseInner.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response with order details |  -  |

<a id="exchangeV1MarginRemoveMarginPost"></a>
# **exchangeV1MarginRemoveMarginPost**
> ExchangeV1MarginRemoveMarginPost200Response exchangeV1MarginRemoveMarginPost(exchangeV1MarginRemoveMarginPostRequest)

Remove Margin

Remove a particular amount from your margin order, increasing the effective leverage

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.MarginApi;

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

    MarginApi apiInstance = new MarginApi(defaultClient);
    ExchangeV1MarginRemoveMarginPostRequest exchangeV1MarginRemoveMarginPostRequest = new ExchangeV1MarginRemoveMarginPostRequest(); // ExchangeV1MarginRemoveMarginPostRequest | 
    try {
      ExchangeV1MarginRemoveMarginPost200Response result = apiInstance.exchangeV1MarginRemoveMarginPost(exchangeV1MarginRemoveMarginPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MarginApi#exchangeV1MarginRemoveMarginPost");
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
| **exchangeV1MarginRemoveMarginPostRequest** | [**ExchangeV1MarginRemoveMarginPostRequest**](ExchangeV1MarginRemoveMarginPostRequest.md)|  | |

### Return type

[**ExchangeV1MarginRemoveMarginPost200Response**](ExchangeV1MarginRemoveMarginPost200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Margin removed successfully |  -  |

