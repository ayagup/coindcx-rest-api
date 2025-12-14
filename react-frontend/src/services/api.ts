import axios, { AxiosInstance, AxiosError } from 'axios';

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// Create axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor to add API keys
apiClient.interceptors.request.use(
  (config) => {
    const apiKey = localStorage.getItem('apiKey');
    const apiSecret = localStorage.getItem('apiSecret');
    
    if (apiKey) {
      config.headers['X-API-KEY'] = apiKey;
    }
    if (apiSecret) {
      config.headers['X-API-SECRET'] = apiSecret;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response) {
      // Server responded with error
      const status = error.response.status;
      if (status === 401) {
        console.error('Unauthorized - Check API credentials');
      } else if (status === 403) {
        console.error('Forbidden - Insufficient permissions');
      } else if (status === 404) {
        console.error('Not found');
      } else if (status >= 500) {
        console.error('Server error');
      }
    } else if (error.request) {
      // Request made but no response
      console.error('No response from server');
    } else {
      // Something else happened
      console.error('Request error:', error.message);
    }
    return Promise.reject(error);
  }
);

// API Service methods
export const apiService = {
  // Public Market Data APIs
  getTicker: () => apiClient.get('/api/public/ticker'),
  getMarkets: () => apiClient.get('/api/public/markets'),
  getMarketsDetails: () => apiClient.get('/api/public/markets_details'),
  getTrades: (market: string) => apiClient.get(`/api/public/trades`, { params: { market } }),
  getOrderBook: (market: string) => apiClient.get(`/api/public/order_book`, { params: { market } }),
  getCandles: (market: string, interval: string) => apiClient.get(`/api/public/candles`, { params: { market, interval } }),

  // Order Management APIs
  createOrder: (orderData: any) => apiClient.post('/api/orders/create', orderData),
  getActiveOrders: (data: any) => apiClient.post('/api/orders/active_orders', data),
  cancelOrder: (orderId: string) => apiClient.delete(`/api/orders/cancel/${orderId}`),
  cancelAllOrders: (data: any) => apiClient.post('/api/orders/cancel_all', data),
  getOrderHistory: (data: any) => apiClient.post('/api/orders/order_status', data),
  getMultipleOrderStatus: (data: any) => apiClient.post('/api/orders/multiple_order_status', data),
  getTradeHistory: (data: any) => apiClient.post('/api/orders/trade_history', data),
  
  // Futures Trading APIs
  createFuturesOrder: (orderData: any) => apiClient.post('/api/futures/orders/create', orderData),
  cancelFuturesOrder: (orderId: string) => apiClient.delete(`/api/futures/orders/cancel/${orderId}`),
  getActiveFuturesOrders: (data: any) => apiClient.post('/api/futures/orders/active', data),
  getFuturesPositions: (data: any) => apiClient.post('/api/futures/positions', data),
  closeFuturesPosition: (data: any) => apiClient.post('/api/futures/positions/exit', data),
  addMarginToPosition: (data: any) => apiClient.post('/api/futures/positions/add_margin', data),
  removeMarginFromPosition: (data: any) => apiClient.post('/api/futures/positions/remove_margin', data),
  updateLeverage: (data: any) => apiClient.post('/api/futures/positions/update_leverage', data),
  createTpSl: (data: any) => apiClient.post('/api/futures/positions/create_tpsl', data),
  cancelAllFuturesOrders: (data: any) => apiClient.post('/api/futures/positions/cancel_all_orders', data),
  getFuturesTrades: (data: any) => apiClient.post('/api/futures/trades', data),
  getPositionTransactions: (data: any) => apiClient.post('/api/futures/positions/transactions', data),

  // Margin Trading APIs
  createMarginOrder: (orderData: any) => apiClient.post('/api/margin/create', orderData),
  cancelMarginOrder: (orderId: string) => apiClient.delete(`/api/margin/cancel/${orderId}`),
  getActiveMarginOrders: (data: any) => apiClient.post('/api/margin/active_orders', data),
  getMarginOrderStatus: (data: any) => apiClient.post('/api/margin/status', data),
  addMargin: (data: any) => apiClient.post('/api/margin/add', data),
  removeMargin: (data: any) => apiClient.post('/api/margin/remove', data),
  editMarginPrice: (data: any) => apiClient.post('/api/margin/edit_price', data),
  editStopLoss: (data: any) => apiClient.post('/api/margin/edit_sl', data),
  editTargetPrice: (data: any) => apiClient.post('/api/margin/edit_target', data),

  // Lending APIs
  lendFunds: (data: any) => apiClient.post('/api/lend/lend', data),
  fetchLendingOrders: (data: any) => apiClient.post('/api/lend/orders', data),

  // User Account APIs
  getBalances: () => apiClient.post('/api/user/balances', {}),
  getUserInfo: () => apiClient.post('/api/user/info', {}),

  // Wallet Management APIs
  transferFunds: (data: any) => apiClient.post('/api/wallet/transfer', data),
  getWalletTransactions: (data: any) => apiClient.post('/api/wallet/transactions', data),

  // API Monitoring APIs (No authentication required)
  getApiCallLogs: () => apiClient.get('/api/logs'),
  getApiCallLogById: (id: string) => apiClient.get(`/api/logs/${id}`),
  getApiCallStats: () => apiClient.get('/api/logs/stats'),
  getApiCallsByEndpoint: (endpoint: string) => apiClient.get(`/api/logs/endpoint/${endpoint}`),
  getApiCallsByStatus: (status: number) => apiClient.get(`/api/logs/status/${status}`),
  getApiCallsByMethod: (method: string) => apiClient.get(`/api/logs/method/${method}`),
  getRecentApiCalls: (count: number) => apiClient.get(`/api/logs/recent/${count}`),
  getApiCallsInTimeRange: (start: string, end: string) => apiClient.get('/api/logs/time-range', { params: { start, end } }),
  getSlowApiCalls: (threshold: number) => apiClient.get(`/api/logs/slow/${threshold}`),
  getFailedApiCalls: () => apiClient.get('/api/logs/failed'),
};

export default apiClient;
