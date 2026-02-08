import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { TrendingUp, TrendingDown, RefreshCw } from 'lucide-react';

type MarketType = 'spot' | 'futures';

interface MarketData {
  market: string;
  price: string;
  volume?: string;
  high?: string;
  low?: string;
  timestamp?: string;
  open?: string;
  close?: string;
}

const MarketPage: React.FC = () => {
  const [spotMarkets, setSpotMarkets] = useState<string[]>([]);
  const [futuresMarkets, setFuturesMarkets] = useState<string[]>([]);
  const [spotData, setSpotData] = useState<Map<string, MarketData>>(new Map());
  const [futuresData, setFuturesData] = useState<Map<string, MarketData>>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState<MarketType>('spot');

  // Fetch list of all markets
  const fetchMarkets = async () => {
    try {
      const [spotResponse, futuresResponse] = await Promise.all([
        apiService.getAllSpotMarkets(),
        apiService.getAllFuturesContracts()
      ]);
      
      setSpotMarkets(spotResponse.data || []);
      setFuturesMarkets(futuresResponse.data || []);
    } catch (err: any) {
      console.error('Error fetching markets:', err);
      setError(err.response?.data?.message || 'Failed to fetch markets list');
    }
  };

  // Fetch latest price data for each market
  const fetchMarketData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Fetch spot market data
      const spotDataMap = new Map<string, MarketData>();
      const spotPromises = spotMarkets.slice(0, 50).map(async (market) => {
        try {
          const response = await apiService.getLatestSpotPrice(market);
          if (response.data) {
            spotDataMap.set(market, {
              market: market,
              price: response.data.price || response.data.lastPrice || '0',
              volume: response.data.volume,
              high: response.data.high,
              low: response.data.low,
              open: response.data.open,
              close: response.data.close,
              timestamp: response.data.timestamp
            });
          }
        } catch (err) {
          // Skip markets with no data
          console.debug(`No data for spot market: ${market}`);
        }
      });

      // Fetch futures market data
      const futuresDataMap = new Map<string, MarketData>();
      const futuresPromises = futuresMarkets.slice(0, 50).map(async (contract) => {
        try {
          const response = await apiService.getLatestFuturesPrice(contract);
          if (response.data) {
            futuresDataMap.set(contract, {
              market: contract,
              price: response.data.lastPrice || response.data.markPrice || '0',
              volume: response.data.volume,
              high: response.data.high,
              low: response.data.low,
              open: response.data.open,
              close: response.data.close,
              timestamp: response.data.timestamp
            });
          }
        } catch (err) {
          // Skip contracts with no data
          console.debug(`No data for futures contract: ${contract}`);
        }
      });

      await Promise.all([...spotPromises, ...futuresPromises]);
      
      setSpotData(spotDataMap);
      setFuturesData(futuresDataMap);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch market data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMarkets();
  }, []);

  useEffect(() => {
    if (spotMarkets.length > 0 || futuresMarkets.length > 0) {
      fetchMarketData();
      // Refresh every 10 seconds for real-time data
      const interval = setInterval(fetchMarketData, 10000);
      return () => clearInterval(interval);
    }
  }, [spotMarkets, futuresMarkets]);

  // Get filtered data based on active tab
  const getFilteredData = () => {
    const dataMap = activeTab === 'spot' ? spotData : futuresData;
    const markets = activeTab === 'spot' ? spotMarkets : futuresMarkets;
    
    return markets
      .filter(market => market.toLowerCase().includes(searchTerm.toLowerCase()))
      .map(market => dataMap.get(market))
      .filter(data => data !== undefined) as MarketData[];
  };

  const filteredData = getFilteredData();

  const handleRefresh = () => {
    fetchMarkets();
    fetchMarketData();
  };

  if (loading && spotData.size === 0 && futuresData.size === 0) return <Loading />;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Market Data (WebSocket Storage)</h1>
        <button onClick={handleRefresh} className="btn btn-secondary" disabled={loading}>
          <RefreshCw size={18} />
          Refresh
        </button>
      </div>

      {error && <ErrorMessage message={error} onClose={() => setError(null)} />}

      {/* Tabs */}
      <div className="tabs">
        <button
          className={`tab ${activeTab === 'spot' ? 'active' : ''}`}
          onClick={() => setActiveTab('spot')}
        >
          Spot Markets
          <span className="tab-count">{spotData.size}</span>
        </button>
        <button
          className={`tab ${activeTab === 'futures' ? 'active' : ''}`}
          onClick={() => setActiveTab('futures')}
        >
          Futures Markets
          <span className="tab-count">{futuresData.size}</span>
        </button>
      </div>

      <div className="search-box">
        <input
          type="text"
          placeholder="Search markets..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="input"
        />
      </div>

      {loading && (
        <div style={{ textAlign: 'center', padding: '1rem', color: '#64748b' }}>
          Loading market data...
        </div>
      )}

      <div className="ticker-grid">
        {filteredData.map((data) => {
          const price = parseFloat(data.price);
          const high = parseFloat(data.high || '0');
          const low = parseFloat(data.low || '0');
          const volume = parseFloat(data.volume || '0');
          const open = parseFloat(data.open || '0');
          
          return (
            <div key={data.market} className="ticker-card">
              <div className="ticker-header">
                <h3>{data.market}</h3>
                {price > low ? (
                  <TrendingUp className="trend-up" size={20} />
                ) : (
                  <TrendingDown className="trend-down" size={20} />
                )}
              </div>
              
              <div className="ticker-price">
                ₹{price.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 8 })}
              </div>
              
              <div className="ticker-details">
                {high > 0 && (
                  <div className="ticker-detail">
                    <span className="label">24h High</span>
                    <span className="value">₹{high.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 8 })}</span>
                  </div>
                )}
                {low > 0 && (
                  <div className="ticker-detail">
                    <span className="label">24h Low</span>
                    <span className="value">₹{low.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 8 })}</span>
                  </div>
                )}
                {volume > 0 && (
                  <div className="ticker-detail">
                    <span className="label">Volume</span>
                    <span className="value">{volume.toLocaleString(undefined, { maximumFractionDigits: 2 })}</span>
                  </div>
                )}
                {open > 0 && (
                  <div className="ticker-detail">
                    <span className="label">Open</span>
                    <span className="value">₹{open.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 8 })}</span>
                  </div>
                )}
                {data.timestamp && (
                  <div className="ticker-detail">
                    <span className="label">Updated</span>
                    <span className="value" style={{ fontSize: '0.8rem' }}>
                      {new Date(data.timestamp).toLocaleTimeString()}
                    </span>
                  </div>
                )}
              </div>
              
              <div style={{ 
                marginTop: '0.75rem', 
                padding: '0.5rem', 
                background: 'rgba(59, 130, 246, 0.1)', 
                borderRadius: '0.5rem',
                fontSize: '0.75rem',
                color: '#64748b',
                textAlign: 'center'
              }}>
                📊 WebSocket Data
              </div>
            </div>
          );
        })}
      </div>

      {filteredData.length === 0 && !loading && (
        <div className="empty-state">
          <p>
            {searchTerm 
              ? `No ${activeTab} markets found matching "${searchTerm}"`
              : `No ${activeTab} market data available yet. WebSocket data will appear here once markets are active.`
            }
          </p>
        </div>
      )}
    </div>
  );
};

export default MarketPage;
