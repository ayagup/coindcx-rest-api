

# ExchangeV1MarginFetchOrdersPostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**market** | **String** | The trading pair (default - orders for all markets) |  [optional] |
|**details** | **Boolean** | Whether you want detailed information or not |  [optional] |
|**status** | [**StatusEnum**](#StatusEnum) | The status of the order (default - all orders) |  [optional] |
|**size** | **BigDecimal** | Number of records per page |  [optional] |
|**timestamp** | **Integer** | EPOCH timestamp in milliseconds |  |



## Enum: StatusEnum

| Name | Value |
|---- | -----|
| INIT | &quot;init&quot; |
| OPEN | &quot;open&quot; |
| CLOSE | &quot;close&quot; |
| REJECTED | &quot;rejected&quot; |
| CANCELLED | &quot;cancelled&quot; |
| PARTIAL_ENTRY | &quot;partial_entry&quot; |
| PARTIAL_CLOSE | &quot;partial_close&quot; |
| TRIGGERED | &quot;triggered&quot; |



