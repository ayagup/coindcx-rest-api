package com.coindcx.springclient.service;

import com.coindcx.springclient.api.MarginApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Margin trading operations
 */
@Service
public class MarginService {

    private final MarginApi marginApi;

    public MarginService() {
        this.marginApi = new MarginApi();
    }

    public MarginService(MarginApi marginApi) {
        this.marginApi = marginApi;
    }

    /**
     * Add margin to position
     * @param request Add margin request
     * @return Add margin response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1MarginAddMarginPost200Response addMargin(ExchangeV1MarginAddMarginPostRequest request) throws ApiException {
        return marginApi.exchangeV1MarginAddMarginPost(request);
    }

    /**
     * Cancel a margin order
     * @param request Cancel order request
     * @throws ApiException if the API call fails
     */
    public void cancelOrder(ExchangeV1MarginCancelPostRequest request) throws ApiException {
        marginApi.exchangeV1MarginCancelPost(request);
    }

    /**
     * Create margin orders
     * @param request Order creation request
     * @return List of created orders
     * @throws ApiException if the API call fails
     */
    public List<ExchangeV1MarginCreatePost200ResponseInner> createOrders(ExchangeV1MarginCreatePostRequest request) throws ApiException {
        return marginApi.exchangeV1MarginCreatePost(request);
    }

    /**
     * Edit price of target order
     * @param request Edit request
     * @throws ApiException if the API call fails
     */
    public void editPriceOfTargetOrder(ExchangeV1MarginEditPriceOfTargetOrderPostRequest request) throws ApiException {
        marginApi.exchangeV1MarginEditPriceOfTargetOrderPost(request);
    }

    /**
     * Edit stop loss
     * @param request Edit SL request
     * @throws ApiException if the API call fails
     */
    public void editStopLoss(ExchangeV1MarginEditSlPostRequest request) throws ApiException {
        marginApi.exchangeV1MarginEditSlPost(request);
    }

    /**
     * Edit target
     * @param request Edit target request
     * @throws ApiException if the API call fails
     */
    public void editTarget(ExchangeV1MarginEditTargetPostRequest request) throws ApiException {
        marginApi.exchangeV1MarginEditTargetPost(request);
    }

    /**
     * Edit trailing stop loss
     * @param request Edit trailing SL request
     * @throws ApiException if the API call fails
     */
    public void editTrailingStopLoss(ExchangeV1MarginEditTrailingSlPostRequest request) throws ApiException {
        marginApi.exchangeV1MarginEditTrailingSlPost(request);
    }

    /**
     * Exit a margin position
     * @param request Exit request
     * @throws ApiException if the API call fails
     */
    public void exitPosition(ExchangeV1MarginExitPostRequest request) throws ApiException {
        marginApi.exchangeV1MarginExitPost(request);
    }

    /**
     * Fetch margin orders
     * @param request Fetch orders request
     * @return List of margin orders
     * @throws ApiException if the API call fails
     */
    public List<ExchangeV1MarginFetchOrdersPost200ResponseInner> fetchOrders(ExchangeV1MarginFetchOrdersPostRequest request) throws ApiException {
        return marginApi.exchangeV1MarginFetchOrdersPost(request);
    }

    /**
     * Get margin order status
     * @param request Order request
     * @return Order status
     * @throws ApiException if the API call fails
     */
    public ExchangeV1MarginFetchOrdersPost200ResponseInner getOrderStatus(ExchangeV1MarginOrderPostRequest request) throws ApiException {
        return marginApi.exchangeV1MarginOrderPost(request);
    }

    /**
     * Remove margin from position
     * @param request Remove margin request
     * @return Remove margin response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1MarginRemoveMarginPost200Response removeMargin(ExchangeV1MarginRemoveMarginPostRequest request) throws ApiException {
        return marginApi.exchangeV1MarginRemoveMarginPost(request);
    }
}
