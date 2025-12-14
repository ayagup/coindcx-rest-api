# WalletApi

All URIs are relative to *https://api.coindcx.com*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**exchangeV1WalletsSubAccountTransferPost**](WalletApi.md#exchangeV1WalletsSubAccountTransferPost) | **POST** /exchange/v1/wallets/sub_account_transfer | Sub Account Transfer |
| [**exchangeV1WalletsTransferPost**](WalletApi.md#exchangeV1WalletsTransferPost) | **POST** /exchange/v1/wallets/transfer | Wallet Transfer |


<a id="exchangeV1WalletsSubAccountTransferPost"></a>
# **exchangeV1WalletsSubAccountTransferPost**
> ExchangeV1WalletsSubAccountTransferPost200Response exchangeV1WalletsSubAccountTransferPost(exchangeV1WalletsSubAccountTransferPostRequest)

Sub Account Transfer

Transfer funds between master account and its corresponding sub-accounts.  Supported transfers: - Main account spot wallet to sub-account spot wallet - Sub-account spot wallet to main account spot wallet - One sub-account spot wallet to another sub-account spot wallet  **Security Notice:** This endpoint is only available to users who created an API key after August 12, 2024. 

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.WalletApi;

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

    WalletApi apiInstance = new WalletApi(defaultClient);
    ExchangeV1WalletsSubAccountTransferPostRequest exchangeV1WalletsSubAccountTransferPostRequest = new ExchangeV1WalletsSubAccountTransferPostRequest(); // ExchangeV1WalletsSubAccountTransferPostRequest | 
    try {
      ExchangeV1WalletsSubAccountTransferPost200Response result = apiInstance.exchangeV1WalletsSubAccountTransferPost(exchangeV1WalletsSubAccountTransferPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling WalletApi#exchangeV1WalletsSubAccountTransferPost");
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
| **exchangeV1WalletsSubAccountTransferPostRequest** | [**ExchangeV1WalletsSubAccountTransferPostRequest**](ExchangeV1WalletsSubAccountTransferPostRequest.md)|  | |

### Return type

[**ExchangeV1WalletsSubAccountTransferPost200Response**](ExchangeV1WalletsSubAccountTransferPost200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful transfer |  -  |
| **400** | Bad request |  -  |
| **401** | Unauthorized access - Transfer initiated by sub account user |  -  |
| **404** | Wallet not found |  -  |
| **422** | Unprocessable entity |  -  |

<a id="exchangeV1WalletsTransferPost"></a>
# **exchangeV1WalletsTransferPost**
> ExchangeV1WalletsSubAccountTransferPost200Response exchangeV1WalletsTransferPost(exchangeV1WalletsTransferPostRequest)

Wallet Transfer

Transfer funds between spot and futures wallets within the master account.  Supported transfers: - Transfer from spot to futures wallet - Transfer from futures to spot wallet 

### Example
```java
// Import classes:
import com.mycompany.client.ApiClient;
import com.mycompany.client.ApiException;
import com.mycompany.client.Configuration;
import com.mycompany.client.auth.*;
import com.mycompany.client.models.*;
import com.mycompany.api.WalletApi;

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

    WalletApi apiInstance = new WalletApi(defaultClient);
    ExchangeV1WalletsTransferPostRequest exchangeV1WalletsTransferPostRequest = new ExchangeV1WalletsTransferPostRequest(); // ExchangeV1WalletsTransferPostRequest | 
    try {
      ExchangeV1WalletsSubAccountTransferPost200Response result = apiInstance.exchangeV1WalletsTransferPost(exchangeV1WalletsTransferPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling WalletApi#exchangeV1WalletsTransferPost");
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
| **exchangeV1WalletsTransferPostRequest** | [**ExchangeV1WalletsTransferPostRequest**](ExchangeV1WalletsTransferPostRequest.md)|  | |

### Return type

[**ExchangeV1WalletsSubAccountTransferPost200Response**](ExchangeV1WalletsSubAccountTransferPost200Response.md)

### Authorization

[SignatureAuth](../my-client-code/README.md#SignatureAuth), [ApiKeyAuth](../my-client-code/README.md#ApiKeyAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Successful transfer |  -  |
| **400** | Insufficient funds |  -  |
| **404** | Wallet not found - Futures wallet not created for user |  -  |
| **422** | Unprocessable entity |  -  |

