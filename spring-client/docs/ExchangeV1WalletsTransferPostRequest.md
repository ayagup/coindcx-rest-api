

# ExchangeV1WalletsTransferPostRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**timestamp** | **Integer** | EPOCH timestamp in milliseconds |  |
|**sourceWalletType** | [**SourceWalletTypeEnum**](#SourceWalletTypeEnum) | The wallet type from which balance has to be transferred |  |
|**destinationWalletType** | [**DestinationWalletTypeEnum**](#DestinationWalletTypeEnum) | The wallet type to which balance has to be transferred |  |
|**currencyShortName** | **String** | The type of asset being transferred (e.g., BTC, ETH, USDT) |  |
|**amount** | **Float** | The amount of the asset to transfer |  |



## Enum: SourceWalletTypeEnum

| Name | Value |
|---- | -----|
| SPOT | &quot;spot&quot; |
| FUTURES | &quot;futures&quot; |



## Enum: DestinationWalletTypeEnum

| Name | Value |
|---- | -----|
| SPOT | &quot;spot&quot; |
| FUTURES | &quot;futures&quot; |



