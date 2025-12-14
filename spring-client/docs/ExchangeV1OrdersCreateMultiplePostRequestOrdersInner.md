

# ExchangeV1OrdersCreateMultiplePostRequestOrdersInner


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**market** | **String** |  |  |
|**totalQuantity** | **BigDecimal** |  |  |
|**pricePerUnit** | **BigDecimal** |  |  [optional] |
|**side** | [**SideEnum**](#SideEnum) |  |  |
|**orderType** | [**OrderTypeEnum**](#OrderTypeEnum) |  |  |
|**ecode** | **String** | Exchange code |  |
|**clientOrderId** | **String** |  |  [optional] |
|**timestamp** | **Long** |  |  |



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



