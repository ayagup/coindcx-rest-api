import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';
import { Ticker } from '../types';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { TrendingUp, TrendingDown, RefreshCw } from 'lucide-react';

const MarketPage: React.FC = () => {
  const [tickers, setTickers] = useState<Ticker[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchTickers = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await apiService.getTicker();
      setTickers(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch market data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTickers();
    // Refresh every 30 seconds
    const interval = setInterval(fetchTickers, 30000);
    return () => clearInterval(interval);
  }, []);

  const filteredTickers = tickers.filter(ticker =>
    ticker.market.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) return <Loading />;

  return (
    <div className="page">
      <div className="page-header">
        <h1>Market Data</h1>
        <button onClick={fetchTickers} className="btn btn-secondary">
          <RefreshCw size={18} />
          Refresh
        </button>
      </div>

      {error && <ErrorMessage message={error} onClose={() => setError(null)} />}

      <div className="search-box">
        <input
          type="text"
          placeholder="Search markets..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="input"
        />
      </div>

      <div className="ticker-grid">
        {filteredTickers.map((ticker) => (
          <div key={ticker.market} className="ticker-card">
            <div className="ticker-header">
              <h3>{ticker.market}</h3>
              {parseFloat(ticker.last_price) > parseFloat(ticker.low) ? (
                <TrendingUp className="trend-up" size={20} />
              ) : (
                <TrendingDown className="trend-down" size={20} />
              )}
            </div>
            
            <div className="ticker-price">
              ₹{parseFloat(ticker.last_price).toLocaleString()}
            </div>
            
            <div className="ticker-details">
              <div className="ticker-detail">
                <span className="label">24h High</span>
                <span className="value">₹{parseFloat(ticker.high).toLocaleString()}</span>
              </div>
              <div className="ticker-detail">
                <span className="label">24h Low</span>
                <span className="value">₹{parseFloat(ticker.low).toLocaleString()}</span>
              </div>
              <div className="ticker-detail">
                <span className="label">Volume</span>
                <span className="value">{parseFloat(ticker.volume).toLocaleString()}</span>
              </div>
              <div className="ticker-detail">
                <span className="label">Bid</span>
                <span className="value">₹{parseFloat(ticker.bid).toLocaleString()}</span>
              </div>
              <div className="ticker-detail">
                <span className="label">Ask</span>
                <span className="value">₹{parseFloat(ticker.ask).toLocaleString()}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {filteredTickers.length === 0 && (
        <div className="empty-state">
          <p>No markets found matching "{searchTerm}"</p>
        </div>
      )}
    </div>
  );
};

export default MarketPage;
