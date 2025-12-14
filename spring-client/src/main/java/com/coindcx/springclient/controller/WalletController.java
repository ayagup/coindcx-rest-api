package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1WalletsSubAccountTransferPost200Response;
import com.coindcx.springclient.model.ExchangeV1WalletsSubAccountTransferPostRequest;
import com.coindcx.springclient.model.ExchangeV1WalletsTransferPostRequest;
import com.coindcx.springclient.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Wallet operations
 */
@RestController
@RequestMapping("/api/wallet")
@Tag(name = "Wallet Management", description = "APIs for wallet and fund transfer operations")
@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "apiSecret")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Transfer funds between sub-accounts
     * 
     * @param request Sub-account transfer request
     * @return Transfer response
     */
    @PostMapping("/sub-account/transfer")
    public ResponseEntity<ExchangeV1WalletsSubAccountTransferPost200Response> subAccountTransfer(
            @RequestBody ExchangeV1WalletsSubAccountTransferPostRequest request) {
        try {
            ExchangeV1WalletsSubAccountTransferPost200Response response = walletService.subAccountTransfer(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Transfer funds between wallets
     * 
     * @param request Wallet transfer request
     * @return Transfer response
     */
    @PostMapping("/transfer")
    public ResponseEntity<ExchangeV1WalletsSubAccountTransferPost200Response> walletTransfer(
            @RequestBody ExchangeV1WalletsTransferPostRequest request) {
        try {
            ExchangeV1WalletsSubAccountTransferPost200Response response = walletService.walletTransfer(request);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }
}
