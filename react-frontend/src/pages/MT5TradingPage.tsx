import React, { useState, useEffect, useCallback, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiService } from '../services/api';
import { useTradeConfig } from '../context/TradeConfigContext';
import {
  Activity, RefreshCw, TrendingUp, TrendingDown, DollarSign, Zap,
  CheckCircle, AlertTriangle, X, BookOpen, Settings2,
} from 'lucide-react';

// ── Constants ──────────────────────────────────────────────────────────────
export const MT5_CONTRACTS = ['XAUUSD', 'XAGUSD', 'XNGUSD'];

type OrderSide = 'buy' | 'sell';
type OrderType = 'market' | 'limit';
type ActiveTab = 'positions' | 'orders' | 'trade-log';

// ── Types ──────────────────────────────────────────────────────────────────
interface Mt5Balance {
  balance?: number;
  currency?: string;
  equity?: number;
  free_margin?: number;
  leverage?: number;
  margin?: number;
  margin_level?: number;
  profit?: number;
  ok?: boolean;
}

interface FuturesPosition {
  id?: string;
  positionId?: string;
  market?: string;
  pair?: string;
  side?: string;
  size?: number;
  entry_price?: number;
  entryPrice?: number;
  mark_price?: number;
  markPrice?: number;
  current_price?: number;
  unrealized_pnl?: number;
  unrealizedPnl?: number;
  pnl?: number;
  leverage?: number;
  liq_price?: number;
  liquidationPrice?: number;
  margin?: number;
  [key: string]: any;
}

interface FuturesOrder {
  id?: string;
  orderId?: string;
  order_id?: string;
  market?: string;
  pair?: string;
  side?: string;
  order_type?: string;
  orderType?: string;
  price?: number;
  quantity?: number;
  total_quantity?: number;
  totalQuantity?: number;
  stop_price?: number;
  stopPrice?: number;
  take_profit_price?: number;
  takeProfitPrice?: number;
  status?: string;
  [key: string]: any;
}

interface TradeLogEntry {
  positionId?: string;
  pair: string;
  side: string;
  status: string;
  entryPrice?: number;
  exitPrice?: number;
  quantity?: number;
  leverage?: number;
  margin?: number;
  tpPrice?: number;
  slPrice?: number;
  tpHit?: boolean;
  slHit?: boolean;
  liquidated?: boolean;
  realizedPnl?: number;
  roi?: number;
  fees?: number;
  netPnl?: number;
  timestamp?: string | number;
}

interface OrderForm {
  contract: string;
  side: OrderSide;
  orderType: OrderType;
  quantity: string;
  price: string;
}

// ── Style helpers ──────────────────────────────────────────────────────────
const cardStyle: React.CSSProperties = {
  background: '#1e293b',
  borderRadius: '0.5rem',
  border: '1px solid #334155',
  padding: '1rem',
};

const labelStyle: React.CSSProperties = {
  fontSize: '0.72rem',
  color: '#64748b',
  marginBottom: '0.2rem',
  textTransform: 'uppercase' as const,
  letterSpacing: '0.04em',
};

const valueStyle: React.CSSProperties = { fontSize: '1rem', fontWeight: 700, color: '#f1f5f9' };

const inputStyle: React.CSSProperties = {
  width: '100%', padding: '0.5rem 0.75rem', background: '#0f172a',
  border: '1px solid #334155', borderRadius: '0.375rem', color: '#f1f5f9',
  fontSize: '0.875rem', outline: 'none', boxSizing: 'border-box' as const,
};

const tabBtnStyle = (active: boolean, color = '#3b82f6'): React.CSSProperties => ({
  flex: 1, padding: '0.5rem', border: 'none', borderRadius: '0.375rem', cursor: 'pointer',
  fontWeight: 600, fontSize: '0.875rem', transition: 'all 0.15s',
  background: active ? color : 'transparent',
  color: active ? '#fff' : '#64748b',
});

const fmt = (v: number | string | undefined | null, decimals = 2) => {
  if (v === undefined || v === null || v === '') return '—';
  const n = Number(v);
  return isNaN(n) ? String(v) : n.toLocaleString(undefined, { minimumFractionDigits: decimals, maximumFractionDigits: decimals });
};

const pnlColor = (v: number | undefined) =>
  v === undefined ? '#94a3b8' : v >= 0 ? '#10b981' : '#ef4444';

// ── Component ──────────────────────────────────────────────────────────────
const MT5TradingPage: React.FC = () => {
  const navigate = useNavigate();
  const { mt5Config: config } = useTradeConfig();

  const [mt5Balance, setMt5Balance] = useState<Mt5Balance | null>(null);
  const [positions, setPositions] = useState<FuturesPosition[]>([]);
  const [activeOrders, setActiveOrders] = useState<FuturesOrder[]>([]);
  const [tradeLog, setTradeLog] = useState<TradeLogEntry[]>([]);
  const [pnlTotals, setPnlTotals] = useState<{ realizedUsdt: number; unrealizedUsdt: number } | null>(null);
  const [latestPrice, setLatestPrice] = useState<number | null>(null);
  const [priceLoading, setPriceLoading] = useState(false);
  const priceContractRef = useRef<string>('');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [orderMsg, setOrderMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [activeTab, setActiveTab] = useState<ActiveTab>('positions');
  const [tradeLogLoading, setTradeLogLoading] = useState(false);
  const [form, setForm] = useState<OrderForm>({
    contract: MT5_CONTRACTS[0],
    side: 'buy',
    orderType: 'market',
    quantity: '',
    price: '',
  });

  // ── Fetchers ──────────────────────────────────────────────────────────────
  const fetchMt5Balance = useCallback(async () => {
    try {
      const res = await apiService.getWsCurrentBalance('USDT');
      const data = res.data;
      if (data?.mt5) {
        setMt5Balance(data.mt5);
      } else if (typeof data === 'object' && data !== null && !Array.isArray(data)) {
        setMt5Balance(data as Mt5Balance);
      }
    } catch (e) { console.error('MT5 balance fetch error:', e); }
  }, []);

  const fetchLeverage = useCallback(async () => {
    try {
      const res = await apiService.getFuturesLeverage();
      const data = res.data;
      if (data?.mt5?.leverage != null) {
        setMt5Balance(prev =>
          prev ? { ...prev, leverage: data.mt5.leverage } : { leverage: data.mt5.leverage },
        );
      }
    } catch (e) { console.error('MT5 leverage fetch error:', e); }
  }, []);

  const fetchPositions = useCallback(async () => {
    try {
      const res = await apiService.getWsPositionsByStatus('open');
      const data = res.data;
      const all: FuturesPosition[] = Array.isArray(data)
        ? data
        : Array.isArray(data?.positions) ? data.positions
        : Array.isArray(data?.data) ? data.data : [];
      setPositions(all.filter(p => {
        const pair = (p.pair ?? p.market ?? '').toUpperCase();
        return MT5_CONTRACTS.some(c => pair.includes(c.replace('USD', '')));
      }));
    } catch (e) { console.error('Positions fetch error:', e); setPositions([]); }
  }, []);

  const fetchOrders = useCallback(async () => {
    try {
      const res = await apiService.getWsOrdersByPairAndStatus(form.contract, 'open');
      const data = res.data;
      const orders = Array.isArray(data) ? data
        : Array.isArray(data?.orders) ? data.orders
        : Array.isArray(data?.data) ? data.data : [];
      setActiveOrders(orders);
    } catch (e) { console.error('Orders fetch error:', e); setActiveOrders([]); }
  }, [form.contract]);

  const fetchLatestPrice = useCallback(async (contract: string) => {
    priceContractRef.current = contract;
    setPriceLoading(true);
    const commit = (price: number | null) => {
      if (priceContractRef.current !== contract) return;
      setLatestPrice(price);
      setPriceLoading(false);
    };
    try {
      const res = await apiService.getFuturesInstrumentPriceLatest(contract);
      if (priceContractRef.current !== contract) return;
      const data = res.data;
      // MT5 shape: mid of ask/bid
      if (data?.ask != null || data?.bid != null) {
        const ask = Number(data.ask ?? 0);
        const bid = Number(data.bid ?? 0);
        if (ask > 0 && bid > 0) { commit((ask + bid) / 2); return; }
        if (ask > 0) { commit(ask); return; }
        if (bid > 0) { commit(bid); return; }
      }
    } catch { /* fall through */ }
    commit(null);
  }, []);

  const fetchTradeLog = useCallback(async () => {
    setTradeLogLoading(true);
    try {
      const [ordersRes, unrealPnlRes] = await Promise.allSettled([
        apiService.getWsRecentOrders(200),
        apiService.getWsTotalUnrealizedPnl(),
      ]);
      const allOrders: any[] = ordersRes.status === 'fulfilled' && Array.isArray(ordersRes.value.data)
        ? ordersRes.value.data : [];

      // Filter to MT5 contracts only
      const mt5Orders = allOrders.filter(o => {
        const p = (o.pair ?? '').toUpperCase();
        return MT5_CONTRACTS.some(c => p.includes(c.replace('USD', '')));
      });

      const entryOrders = mt5Orders
        .filter(o => o.stage === 'default' && o.status === 'filled')
        .sort((a, b) => (a.createdAt ?? 0) - (b.createdAt ?? 0));

      const exitOrders = mt5Orders
        .filter(o => ['exit', 'tpsl_exit', 'liquidate'].includes(o.stage ?? '') && o.status === 'filled')
        .sort((a, b) => (b.createdAt ?? 0) - (a.createdAt ?? 0));

      const entryByPair = new Map<string, any[]>();
      for (const o of entryOrders) {
        if (!entryByPair.has(o.pair)) entryByPair.set(o.pair, []);
        entryByPair.get(o.pair)!.push(o);
      }
      const entryPointer = new Map<string, number>();

      const entries: TradeLogEntry[] = exitOrders.map(exit => {
        const pair: string = exit.pair ?? '—';
        const pool = entryByPair.get(pair) ?? [];
        const ptr = entryPointer.get(pair) ?? 0;
        const entry = pool[ptr];
        entryPointer.set(pair, ptr + 1);

        const entryAvg: number | undefined = entry?.avgPrice || entry?.price || undefined;
        const exitAvg: number | undefined = exit.avgPrice || exit.price || undefined;
        const qty: number | undefined = exit.filledQuantity || exit.totalQuantity || undefined;
        const exitSide = (exit.side ?? '').toLowerCase();
        const isLong = exitSide === 'sell';
        const leverage: number | undefined = (entry?.leverage && entry.leverage !== 1.0)
          ? entry.leverage : exit.leverage || undefined;
        const margin: number | undefined = entry?.idealMargin || undefined;
        const tpPrice: number | undefined = entry?.takeProfitPrice || undefined;
        const slPrice: number | undefined = entry?.stopLossPrice || undefined;
        const exitType = (exit.orderType ?? '').toLowerCase();
        const tpHit = exit.stage === 'tpsl_exit' && exitType.includes('take_profit');
        const slHit = exit.stage === 'tpsl_exit' && exitType.includes('stop_');
        const liquidated = exit.stage === 'liquidate';
        const exitFees = Number(exit.feeAmount) || 0;
        const entryFees = Number(entry?.feeAmount) || 0;
        const totalFees = exitFees + entryFees;
        let grossPnl: number | undefined;
        if (entryAvg && exitAvg && qty) {
          grossPnl = isLong ? (exitAvg - entryAvg) * qty : (entryAvg - exitAvg) * qty;
        }
        const netPnl = grossPnl !== undefined ? grossPnl - totalFees : undefined;
        const roi = grossPnl !== undefined && margin ? (grossPnl / margin) * 100 : undefined;
        return { positionId: exit.orderId, pair, side: isLong ? 'buy' : 'sell', status: exit.stage,
          entryPrice: entryAvg, exitPrice: exitAvg, quantity: qty, leverage, margin, tpPrice, slPrice,
          tpHit, slHit, liquidated, realizedPnl: grossPnl, roi, fees: totalFees, netPnl, timestamp: exit.createdAt };
      });

      const totalRealized = entries.reduce((s, e) => s + (e.realizedPnl ?? 0), 0);
      const totalUnrealized = unrealPnlRes.status === 'fulfilled'
        ? (() => { const u = unrealPnlRes.value.data as any; return u?.USDT ?? u?.usdt ?? Object.values(u ?? {})[0] ?? 0; })()
        : 0;
      setPnlTotals({ realizedUsdt: totalRealized, unrealizedUsdt: Number(totalUnrealized) });
      setTradeLog(entries);
    } catch (e) { console.error('Trade log fetch error:', e); }
    finally { setTradeLogLoading(false); }
  }, []);

  const fetchAll = useCallback(async (showLoad = false) => {
    if (showLoad) setLoading(true); else setRefreshing(true);
    setError(null);
    try {
      await Promise.all([fetchMt5Balance(), fetchLeverage(), fetchPositions(), fetchOrders(), fetchTradeLog()]);
    } catch (e: any) { setError(e.message ?? 'Failed to fetch data'); }
    finally { setLoading(false); setRefreshing(false); }
  }, [fetchMt5Balance, fetchLeverage, fetchPositions, fetchOrders, fetchTradeLog]);

  useEffect(() => { fetchAll(true); }, []); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    fetchLatestPrice(form.contract);
    fetchOrders();
  }, [form.contract]); // eslint-disable-line react-hooks/exhaustive-deps

  // ── Derived ───────────────────────────────────────────────────────────────
  const refPrice = form.orderType === 'market' ? latestPrice : (Number(form.price) || latestPrice);
  const isLong = form.side === 'buy';
  const tp1Price = refPrice ? (isLong ? refPrice + config.tp1Pips : refPrice - config.tp1Pips) : null;
  const tp2Price = refPrice ? (isLong ? refPrice + config.tp2Pips : refPrice - config.tp2Pips) : null;
  const tp3Price = refPrice ? (isLong ? refPrice + config.tp3Pips : refPrice - config.tp3Pips) : null;
  const slAutoPrice = refPrice ? (isLong ? refPrice - config.slPips : refPrice + config.slPips) : null;

  const estimatedCost = (() => {
    const qty = Number(form.quantity);
    const price = form.orderType === 'market' ? (latestPrice ?? 0) : Number(form.price);
    if (!qty || !price) return null;
    return ((qty * price) / config.leverage).toFixed(2);
  })();

  const handleAutoFillQty = () => {
    setForm(f => ({ ...f, quantity: config.quantity.toString() }));
  };

  // ── Order placement ───────────────────────────────────────────────────────
  const handlePlaceOrder = async () => {
    let qty = Number(form.quantity);
    let currentPrice = latestPrice;

    if (!currentPrice && form.orderType === 'market') {
      try {
        const res = await apiService.getFuturesMarketDataPublic(form.contract, 20);
        const arr: any[] = Array.isArray(res.data) ? res.data : [];
        const rec = [...arr].reverse().find(r => r.markPrice != null && Number(r.markPrice) > 0);
        const p = rec?.markPrice ?? rec?.price ?? rec?.mark_price;
        if (p && Number(p) > 0) { currentPrice = Number(p); setLatestPrice(currentPrice); }
      } catch (_) {}
    }

    if (!qty || qty <= 0) {
      if (config.quantity > 0) {
        qty = config.quantity;
        setForm(f => ({ ...f, quantity: qty.toString() }));
      } else {
        setOrderMsg({ type: 'error', text: 'Enter a valid quantity (or set Config Quantity in Trade Config)' });
        return;
      }
    }
    if (form.orderType === 'limit' && (!form.price || Number(form.price) <= 0)) {
      setOrderMsg({ type: 'error', text: 'Enter a valid limit price' });
      return;
    }

    const resolvedPrice = form.orderType === 'market' ? currentPrice : (Number(form.price) || currentPrice);
    const isBuy = form.side === 'buy';
    const cTp1 = resolvedPrice ? (isBuy ? resolvedPrice + config.tp1Pips : resolvedPrice - config.tp1Pips) : null;
    const cTp2 = resolvedPrice ? (isBuy ? resolvedPrice + config.tp2Pips : resolvedPrice - config.tp2Pips) : null;
    const cTp3 = resolvedPrice ? (isBuy ? resolvedPrice + config.tp3Pips : resolvedPrice - config.tp3Pips) : null;
    const cSl  = resolvedPrice ? (isBuy ? resolvedPrice - config.slPips  : resolvedPrice + config.slPips)  : null;

    setSubmitting(true);
    setOrderMsg(null);
    const contract = form.contract;
    const side = form.side;

    try {
      const payload: any = {
        market: contract, side, order_type: form.orderType, quantity: qty,
        leverage: config.leverage,
        ...(form.orderType === 'limit' ? { price: Number(form.price) } : {}),
      };
      await apiService.createFuturesOrderPublic(payload);
      setOrderMsg({ type: 'success', text: `${side === 'buy' ? 'Long' : 'Short'} order placed! Setting TP/SL…` });
      setForm(f => ({ ...f, quantity: '', price: '' }));

      const tpQty = Number((qty / 3).toFixed(4));
      const tp3Qty = Number((qty - tpQty * 2).toFixed(4));
      if (cTp1 || cTp2 || cTp3 || cSl) {
        const RETRY_DELAYS = [800, 1500, 2000, 2000, 2000, 3000, 3000, 3000, 3000, 3000];
        let posActive = false;
        for (let a = 0; a < RETRY_DELAYS.length; a++) {
          await new Promise(r => setTimeout(r, RETRY_DELAYS[a]));
          try {
            await apiService.createTpSlPublic({ market: contract, take_profit_price: Number(cTp1!.toFixed(2)), quantity: tpQty });
            posActive = true;
            break;
          } catch (err: any) {
            const msg = err.response?.data?.error ?? err.response?.data?.message ?? err.message ?? '';
            if (!msg.toLowerCase().match(/no active|not found|pending/) || a === RETRY_DELAYS.length - 1) {
              setOrderMsg({ type: 'success', text: `Order placed, but TP1 failed: ${msg}` });
              break;
            }
          }
        }
        if (posActive) {
          const rest = [
            cSl  ? { label: 'SL',  stop_loss_price:   Number(cSl.toFixed(2)),  quantity: qty    } : null,
            cTp2 ? { label: 'TP2', take_profit_price: Number(cTp2.toFixed(2)), quantity: tpQty  } : null,
            cTp3 ? { label: 'TP3', take_profit_price: Number(cTp3.toFixed(2)), quantity: tp3Qty } : null,
          ].filter(Boolean) as any[];
          const results = ['TP1 ✓'];
          for (const { label, ...p } of rest) {
            try { await apiService.createTpSlPublic({ market: contract, ...p }); results.push(`${label} ✓`); }
            catch (err: any) { results.push(`${label} ✗`); }
          }
          setOrderMsg({ type: 'success', text: `${side === 'buy' ? 'Long' : 'Short'} opened — ${results.join('  ')}` });
        }
      }
      fetchAll();
    } catch (e: any) {
      setOrderMsg({ type: 'error', text: e.response?.data?.message ?? e.message ?? 'Order failed' });
    } finally { setSubmitting(false); }
  };

  const handleCancelOrder = async (orderId: string) => {
    try {
      await apiService.cancelFuturesOrderPublic(orderId);
      fetchOrders();
    } catch (e: any) { setError(e.response?.data?.message ?? 'Failed to cancel'); }
  };

  const handleClosePosition = async (pos: FuturesPosition) => {
    try {
      const market = pos.market ?? pos.pair;
      await apiService.closeFuturesPositionPublic({ market, size: pos.size });
      fetchPositions();
    } catch (e: any) { setError(e.response?.data?.message ?? 'Failed to close position'); }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '60vh' }}>
        <div style={{ textAlign: 'center', color: '#64748b' }}>
          <Activity size={32} style={{ marginBottom: '0.75rem', opacity: 0.6 }} />
          <div>Loading MT5 data…</div>
        </div>
      </div>
    );
  }

  const profit = mt5Balance?.profit ?? 0;
  const marginLevel = mt5Balance?.margin_level;

  return (
    <div style={{ maxWidth: 1400, margin: '0 auto', padding: '1rem 1.5rem' }}>

      {/* ── Header ── */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Zap size={22} style={{ color: '#818cf8' }} />
          <h1 style={{ margin: 0, fontSize: '1.4rem', fontWeight: 700, color: '#f1f5f9' }}>MT5 Trading</h1>
          {mt5Balance?.currency && (
            <span style={{ background: '#312e81', color: '#818cf8', borderRadius: '0.375rem', padding: '0.1rem 0.5rem', fontSize: '0.75rem', fontWeight: 600 }}>
              {mt5Balance.currency}
            </span>
          )}
        </div>
        <button
          onClick={() => fetchAll(false)} disabled={refreshing}
          style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.375rem', color: '#94a3b8', cursor: refreshing ? 'default' : 'pointer', padding: '0.4rem 0.75rem', fontSize: '0.85rem' }}
        >
          <RefreshCw size={15} style={{ animation: refreshing ? 'spin 1s linear infinite' : 'none' }} />
          Refresh
        </button>
      </div>

      {error && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.6rem 0.75rem', borderRadius: '0.375rem', marginBottom: '0.75rem', background: 'rgba(239,68,68,0.12)', border: '1px solid #ef4444', color: '#ef4444', fontSize: '0.85rem' }}>
          <AlertTriangle size={14} />
          {error}
          <button onClick={() => setError(null)} style={{ marginLeft: 'auto', background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer' }}><X size={14} /></button>
        </div>
      )}

      {/* ── MT5 Wallet bar ── */}
      <div style={{ ...cardStyle, marginBottom: '1rem', padding: '0.75rem 1rem' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.25rem', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <DollarSign size={15} style={{ color: '#818cf8' }} />
            <span style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>MT5 Wallet</span>
          </div>
          {mt5Balance ? (
            <>
              {[
                { label: 'Balance',     val: fmt(mt5Balance.balance),     color: '#f1f5f9' },
                { label: 'Equity',      val: fmt(mt5Balance.equity),      color: '#f1f5f9' },
                { label: 'Free Margin', val: fmt(mt5Balance.free_margin), color: '#10b981' },
                { label: 'Margin Used', val: fmt(mt5Balance.margin),      color: '#f59e0b' },
              ].map(({ label, val, color }) => (
                <div key={label}>
                  <div style={labelStyle}>{label}</div>
                  <div style={{ ...valueStyle, fontSize: '0.95rem', color }}>{val}</div>
                </div>
              ))}
              {marginLevel != null && (
                <div>
                  <div style={labelStyle}>Margin Level</div>
                  <div style={{ ...valueStyle, fontSize: '0.95rem', color: marginLevel >= 200 ? '#10b981' : marginLevel >= 100 ? '#f59e0b' : '#ef4444' }}>
                    {fmt(marginLevel, 1)}%
                  </div>
                </div>
              )}
              <div>
                <div style={labelStyle}>Float P&amp;L</div>
                <div style={{ ...valueStyle, fontSize: '0.95rem', color: profit >= 0 ? '#10b981' : '#ef4444' }}>
                  {profit >= 0 ? '+' : ''}{fmt(profit)}
                </div>
              </div>
              <div style={{ background: '#0f172a', border: '1px solid #4338ca', borderRadius: '0.375rem', padding: '0.35rem 0.75rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                <Zap size={13} style={{ color: '#818cf8' }} />
                <span style={{ fontSize: '0.75rem', color: '#64748b' }}>Leverage</span>
                <span style={{ fontSize: '1rem', fontWeight: 800, color: '#818cf8' }}>
                  {mt5Balance.leverage != null ? `1:${mt5Balance.leverage}` : '—'}
                </span>
              </div>
            </>
          ) : (
            <span style={{ color: '#64748b', fontStyle: 'italic', fontSize: '0.85rem' }}>No MT5 balance data</span>
          )}
          {latestPrice && (
            <div style={{ marginLeft: 'auto' }}>
              <div style={labelStyle}>{form.contract} Price</div>
              <div style={{ fontSize: '1.05rem', fontWeight: 700, color: '#f59e0b' }}>${fmt(latestPrice, 2)}</div>
            </div>
          )}
        </div>
      </div>

      {/* ── Two-column layout ── */}
      <div style={{ display: 'grid', gridTemplateColumns: '320px 1fr', gap: '1rem', alignItems: 'start' }}>

        {/* ── LEFT: Order Form ── */}
        <div style={cardStyle}>
          <h2 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem', color: '#f1f5f9' }}>Place Order</h2>

          {/* Config strip */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#0f172a', borderRadius: '0.375rem', padding: '0.4rem 0.65rem', marginBottom: '0.85rem', fontSize: '0.75rem', color: '#64748b', gap: '0.5rem' }}>
            <span><strong style={{ color: '#f59e0b' }}>{config.quantity} lots</strong> · <strong style={{ color: '#f59e0b' }}>{config.leverage}×</strong> · SL <strong style={{ color: '#ef4444' }}>{config.slPips}p</strong></span>
            <Link to="/trade-config" style={{ color: '#3b82f6', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.2rem', flexShrink: 0 }}>
              <Settings2 size={11} /> Edit
            </Link>
          </div>

          {/* Contract */}
          <div style={{ marginBottom: '0.75rem' }}>
            <div style={labelStyle}>Contract</div>
            <select value={form.contract} onChange={e => { setLatestPrice(null); setForm(f => ({ ...f, contract: e.target.value })); }} style={{ ...inputStyle, cursor: 'pointer' }}>
              {MT5_CONTRACTS.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
            <div style={{ marginTop: '0.35rem', minHeight: '1.2rem' }}>
              {priceLoading
                ? <span style={{ fontSize: '0.75rem', color: '#64748b' }}>Fetching price…</span>
                : latestPrice != null
                  ? <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#f59e0b' }}>Mark: ${latestPrice.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}</span>
                  : <span style={{ fontSize: '0.75rem', color: '#64748b' }}>No mark price</span>
              }
            </div>
          </div>

          {/* Buy / Sell */}
          <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.75rem' }}>
            <button style={tabBtnStyle(form.side === 'buy', '#10b981')} onClick={() => setForm(f => ({ ...f, side: 'buy' }))}>
              <TrendingUp size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} /> Long / Buy
            </button>
            <button style={tabBtnStyle(form.side === 'sell', '#ef4444')} onClick={() => setForm(f => ({ ...f, side: 'sell' }))}>
              <TrendingDown size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} /> Short / Sell
            </button>
          </div>

          {/* Market / Limit */}
          <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.75rem' }}>
            {(['market', 'limit'] as OrderType[]).map(t => (
              <button key={t} onClick={() => setForm(f => ({ ...f, orderType: t }))} style={{ ...tabBtnStyle(form.orderType === t, '#3b82f6'), flex: 'none', padding: '0.35rem 0.75rem', borderRadius: '999px', border: `1px solid ${form.orderType === t ? '#3b82f6' : '#334155'}`, fontSize: '0.8rem' }}>
                {t.charAt(0).toUpperCase() + t.slice(1)}
              </button>
            ))}
          </div>

          {/* Leverage (read-only) */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#0f172a', borderRadius: '0.375rem', padding: '0.45rem 0.75rem', marginBottom: '0.75rem' }}>
            <span style={{ fontSize: '0.8rem', color: '#64748b' }}>Leverage</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <span style={{ fontSize: '0.95rem', fontWeight: 700, color: '#f59e0b' }}>{config.leverage}×</span>
              <Link to="/trade-config" style={{ fontSize: '0.72rem', color: '#3b82f6', textDecoration: 'none' }}>Change</Link>
            </div>
          </div>

          {/* Limit price */}
          {form.orderType === 'limit' && (
            <div style={{ marginBottom: '0.75rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem' }}>
                <span style={labelStyle}>Limit Price</span>
                {latestPrice && (
                  <button onClick={() => setForm(f => ({ ...f, price: String(latestPrice) }))} style={{ fontSize: '0.7rem', color: '#3b82f6', background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}>
                    Mark: ${fmt(latestPrice, 2)}
                  </button>
                )}
              </div>
              <input type="number" min={0} step="any" placeholder="0.00" value={form.price} onChange={e => setForm(f => ({ ...f, price: e.target.value }))} style={inputStyle} />
            </div>
          )}

          {/* Quantity */}
          <div style={{ marginBottom: '0.75rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem' }}>
              <span style={labelStyle}>Quantity (contracts)</span>
              {refPrice && (
                <button onClick={handleAutoFillQty} style={{ fontSize: '0.7rem', color: '#f59e0b', background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}>
                  Auto ({config.quantity} lots)
                </button>
              )}
            </div>
            <input type="number" min={0} step="any" placeholder="0.00" value={form.quantity} onChange={e => setForm(f => ({ ...f, quantity: e.target.value }))} style={inputStyle} />
          </div>

          {/* Cost + TP/SL preview */}
          <div style={{ background: '#0f172a', borderRadius: '0.375rem', padding: '0.5rem 0.75rem', marginBottom: '0.75rem', fontSize: '0.8rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748b', marginBottom: '0.25rem' }}>
              <span>Est. Margin</span>
              <span style={{ color: '#f1f5f9', fontWeight: 600 }}>{estimatedCost ? `${estimatedCost} USDT` : '—'}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748b', marginBottom: '0.25rem' }}>
              <span>Config qty</span>
              <span style={{ color: '#f59e0b', fontWeight: 600 }}>{config.quantity} lots</span>
            </div>
          </div>

          <div style={{ background: '#0f172a', borderRadius: '0.375rem', padding: '0.5rem 0.75rem', marginBottom: '0.75rem', fontSize: '0.8rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
              <span style={{ color: '#64748b', fontWeight: 600 }}>Auto TP / SL</span>
              <Link to="/trade-config" style={{ fontSize: '0.72rem', color: '#3b82f6', textDecoration: 'none' }}>Edit pips</Link>
            </div>
            {refPrice ? (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.25rem 0.75rem' }}>
                {[
                  { label: `TP1 (+${config.tp1Pips}p)`, value: tp1Price, color: '#10b981' },
                  { label: `TP2 (+${config.tp2Pips}p)`, value: tp2Price, color: '#10b981' },
                  { label: `TP3 (+${config.tp3Pips}p)`, value: tp3Price, color: '#10b981' },
                  { label: `SL (−${config.slPips}p)`,   value: slAutoPrice, color: '#ef4444' },
                ].map(({ label, value, color }) => (
                  <div key={label} style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: '#475569' }}>{label}</span>
                    <span style={{ color, fontWeight: 700 }}>${fmt(value ?? undefined)}</span>
                  </div>
                ))}
              </div>
            ) : <span style={{ color: '#475569', fontStyle: 'italic' }}>Enter price above to preview</span>}
          </div>

          {orderMsg && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.5rem 0.75rem', borderRadius: '0.375rem', marginBottom: '0.75rem', background: orderMsg.type === 'success' ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)', border: `1px solid ${orderMsg.type === 'success' ? '#10b981' : '#ef4444'}`, fontSize: '0.8rem', color: orderMsg.type === 'success' ? '#10b981' : '#ef4444' }}>
              {orderMsg.type === 'success' ? <CheckCircle size={14} /> : <AlertTriangle size={14} />}
              {orderMsg.text}
            </div>
          )}

          <button onClick={handlePlaceOrder} disabled={submitting} style={{ width: '100%', padding: '0.65rem', border: 'none', borderRadius: '0.375rem', fontWeight: 700, fontSize: '0.95rem', cursor: submitting ? 'not-allowed' : 'pointer', background: form.side === 'buy' ? (submitting ? '#065f46' : '#10b981') : (submitting ? '#7f1d1d' : '#ef4444'), color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}>
            {submitting ? <RefreshCw size={16} style={{ animation: 'spin 1s linear infinite' }} /> : (form.side === 'buy' ? <TrendingUp size={16} /> : <TrendingDown size={16} />)}
            {submitting ? 'Placing…' : `${form.side === 'buy' ? 'Buy / Long' : 'Sell / Short'} ${form.contract}`}
          </button>

          <button onClick={() => navigate(`/chart/futures/${form.contract}`)} style={{ width: '100%', marginTop: '0.5rem', padding: '0.45rem', border: '1px solid #334155', borderRadius: '0.375rem', background: 'transparent', color: '#64748b', fontSize: '0.8rem', cursor: 'pointer' }}>
            View Chart →
          </button>
        </div>

        {/* ── RIGHT: Tabs ── */}
        <div>
          <div style={{ display: 'flex', gap: 0, borderBottom: '1px solid #334155', marginBottom: '1rem' }}>
            {([
              ['positions', `Open Positions (${positions.length})`],
              ['orders',    `Active Orders / TP-SL (${activeOrders.length})`],
              ['trade-log', `Trade History (${tradeLog.length})`],
            ] as [ActiveTab, string][]).map(([tab, label]) => (
              <button key={tab} onClick={() => { setActiveTab(tab); if (tab === 'trade-log') fetchTradeLog(); }}
                style={{ padding: '0.6rem 1.1rem', border: 'none', background: 'none', cursor: 'pointer', fontWeight: 600, fontSize: '0.85rem', color: activeTab === tab ? '#3b82f6' : '#64748b', borderBottom: activeTab === tab ? '2px solid #3b82f6' : '2px solid transparent', transition: 'all 0.15s', marginBottom: '-1px', whiteSpace: 'nowrap' }}>
                {label}
              </button>
            ))}
          </div>

          {/* ── Positions ── */}
          {activeTab === 'positions' && (
            positions.length === 0
              ? <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}><Activity size={32} style={{ opacity: 0.4 }} /><p>No open MT5 positions</p></div>
              : <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                  {positions.map((pos, idx) => {
                    const pnl = pos.unrealizedPnl ?? pos.unrealized_pnl ?? pos.pnl;
                    const market = pos.market ?? pos.pair ?? '—';
                    const entryP = pos.entry_price ?? pos.entryPrice;
                    const markP = pos.mark_price ?? pos.markPrice ?? pos.current_price;
                    const liqP = pos.liq_price ?? pos.liquidationPrice;
                    const isBull = (pos.side ?? '').toLowerCase() === 'buy' || (pos.side ?? '').toLowerCase() === 'long';
                    const posId = pos.id ?? pos.positionId ?? String(idx);
                    return (
                      <div key={posId} style={{ ...cardStyle, padding: '0.9rem 1rem', borderLeft: `3px solid ${isBull ? '#10b981' : '#ef4444'}` }}>
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.6rem' }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <span style={{ fontWeight: 700, fontSize: '0.95rem' }}>{market}</span>
                            <span style={{ padding: '0.15rem 0.5rem', borderRadius: '999px', fontSize: '0.72rem', fontWeight: 700, background: isBull ? 'rgba(16,185,129,0.2)' : 'rgba(239,68,68,0.2)', color: isBull ? '#10b981' : '#ef4444' }}>
                              {isBull ? '▲ LONG' : '▼ SHORT'}{pos.leverage ? ` ${pos.leverage}×` : ''}
                            </span>
                          </div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <span style={{ fontSize: '1rem', fontWeight: 700, color: pnlColor(pnl) }}>{pnl !== undefined ? `${pnl >= 0 ? '+' : ''}${fmt(pnl, 4)} USDT` : '—'}</span>
                            <button onClick={() => handleClosePosition(pos)} style={{ background: '#7f1d1d', border: 'none', borderRadius: '0.25rem', color: '#fca5a5', cursor: 'pointer', padding: '0.25rem 0.5rem', fontSize: '0.75rem', fontWeight: 700 }}>Close</button>
                          </div>
                        </div>
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '0.5rem', fontSize: '0.8rem' }}>
                          {[
                            { label: 'Size',      value: fmt(pos.size, 4) },
                            { label: 'Entry',     value: entryP ? `$${fmt(entryP)}` : '—' },
                            { label: 'Mark',      value: markP ? `$${fmt(markP)}` : '—' },
                            { label: 'Liq. Price',value: liqP ? `$${fmt(liqP)}` : '—' },
                            { label: 'Margin',    value: pos.margin ? `${fmt(pos.margin, 4)} USDT` : '—' },
                          ].map(({ label, value }) => (
                            <div key={label}><div style={{ color: '#64748b', marginBottom: '0.1rem' }}>{label}</div><div style={{ color: '#f1f5f9', fontWeight: 600 }}>{value}</div></div>
                          ))}
                        </div>
                      </div>
                    );
                  })}
                </div>
          )}

          {/* ── Active Orders / TP-SL ── */}
          {activeTab === 'orders' && (
            activeOrders.length === 0
              ? <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}><Activity size={32} style={{ opacity: 0.4 }} /><p>No active orders or TP/SL for {form.contract}</p></div>
              : <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
                    <thead>
                      <tr style={{ borderBottom: '1px solid #334155' }}>
                        {['Contract', 'Side', 'Type', 'Price', 'Quantity', 'Target', 'Status', ''].map(h => (
                          <th key={h} style={{ padding: '0.5rem 0.75rem', textAlign: 'left', color: '#64748b', fontWeight: 600, whiteSpace: 'nowrap' }}>{h}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {activeOrders.map((order, idx) => {
                        const isBuy = (order.side ?? '').toLowerCase() === 'buy';
                        const orderId = order.id ?? order.orderId ?? order.order_id ?? '';
                        const market = order.market ?? order.pair ?? '—';
                        const orderType = order.order_type ?? order.orderType ?? '—';
                        const targetPrice = order.stop_price || order.stopPrice || order.take_profit_price || order.takeProfitPrice || order.price;
                        return (
                          <tr key={orderId || idx} style={{ borderBottom: '1px solid #1e293b' }}>
                            <td style={{ padding: '0.6rem 0.75rem', fontWeight: 600 }}>{market}</td>
                            <td style={{ padding: '0.6rem 0.75rem', color: isBuy ? '#10b981' : '#ef4444', fontWeight: 700 }}>{isBuy ? '▲ Buy' : '▼ Sell'}</td>
                            <td style={{ padding: '0.6rem 0.75rem', color: '#94a3b8', textTransform: 'capitalize' }}>{String(orderType).replace(/_/g, ' ')}</td>
                            <td style={{ padding: '0.6rem 0.75rem' }}>${fmt(order.price)}</td>
                            <td style={{ padding: '0.6rem 0.75rem' }}>{fmt(order.total_quantity ?? order.totalQuantity ?? order.quantity, 4)}</td>
                            <td style={{ padding: '0.6rem 0.75rem' }}>${fmt(targetPrice)}</td>
                            <td style={{ padding: '0.6rem 0.75rem' }}>
                              <span style={{ padding: '0.15rem 0.5rem', borderRadius: '999px', fontSize: '0.72rem', background: 'rgba(245,158,11,0.2)', color: '#f59e0b' }}>{order.status ?? 'open'}</span>
                            </td>
                            <td style={{ padding: '0.6rem 0.75rem' }}>
                              <button onClick={() => handleCancelOrder(orderId)} style={{ background: 'none', border: '1px solid #334155', borderRadius: '0.25rem', color: '#ef4444', cursor: 'pointer', padding: '0.2rem 0.4rem', display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.75rem' }}>
                                <X size={12} /> Cancel
                              </button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
          )}

          {/* ── Trade History ── */}
          {activeTab === 'trade-log' && (
            <div>
              {pnlTotals && (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '0.75rem', marginBottom: '1rem' }}>
                  {[
                    { label: 'Total Realized PnL',   value: `${pnlTotals.realizedUsdt >= 0 ? '+' : ''}${fmt(pnlTotals.realizedUsdt, 4)} USDT`,   color: pnlColor(pnlTotals.realizedUsdt) },
                    { label: 'Total Unrealized PnL',  value: `${pnlTotals.unrealizedUsdt >= 0 ? '+' : ''}${fmt(pnlTotals.unrealizedUsdt, 4)} USDT`, color: pnlColor(pnlTotals.unrealizedUsdt) },
                    { label: 'Total Fees Paid',        value: `${fmt(tradeLog.reduce((s, e) => s + (e.fees ?? 0), 0), 4)} USDT`,                    color: '#f59e0b' },
                    { label: 'Win Rate',               value: tradeLog.length ? `${((tradeLog.filter(e => (e.realizedPnl ?? 0) > 0).length / tradeLog.length) * 100).toFixed(0)}%  (${tradeLog.filter(e => (e.realizedPnl ?? 0) > 0).length}W/${tradeLog.filter(e => (e.realizedPnl ?? 0) <= 0).length}L)` : '—', color: '#94a3b8' },
                  ].map(({ label, value, color }) => (
                    <div key={label} style={{ ...cardStyle, padding: '0.6rem 0.75rem' }}>
                      <div style={{ fontSize: '0.7rem', color: '#64748b', marginBottom: '0.25rem' }}>{label}</div>
                      <div style={{ fontWeight: 700, color, fontSize: '0.9rem' }}>{value}</div>
                    </div>
                  ))}
                </div>
              )}

              {tradeLogLoading
                ? <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}><RefreshCw size={28} style={{ animation: 'spin 1s linear infinite' }} /><p>Loading trade history…</p></div>
                : tradeLog.length === 0
                  ? <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}><BookOpen size={32} style={{ opacity: 0.4 }} /><p>No closed MT5 trades yet</p></div>
                  : (
                    <div style={{ overflowX: 'auto' }}>
                      <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.8rem' }}>
                        <thead>
                          <tr style={{ borderBottom: '2px solid #334155' }}>
                            {['Time', 'Contract', 'Side', 'Entry', 'Exit', 'Qty', 'Lev', 'TP Set', 'SL Set', 'Outcome', 'Gross PnL', 'Fees', 'Net PnL', 'ROI'].map(h => (
                              <th key={h} style={{ padding: '0.5rem 0.6rem', textAlign: 'left', color: '#64748b', fontWeight: 600, whiteSpace: 'nowrap', fontSize: '0.75rem' }}>{h}</th>
                            ))}
                          </tr>
                        </thead>
                        <tbody>
                          {tradeLog.map((entry, idx) => {
                            const isBull = (entry.side ?? '').toLowerCase() === 'buy';
                            const gross = entry.realizedPnl;
                            const net = entry.netPnl;
                            const ts = entry.timestamp;
                            const dateStr = ts ? new Date(typeof ts === 'number' && ts > 1e12 ? ts : Number(ts) * 1000).toLocaleString([], { month: 'short', day: '2-digit', hour: '2-digit', minute: '2-digit' }) : '—';
                            let outcomeLabel = '—', outcomeColor = '#64748b';
                            if (entry.liquidated) { outcomeLabel = '⚡ Liq'; outcomeColor = '#f97316'; }
                            else if (entry.tpHit) { outcomeLabel = '✓ TP'; outcomeColor = '#10b981'; }
                            else if (entry.slHit) { outcomeLabel = '✗ SL'; outcomeColor = '#ef4444'; }
                            else if (gross !== undefined && gross > 0) { outcomeLabel = '+ Win'; outcomeColor = '#10b981'; }
                            else if (gross !== undefined && gross < 0) { outcomeLabel = '− Loss'; outcomeColor = '#ef4444'; }
                            else if (gross !== undefined) { outcomeLabel = '= Even'; outcomeColor = '#94a3b8'; }
                            return (
                              <tr key={entry.positionId ?? idx} style={{ borderBottom: '1px solid #1e293b', background: idx % 2 === 0 ? 'transparent' : 'rgba(255,255,255,0.015)' }}>
                                <td style={{ padding: '0.55rem 0.6rem', color: '#475569', whiteSpace: 'nowrap', fontSize: '0.75rem' }}>{dateStr}</td>
                                <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, whiteSpace: 'nowrap' }}>{entry.pair}</td>
                                <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: isBull ? '#10b981' : '#ef4444', whiteSpace: 'nowrap' }}>{isBull ? '▲ Long' : '▼ Short'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', whiteSpace: 'nowrap' }}>{entry.entryPrice ? `$${fmt(entry.entryPrice)}` : '—'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', whiteSpace: 'nowrap' }}>{entry.exitPrice ? `$${fmt(entry.exitPrice)}` : '—'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', whiteSpace: 'nowrap' }}>{fmt(entry.quantity, 4)}</td>
                                <td style={{ padding: '0.55rem 0.6rem', color: '#f59e0b' }}>{entry.leverage ? `${entry.leverage}×` : '—'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', color: '#10b981', whiteSpace: 'nowrap' }}>{entry.tpPrice ? `$${fmt(entry.tpPrice)}` : '—'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', color: '#ef4444', whiteSpace: 'nowrap' }}>{entry.slPrice ? `$${fmt(entry.slPrice)}` : '—'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: outcomeColor, whiteSpace: 'nowrap' }}>{outcomeLabel}</td>
                                <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: pnlColor(gross), whiteSpace: 'nowrap' }}>{gross !== undefined ? `${gross >= 0 ? '+' : ''}${fmt(gross, 4)}` : '—'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', color: '#f59e0b', whiteSpace: 'nowrap' }}>{entry.fees ? `-${fmt(entry.fees, 4)}` : '0.0000'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: pnlColor(net), whiteSpace: 'nowrap' }}>{net !== undefined ? `${net >= 0 ? '+' : ''}${fmt(net, 4)}` : '—'}</td>
                                <td style={{ padding: '0.55rem 0.6rem', color: pnlColor(entry.roi), whiteSpace: 'nowrap' }}>{entry.roi !== undefined ? `${entry.roi >= 0 ? '+' : ''}${fmt(entry.roi, 2)}%` : '—'}</td>
                              </tr>
                            );
                          })}
                        </tbody>
                        <tfoot>
                          <tr style={{ borderTop: '2px solid #334155', background: 'rgba(59,130,246,0.05)' }}>
                            <td colSpan={10} style={{ padding: '0.55rem 0.6rem', color: '#64748b', fontWeight: 700, fontSize: '0.75rem' }}>TOTAL ({tradeLog.length} trades)</td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, whiteSpace: 'nowrap', color: pnlColor(tradeLog.reduce((s, e) => s + (e.realizedPnl ?? 0), 0)) }}>{fmt(tradeLog.reduce((s, e) => s + (e.realizedPnl ?? 0), 0), 4)}</td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: '#f59e0b', whiteSpace: 'nowrap' }}>-{fmt(tradeLog.reduce((s, e) => s + (e.fees ?? 0), 0), 4)}</td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, whiteSpace: 'nowrap', color: pnlColor(tradeLog.reduce((s, e) => s + (e.netPnl ?? 0), 0)) }}>{fmt(tradeLog.reduce((s, e) => s + (e.netPnl ?? 0), 0), 4)}</td>
                            <td />
                          </tr>
                        </tfoot>
                      </table>
                    </div>
                  )
              }
            </div>
          )}
        </div>
      </div>

      <style>{`@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }`}</style>
    </div>
  );
};

export default MT5TradingPage;
