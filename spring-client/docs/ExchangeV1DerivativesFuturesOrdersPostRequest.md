

# ExchangeV1DerivativesFuturesOrdersPostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**timestamp** | **Long** | EPOCH timestamp in milliseconds |  |
|**page** | **String** | Page number |  |
|**size** | **String** | Number of records per page |  |
|**pairs** | **String** | Comma-separated list of pairs (optional) |  [optional] |
|**orderIds** | **String** | Comma-separated list of order IDs (optional) |  [optional] |
|**marginCurrencyShortName** | [**List&lt;MarginCurrencyShortNameEnum&gt;**](#List&lt;MarginCurrencyShortNameEnum&gt;) | Futures margin mode |  [optional] |



## Enum: List&lt;MarginCurrencyShortNameEnum&gt;

| Name | Value |
|---- | -----|
| INR | &quot;INR&quot; |
| USDT | &quot;USDT&quot; |



