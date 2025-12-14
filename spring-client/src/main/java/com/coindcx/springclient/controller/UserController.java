package com.coindcx.springclient.controller;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPost200ResponseInner;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPostRequest;
import com.coindcx.springclient.model.ExchangeV1UsersInfoPost200Response;
import com.coindcx.springclient.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get account balances
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
     * Get user account information
     * 
     * @param request User info request with timestamp
     * @return User account info
     */
    @PostMapping("/info")
    public ResponseEntity<ExchangeV1UsersInfoPost200Response> getUserInfo(
            @RequestBody ExchangeV1UsersBalancesPostRequest request) {
        try {
            ExchangeV1UsersInfoPost200Response userInfo = userService.getUserInfo(request);
            return ResponseEntity.ok(userInfo);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }
}
