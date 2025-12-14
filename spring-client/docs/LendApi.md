# LendApi

All URIs are relative to *https://api.coindcx.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exchangeV1FundingFetchOrdersPost**](LendApi.md#exchangeV1FundingFetchOrdersPost) | **POST** /exchange/v1/funding/fetch_orders | Fetch lend orders |
| [**exchangeV1FundingLendPost**](LendApi.md#exchangeV1FundingLendPost) | **POST** /exchange/v1/funding/lend | Create lend order |


<a id="exchangeV1FundingFetchOrdersPost"></a>
# **exchangeV1FundingFetchOrdersPost**
> List&lt;ExchangeV1FundingFetchOrdersPost200ResponseInner&gt; exchangeV1FundingFetchOrdersPost(exchangeV1FundingFetchOrdersPostRequest)

Fetch lend orders

Fetch lending orders and their details (supports pagination)

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.LendApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    LendApi apiInstance = new LendApi(defaultClient);
    ExchangeV1FundingFetchOrdersPostRequest exchangeV1FundingFetchOrdersPostRequest = new ExchangeV1FundingFetchOrdersPostRequest(); // ExchangeV1FundingFetchOrdersPostRequest | 
    try {
      List<ExchangeV1FundingFetchOrdersPost200ResponseInner> result = apiInstance.exchangeV1FundingFetchOrdersPost(exchangeV1FundingFetchOrdersPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling LendApi#exchangeV1FundingFetchOrdersPost");
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
| **exchangeV1FundingFetchOrdersPostRequest** | [**ExchangeV1FundingFetchOrdersPostRequest**](ExchangeV1FundingFetchOrdersPostRequest.md)|  | |

### Return type

[**List&lt;ExchangeV1FundingFetchOrdersPost200ResponseInner&gt;**](ExchangeV1FundingFetchOrdersPost200ResponseInner.md)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1FundingLendPost"></a>
# **exchangeV1FundingLendPost**
> exchangeV1FundingLendPost(exchangeV1FundingLendPostRequest)

Create lend order

Create a new lending order

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.LendApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    LendApi apiInstance = new LendApi(defaultClient);
    ExchangeV1FundingLendPostRequest exchangeV1FundingLendPostRequest = new ExchangeV1FundingLendPostRequest(); // ExchangeV1FundingLendPostRequest | 
    try {
      apiInstance.exchangeV1FundingLendPost(exchangeV1FundingLendPostRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling LendApi#exchangeV1FundingLendPost");
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
| **exchangeV1FundingLendPostRequest** | [**ExchangeV1FundingLendPostRequest**](ExchangeV1FundingLendPostRequest.md)|  | |

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
| **200** | Lend order created successfully |  -  |

