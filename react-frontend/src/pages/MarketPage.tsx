import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../services/api';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { TrendingUp, TrendingDown, RefreshCw, Zap } from 'lucide-react';

type MarketType = 'spot' | 'futures';

const FUTURES_SYMBOLS = ['B-BTC_USDT', 'B-ETH_USDT', 'B-XAU_USDT', 'B-XAG_USDT'];

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

// Restricted futures symbols to monitor
const ALLOWED_FUTURES = [
  'B-BTC_USDT', 'B-ETH_USDT', 'B-XAU_USDT', 'B-XAG_USDT', 'B-GOAT_USDT',
  'B-TIA_USDT', 'B-PENDLE_USDT', 'B-LUNA2_USDT', 'B-COMP_USDT', 'B-1000FLOKI_USDT',
  'B-KAS_USDT', 'B-ZK_USDT', 'B-AVA_USDT', 'B-STX_USDT', 'B-MOODENG_USDT',
  'B-VINE_USDT', 'B-APT_USDT', 'B-BNB_USDT', 'B-AR_USDT', 'B-LINK_USDT',
  'B-MANA_USDT', 'B-MEW_USDT'
];

const MarketPage: React.FC = () => {
  const navigate = useNavigate();
  const [spotMarkets, setSpotMarkets] = useState<string[]>([]);
  const [futuresMarkets] = useState<string[]>(FUTURES_SYMBOLS);
  const [spotData, setSpotData] = useState<Map<string, MarketData>>(new Map());
  const [futuresData, setFuturesData] = useState<Map<string, MarketData>>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState<MarketType>('spot');

  // Fetch list of spot markets
  const fetchMarkets = async () => {
    try {
      const [spotResponse] = await Promise.all([
        apiService.getAllSpotMarkets(),
        apiService.getAllFuturesContracts()
      ]);
      
      setSpotMarkets(spotResponse.data || []);
      // Always set futures to our restricted list
      setFuturesMarkets(ALLOWED_FUTURES);
      
      // If no spot markets are available, stop loading
      if (!spotResponse.data || spotResponse.data.length === 0) {
        setLoading(false);
      }
    } catch (err: any) {
      console.error('Error fetching spot markets:', err);
      setError(err.response?.data?.message || 'Failed to fetch markets list');
      // Still set futures markets even on error
      setFuturesMarkets(ALLOWED_FUTURES);
      setLoading(false);
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

      // Fetch futures market data — retain last known price; never drop a symbol
      const freshFuturesMap = new Map<string, MarketData>();
      const futuresPromises = futuresMarkets.map(async (contract) => {
        try {
          const response = await apiService.getLatestFuturesPrice(contract);
          if (response.data) {
            freshFuturesMap.set(contract, {
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
          console.debug(`No data for futures contract: ${contract}`);
        }
      });

      await Promise.all([...spotPromises, ...futuresPromises]);

      setSpotData(spotDataMap);
      // Only overwrite entries where fresh data arrived; keep everything else
      setFuturesData(prev => {
        const merged = new Map(prev);
        for (const [k, v] of freshFuturesMap) {
          merged.set(k, v);
        }
        return merged;
      });
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
    // Futures symbols are hardcoded; always fetch futures + spot when spot markets are ready
    fetchMarketData();
    const interval = setInterval(fetchMarketData, 10000);
    return () => clearInterval(interval);
  }, [spotMarkets]);

  // Get filtered data based on active tab
  const getFilteredData = () => {
    if (activeTab === 'futures') {
      return futuresMarkets
        .filter(market => market.toLowerCase().includes(searchTerm.toLowerCase()))
        .map(market => futuresData.get(market) ?? { market, price: '' });
    }
    return spotMarkets
      .filter(market => market.toLowerCase().includes(searchTerm.toLowerCase()))
      .map(market => {
        const data = dataMap.get(market);
        // For futures, always return a market entry even if no data yet
        if (!data && activeTab === 'futures') {
          return {
            market: market,
            price: '0',
            volume: undefined,
            high: undefined,
            low: undefined,
            timestamp: undefined
          };
        }
        return data;
      })
      .filter(data => data !== undefined) as MarketData[];
  };

  const filteredData = getFilteredData();

  const handleRefresh = () => {
    fetchMarkets();
    fetchMarketData();
  };

  const handleCardClick = (market: string) => {
    navigate(`/chart/${activeTab}/${encodeURIComponent(market)}`);
  };

  const handleTradeClick = (e: React.MouseEvent, market: string) => {
    e.stopPropagation();
    navigate(`/futures?symbol=${encodeURIComponent(market)}`);
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

      {!loading && spotData.size === 0 && futuresData.size === 0 && activeTab === 'spot' && (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
          <p style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>No market data available</p>
          <p style={{ fontSize: '0.9rem' }}>
            The WebSocket service hasn't collected any market data yet. 
            Please make sure the WebSocket connection is active and collecting data.
          </p>
        </div>
      )}

      {filteredData.length === 0 && (spotData.size > 0 || futuresData.size > 0) && (
        <div style={{ textAlign: 'center', padding: '2rem', color: '#64748b' }}>
          No markets found matching "{searchTerm}"
        </div>
      )}

      <div className="ticker-grid">
        {filteredData.map((data) => {
          const price = parseFloat(data.price);
          const close = parseFloat(data.close || '0');
          const high = parseFloat(data.high || '0');
          const low = parseFloat(data.low || '0');
          const volume = parseFloat(data.volume || '0');
          const open = parseFloat(data.open || '0');
          // Use close price if available (last updated price), otherwise use current price
          const displayPrice = close > 0 ? close : price;
          const hasData = displayPrice > 0 || high > 0 || volume > 0;
          
          return (
            <div 
              key={data.market} 
              className="ticker-card" 
              style={{
                ...(!hasData ? { opacity: 0.6 } : {}),
                cursor: 'pointer',
                transition: 'transform 0.2s, box-shadow 0.2s'
              }}
              onClick={() => handleCardClick(data.market)}
              onMouseEnter={(e) => {
                e.currentTarget.style.transform = 'translateY(-2px)';
                e.currentTarget.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.15)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)';
                e.currentTarget.style.boxShadow = '';
              }}
            >
              <div className="ticker-header">
                <h3>{data.market}</h3>
                {hasData && displayPrice > low ? (
                  <TrendingUp className="trend-up" size={20} />
                ) : hasData ? (
                  <TrendingDown className="trend-down" size={20} />
                ) : (
                  <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>Waiting for data...</span>
                )}
              </div>
              
              <div className="ticker-price">
                {hasData ? (
                  `₹${displayPrice.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 8 })}`
                ) : (
                  <span style={{ color: '#94a3b8' }}>--</span>
                )}
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

              {activeTab === 'futures' && (
                <button
                  onClick={(e) => handleTradeClick(e, data.market)}
                  style={{
                    display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.35rem',
                    marginTop: '0.6rem', width: '100%',
                    padding: '0.45rem', border: 'none', borderRadius: '0.375rem',
                    background: 'linear-gradient(135deg, #f59e0b, #d97706)',
                    color: '#0f172a', fontWeight: 700, fontSize: '0.8rem',
                    cursor: 'pointer', letterSpacing: '0.03em',
                    transition: 'opacity 0.15s',
                  }}
                  onMouseEnter={e => (e.currentTarget.style.opacity = '0.85')}
                  onMouseLeave={e => (e.currentTarget.style.opacity = '1')}
                >
                  <Zap size={13} />
                  Trade Futures
                </button>
              )}
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
