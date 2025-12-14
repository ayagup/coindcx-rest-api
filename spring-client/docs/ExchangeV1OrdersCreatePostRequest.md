

# ExchangeV1OrdersCreatePostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**market** | **String** | The trading pair |  |
|**totalQuantity** | **BigDecimal** | Quantity to trade |  |
|**pricePerUnit** | **BigDecimal** | Price per unit (not required for market order) |  [optional] |
|**side** | [**SideEnum**](#SideEnum) | Specify buy or sell |  |
|**orderType** | [**OrderTypeEnum**](#OrderTypeEnum) | Order Type |  |
|**clientOrderId** | **String** | Client order id of the order |  [optional] |
|**timestamp** | **Long** | Current timestamp in milliseconds |  |



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



