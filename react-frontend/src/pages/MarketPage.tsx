import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { apiService } from '../services/api';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { TrendingUp, TrendingDown, RefreshCw, Zap, ClipboardList } from 'lucide-react';
import { MT5_CONTRACTS } from './MT5TradingPage';

type MarketType = 'spot' | 'futures' | 'mt5';

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
  const navigate = useNavigate();
  const [futuresMarkets, setFuturesMarkets] = useState<string[]>([]);
  const [spotData, setSpotData] = useState<Map<string, MarketData>>(new Map());
  const [futuresData, setFuturesData] = useState<Map<string, MarketData>>(new Map());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState<MarketType>('spot');

  // Fetch futures instruments list
  const fetchMarkets = async () => {
    try {
      const instrResponse = await apiService.getActiveFuturesInstruments();
      const allFutures: string[] = instrResponse.data?.futures ?? instrResponse.data ?? [];
      const merged = Array.from(new Set([...allFutures, ...MT5_CONTRACTS])).sort();
      setFuturesMarkets(merged);
    } catch (err: any) {
      console.error('Error fetching futures markets:', err);
    }
  };

  // Fetch all prices in two bulk calls instead of per-symbol calls
  const fetchMarketData = async () => {
    try {
      setLoading(true);
      setError(null);

      const [spotRes, futuresRes] = await Promise.allSettled([
        apiService.getMarketsDetails(),
        apiService.getFuturesCurrentPricesPublic(),
      ]);

      // ── Spot: parse markets_details array ────────────────────────────────
      if (spotRes.status === 'fulfilled') {
        const arr: any[] = Array.isArray(spotRes.value.data) ? spotRes.value.data : [];
        const spotMap = new Map<string, MarketData>();
        for (const item of arr) {
          if (!item?.market) continue;
          const lastPrice = item.lastPrice ?? item.price ?? '0';
          spotMap.set(item.market, {
            market: item.market,
            price: String(lastPrice),
            volume: item.volume != null ? String(item.volume) : undefined,
            high: item.high != null ? String(item.high) : undefined,
            low: item.low != null ? String(item.low) : undefined,
            open: item.open != null ? String(item.open) : undefined,
            close: item.close != null ? String(item.close) : lastPrice != null ? String(lastPrice) : undefined,
            timestamp: item.timestamp != null ? String(item.timestamp) : undefined,
          });
        }
        setSpotData(spotMap);
      }

      // ── Futures/MT5: parse bulk prices JSON string ────────────────────────
      if (futuresRes.status === 'fulfilled') {
        const pricesRaw = futuresRes.value.data?.prices;
        const prices: Record<string, { mp?: number; ls?: number } | null> =
          typeof pricesRaw === 'string' ? JSON.parse(pricesRaw) : (pricesRaw ?? {});
        const freshFuturesMap = new Map<string, MarketData>();
        for (const [contract, entry] of Object.entries(prices)) {
          if (!entry) continue;
          const price = entry.mp ?? entry.ls;
          if (price != null && Number(price) > 0) {
            freshFuturesMap.set(contract, { market: contract, price: String(price) });
          }
        }
        setFuturesData(prev => {
          const merged = new Map(prev);
          for (const [k, v] of freshFuturesMap) merged.set(k, v);
          return merged;
        });
      }
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
    fetchMarketData();
    const interval = setInterval(fetchMarketData, 10000);
    return () => clearInterval(interval);
  }, [futuresMarkets]); // eslint-disable-line react-hooks/exhaustive-deps

  // Get filtered data based on active tab
  const bFuturesMarkets = futuresMarkets.filter(m => m.startsWith('B-'));
  const mt5Markets = futuresMarkets.filter(m => !m.startsWith('B-'));

  const getFilteredData = () => {
    const term = searchTerm.toLowerCase();
    if (activeTab === 'futures') {
      return bFuturesMarkets
        .filter(market => market.toLowerCase().includes(term))
        .map(market => futuresData.get(market) ?? { market, price: '' });
    }
    if (activeTab === 'mt5') {
      return mt5Markets
        .filter(market => market.toLowerCase().includes(term))
        .map(market => futuresData.get(market) ?? { market, price: '' });
    }
    return Array.from(spotData.values())
      .filter(data => data.market.toLowerCase().includes(term));
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
    const route = activeTab === 'mt5' ? '/mt5' : '/futures';
    navigate(`${route}?symbol=${encodeURIComponent(market)}`);
  };

  if (loading && spotData.size === 0 && futuresData.size === 0) return <Loading />;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Market Data (WebSocket Storage)</h1>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <Link to="/trade-log" className="btn btn-secondary" style={{ display: 'flex', alignItems: 'center', gap: 6, textDecoration: 'none' }}>
            <ClipboardList size={18} />
            Trade Log
          </Link>
          <button onClick={handleRefresh} className="btn btn-secondary" disabled={loading}>
            <RefreshCw size={18} />
            Refresh
          </button>
        </div>
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
          CoinDCX Futures
          <span className="tab-count">{bFuturesMarkets.length}</span>
        </button>
        <button
          className={`tab ${activeTab === 'mt5' ? 'active' : ''}`}
          onClick={() => setActiveTab('mt5')}
        >
          MT5 Markets
          <span className="tab-count">{mt5Markets.length}</span>
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

              {(activeTab === 'futures' || activeTab === 'mt5') && (
                <button
                  onClick={(e) => handleTradeClick(e, data.market)}
                  style={{
                    display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.35rem',
                    marginTop: '0.6rem', width: '100%',
                    padding: '0.45rem', border: 'none', borderRadius: '0.375rem',
                    background: activeTab === 'mt5'
                      ? 'linear-gradient(135deg, #818cf8, #6366f1)'
                      : 'linear-gradient(135deg, #f59e0b, #d97706)',
                    color: activeTab === 'mt5' ? '#fff' : '#0f172a',
                    fontWeight: 700, fontSize: '0.8rem',
                    cursor: 'pointer', letterSpacing: '0.03em',
                    transition: 'opacity 0.15s',
                  }}
                  onMouseEnter={e => (e.currentTarget.style.opacity = '0.85')}
                  onMouseLeave={e => (e.currentTarget.style.opacity = '1')}
                >
                  <Zap size={13} />
                  {activeTab === 'mt5' ? 'Trade MT5' : 'Trade Futures'}
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
