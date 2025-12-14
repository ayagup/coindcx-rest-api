# UserApi

All URIs are relative to *https://api.coindcx.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exchangeV1UsersBalancesPost**](UserApi.md#exchangeV1UsersBalancesPost) | **POST** /exchange/v1/users/balances | Get account balances |
| [**exchangeV1UsersInfoPost**](UserApi.md#exchangeV1UsersInfoPost) | **POST** /exchange/v1/users/info | Get user info |


<a id="exchangeV1UsersBalancesPost"></a>
# **exchangeV1UsersBalancesPost**
> List&lt;ExchangeV1UsersBalancesPost200ResponseInner&gt; exchangeV1UsersBalancesPost(exchangeV1UsersBalancesPostRequest)

Get account balances

Retrieves account&#39;s balances

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.UserApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    UserApi apiInstance = new UserApi(defaultClient);
    ExchangeV1UsersBalancesPostRequest exchangeV1UsersBalancesPostRequest = new ExchangeV1UsersBalancesPostRequest(); // ExchangeV1UsersBalancesPostRequest | 
    try {
      List<ExchangeV1UsersBalancesPost200ResponseInner> result = apiInstance.exchangeV1UsersBalancesPost(exchangeV1UsersBalancesPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserApi#exchangeV1UsersBalancesPost");
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
| **exchangeV1UsersBalancesPostRequest** | [**ExchangeV1UsersBalancesPostRequest**](ExchangeV1UsersBalancesPostRequest.md)|  | |

### Return type

[**List&lt;ExchangeV1UsersBalancesPost200ResponseInner&gt;**](ExchangeV1UsersBalancesPost200ResponseInner.md)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

<a id="exchangeV1UsersInfoPost"></a>
# **exchangeV1UsersInfoPost**
> ExchangeV1UsersInfoPost200Response exchangeV1UsersInfoPost(exchangeV1UsersBalancesPostRequest)

Get user info

Retrieves user information

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.UserApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.coindcx.com");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    UserApi apiInstance = new UserApi(defaultClient);
    ExchangeV1UsersBalancesPostRequest exchangeV1UsersBalancesPostRequest = new ExchangeV1UsersBalancesPostRequest(); // ExchangeV1UsersBalancesPostRequest | 
    try {
      ExchangeV1UsersInfoPost200Response result = apiInstance.exchangeV1UsersInfoPost(exchangeV1UsersBalancesPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling UserApi#exchangeV1UsersInfoPost");
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
| **exchangeV1UsersBalancesPostRequest** | [**ExchangeV1UsersBalancesPostRequest**](ExchangeV1UsersBalancesPostRequest.md)|  | |

### Return type

[**ExchangeV1UsersInfoPost200Response**](ExchangeV1UsersInfoPost200Response.md)

### Authorization

[ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful response |  -  |

