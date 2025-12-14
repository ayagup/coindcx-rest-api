

# ExchangeV1DerivativesFuturesPositionsTransactionsPostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**timestamp** | **Long** |  |  |
|**stage** | [**StageEnum**](#StageEnum) | Transaction stage filter |  |
|**page** | **String** |  |  |
|**size** | **String** |  |  |
|**marginCurrencyShortName** | [**List&lt;MarginCurrencyShortNameEnum&gt;**](#List&lt;MarginCurrencyShortNameEnum&gt;) |  |  [optional] |



## Enum: StageEnum

| Name | Value |
|---- | -----|
| ALL | &quot;all&quot; |
| DEFAULT | &quot;default&quot; |
| FUNDING | &quot;funding&quot; |
| EXIT | &quot;exit&quot; |
| TPSL_EXIT | &quot;tpsl_exit&quot; |
| LIQUIDATION | &quot;liquidation&quot; |



## Enum: List&lt;MarginCurrencyShortNameEnum&gt;

| Name | Value |
|---- | -----|
| INR | &quot;INR&quot; |
| USDT | &quot;USDT&quot; |



