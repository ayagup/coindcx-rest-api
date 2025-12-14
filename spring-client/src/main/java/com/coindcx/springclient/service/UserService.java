package com.coindcx.springclient.service;

import com.coindcx.springclient.api.UserApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPost200ResponseInner;
import com.coindcx.springclient.model.ExchangeV1UsersBalancesPostRequest;
import com.coindcx.springclient.model.ExchangeV1UsersInfoPost200Response;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for User account operations
 */
@Service
public class UserService {

    private final UserApi userApi;

    public UserService() {
        this.userApi = new UserApi();
    }

    public UserService(UserApi userApi) {
        this.userApi = userApi;
    }

    /**
     * Get account balances
     * @param request Balance request with timestamp
     * @return List of account balances
     * @throws ApiException if the API call fails
     */
    public List<ExchangeV1UsersBalancesPost200ResponseInner> getBalances(ExchangeV1UsersBalancesPostRequest request) throws ApiException {
        return userApi.exchangeV1UsersBalancesPost(request);
    }

    /**
     * Get user account information
     * @param request User info request with timestamp
     * @return User account info
     * @throws ApiException if the API call fails
     */
    public ExchangeV1UsersInfoPost200Response getUserInfo(ExchangeV1UsersBalancesPostRequest request) throws ApiException {
        return userApi.exchangeV1UsersInfoPost(request);
    }
}
