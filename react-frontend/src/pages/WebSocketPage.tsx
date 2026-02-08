import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { Activity, RefreshCw, Database, Wifi, WifiOff } from 'lucide-react';

interface WebSocketStatus {
  connected: boolean;
  status: string;
  subscribedChannels: string[];
  storageStats: {
    totalRecords: number;
    spotRecords: number;
    futuresRecords: number;
    oldestRecordAge: string;
    newestRecordAge: string;
  };
}

interface WebSocketMessage {
  event: string;
  data: any;
  timestamp: string;
}

const WebSocketPage: React.FC = () => {
  const [status, setStatus] = useState<WebSocketStatus | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedEvent, setSelectedEvent] = useState<string>('');
  const [messages, setMessages] = useState<WebSocketMessage[]>([]);
  const [messagesLoading, setMessagesLoading] = useState(false);

  const fetchStatus = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await apiService.getWebSocketStatus();
      setStatus(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch WebSocket status');
    } finally {
      setLoading(false);
    }
  };

  const fetchMessages = async (event: string) => {
    if (!event) return;
    
    try {
      setMessagesLoading(true);
      const response = await apiService.getWebSocketMessages(event);
      setMessages(response.data.messages || []);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch messages');
    } finally {
      setMessagesLoading(false);
    }
  };

  const handleClearMessages = async (event: string) => {
    try {
      await apiService.clearWebSocketMessages(event);
      setMessages([]);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to clear messages');
    }
  };

  const handleClearAllMessages = async () => {
    try {
      await apiService.clearAllWebSocketMessages();
      setMessages([]);
      setSelectedEvent('');
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to clear all messages');
    }
  };

  useEffect(() => {
    fetchStatus();
    // Refresh status every 10 seconds
    const interval = setInterval(fetchStatus, 10000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (selectedEvent) {
      fetchMessages(selectedEvent);
    }
  }, [selectedEvent]);

  if (loading && !status) return <Loading />;

  return (
    <div className="page">
      <div className="page-header">
        <h1>WebSocket Monitor</h1>
        <button onClick={fetchStatus} className="btn btn-secondary">
          <RefreshCw size={18} />
          Refresh
        </button>
      </div>

      {error && <ErrorMessage message={error} onClose={() => setError(null)} />}

      {/* Connection Status Card */}
      <div className="status-card">
        <div className="status-header">
          <h2>Connection Status</h2>
          {status?.connected ? (
            <Wifi className="status-icon connected" size={24} />
          ) : (
            <WifiOff className="status-icon disconnected" size={24} />
          )}
        </div>
        <div className="status-details">
          <div className="status-item">
            <span className="label">Status:</span>
            <span className={`value ${status?.connected ? 'connected' : 'disconnected'}`}>
              {status?.status || 'Unknown'}
            </span>
          </div>
          <div className="status-item">
            <span className="label">Active Channels:</span>
            <span className="value">{status?.subscribedChannels?.length || 0}</span>
          </div>
        </div>
      </div>

      {/* Storage Statistics Card */}
      {status?.storageStats && (
        <div className="stats-card">
          <div className="stats-header">
            <h2>Data Storage Statistics</h2>
            <Database size={24} />
          </div>
          <div className="stats-grid">
            <div className="stat-item">
              <div className="stat-label">Total Records</div>
              <div className="stat-value">{status.storageStats.totalRecords.toLocaleString()}</div>
            </div>
            <div className="stat-item">
              <div className="stat-label">Spot Market Data</div>
              <div className="stat-value">{status.storageStats.spotRecords.toLocaleString()}</div>
            </div>
            <div className="stat-item">
              <div className="stat-label">Futures Market Data</div>
              <div className="stat-value">{status.storageStats.futuresRecords.toLocaleString()}</div>
            </div>
            <div className="stat-item">
              <div className="stat-label">Data Range</div>
              <div className="stat-value-small">
                {status.storageStats.oldestRecordAge || 'N/A'}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Subscribed Channels */}
      {status?.subscribedChannels && status.subscribedChannels.length > 0 && (
        <div className="channels-card">
          <div className="channels-header">
            <h2>Subscribed Channels ({status.subscribedChannels.length})</h2>
            <Activity size={24} />
          </div>
          <div className="channels-list">
            {status.subscribedChannels.map((channel, index) => (
              <div key={index} className="channel-item">
                <span className="channel-name">{channel}</span>
                <span className="channel-status">Active</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Messages Monitor */}
      <div className="messages-card">
        <div className="messages-header">
          <h2>Message Monitor</h2>
          <div className="messages-actions">
            <select
              value={selectedEvent}
              onChange={(e) => setSelectedEvent(e.target.value)}
              className="select"
            >
              <option value="">Select Event Type</option>
              <option value="price_change">Price Changes</option>
              <option value="new_trade">New Trades</option>
              <option value="depth_update">Depth Updates</option>
              <option value="depth_snapshot">Depth Snapshots</option>
              <option value="candlestick">Candlestick</option>
              <option value="current_prices_update">Current Prices</option>
              <option value="price_stats_update">Price Statistics</option>
            </select>
            {selectedEvent && (
              <>
                <button
                  onClick={() => fetchMessages(selectedEvent)}
                  className="btn btn-secondary"
                  disabled={messagesLoading}
                >
                  <RefreshCw size={16} />
                  Refresh
                </button>
                <button
                  onClick={() => handleClearMessages(selectedEvent)}
                  className="btn btn-warning"
                >
                  Clear
                </button>
              </>
            )}
            <button
              onClick={handleClearAllMessages}
              className="btn btn-danger"
            >
              Clear All
            </button>
          </div>
        </div>

        {messagesLoading ? (
          <Loading />
        ) : messages.length > 0 ? (
          <div className="messages-list">
            <div className="messages-count">
              Showing {messages.length} messages for {selectedEvent}
            </div>
            {messages.slice(0, 50).map((message, index) => (
              <div key={index} className="message-item">
                <pre>{JSON.stringify(message, null, 2)}</pre>
              </div>
            ))}
            {messages.length > 50 && (
              <div className="messages-more">
                Showing first 50 of {messages.length} messages
              </div>
            )}
          </div>
        ) : selectedEvent ? (
          <div className="empty-state">
            <p>No messages received for event: {selectedEvent}</p>
          </div>
        ) : (
          <div className="empty-state">
            <p>Select an event type to view messages</p>
          </div>
        )}
      </div>

      {/* Info Box */}
      <div className="info-card">
        <h3>📝 Note</h3>
        <p>
          WebSocket subscriptions are now managed automatically by the backend service.
          This page provides real-time monitoring of the connection status, active channels,
          and data storage statistics.
        </p>
        <p>
          All WebSocket data is automatically persisted to the database and can be queried
          via the REST API endpoints at <code>/api/websocket/data/*</code>
        </p>
      </div>
    </div>
  );
};

export default WebSocketPage;
