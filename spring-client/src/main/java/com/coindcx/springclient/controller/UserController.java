package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPost200ResponseInner;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPostRequest;
import com.coindcx.springclient.model.ExchangeV1UsersInfoPost200Response;
import com.coindcx.springclient.service.MetaTraderService;
import com.coindcx.springclient.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for User account operations
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account", description = "APIs for user account information and balances")
@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "apiSecret")
public class UserController {

    private final UserService userService;
    private final MetaTraderService metaTraderService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService, MetaTraderService metaTraderService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.metaTraderService = metaTraderService;
        this.objectMapper = objectMapper;
    }

    /**
     * Get spot account balances
     * 
     * @param request Balance request with timestamp
     * @return List of account balances
     */
    @PostMapping("/balances")
    public ResponseEntity<List<ExchangeV1UsersBalancesPost200ResponseInner>> getBalances(
            @RequestBody ExchangeV1UsersBalancesPostRequest request) {
        try {
            List<ExchangeV1UsersBalancesPost200ResponseInner> balances = userService.getBalances(request);
            return ResponseEntity.ok(balances);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    /**
     * Get merged user account information from CoinDCX and MT5.
     * Always fetches from both sources.
     * Returns: { "coindcx": {...}, "mt5": {...} } with optional error fields if a source fails.
     */
    @PostMapping("/info")
    @Operation(summary = "Get merged CoinDCX and MT5 account info",
               description = "Fetches CoinDCX user account info and MetaTrader 5 full account snapshot simultaneously, "
                       + "merging them into { \"coindcx\": {...}, \"mt5\": {...} }. "
                       + "If a source fails, its error is included in 'coindcx_error' or 'mt5_error' "
                       + "and the other source result is still returned.")
    public ResponseEntity<String> getUserInfo(
            @RequestBody ExchangeV1UsersBalancesPostRequest request) {
        try {
            // --- CoinDCX user info ---
            Object coindcxInfo = null;
            String coindcxError = null;
            try {
                coindcxInfo = userService.getUserInfo(request);
            } catch (ApiException e) {
                coindcxError = e.getMessage();
            } catch (Exception e) {
                coindcxError = e.getMessage();
            }

            // --- MT5 account snapshot ---
            Object mt5Account = null;
            String mt5Error = null;
            try {
                mt5Account = metaTraderService.getAccount();
            } catch (Exception e) {
                mt5Error = e.getMessage();
            }

            // Build merged response
            Map<String, Object> merged = new LinkedHashMap<>();
            merged.put("coindcx", coindcxInfo != null ? coindcxInfo : new LinkedHashMap<>());
            merged.put("mt5", mt5Account != null ? mt5Account : new LinkedHashMap<>());
            if (coindcxError != null) merged.put("coindcx_error", coindcxError);
            if (mt5Error != null) merged.put("mt5_error", mt5Error);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(merged));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    // -------------------------------------------------------------------------
    // MetaTrader 5 – Gateway
    // -------------------------------------------------------------------------

    @GetMapping("/mt5/health")
    @Operation(summary = "MT5 gateway liveness check",
               description = "Verifies the MetaTrader 5 gateway is reachable.")
    public ResponseEntity<Map<String, Object>> mt5Health() {
        return ResponseEntity.ok(metaTraderService.health());
    }

}
