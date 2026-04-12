import React, { useState, useEffect, useCallback } from 'react';
import { publicApiClient } from '../services/api';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import {
  RefreshCw, Search, Filter, BarChart2, Clock, TrendingUp,
  TrendingDown, AlertCircle, CheckCircle, Zap, List, Activity,
} from 'lucide-react';

// ── Types ─────────────────────────────────────────────────────────────────────

interface FuturesTradeLog {
  id?: number;
  source?: string;
  eventType?: string;
  pair?: string;
  positionId?: string;
  orderId?: string;
  groupId?: string;
  side?: string;
  status?: string;
  orderType?: string;
  triggerType?: string;
  quantity?: number;
  price?: number;
  triggerPrice?: number;
  eventTimestamp?: number;
  channelName?: string;
  message?: string;
  rawData?: string;
  recordTimestamp?: string;
}

interface TradeLogStats {
  [key: string]: any;
}

type FilterMode = 'recent' | 'pair' | 'eventType' | 'positionId' | 'orderId' | 'all';

const KNOWN_PAIRS = [
  'B-BTC_USDT', 'B-ETH_USDT', 'B-XAU_USDT', 'B-XAG_USDT',
  'B-GOAT_USDT', 'B-TIA_USDT', 'B-PENDLE_USDT', 'B-LUNA2_USDT',
  'B-APT_USDT', 'B-BNB_USDT', 'B-LINK_USDT', 'B-SOL_USDT',
];

const EVENT_TYPES = [
  'ORDER_SUBMITTED', 'POSITION_OPENED', 'POSITION_CLOSED',
  'TAKE_PROFIT_SET', 'STOP_LOSS_SET', 'TAKE_PROFIT_TRIGGERED',
  'STOP_LOSS_TRIGGERED', 'TRIGGERED_ORDER', 'ORDER_FILLED',
  'POSITION_CLOSE_REQUESTED',
];

// ── Helpers ───────────────────────────────────────────────────────────────────

const fmtTime = (ts?: number | string): string => {
  if (!ts) return '—';
  const d = typeof ts === 'number' ? new Date(ts) : new Date(ts);
  return d.toLocaleString();
};

const fmtNum = (n?: number, decimals = 4): string => {
  if (n == null) return '—';
  return n.toLocaleString(undefined, { maximumFractionDigits: decimals });
};

const eventBadgeClass = (eventType?: string): string => {
  switch (eventType) {
    case 'POSITION_OPENED':       return 'badge badge-open';
    case 'POSITION_CLOSED':       return 'badge badge-closed';
    case 'TAKE_PROFIT_TRIGGERED':
    case 'TAKE_PROFIT_SET':       return 'badge badge-tp';
    case 'STOP_LOSS_TRIGGERED':
    case 'STOP_LOSS_SET':         return 'badge badge-sl';
    case 'ORDER_FILLED':          return 'badge badge-filled';
    case 'TRIGGERED_ORDER':       return 'badge badge-triggered';
    case 'ORDER_SUBMITTED':       return 'badge badge-submitted';
    default:                      return 'badge badge-default';
  }
};

// ── Component ─────────────────────────────────────────────────────────────────

const TradeLogPage: React.FC = () => {
  const [entries, setEntries] = useState<FuturesTradeLog[]>([]);
  const [stats, setStats] = useState<TradeLogStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Filter state
  const [filterMode, setFilterMode] = useState<FilterMode>('recent');
  const [recentLimit, setRecentLimit] = useState(50);
  const [pairInput, setPairInput] = useState('B-BTC_USDT');
  const [eventTypeInput, setEventTypeInput] = useState('POSITION_OPENED');
  const [positionIdInput, setPositionIdInput] = useState('');
  const [orderIdInput, setOrderIdInput] = useState('');

  // Local text search
  const [searchQuery, setSearchQuery] = useState('');
  const [showStats, setShowStats] = useState(false);

  // ── Data fetching ─────────────────────────────────────────────────────────

  const fetchStats = useCallback(async () => {
    try {
      const res = await publicApiClient.get('/api/futures/trade-log/stats');
      setStats(res.data);
    } catch {
      // stats are optional; silence the error
    }
  }, []);

  const fetchEntries = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      let url = '';
      switch (filterMode) {
        case 'recent':
          url = `/api/futures/trade-log/recent?limit=${recentLimit}`;
          break;
        case 'pair':
          if (!pairInput.trim()) { setError('Please enter a trading pair.'); setLoading(false); return; }
          url = `/api/futures/trade-log/pair/${encodeURIComponent(pairInput.trim())}`;
          break;
        case 'eventType':
          url = `/api/futures/trade-log/event-type/${encodeURIComponent(eventTypeInput)}`;
          break;
        case 'positionId':
          if (!positionIdInput.trim()) { setError('Please enter a position ID.'); setLoading(false); return; }
          url = `/api/futures/trade-log/position/${encodeURIComponent(positionIdInput.trim())}`;
          break;
        case 'orderId':
          if (!orderIdInput.trim()) { setError('Please enter an order ID.'); setLoading(false); return; }
          url = `/api/futures/trade-log/order/${encodeURIComponent(orderIdInput.trim())}`;
          break;
        case 'all':
          url = '/api/futures/trade-log/all';
          break;
      }
      const res = await publicApiClient.get(url);
      // API may return a single object or an array
      const data = Array.isArray(res.data) ? res.data : (res.data ? [res.data] : []);
      setEntries(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || 'Failed to load trade log.');
      setEntries([]);
    } finally {
      setLoading(false);
    }
  }, [filterMode, recentLimit, pairInput, eventTypeInput, positionIdInput, orderIdInput]);

  useEffect(() => {
    fetchEntries();
    fetchStats();
  }, []); // initial load only

  // ── Filtered display ──────────────────────────────────────────────────────

  const filteredEntries = searchQuery.trim()
    ? entries.filter(e => {
        const q = searchQuery.toLowerCase();
        return (
          e.pair?.toLowerCase().includes(q) ||
          e.eventType?.toLowerCase().includes(q) ||
          e.positionId?.toLowerCase().includes(q) ||
          e.orderId?.toLowerCase().includes(q) ||
          e.side?.toLowerCase().includes(q) ||
          e.status?.toLowerCase().includes(q) ||
          e.message?.toLowerCase().includes(q) ||
          String(e.id).includes(q)
        );
      })
    : entries;

  // ── Render ────────────────────────────────────────────────────────────────

  return (
    <div className="page trade-log-page">
      {/* ── Header ── */}
      <div className="page-header" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h1 style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
            <Activity size={24} /> Futures Trade Log
          </h1>
          <p style={{ color: 'var(--text-secondary)', margin: 0, fontSize: 14 }}>
            Append-only lifecycle events for futures positions and orders
          </p>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button
            className="btn btn-secondary"
            onClick={() => setShowStats(s => !s)}
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <BarChart2 size={16} /> {showStats ? 'Hide Stats' : 'Show Stats'}
          </button>
          <button
            className="btn btn-primary"
            onClick={fetchEntries}
            disabled={loading}
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <RefreshCw size={16} className={loading ? 'spin' : ''} /> Refresh
          </button>
        </div>
      </div>

      {/* ── Stats Panel ── */}
      {showStats && stats && (
        <div className="card" style={{ marginBottom: 16 }}>
          <h3 style={{ marginBottom: 12, display: 'flex', alignItems: 'center', gap: 6 }}><BarChart2 size={18} /> Aggregate Statistics</h3>
          <div className="stats-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: 12 }}>
            {Object.entries(stats).map(([key, val]) => (
              <div key={key} className="stat-card" style={{ background: 'var(--bg-secondary)', borderRadius: 8, padding: '10px 14px' }}>
                <div style={{ fontSize: 12, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: 1 }}>{key}</div>
                <div style={{ fontSize: 18, fontWeight: 600, marginTop: 4 }}>
                  {typeof val === 'object' ? JSON.stringify(val) : String(val)}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── Filter Controls ── */}
      <div className="card" style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          {/* Mode tabs */}
          <div style={{ display: 'flex', gap: 4, flexWrap: 'wrap' }}>
            {([
              { mode: 'recent', label: 'Recent', icon: <Clock size={14} /> },
              { mode: 'pair', label: 'By Pair', icon: <TrendingUp size={14} /> },
              { mode: 'eventType', label: 'By Event', icon: <Zap size={14} /> },
              { mode: 'positionId', label: 'By Position', icon: <List size={14} /> },
              { mode: 'orderId', label: 'By Order', icon: <CheckCircle size={14} /> },
              { mode: 'all', label: 'All', icon: <Activity size={14} /> },
            ] as { mode: FilterMode; label: string; icon: React.ReactNode }[]).map(({ mode, label, icon }) => (
              <button
                key={mode}
                className={`btn ${filterMode === mode ? 'btn-primary' : 'btn-secondary'}`}
                style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 13, padding: '6px 12px' }}
                onClick={() => setFilterMode(mode)}
              >
                {icon} {label}
              </button>
            ))}
          </div>
        </div>

        {/* Mode-specific inputs */}
        <div style={{ marginTop: 12, display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          {filterMode === 'recent' && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <label style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Limit:</label>
              <select
                className="input"
                value={recentLimit}
                onChange={e => setRecentLimit(Number(e.target.value))}
                style={{ width: 100 }}
              >
                {[20, 50, 100, 200, 500].map(n => (
                  <option key={n} value={n}>{n}</option>
                ))}
              </select>
            </div>
          )}

          {filterMode === 'pair' && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <label style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Pair:</label>
              <select
                className="input"
                value={pairInput}
                onChange={e => setPairInput(e.target.value)}
                style={{ width: 180 }}
              >
                {KNOWN_PAIRS.map(p => <option key={p} value={p}>{p}</option>)}
              </select>
              <input
                className="input"
                placeholder="or type custom pair…"
                value={pairInput}
                onChange={e => setPairInput(e.target.value)}
                style={{ width: 180 }}
              />
            </div>
          )}

          {filterMode === 'eventType' && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <label style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Event Type:</label>
              <select
                className="input"
                value={eventTypeInput}
                onChange={e => setEventTypeInput(e.target.value)}
                style={{ width: 240 }}
              >
                {EVENT_TYPES.map(et => <option key={et} value={et}>{et}</option>)}
              </select>
            </div>
          )}

          {filterMode === 'positionId' && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <label style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Position ID:</label>
              <input
                className="input"
                placeholder="Enter position ID…"
                value={positionIdInput}
                onChange={e => setPositionIdInput(e.target.value)}
                style={{ width: 280 }}
              />
            </div>
          )}

          {filterMode === 'orderId' && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <label style={{ fontSize: 13, color: 'var(--text-secondary)' }}>Order ID:</label>
              <input
                className="input"
                placeholder="Enter order ID…"
                value={orderIdInput}
                onChange={e => setOrderIdInput(e.target.value)}
                style={{ width: 280 }}
              />
            </div>
          )}

          {filterMode === 'all' && (
            <span style={{ fontSize: 13, color: 'var(--text-secondary)' }}>
              Fetches all records — may be large on active accounts.
            </span>
          )}

          <button
            className="btn btn-primary"
            onClick={fetchEntries}
            disabled={loading}
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <Filter size={14} /> Apply Filter
          </button>
        </div>
      </div>

      {/* ── Local search ── */}
      <div style={{ marginBottom: 12, display: 'flex', alignItems: 'center', gap: 8 }}>
        <Search size={16} style={{ color: 'var(--text-secondary)' }} />
        <input
          className="input"
          placeholder="Search results (pair, event type, ID, message…)"
          value={searchQuery}
          onChange={e => setSearchQuery(e.target.value)}
          style={{ maxWidth: 400 }}
        />
        <span style={{ fontSize: 13, color: 'var(--text-secondary)' }}>
          {filteredEntries.length} / {entries.length} entries
        </span>
      </div>

      {/* ── Status ── */}
      {loading && <Loading />}
      {error && <ErrorMessage message={error} onClose={() => setError(null)} />}

      {/* ── Table ── */}
      {!loading && !error && filteredEntries.length === 0 && (
        <div className="card" style={{ textAlign: 'center', color: 'var(--text-secondary)', padding: 40 }}>
          <AlertCircle size={32} style={{ marginBottom: 8 }} />
          <p>No trade log entries found for the selected filter.</p>
        </div>
      )}

      {!loading && filteredEntries.length > 0 && (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <div className="table-container" style={{ overflowX: 'auto' }}>
            <table className="data-table" style={{ minWidth: 1100 }}>
              <thead>
                <tr>
                  <th style={{ width: 60 }}>ID</th>
                  <th>Event Type</th>
                  <th>Pair</th>
                  <th>Side</th>
                  <th>Order Type</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Price</th>
                  <th style={{ textAlign: 'right' }}>Quantity</th>
                  <th style={{ textAlign: 'right' }}>Trigger Price</th>
                  <th>Position ID</th>
                  <th>Order ID</th>
                  <th>Event Time</th>
                  <th>Source</th>
                  <th>Message</th>
                </tr>
              </thead>
              <tbody>
                {filteredEntries.map((entry, idx) => (
                  <tr key={entry.id ?? idx}>
                    <td style={{ fontSize: 12, color: 'var(--text-secondary)' }}>{entry.id ?? '—'}</td>
                    <td>
                      <span className={eventBadgeClass(entry.eventType)} style={{ fontSize: 11 }}>
                        {entry.eventType ?? '—'}
                      </span>
                    </td>
                    <td style={{ fontWeight: 600 }}>{entry.pair ?? '—'}</td>
                    <td>
                      {entry.side ? (
                        <span style={{
                          color: entry.side.toLowerCase() === 'buy' ? 'var(--color-green, #22c55e)' : 'var(--color-red, #ef4444)',
                          display: 'flex', alignItems: 'center', gap: 4, fontSize: 13,
                        }}>
                          {entry.side.toLowerCase() === 'buy' ? <TrendingUp size={13} /> : <TrendingDown size={13} />}
                          {entry.side.toUpperCase()}
                        </span>
                      ) : '—'}
                    </td>
                    <td style={{ fontSize: 12 }}>{entry.orderType ?? '—'}</td>
                    <td style={{ fontSize: 12 }}>{entry.status ?? '—'}</td>
                    <td style={{ textAlign: 'right', fontFamily: 'monospace' }}>{fmtNum(entry.price)}</td>
                    <td style={{ textAlign: 'right', fontFamily: 'monospace' }}>{fmtNum(entry.quantity, 6)}</td>
                    <td style={{ textAlign: 'right', fontFamily: 'monospace' }}>{fmtNum(entry.triggerPrice)}</td>
                    <td style={{ fontSize: 11, color: 'var(--text-secondary)', maxWidth: 120, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} title={entry.positionId}>
                      {entry.positionId ?? '—'}
                    </td>
                    <td style={{ fontSize: 11, color: 'var(--text-secondary)', maxWidth: 120, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} title={entry.orderId}>
                      {entry.orderId ?? '—'}
                    </td>
                    <td style={{ fontSize: 11, whiteSpace: 'nowrap' }}>
                      {entry.eventTimestamp ? fmtTime(entry.eventTimestamp) : fmtTime(entry.recordTimestamp)}
                    </td>
                    <td style={{ fontSize: 11, color: 'var(--text-secondary)' }}>{entry.source ?? '—'}</td>
                    <td style={{ fontSize: 11, maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} title={entry.message}>
                      {entry.message ?? '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ── Inline styles for badges ── */}
      <style>{`
        .trade-log-page .badge {
          display: inline-block;
          padding: 2px 8px;
          border-radius: 9999px;
          font-weight: 600;
          font-size: 11px;
          white-space: nowrap;
        }
        .trade-log-page .badge-open       { background: #16a34a22; color: #16a34a; }
        .trade-log-page .badge-closed     { background: #6b728022; color: #6b7280; }
        .trade-log-page .badge-tp         { background: #2563eb22; color: #2563eb; }
        .trade-log-page .badge-sl         { background: #dc262622; color: #dc2626; }
        .trade-log-page .badge-filled     { background: #059669 22; color: #059669; }
        .trade-log-page .badge-triggered  { background: #d9770622; color: #d97706; }
        .trade-log-page .badge-submitted  { background: #7c3aed22; color: #7c3aed; }
        .trade-log-page .badge-default    { background: #37415122; color: #374151; }
        .trade-log-page .spin {
          animation: spin 1s linear infinite;
        }
        @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
        .trade-log-page .data-table th,
        .trade-log-page .data-table td {
          padding: 8px 12px;
          border-bottom: 1px solid var(--border-color, #e5e7eb);
          vertical-align: middle;
        }
        .trade-log-page .data-table thead th {
          background: var(--bg-secondary, #f9fafb);
          font-size: 11px;
          text-transform: uppercase;
          letter-spacing: 0.5px;
          color: var(--text-secondary, #6b7280);
          position: sticky;
          top: 0;
          z-index: 1;
        }
        .trade-log-page .data-table tbody tr:hover {
          background: var(--hover-bg, rgba(0,0,0,0.03));
        }
      `}</style>
    </div>
  );
};

export default TradeLogPage;
