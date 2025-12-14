

# ExchangeV1MarginCreatePostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**market** | **String** | The trading pair |  |
|**quantity** | **BigDecimal** | Quantity to trade |  |
|**price** | **BigDecimal** | Price per unit (not required for market order, mandatory for rest) |  [optional] |
|**leverage** | **BigDecimal** | Borrowed capital to increase the potential returns |  [optional] |
|**side** | [**SideEnum**](#SideEnum) | Specify buy or sell |  |
|**stopPrice** | **BigDecimal** | Price to stop the order at (mandatory in case of stop_limit &amp; take_profit) |  [optional] |
|**orderType** | [**OrderTypeEnum**](#OrderTypeEnum) | Order Type |  |
|**trailingSl** | **Boolean** | To place order with Trailing Stop Loss |  [optional] |
|**targetPrice** | **BigDecimal** | The price to buy/sell or close the order position |  [optional] |
|**slPrice** | **BigDecimal** | Stop loss price |  [optional] |
|**ecode** | **String** | Exchange code in which the order will be placed (always &#39;B&#39;) |  |
|**timestamp** | **Integer** | EPOCH timestamp in milliseconds |  |



## Enum: SideEnum

| Name | Value |
|---- | -----|
| BUY | &quot;buy&quot; |
| SELL | &quot;sell&quot; |



## Enum: OrderTypeEnum

| Name | Value |
|---- | -----|
| MARKET_ORDER | &quot;market_order&quot; |
| LIMIT_ORDER | &quot;limit_order&quot; |
| STOP_LIMIT | &quot;stop_limit&quot; |
| TAKE_PROFIT | &quot;take_profit&quot; |



