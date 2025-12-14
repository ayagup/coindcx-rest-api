// Market Data Types
export interface Ticker {
  market: string;
  last_price: string;
  bid: string;
  ask: string;
  high: string;
  low: string;
  volume: string;
  timestamp: number;
}

export interface Market {
  symbol: string;
  base_currency_short_name: string;
  target_currency_short_name: string;
  status: string;
}

export interface Trade {
  price: string;
  quantity: string;
  timestamp: number;
  side: 'buy' | 'sell';
}

export interface OrderBookEntry {
  price: string;
  quantity: string;
}

export interface OrderBook {
  bids: OrderBookEntry[];
  asks: OrderBookEntry[];
}

// Order Types
export interface Order {
  id: string;
  market: string;
  side: 'buy' | 'sell';
  order_type: 'limit_order' | 'market_order';
  price: string;
  quantity: string;
  filled_quantity: string;
  status: 'open' | 'filled' | 'partially_filled' | 'cancelled';
  created_at: string;
  updated_at: string;
}

export interface CreateOrderRequest {
  market: string;
  side: 'buy' | 'sell';
  order_type: 'limit_order' | 'market_order';
  price?: string;
  quantity: string;
  client_order_id?: string;
}

// Futures Types
export interface FuturesPosition {
  id: string;
  market: string;
  side: 'long' | 'short';
  entry_price: string;
  current_price: string;
  quantity: string;
  leverage: number;
  margin: string;
  unrealized_pnl: string;
  liquidation_price: string;
  created_at: string;
}

export interface FuturesOrder {
  id: string;
  market: string;
  side: 'buy' | 'sell';
  order_type: string;
  price: string;
  quantity: string;
  status: string;
  created_at: string;
}

// Balance Types
export interface Balance {
  currency: string;
  available_balance: string;
  locked_balance: string;
  total_balance: string;
}

// User Types
export interface UserInfo {
  id: string;
  email: string;
  name: string;
  verified: boolean;
  created_at: string;
}

// API Log Types
export interface ApiCallLog {
  id: string;
  endpoint: string;
  method: string;
  status_code: number;
  response_time: number;
  timestamp: string;
  error_message?: string;
}

export interface ApiCallStats {
  total_calls: number;
  successful_calls: number;
  failed_calls: number;
  average_response_time: number;
  slowest_endpoint: string;
  most_called_endpoint: string;
}

// Auth Context Types
export interface AuthContextType {
  apiKey: string | null;
  apiSecret: string | null;
  isAuthenticated: boolean;
  login: (apiKey: string, apiSecret: string) => void;
  logout: () => void;
}

// Component Props Types
export interface TickerCardProps {
  ticker: Ticker;
}

export interface OrderFormProps {
  market: string;
  onOrderCreated: () => void;
}

export interface OrderListProps {
  orders: Order[];
  onCancelOrder: (orderId: string) => void;
}
