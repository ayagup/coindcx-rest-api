package com.coindcx.springclient.service;

import com.coindcx.springclient.api.OrderApi;
import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for Order management operations
 */
@Service
public class OrderService {

    private final OrderApi orderApi;

    public OrderService() {
        this.orderApi = new OrderApi();
    }

    public OrderService(OrderApi orderApi) {
        this.orderApi = orderApi;
    }

    /**
     * Get count of active orders
     * @param request Active orders request
     * @return Active orders count
     * @throws ApiException if the API call fails
     */
    public ExchangeV1OrdersActiveOrdersCountPost200Response getActiveOrdersCount(ExchangeV1OrdersActiveOrdersPostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersActiveOrdersCountPost(request);
    }

    /**
     * Get active orders
     * @param request Active orders request
     * @return List of active orders
     * @throws ApiException if the API call fails
     */
    public List<Object> getActiveOrders(ExchangeV1OrdersActiveOrdersPostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersActiveOrdersPost(request);
    }

    /**
     * Cancel all orders
     * @param request Cancel all request
     * @throws ApiException if the API call fails
     */
    public void cancelAllOrders(ExchangeV1OrdersCancelAllPostRequest request) throws ApiException {
        orderApi.exchangeV1OrdersCancelAllPost(request);
    }

    /**
     * Cancel orders by IDs
     * @param request Cancel by IDs request
     * @throws ApiException if the API call fails
     */
    public void cancelOrdersByIds(ExchangeV1OrdersCancelByIdsPostRequest request) throws ApiException {
        orderApi.exchangeV1OrdersCancelByIdsPost(request);
    }

    /**
     * Cancel a single order
     * @param request Cancel order request
     * @throws ApiException if the API call fails
     */
    public void cancelOrder(ExchangeV1OrdersCancelPostRequest request) throws ApiException {
        orderApi.exchangeV1OrdersCancelPost(request);
    }

    /**
     * Create multiple orders
     * @param request Multiple orders creation request
     * @return Multiple orders response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1OrdersCreateMultiplePost200Response createMultipleOrders(ExchangeV1OrdersCreateMultiplePostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersCreateMultiplePost(request);
    }

    /**
     * Create a single order
     * @param request Order creation request
     * @return Order creation response
     * @throws ApiException if the API call fails
     */
    public ExchangeV1OrdersCreatePost200Response createOrder(ExchangeV1OrdersCreatePostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersCreatePost(request);
    }

    /**
     * Edit an existing order
     * @param request Edit order request
     * @return Edit response
     * @throws ApiException if the API call fails
     */
    public Object editOrder(ExchangeV1OrdersEditPostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersEditPost(request);
    }

    /**
     * Get status of multiple orders
     * @param request Status request for multiple orders
     * @return List of order statuses
     * @throws ApiException if the API call fails
     */
    public List<Object> getMultipleOrdersStatus(ExchangeV1OrdersStatusMultiplePostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersStatusMultiplePost(request);
    }

    /**
     * Get status of a single order
     * @param request Status request
     * @return Order status
     * @throws ApiException if the API call fails
     */
    public ExchangeV1OrdersCreatePost200ResponseOrdersInner getOrderStatus(ExchangeV1OrdersStatusPostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersStatusPost(request);
    }

    /**
     * Get trade history
     * @param request Trade history request
     * @return List of trades
     * @throws ApiException if the API call fails
     */
    public List<Object> getTradeHistory(ExchangeV1OrdersTradeHistoryPostRequest request) throws ApiException {
        return orderApi.exchangeV1OrdersTradeHistoryPost(request);
    }
}
