

# ExchangeV1DerivativesFuturesPositionsUpdateLeveragePostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**timestamp** | **Long** |  |  |
|**leverage** | **String** | Leverage value |  |
|**pair** | **String** | Instrument pair (use either pair or id) |  [optional] |
|**id** | **String** | Position ID (use either pair or id) |  [optional] |
|**marginCurrencyShortName** | [**MarginCurrencyShortNameEnum**](#MarginCurrencyShortNameEnum) |  |  |



## Enum: MarginCurrencyShortNameEnum

| Name | Value |
|---- | -----|
| INR | &quot;INR&quot; |
| USDT | &quot;USDT&quot; |



