

# ExchangeV1DerivativesFuturesTradesPostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**timestamp** | **Long** |  |  |
|**pair** | **String** | Instrument pair (optional) |  [optional] |
|**orderId** | **String** | Order ID (optional) |  [optional] |
|**fromDate** | **LocalDate** | Start date (YYYY-MM-DD) |  [optional] |
|**toDate** | **LocalDate** | End date (YYYY-MM-DD) |  [optional] |
|**page** | **String** |  |  |
|**size** | **String** |  |  |
|**marginCurrencyShortName** | [**List&lt;MarginCurrencyShortNameEnum&gt;**](#List&lt;MarginCurrencyShortNameEnum&gt;) |  |  [optional] |



## Enum: List&lt;MarginCurrencyShortNameEnum&gt;

| Name | Value |
|---- | -----|
| INR | &quot;INR&quot; |
| USDT | &quot;USDT&quot; |



