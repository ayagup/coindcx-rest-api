package com.coindcx.springclient.service;

import com.coindcx.springclient.api.WalletApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1WalletsSubAccountTransferPost200Response;
import com.coindcx.springclient.model.ExchangeV1WalletsSubAccountTransferPostRequest;
import com.coindcx.springclient.model.ExchangeV1WalletsTransferPostRequest;
import org.springframework.stereotype.Service;

/**
 * Service class for Wallet operations
 */
@Service
public class WalletService {

    private final WalletApi walletApi;

    public WalletService() {
        this.walletApi = new WalletApi();
    }

    public WalletService(WalletApi walletApi) {
        this.walletApi = walletApi;
    }

    /**
     * Transfer funds between sub-accounts
     * @param request Sub-account transfer request
     * @return Transfer response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1WalletsSubAccountTransferPost200Response subAccountTransfer(ExchangeV1WalletsSubAccountTransferPostRequest request) throws ApiException {
        return walletApi.exchangeV1WalletsSubAccountTransferPost(request);
    }

    /**
     * Transfer funds between wallets
     * @param request Wallet transfer request
     * @return Transfer response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1WalletsSubAccountTransferPost200Response walletTransfer(ExchangeV1WalletsTransferPostRequest request) throws ApiException {
        return walletApi.exchangeV1WalletsTransferPost(request);
    }
}
