

# ExchangeV1DerivativesFuturesOrdersCreatePostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**timestamp** | **Long** | EPOCH timestamp in milliseconds. Orders with delay &gt; 10 seconds will be rejected |  |
|**side** | [**SideEnum**](#SideEnum) | Order side |  |
|**pair** | **String** | Pair name (format B-ETH_USDT) |  |
|**orderType** | [**OrderTypeEnum**](#OrderTypeEnum) | Order type |  |
|**price** | **BigDecimal** | Order Price (limit price for limit, stop limit, and take profit limit orders). NULL for market orders |  [optional] |
|**stopPrice** | **BigDecimal** | Stop Price (for stop_limit, stop_market, take_profit_limit, take_profit_market orders). Trigger price |  [optional] |
|**totalQuantity** | **BigDecimal** | Order total quantity |  |
|**leverage** | **BigDecimal** | Leverage at which to take position. Should match position leverage |  [optional] |
|**notification** | [**NotificationEnum**](#NotificationEnum) | Email notification preference |  |
|**timeInForce** | [**TimeInForceEnum**](#TimeInForceEnum) | Time in force (null for market orders) |  [optional] |
|**hidden** | **Boolean** | Not supported at the moment |  [optional] |
|**postOnly** | **Boolean** | Not supported at the moment |  [optional] |
|**marginCurrencyShortName** | [**MarginCurrencyShortNameEnum**](#MarginCurrencyShortNameEnum) | Futures margin mode |  [optional] |
|**positionMarginType** | [**PositionMarginTypeEnum**](#PositionMarginTypeEnum) | Position margin type |  [optional] |
|**takeProfitPrice** | **BigDecimal** | Take profit trigger price (only for market_order, limit_order) |  [optional] |
|**stopLossPrice** | **BigDecimal** | Stop loss trigger price (only for market_order, limit_order) |  [optional] |



## Enum: SideEnum

| Name | Value |
|---- | -----|
| BUY | &quot;buy&quot; |
| SELL | &quot;sell&quot; |



## Enum: OrderTypeEnum

| Name | Value |
|---- | -----|
| MARKET | &quot;market&quot; |
| LIMIT | &quot;limit&quot; |
| STOP_LIMIT | &quot;stop_limit&quot; |
| STOP_MARKET | &quot;stop_market&quot; |
| TAKE_PROFIT_LIMIT | &quot;take_profit_limit&quot; |
| TAKE_PROFIT_MARKET | &quot;take_profit_market&quot; |



## Enum: NotificationEnum

| Name | Value |
|---- | -----|
| NO_NOTIFICATION | &quot;no_notification&quot; |
| EMAIL_NOTIFICATION | &quot;email_notification&quot; |



## Enum: TimeInForceEnum

| Name | Value |
|---- | -----|
| GOOD_TILL_CANCEL | &quot;good_till_cancel&quot; |
| FILL_OR_KILL | &quot;fill_or_kill&quot; |
| IMMEDIATE_OR_CANCEL | &quot;immediate_or_cancel&quot; |



## Enum: MarginCurrencyShortNameEnum

| Name | Value |
|---- | -----|
| INR | &quot;INR&quot; |
| USDT | &quot;USDT&quot; |



## Enum: PositionMarginTypeEnum

| Name | Value |
|---- | -----|
| ISOLATED | &quot;isolated&quot; |
| CROSSED | &quot;crossed&quot; |



