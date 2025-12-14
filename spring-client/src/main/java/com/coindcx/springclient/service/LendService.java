package com.coindcx.springclient.service;

import com.coindcx.springclient.api.LendApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.ExchangeV1FundingFetchOrdersPost200ResponseInner;
import com.coindcx.springclient.model.ExchangeV1FundingFetchOrdersPostRequest;
import com.coindcx.springclient.model.ExchangeV1FundingLendPostRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Lending operations
 */
@Service
public class LendService {

    private final LendApi lendApi;

    public LendService() {
        this.lendApi = new LendApi();
    }

    public LendService(LendApi lendApi) {
        this.lendApi = lendApi;
    }

    /**
     * Fetch lending orders
     * @param request Fetch orders request with pagination
     * @return List of lending orders
     * @throws ApiException if the API call fails
     */
    public List<ExchangeV1FundingFetchOrdersPost200ResponseInner> fetchOrders(ExchangeV1FundingFetchOrdersPostRequest request) throws ApiException {
        return lendApi.exchangeV1FundingFetchOrdersPost(request);
    }

    /**
     * Create a new lending order
     * @param request Lend order request
     * @throws ApiException if the API call fails
     */
    public void createLendOrder(ExchangeV1FundingLendPostRequest request) throws ApiException {
        lendApi.exchangeV1FundingLendPost(request);
    }
}
