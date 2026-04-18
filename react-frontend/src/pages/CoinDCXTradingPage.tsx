import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { apiService } from '../services/api';
import { useTradeConfig } from '../context/TradeConfigContext';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import {
  RefreshCw, TrendingUp, TrendingDown, X,
  DollarSign, Activity, AlertTriangle, CheckCircle, Zap, Settings2, BookOpen,
} from 'lucide-react';

// ── Constants ────────────────────────────────────────────────────────────────
export const COINDCX_CONTRACTS = [
  'B-BTC_USDT', 'B-ETH_USDT', 'B-XAU_USDT', 'B-XAG_USDT', 'B-XNG_USDT',
  'B-CL_USDT', 'B-NATGAS_USDT', 'B-SPX_USDT', 'B-GOAT_USDT', 'B-TIA_USDT',
  'B-PENDLE_USDT', 'B-LUNA2_USDT', 'B-COMP_USDT', 'B-1000FLOKI_USDT', 'B-KAS_USDT',
  'B-ZK_USDT', 'B-AVA_USDT', 'B-STX_USDT', 'B-MOODENG_USDT', 'B-VINE_USDT',
  'B-APT_USDT', 'B-BNB_USDT', 'B-AR_USDT', 'B-LINK_USDT', 'B-MANA_USDT', 'B-MEW_USDT',
];


type OrderSide = 'buy' | 'sell';
type OrderType = 'market' | 'limit';
type ActiveTab = 'positions' | 'orders' | 'trade-log';

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
  totalPnl?: number;
  roi?: number;
  fees?: number;
  netPnl?: number;
  timestamp?: string | number;
}

interface Balance {
  // WebSocketBalanceUpdateData fields
  currencyShortName?: string;  // primary field from WS endpoint
  balance?: number;
  lockedBalance?: number;
  availableBalance?: number;   // pre-computed by backend
  // legacy / fallback field names
  currency?: string;
  locked_balance?: number;
  available?: number;
  [key: string]: any;
}

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
  // snake_case (direct API)
  market?: string;
  side?: string;
  size?: number;
  entry_price?: number;
  mark_price?: number;
  current_price?: number;
  pnl?: number;
  unrealized_pnl?: number;
  leverage?: number;
  liq_price?: number;
  margin?: number;
  // camelCase (WebSocket data)
  pair?: string;
  positionId?: string;
  entryPrice?: number;
  markPrice?: number;
  unrealizedPnl?: number;
  liquidationPrice?: number;
  [key: string]: any;
}

interface FuturesOrder {
  id?: string;
  order_id?: string;
  // snake_case (direct API)
  market?: string;
  side?: string;
  order_type?: string;
  quantity?: number;
  price?: number;
  status?: string;
  created_at?: string;
  // camelCase (WebSocket data)
  orderId?: string;
  pair?: string;
  orderType?: string;
  createdAt?: string;
  [key: string]: any;
}

interface OrderForm {
  contract: string;
  side: OrderSide;
  orderType: OrderType;
  quantity: string;
  price: string;
}

const DEFAULT_FORM: OrderForm = {
  contract: 'B-BTC_USDT',
  side: 'buy',
  orderType: 'limit',
  quantity: '',
  price: '',
};

// ── Helpers ──────────────────────────────────────────────────────────────────
const pnlColor = (v: number | undefined) =>
  v === undefined ? '#94a3b8' : v >= 0 ? '#10b981' : '#ef4444';

const fmt = (v: number | string | undefined, decimals = 2) => {
  if (v === undefined || v === null || v === '') return '—';
  const n = Number(v);
  return isNaN(n) ? String(v) : n.toLocaleString(undefined, { minimumFractionDigits: decimals, maximumFractionDigits: decimals });
};

// ── Styles helpers ───────────────────────────────────────────────────────────
const cardStyle: React.CSSProperties = {
  background: '#1e293b', borderRadius: '0.5rem', border: '1px solid #334155', padding: '1rem',
};
const labelStyle: React.CSSProperties = { fontSize: '0.75rem', color: '#64748b', marginBottom: '0.25rem' };
const inputStyle: React.CSSProperties = {
  width: '100%', padding: '0.5rem 0.75rem', background: '#0f172a',
  border: '1px solid #334155', borderRadius: '0.375rem', color: '#f1f5f9',
  fontSize: '0.875rem', outline: 'none',
};
const selectStyle: React.CSSProperties = { ...inputStyle, cursor: 'pointer' };
const tabBtnStyle = (active: boolean, color = '#3b82f6'): React.CSSProperties => ({
  flex: 1, padding: '0.5rem', border: 'none', borderRadius: '0.375rem', cursor: 'pointer',
  fontWeight: 600, fontSize: '0.875rem', transition: 'all 0.15s',
  background: active ? color : 'transparent',
  color: active ? '#fff' : '#64748b',
});

// ── Main Component ────────────────────────────────────────────────────────────
const CoinDCXTradingPage: React.FC = () => {
  const contracts = COINDCX_CONTRACTS;
  const pageTitle = 'CoinDCX Futures';
  const defaultContract = 'B-BTC_USDT';
  const showMt5 = false;
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { coindcxConfig: config } = useTradeConfig();

  // State
  const [balances, setBalances] = useState<Balance[]>([]);
  const [mt5Balance, setMt5Balance] = useState<Mt5Balance | null>(null);
  const [positions, setPositions] = useState<FuturesPosition[]>([]);
  const [activeOrders, setActiveOrders] = useState<FuturesOrder[]>([]);
  const [latestPrice, setLatestPrice] = useState<number | null>(null);
  const [priceLoading, setPriceLoading] = useState(false);
  // Ref tracks the contract that the most-recently-initiated price fetch targets.
  // Any fetch that resolves for a different contract is discarded (stale race guard).
  const priceContractRef = React.useRef<string>('');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [orderMsg, setOrderMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [activeTab, setActiveTab] = useState<ActiveTab>('positions');
  const [tradeLog, setTradeLog] = useState<TradeLogEntry[]>([]);
  const [tradeLogLoading, setTradeLogLoading] = useState(false);
  const [pnlTotals, setPnlTotals] = useState<{ realizedUsdt: number; unrealizedUsdt: number } | null>(null);
  const [form, setForm] = useState<OrderForm>({
    ...DEFAULT_FORM,
    contract: searchParams.get('symbol') ?? defaultContract ?? DEFAULT_FORM.contract,
  });

  // ── Data fetchers (use WebSocket data endpoints — no credentials required) ──
  const fetchBalances = useCallback(async () => {
    try {
      // Current endpoint returns { futures: {...}, mt5: {...} }
      const res = await apiService.getWsCurrentBalance('USDT');
      const data = res.data;
      if (data && typeof data === 'object' && !Array.isArray(data)) {
        // New shape: { futures: Balance, mt5: Mt5Balance }
        if (data.futures || data.mt5) {
          if (data.futures) setBalances([data.futures]);
          if (data.mt5) setMt5Balance(data.mt5);
          return;
        }
        // Legacy shape: single balance object directly
        setBalances([data]);
        return;
      }
    } catch (e: any) {
      console.error('Balance fetch error:', e);
    }
  }, []);

  const fetchLeverage = useCallback(async () => {
    try {
      const res = await apiService.getFuturesLeverage();
      const data = res.data;
      if (data?.mt5?.leverage != null) {
        setMt5Balance(prev => prev ? { ...prev, leverage: data.mt5.leverage } : { leverage: data.mt5.leverage });
      }
    } catch (e) {
      console.error('Leverage fetch error:', e);
    }
  }, []);

  const fetchPositions = useCallback(async () => {
    try {
      // Fetch open positions from WebSocket data store (no auth needed)
      const res = await apiService.getWsPositionsByStatus('open');
      const data = res.data;
      if (Array.isArray(data)) { setPositions(data); return; }
      if (Array.isArray(data?.positions)) { setPositions(data.positions); return; }
      if (Array.isArray(data?.data)) { setPositions(data.data); return; }
      setPositions([]);
    } catch (e: any) {
      console.error('Positions fetch error:', e);
      setPositions([]);
    }
  }, []);

  const fetchOrders = useCallback(async () => {
    try {
      // Fetch open orders for selected contract from WebSocket data store (no auth needed)
      const res = await apiService.getWsOrdersByPairAndStatus(form.contract, 'open');
      const data = res.data;
      if (Array.isArray(data)) { setActiveOrders(data); return; }
      if (Array.isArray(data?.orders)) { setActiveOrders(data.orders); return; }
      if (Array.isArray(data?.data)) { setActiveOrders(data.data); return; }
      setActiveOrders([]);
    } catch (e: any) {
      console.error('Orders fetch error:', e);
      setActiveOrders([]);
    }
  }, [form.contract]);

  const fetchLatestPrice = useCallback(async (contract: string) => {
    // Register this contract as the active request — any earlier in-flight call
    // that resolves after this point will see a mismatch and discard its result.
    priceContractRef.current = contract;
    setPriceLoading(true);

    const commit = (price: number | null) => {
      // Only apply if this call is still the latest for this contract
      if (priceContractRef.current !== contract) return;
      setLatestPrice(price);
      setPriceLoading(false);
    };

    try {
      // Per-contract endpoint — two response shapes:
      // MT5 (e.g. XAUUSD):        { ask, bid, last, ... }
      // CoinDCX (e.g. B-BTC_USDT): { markPrice, instrument, ... } — markPrice is often null
      const res = await apiService.getFuturesInstrumentPriceLatest(contract);
      if (priceContractRef.current !== contract) return; // stale — bail immediately
      const data = res.data;

      // MT5 shape: mid-point of ask/bid
      if (data?.ask != null || data?.bid != null) {
        const ask = Number(data.ask ?? 0);
        const bid = Number(data.bid ?? 0);
        if (ask > 0 && bid > 0) { commit((ask + bid) / 2); return; }
        if (ask > 0) { commit(ask); return; }
        if (bid > 0) { commit(bid); return; }
        const last = Number(data.last ?? 0);
        if (last > 0) { commit(last); return; }
      }

      // CoinDCX shape: markPrice (often null — fall through to bulk endpoint)
      if (data?.markPrice != null && Number(data.markPrice) > 0) {
        commit(Number(data.markPrice)); return;
      }
    } catch { /* instrument endpoint unavailable — fall through */ }

    // Fallback: bulk current-prices endpoint (JSON string, 'ls' = last/mark price)
    try {
      const res = await apiService.getFuturesCurrentPricesPublic();
      if (priceContractRef.current !== contract) return; // stale
      const pricesRaw = res.data?.prices;
      const prices: Record<string, { ls?: number; mp?: number }> =
        typeof pricesRaw === 'string' ? JSON.parse(pricesRaw) : (pricesRaw ?? {});
      const entry = prices[contract];
      const price = entry?.ls ?? entry?.mp;
      if (price != null && Number(price) > 0) { commit(Number(price)); return; }
    } catch { /* silently ignore */ }

    commit(null);
  }, []);

  const fetchTradeLog = useCallback(async () => {
    setTradeLogLoading(true);
    try {
      // All real trade data lives in orders (closed positions have 0.0 for all financial fields)
      const [ordersRes, unrealPnlRes] = await Promise.allSettled([
        apiService.getWsRecentOrders(200),
        apiService.getWsTotalUnrealizedPnl(),
      ]);

      const allOrders: any[] =
        ordersRes.status === 'fulfilled' && Array.isArray(ordersRes.value.data)
          ? ordersRes.value.data
          : [];

      // Separate entry orders (stage='default', filled) from exit orders
      const entryOrders = allOrders
        .filter(o => o.stage === 'default' && o.status === 'filled')
        .sort((a, b) => (a.createdAt ?? 0) - (b.createdAt ?? 0)); // oldest first for FIFO

      const exitOrders = allOrders
        .filter(o => ['exit', 'tpsl_exit', 'liquidate'].includes(o.stage ?? '') && o.status === 'filled')
        .sort((a, b) => (b.createdAt ?? 0) - (a.createdAt ?? 0)); // newest first for display

      // Build per-pair entry pool for FIFO matching
      const entryByPair = new Map<string, any[]>();
      for (const o of entryOrders) {
        if (!entryByPair.has(o.pair)) entryByPair.set(o.pair, []);
        entryByPair.get(o.pair)!.push(o);
      }
      const entryPointer = new Map<string, number>(); // pair → next unused index

      const entries: TradeLogEntry[] = exitOrders.map(exit => {
        const pair: string = exit.pair ?? '—';
        const pool = entryByPair.get(pair) ?? [];
        const ptr = entryPointer.get(pair) ?? 0;
        const entry = pool[ptr];
        entryPointer.set(pair, ptr + 1);

        // Prices & qty from actual fill data
        const entryAvg: number | undefined = entry?.avgPrice || entry?.price || undefined;
        const exitAvg: number | undefined = exit.avgPrice || exit.price || undefined;
        const qty: number | undefined = exit.filledQuantity || exit.totalQuantity || undefined;

        // Direction: exit side is the closing side, so entry direction is opposite
        // e.g. exit side='sell' means we closed a LONG
        const exitSide = (exit.side ?? '').toLowerCase();
        const isLong = exitSide === 'sell'; // selling to close = was long

        // Leverage from entry order (exit orders often show 1.0)
        const leverage: number | undefined = (entry?.leverage && entry.leverage !== 1.0)
          ? entry.leverage
          : exit.leverage || undefined;

        // Margin from entry's idealMargin
        const margin: number | undefined = entry?.idealMargin || undefined;

        // TP/SL set on entry order
        const tpPrice: number | undefined = entry?.takeProfitPrice || undefined;
        const slPrice: number | undefined = entry?.stopLossPrice || undefined;

        // Outcome detection from orderType of exit
        const exitType = (exit.orderType ?? '').toLowerCase();
        const tpHit = exit.stage === 'tpsl_exit' && exitType.includes('take_profit');
        const slHit = exit.stage === 'tpsl_exit' && exitType.includes('stop_');
        const liquidated = exit.stage === 'liquidate';

        // Fees from both legs
        const exitFees = Number(exit.feeAmount) || 0;
        const entryFees = Number(entry?.feeAmount) || 0;
        const totalFees = exitFees + entryFees;

        // PnL from prices
        let grossPnl: number | undefined;
        if (entryAvg && exitAvg && qty) {
          grossPnl = isLong
            ? (exitAvg - entryAvg) * qty
            : (entryAvg - exitAvg) * qty;
        }
        const netPnl = grossPnl !== undefined ? grossPnl - totalFees : undefined;
        const roi = grossPnl !== undefined && margin ? (grossPnl / margin) * 100 : undefined;

        return {
          positionId: exit.orderId,
          pair,
          side: isLong ? 'buy' : 'sell',
          status: exit.stage,
          entryPrice: entryAvg,
          exitPrice: exitAvg,
          quantity: qty,
          leverage,
          margin,
          tpPrice,
          slPrice,
          tpHit,
          slHit,
          liquidated,
          realizedPnl: grossPnl,
          totalPnl: grossPnl,
          roi,
          fees: totalFees,
          netPnl,
          timestamp: exit.createdAt,
        } as TradeLogEntry;
      });

      // Summary totals computed from our entries (positions API has null PnL)
      const totalRealized = entries.reduce((s, e) => s + (e.realizedPnl ?? 0), 0);
      const totalUnrealized = unrealPnlRes.status === 'fulfilled'
        ? (() => { const u = unrealPnlRes.value.data as any; return u?.USDT ?? u?.usdt ?? Object.values(u ?? {})[0] ?? 0; })()
        : 0;
      setPnlTotals({ realizedUsdt: totalRealized, unrealizedUsdt: Number(totalUnrealized) });

      setTradeLog(entries);
    } catch (e) {
      console.error('Trade log fetch error:', e);
    } finally {
      setTradeLogLoading(false);
    }
  }, []);

  const fetchAll = useCallback(async (showLoad = false) => {
    if (showLoad) setLoading(true);
    else setRefreshing(true);
    setError(null);
    try {
      await Promise.all([
        fetchBalances(), fetchLeverage(), fetchPositions(),
        fetchOrders(), fetchTradeLog(),
      ]);
    } catch (e: any) {
      setError(e.message ?? 'Failed to fetch data');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [fetchBalances, fetchLeverage, fetchPositions, fetchOrders, fetchTradeLog]);

  useEffect(() => { fetchAll(true); }, []); // eslint-disable-line react-hooks/exhaustive-deps

  // Re-fetch price when contract changes
  useEffect(() => {
    fetchLatestPrice(form.contract);
    fetchOrders();
  }, [form.contract]); // eslint-disable-line react-hooks/exhaustive-deps

  // ── Derived values ────────────────────────────────────────────────────────
  const usdtBalance = balances.find(
    b => (b.currencyShortName ?? b.currency ?? b.coin ?? b.asset ?? '').toUpperCase() === 'USDT'
  );
  const availableUsdt: string | undefined = usdtBalance
    ? (
        usdtBalance.availableBalance != null
          ? Number(usdtBalance.availableBalance)
          : Number(usdtBalance.balance ?? usdtBalance.available ?? 0) -
            Number(usdtBalance.lockedBalance ?? usdtBalance.locked_balance ?? 0)
      ).toFixed(4)
    : undefined;

  // Order cost estimate
  // ── Auto-computed TP/SL prices from config pips ───────────────────────────
  const refPrice = form.orderType === 'market' ? latestPrice : (Number(form.price) || latestPrice);
  const isLong = form.side === 'buy';
  const tp1Price = refPrice ? (isLong ? refPrice + config.tp1Pips : refPrice - config.tp1Pips) : null;
  const tp2Price = refPrice ? (isLong ? refPrice + config.tp2Pips : refPrice - config.tp2Pips) : null;
  const tp3Price = refPrice ? (isLong ? refPrice + config.tp3Pips : refPrice - config.tp3Pips) : null;
  const slPrice  = refPrice ? (isLong ? refPrice - config.slPips  : refPrice + config.slPips)  : null;

  // ── Auto-fill quantity from margin config ────────────────────────────────
  const handleAutoFillQty = () => {
    if (!refPrice) return;
    const qty = (config.marginUsdt * config.leverage) / refPrice;
    setForm(f => ({ ...f, quantity: qty.toFixed(4) }));
  };

  const estimatedCost = (() => {
    const qty = Number(form.quantity);
    const price = form.orderType === 'market' ? (latestPrice ?? 0) : Number(form.price);
    if (!qty || !price) return null;
    return ((qty * price) / config.leverage).toFixed(2);
  })();

  // ── Order placement ───────────────────────────────────────────────────────
  const handlePlaceOrder = async () => {
    // Auto-compute quantity from config margin if not manually entered
    let qty = Number(form.quantity);

    // Resolve a live price — needed for both quantity calc and TP/SL computation.
    // Re-fetch on demand when latestPrice hasn't loaded yet (common for market orders).
    let currentPrice = latestPrice;
    if (!currentPrice && form.orderType === 'market') {
      try {
        const res = await apiService.getFuturesMarketDataPublic(form.contract, 20);
        const arr: any[] = Array.isArray(res.data) ? res.data : [];
        const record = [...arr].reverse().find(r => r.markPrice != null && Number(r.markPrice) > 0);
        const p = record?.markPrice ?? record?.price ?? record?.mark_price;
        if (p && Number(p) > 0) { currentPrice = Number(p); setLatestPrice(currentPrice); }
      } catch (_) {}
    }

    if (!qty || qty <= 0) {
      const priceForQty = form.orderType === 'market' ? (currentPrice ?? 0) : Number(form.price);
      if (priceForQty > 0 && config.marginUsdt > 0 && config.leverage > 0) {
        qty = (config.marginUsdt * config.leverage) / priceForQty;
        setForm(f => ({ ...f, quantity: qty.toFixed(4) }));
      } else {
        setOrderMsg({ type: 'error', text: 'Enter a valid quantity (or set a price so it can be auto-computed)' });
        return;
      }
    }
    if (form.orderType === 'limit' && (!form.price || Number(form.price) <= 0)) {
      setOrderMsg({ type: 'error', text: 'Enter a valid limit price' });
      return;
    }

    // Compute TP/SL prices from the *resolved* price (not from stale render-time derived values).
    const resolvedPrice = form.orderType === 'market'
      ? currentPrice
      : (Number(form.price) || currentPrice);
    const isBuyOrder = form.side === 'buy';
    const computedTp1 = resolvedPrice
      ? (isBuyOrder ? resolvedPrice + config.tp1Pips : resolvedPrice - config.tp1Pips)
      : null;
    const computedTp2 = resolvedPrice
      ? (isBuyOrder ? resolvedPrice + config.tp2Pips : resolvedPrice - config.tp2Pips)
      : null;
    const computedTp3 = resolvedPrice
      ? (isBuyOrder ? resolvedPrice + config.tp3Pips : resolvedPrice - config.tp3Pips)
      : null;
    const computedSl = resolvedPrice
      ? (isBuyOrder ? resolvedPrice - config.slPips : resolvedPrice + config.slPips)
      : null;

    setSubmitting(true);
    setOrderMsg(null);

    // Snapshot contract/side before any state mutations
    const contract = form.contract;
    const side = form.side;

    try {
      const payload: any = {
        market: contract,
        side,
        order_type: form.orderType,
        quantity: qty,
        leverage: config.leverage,
        margin_usdt: config.marginUsdt,
        ...(form.orderType === 'limit' ? { price: Number(form.price) } : {}),
      };
      const orderRes = await apiService.createFuturesOrderPublic(payload);

      // Extract order ID for logging only — not sent to create_tpsl.
      const orderData = orderRes.data;
      console.log('[createOrder] response:', orderData);
      const orderItem = Array.isArray(orderData) ? orderData[0] : (orderData?.data ?? orderData);
      const orderId: string | undefined = orderItem?.id ?? orderItem?.order_id ?? orderItem?.orderId;
      console.log('[createOrder] orderId:', orderId, 'tp1:', computedTp1, 'tp2:', computedTp2, 'tp3:', computedTp3, 'sl:', computedSl);

      setOrderMsg({ type: 'success', text: `${side === 'buy' ? 'Long' : 'Short'} order placed! Setting TP/SL…` });
      setForm(f => ({ ...f, quantity: '', price: '' }));

      // ── Create 3 × TP + 1 × SL after position becomes active ──────────────
      // Each TP covers exactly 1/3 of the position quantity.
      // SL covers the full quantity.
      // Each order is a SEPARATE call — a single payload can only hold one `quantity` key.
      const tpQty = Number((qty / 3).toFixed(4));
      const tp3Qty = Number((qty - tpQty * 2).toFixed(4)); // remaining after TP1 + TP2

      if (computedTp1 || computedTp2 || computedTp3 || computedSl) {
        const RETRY_DELAYS_MS = [800, 1500, 2000, 2000, 2000, 3000, 3000, 3000, 3000, 3000];
        let positionActive = false;

        // — Step 1: TP1 alone (retry until position is active) —
        for (let attempt = 0; attempt < RETRY_DELAYS_MS.length; attempt++) {
          await new Promise(resolve => setTimeout(resolve, RETRY_DELAYS_MS[attempt]));
          try {
            const tp1Payload = { market: contract, take_profit_price: Number(computedTp1!.toFixed(2)), quantity: tpQty };
            console.log(`[TP1] attempt ${attempt + 1}:`, tp1Payload);
            await apiService.createTpSlPublic(tp1Payload);
            positionActive = true;
            break;
          } catch (err: any) {
            const errMsg: string =
              err.response?.data?.error ?? err.response?.data?.message ?? err.message ?? '';
            console.warn(`[TP1] attempt ${attempt + 1} failed:`, errMsg);
            const isNotActiveYet =
              errMsg.toLowerCase().includes('no active') ||
              errMsg.toLowerCase().includes('not found') ||
              errMsg.toLowerCase().includes('pending');
            if (!isNotActiveYet || attempt === RETRY_DELAYS_MS.length - 1) {
              setOrderMsg({ type: 'success', text: `Order placed, but TP1 failed: ${errMsg}` });
              break;
            }
          }
        }

        // — Step 2: SL, TP2, TP3 — position confirmed active, fire sequentially —
        if (positionActive) {
          const remaining = [
            computedSl  ? { label: 'SL',  stop_loss_price:   Number(computedSl.toFixed(2)),  quantity: qty    } : null,
            computedTp2 ? { label: 'TP2', take_profit_price: Number(computedTp2.toFixed(2)), quantity: tpQty } : null,
            computedTp3 ? { label: 'TP3', take_profit_price: Number(computedTp3.toFixed(2)), quantity: tp3Qty } : null,
          ].filter(Boolean) as { label: string; quantity: number; take_profit_price?: number; stop_loss_price?: number }[];

          const results: string[] = ['TP1 ✓'];
          for (const order of remaining) {
            const { label, ...apiPayload } = order;
            try {
              console.log(`[${label}] payload:`, { market: contract, ...apiPayload });
              await apiService.createTpSlPublic({ market: contract, ...apiPayload });
              results.push(`${label} ✓`);
            } catch (err: any) {
              const errMsg = err.response?.data?.error ?? err.response?.data?.message ?? err.message ?? '';
              console.warn(`[${label}] failed:`, errMsg);
              results.push(`${label} ✗`);
            }
          }

          setOrderMsg({ type: 'success', text: `${side === 'buy' ? 'Long' : 'Short'} position opened — ${results.join('  ')}` });
        }
      }

      fetchAll();
    } catch (e: any) {
      setOrderMsg({ type: 'error', text: e.response?.data?.message ?? e.message ?? 'Order failed' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancelOrder = async (orderId: string) => {
    try {
      await apiService.cancelFuturesOrderPublic(orderId);
      fetchOrders();
    } catch (e: any) {
      setError(e.response?.data?.message ?? 'Failed to cancel order');
    }
  };

  const handleClosePosition = async (pos: FuturesPosition) => {
    try {
      const market = pos.market ?? pos.pair;
      await apiService.closeFuturesPositionPublic({ market, size: pos.size });
      fetchPositions();
    } catch (e: any) {
      setError(e.response?.data?.message ?? 'Failed to close position');
    }
  };

  // ── Set market price as order price ──────────────────────────────────────
  const fillMarketPrice = () => {
    if (latestPrice) setForm(f => ({ ...f, price: String(latestPrice) }));
  };

  if (loading) return <Loading />;

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div className="page" style={{ maxWidth: 1400, margin: '0 auto', padding: '1rem 1.5rem' }}>

      {/* ── Page header ── */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Zap size={22} style={{ color: '#f59e0b' }} />
          <h1 style={{ fontSize: '1.4rem', margin: 0 }}>{pageTitle}</h1>
        </div>
        <button
          onClick={() => fetchAll()}
          disabled={refreshing}
          className="btn btn-secondary"
          style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.85rem' }}
        >
          <RefreshCw size={15} className={refreshing ? 'spin' : ''} />
          Refresh
        </button>
      </div>

      {error && <ErrorMessage message={error} onClose={() => setError(null)} />}

      {/* ── Wallet balance bar ── */}
      <div style={{ ...cardStyle, marginBottom: '1rem', padding: '0.75rem 1rem' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.5rem', alignItems: 'center' }}>

          {/* ── Futures wallet ── */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <DollarSign size={16} style={{ color: '#f59e0b' }} />
            <span style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>Futures</span>
          </div>
          {usdtBalance ? (
            <>
              <div>
                <div style={labelStyle}>Available</div>
                <div style={{ fontSize: '1.05rem', fontWeight: 700, color: '#10b981' }}>
                  {fmt(availableUsdt, 4)} USDT
                </div>
              </div>
              <div>
                <div style={labelStyle}>Total</div>
                <div style={{ fontSize: '0.95rem', fontWeight: 600, color: '#f1f5f9' }}>
                  {fmt(usdtBalance.balance ?? usdtBalance.available, 4)} USDT
                </div>
              </div>
              {(usdtBalance.lockedBalance ?? usdtBalance.locked_balance) != null && (
                <div>
                  <div style={labelStyle}>In Order</div>
                  <div style={{ fontSize: '0.9rem', color: '#f59e0b' }}>
                    {fmt(usdtBalance.lockedBalance ?? usdtBalance.locked_balance, 4)} USDT
                  </div>
                </div>
              )}
            </>
          ) : (
            <span style={{ fontSize: '0.8rem', color: '#64748b', fontStyle: 'italic' }}>No futures balance</span>
          )}

          {/* ── Divider ── */}
          {showMt5 && mt5Balance && (
            <div style={{ width: 1, height: 36, background: '#334155', flexShrink: 0 }} />
          )}

          {/* ── MT5 wallet ── */}
          {showMt5 && mt5Balance && (
            <>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Activity size={15} style={{ color: '#818cf8' }} />
                <span style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 600 }}>MT5 ({mt5Balance.currency ?? 'USD'})</span>
              </div>
              <div>
                <div style={labelStyle}>Balance</div>
                <div style={{ fontSize: '1.05rem', fontWeight: 700, color: '#f1f5f9' }}>
                  {fmt(mt5Balance.balance, 2)}
                </div>
              </div>
              <div>
                <div style={labelStyle}>Equity</div>
                <div style={{ fontSize: '0.95rem', fontWeight: 600, color: '#f1f5f9' }}>
                  {fmt(mt5Balance.equity, 2)}
                </div>
              </div>
              <div>
                <div style={labelStyle}>Free Margin</div>
                <div style={{ fontSize: '0.9rem', color: '#10b981' }}>
                  {fmt(mt5Balance.free_margin, 2)}
                </div>
              </div>
              <div>
                <div style={labelStyle}>Margin Used</div>
                <div style={{ fontSize: '0.9rem', color: '#f59e0b' }}>
                  {fmt(mt5Balance.margin, 2)}
                </div>
              </div>
              <div>
                <div style={labelStyle}>Float P&L</div>
                <div style={{ fontSize: '0.9rem', fontWeight: 700, color: (mt5Balance.profit ?? 0) >= 0 ? '#10b981' : '#ef4444' }}>
                  {(mt5Balance.profit ?? 0) >= 0 ? '+' : ''}{fmt(mt5Balance.profit, 2)}
                </div>
              </div>
              {mt5Balance.leverage != null && (
                <div>
                  <div style={labelStyle}>Leverage</div>
                  <div style={{ fontSize: '0.9rem', fontWeight: 700, color: '#818cf8' }}>
                    1:{mt5Balance.leverage}
                  </div>
                </div>
              )}
            </>
          )}

          {latestPrice && (
            <div style={{ marginLeft: 'auto', textAlign: 'right' }}>
              <div style={labelStyle}>{form.contract} Mark Price</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#f1f5f9' }}>
                ${fmt(latestPrice, 2)}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* ── Two-column layout ── */}
      <div style={{ display: 'grid', gridTemplateColumns: '340px 1fr', gap: '1rem', alignItems: 'start' }}>

        {/* ─────────────────────────── LEFT: Order Form ─────────────────────────── */}
        <div style={cardStyle}>
          <h2 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.75rem', color: '#f1f5f9' }}>
            Place Order
          </h2>

          {/* Config info strip */}
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            background: '#0f172a', borderRadius: '0.375rem',
            padding: '0.4rem 0.65rem', marginBottom: '0.85rem',
            fontSize: '0.75rem', color: '#64748b', gap: '0.5rem',
          }}>
            <span>Configured: <strong style={{ color: '#f59e0b' }}>{config.marginUsdt} USDT</strong> margin · <strong style={{ color: '#f59e0b' }}>{config.leverage}×</strong> lev · SL <strong style={{ color: '#ef4444' }}>{config.slPips}p</strong></span>
            <Link to="/trade-config" style={{ display: 'flex', alignItems: 'center', gap: '0.2rem', color: '#3b82f6', textDecoration: 'none', whiteSpace: 'nowrap', flexShrink: 0 }}>
              <Settings2 size={11} /> Edit
            </Link>
          </div>

          {/* Contract selector */}
          <div style={{ marginBottom: '0.75rem' }}>
            <div style={labelStyle}>Contract</div>
            <select
              value={form.contract}
              onChange={e => {
                setLatestPrice(null);
                setForm(f => ({ ...f, contract: e.target.value }));
              }}
              style={selectStyle}
            >
              {contracts.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
            {/* Mark price badge */}
            <div style={{ marginTop: '0.35rem', minHeight: '1.2rem' }}>
              {priceLoading ? (
                <span style={{ fontSize: '0.75rem', color: '#64748b' }}>Fetching price…</span>
              ) : latestPrice != null ? (
                <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#f59e0b' }}>
                  Mark: ${latestPrice.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 6 })}
                </span>
              ) : (
                <span style={{ fontSize: '0.75rem', color: '#64748b' }}>No mark price available</span>
              )}
            </div>
          </div>

          {/* Long / Short */}
          <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.75rem' }}>
            <button
              style={tabBtnStyle(form.side === 'buy', '#10b981')}
              onClick={() => setForm(f => ({ ...f, side: 'buy' }))}
            >
              <TrendingUp size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} />
              Long / Buy
            </button>
            <button
              style={tabBtnStyle(form.side === 'sell', '#ef4444')}
              onClick={() => setForm(f => ({ ...f, side: 'sell' }))}
            >
              <TrendingDown size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} />
              Short / Sell
            </button>
          </div>

          {/* Order type */}
          <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.75rem' }}>
            {(['limit', 'market'] as OrderType[]).map(t => (
              <button
                key={t}
                style={{
                  ...tabBtnStyle(form.orderType === t, '#3b82f6'),
                  padding: '0.35rem 0.75rem',
                  flex: 'none',
                  borderRadius: '999px',
                  border: `1px solid ${form.orderType === t ? '#3b82f6' : '#334155'}`,
                  fontSize: '0.8rem',
                }}
                onClick={() => setForm(f => ({ ...f, orderType: t }))}
              >
                {t.charAt(0).toUpperCase() + t.slice(1)}
              </button>
            ))}
          </div>

          {/* Leverage — read-only from config */}
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            background: '#0f172a', borderRadius: '0.375rem',
            padding: '0.45rem 0.75rem', marginBottom: '0.75rem',
          }}>
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
                <span style={labelStyle}>Limit Price (USDT)</span>
                {latestPrice && (
                  <button
                    onClick={fillMarketPrice}
                    style={{ fontSize: '0.7rem', color: '#3b82f6', background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}
                  >
                    Mark: ${fmt(latestPrice, 2)}
                  </button>
                )}
              </div>
              <input
                type="number" min={0} step="any"
                placeholder="0.00"
                value={form.price}
                onChange={e => setForm(f => ({ ...f, price: e.target.value }))}
                style={inputStyle}
              />
            </div>
          )}

          {/* Quantity */}
          <div style={{ marginBottom: '0.75rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.25rem' }}>
              <span style={labelStyle}>Quantity (contracts)</span>
              {refPrice && (
                <button
                  onClick={handleAutoFillQty}
                  title={`Fill based on ${config.marginUsdt} USDT × ${config.leverage}×`}
                  style={{ fontSize: '0.7rem', color: '#f59e0b', background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}
                >
                  Auto ({config.marginUsdt}×{config.leverage}×)
                </button>
              )}
            </div>
            <input
              type="number" min={0} step="any"
              placeholder="0.00"
              value={form.quantity}
              onChange={e => setForm(f => ({ ...f, quantity: e.target.value }))}
              style={inputStyle}
            />
          </div>

          {/* Cost estimate */}
          <div style={{
            background: '#0f172a', borderRadius: '0.375rem', padding: '0.5rem 0.75rem',
            marginBottom: '0.75rem', fontSize: '0.8rem',
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748b' }}>
              <span>Est. Margin Required</span>
              <span style={{ color: '#f1f5f9', fontWeight: 600 }}>
                {estimatedCost ? `${estimatedCost} USDT` : '—'}
              </span>
            </div>
            {availableUsdt && estimatedCost && (
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748b', marginTop: '0.25rem' }}>
                <span>Available</span>
                <span style={{ color: Number(availableUsdt) >= Number(estimatedCost) ? '#10b981' : '#ef4444', fontWeight: 600 }}>
                  {availableUsdt} USDT
                </span>
              </div>
            )}
            <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748b', marginTop: '0.25rem' }}>
              <span>Configured margin</span>
              <span style={{ color: '#f59e0b', fontWeight: 600 }}>{config.marginUsdt} USDT</span>
            </div>
          </div>

          {/* TP/SL — auto-computed from config pips, read-only */}
          <div style={{
            background: '#0f172a', borderRadius: '0.375rem', padding: '0.5rem 0.75rem',
            marginBottom: '0.75rem', fontSize: '0.8rem',
          }}>
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
                  { label: `SL  (−${config.slPips}p)`, value: slPrice,  color: '#ef4444' },
                ].map(({ label, value, color }) => (
                  <div key={label} style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: '#475569' }}>{label}</span>
                    <span style={{ color, fontWeight: 700 }}>${fmt(value ?? undefined)}</span>
                  </div>
                ))}
              </div>
            ) : (
              <span style={{ color: '#475569', fontStyle: 'italic' }}>Enter a price above to preview</span>
            )}
          </div>

          {/* Order message */}
          {orderMsg && (
            <div style={{
              display: 'flex', alignItems: 'center', gap: '0.5rem',
              padding: '0.5rem 0.75rem', borderRadius: '0.375rem', marginBottom: '0.75rem',
              background: orderMsg.type === 'success' ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)',
              border: `1px solid ${orderMsg.type === 'success' ? '#10b981' : '#ef4444'}`,
              fontSize: '0.8rem',
              color: orderMsg.type === 'success' ? '#10b981' : '#ef4444',
            }}>
              {orderMsg.type === 'success' ? <CheckCircle size={14} /> : <AlertTriangle size={14} />}
              {orderMsg.text}
            </div>
          )}

          {/* Submit */}
          <button
            onClick={handlePlaceOrder}
            disabled={submitting}
            style={{
              width: '100%', padding: '0.65rem', border: 'none', borderRadius: '0.375rem',
              fontWeight: 700, fontSize: '0.95rem', cursor: submitting ? 'not-allowed' : 'pointer',
              background: form.side === 'buy'
                ? (submitting ? '#065f46' : '#10b981')
                : (submitting ? '#7f1d1d' : '#ef4444'),
              color: '#fff', transition: 'background 0.15s',
              display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem',
            }}
          >
            {submitting ? <RefreshCw size={16} className="spin" /> : (
              form.side === 'buy' ? <TrendingUp size={16} /> : <TrendingDown size={16} />
            )}
            {submitting ? 'Placing…' : `${form.side === 'buy' ? 'Buy / Long' : 'Sell / Short'} ${form.contract}`}
          </button>

          {/* Chart link */}
          <button
            onClick={() => navigate(`/chart/futures/${form.contract}`)}
            style={{
              width: '100%', marginTop: '0.5rem', padding: '0.45rem', border: '1px solid #334155',
              borderRadius: '0.375rem', background: 'transparent', color: '#64748b',
              fontSize: '0.8rem', cursor: 'pointer',
            }}
          >
            View Chart →
          </button>

          <Link
            to="/trade-config"
            style={{
              display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.3rem',
              marginTop: '0.4rem', width: '100%', padding: '0.4rem',
              border: '1px solid #334155', borderRadius: '0.375rem',
              color: '#64748b', fontSize: '0.78rem', textDecoration: 'none',
              transition: 'color 0.15s',
            }}
          >
            <Settings2 size={12} /> Trade Config
          </Link>
        </div>

        {/* ─────────────────────────── RIGHT: Positions + Orders ─────────────────── */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0' }}>

          {/* Tab header */}
          <div style={{ display: 'flex', gap: '0', borderBottom: '1px solid #334155', marginBottom: '1rem' }}>
            {(['positions', 'orders', 'trade-log'] as ActiveTab[]).map(tab => (
              <button
                key={tab}
                onClick={() => {
                  setActiveTab(tab);
                  if (tab === 'trade-log') fetchTradeLog();
                }}
                style={{
                  padding: '0.6rem 1.2rem', border: 'none', background: 'none', cursor: 'pointer',
                  fontWeight: 600, fontSize: '0.875rem',
                  color: activeTab === tab ? '#3b82f6' : '#64748b',
                  borderBottom: activeTab === tab ? '2px solid #3b82f6' : '2px solid transparent',
                  transition: 'all 0.15s', marginBottom: '-1px', whiteSpace: 'nowrap',
                }}
              >
                {tab === 'positions'
                  ? `Open Positions (${positions.length})`
                  : tab === 'orders'
                  ? `Active Orders (${activeOrders.length})`
                  : `Trade Log (${tradeLog.length})`}
              </button>
            ))}
          </div>

          {/* ── Positions ── */}
          {activeTab === 'positions' && (
            positions.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}>
                <Activity size={32} style={{ marginBottom: '0.75rem', opacity: 0.4 }} />
                <p>No open positions</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {positions.map((pos, idx) => {
                  const pnl = pos.unrealizedPnl ?? pos.unrealized_pnl ?? pos.pnl;
                  const market = pos.market ?? pos.pair ?? '—';
                  const entryPrice = pos.entry_price ?? pos.entryPrice;
                  const markPrice = pos.mark_price ?? pos.markPrice ?? pos.current_price;
                  const liqPrice = pos.liq_price ?? pos.liquidationPrice;
                  const isBull = (pos.side ?? '').toLowerCase() === 'buy' || (pos.side ?? '').toLowerCase() === 'long';
                  const posId = pos.id ?? pos.positionId ?? String(idx);
                  return (
                    <div key={posId} style={{
                      ...cardStyle, padding: '0.9rem 1rem',
                      borderLeft: `3px solid ${isBull ? '#10b981' : '#ef4444'}`,
                    }}>
                      {/* top row */}
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.6rem' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <span style={{ fontWeight: 700, fontSize: '0.95rem' }}>{market}</span>
                          <span style={{
                            padding: '0.15rem 0.5rem', borderRadius: '999px', fontSize: '0.72rem', fontWeight: 700,
                            background: isBull ? 'rgba(16,185,129,0.2)' : 'rgba(239,68,68,0.2)',
                            color: isBull ? '#10b981' : '#ef4444',
                          }}>
                            {isBull ? '▲ LONG' : '▼ SHORT'} {pos.leverage ? `${pos.leverage}×` : ''}
                          </span>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <span style={{ fontSize: '1rem', fontWeight: 700, color: pnlColor(pnl) }}>
                            {pnl !== undefined ? `${pnl >= 0 ? '+' : ''}${fmt(pnl, 4)} USDT` : '—'}
                          </span>
                          <button
                            onClick={() => handleClosePosition(pos)}
                            title="Close Position"
                            style={{
                              background: '#7f1d1d', border: 'none', borderRadius: '0.25rem',
                              color: '#fca5a5', cursor: 'pointer', padding: '0.25rem 0.5rem',
                              fontSize: '0.75rem', fontWeight: 700,
                            }}
                          >
                            Close
                          </button>
                        </div>
                      </div>

                      {/* detail grid */}
                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '0.5rem', fontSize: '0.8rem' }}>
                        {[
                          { label: 'Size', value: fmt(pos.size, 4) },
                          { label: 'Entry', value: entryPrice ? `$${fmt(entryPrice)}` : '—' },
                          { label: 'Mark', value: markPrice ? `$${fmt(markPrice)}` : '—' },
                          { label: 'Liq. Price', value: liqPrice ? `$${fmt(liqPrice)}` : '—' },
                          { label: 'Margin', value: pos.margin ? `${fmt(pos.margin, 4)} USDT` : '—' },
                          { label: 'PnL %', value: entryPrice && pnl != null ? `${(((pnl / (Number(entryPrice) * Number(pos.size || 1))) * 100)).toFixed(2)}%` : '—' },
                        ].map(({ label, value }) => (
                          <div key={label}>
                            <div style={{ color: '#64748b', marginBottom: '0.1rem' }}>{label}</div>
                            <div style={{ color: '#f1f5f9', fontWeight: 600 }}>{value}</div>
                          </div>
                        ))}
                      </div>
                    </div>
                  );
                })}
              </div>
            )
          )}

          {/* ── Active Orders ── */}
          {activeTab === 'orders' && (
            activeOrders.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}>
                <Activity size={32} style={{ marginBottom: '0.75rem', opacity: 0.4 }} />
                <p>No active orders</p>
              </div>
            ) : (
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid #334155' }}>
                      {['Contract', 'Side', 'Type', 'Price', 'Quantity', 'Target', 'Status', ''].map(h => (
                        <th key={h} style={{ padding: '0.5rem 0.75rem', textAlign: 'left', color: '#64748b', fontWeight: 600, whiteSpace: 'nowrap' }}>
                          {h}
                        </th>
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
                          <td style={{ padding: '0.6rem 0.75rem', color: isBuy ? '#10b981' : '#ef4444', fontWeight: 700 }}>
                            {isBuy ? '▲ Buy' : '▼ Sell'}
                          </td>
                          <td style={{ padding: '0.6rem 0.75rem', color: '#94a3b8', textTransform: 'capitalize' }}>
                            {String(orderType).replace(/_/g, ' ')}
                          </td>
                          <td style={{ padding: '0.6rem 0.75rem' }}>${fmt(order.price)}</td>
                          <td style={{ padding: '0.6rem 0.75rem' }}>{fmt(order.total_quantity || order.totalQuantity, 4)}</td>
                          <td style={{ padding: '0.6rem 0.75rem' }}>${fmt(targetPrice)}</td>
                          <td style={{ padding: '0.6rem 0.75rem' }}>
                            <span style={{
                              padding: '0.15rem 0.5rem', borderRadius: '999px', fontSize: '0.72rem',
                              background: 'rgba(245,158,11,0.2)', color: '#f59e0b',
                            }}>
                              {order.status ?? 'open'}
                            </span>
                          </td>
                          <td style={{ padding: '0.6rem 0.75rem' }}>
                            <button
                              onClick={() => handleCancelOrder(orderId)}
                              title="Cancel order"
                              style={{
                                background: 'none', border: '1px solid #334155', borderRadius: '0.25rem',
                                color: '#ef4444', cursor: 'pointer', padding: '0.2rem 0.4rem',
                                display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.75rem',
                              }}
                            >
                              <X size={12} /> Cancel
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )
          )}

          {/* ── Trade Log ── */}
          {activeTab === 'trade-log' && (
            <div>
              {/* PnL Summary bar */}
              {pnlTotals && (
                <div style={{
                  display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '0.75rem',
                  marginBottom: '1rem',
                }}>
                  {[
                    {
                      label: 'Total Realized PnL',
                      value: `${pnlTotals.realizedUsdt >= 0 ? '+' : ''}${fmt(pnlTotals.realizedUsdt, 4)} USDT`,
                      color: pnlColor(pnlTotals.realizedUsdt),
                    },
                    {
                      label: 'Total Unrealized PnL',
                      value: `${pnlTotals.unrealizedUsdt >= 0 ? '+' : ''}${fmt(pnlTotals.unrealizedUsdt, 4)} USDT`,
                      color: pnlColor(pnlTotals.unrealizedUsdt),
                    },
                    {
                      label: 'Total Fees Paid',
                      value: `${fmt(tradeLog.reduce((s, e) => s + (e.fees ?? 0), 0), 4)} USDT`,
                      color: '#f59e0b',
                    },
                    {
                      label: 'Win Rate',
                      value: tradeLog.length
                        ? `${((tradeLog.filter(e => (e.realizedPnl ?? 0) > 0).length / tradeLog.length) * 100).toFixed(0)}%  (${tradeLog.filter(e => (e.realizedPnl ?? 0) > 0).length}W / ${tradeLog.filter(e => (e.realizedPnl ?? 0) <= 0).length}L)`
                        : '—',
                      color: '#94a3b8',
                    },
                  ].map(({ label, value, color }) => (
                    <div key={label} style={{ ...cardStyle, padding: '0.6rem 0.75rem' }}>
                      <div style={{ fontSize: '0.7rem', color: '#64748b', marginBottom: '0.25rem' }}>{label}</div>
                      <div style={{ fontWeight: 700, color, fontSize: '0.9rem' }}>{value}</div>
                    </div>
                  ))}
                </div>
              )}

              {tradeLogLoading ? (
                <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}>
                  <RefreshCw size={28} style={{ marginBottom: '0.75rem', opacity: 0.5 }} className="spin" />
                  <p>Loading trade history…</p>
                </div>
              ) : tradeLog.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '3rem', color: '#475569' }}>
                  <BookOpen size={32} style={{ marginBottom: '0.75rem', opacity: 0.4 }} />
                  <p>No closed positions yet</p>
                </div>
              ) : (
                <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.8rem' }}>
                    <thead>
                      <tr style={{ borderBottom: '2px solid #334155' }}>
                        {['Time', 'Contract', 'Side', 'Entry', 'Exit', 'Qty', 'Lev', 'TP Set', 'SL Set', 'Outcome', 'Gross PnL', 'Fees', 'Net PnL', 'ROI'].map(h => (
                          <th key={h} style={{
                            padding: '0.5rem 0.6rem', textAlign: 'left',
                            color: '#64748b', fontWeight: 600, whiteSpace: 'nowrap', fontSize: '0.75rem',
                          }}>
                            {h}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {tradeLog.map((entry, idx) => {
                        const isBull = (entry.side ?? '').toLowerCase() === 'buy' || (entry.side ?? '').toLowerCase() === 'long';
                        const gross = entry.realizedPnl;
                        const net = entry.netPnl;
                        const ts = entry.timestamp;
                        const dateStr = ts
                          ? new Date(typeof ts === 'number' && ts > 1e12 ? ts : Number(ts) * 1000).toLocaleString([], {
                              month: 'short', day: '2-digit', hour: '2-digit', minute: '2-digit',
                            })
                          : '—';
                        let outcomeLabel = '—';
                        let outcomeColor = '#64748b';
                        if (entry.liquidated) { outcomeLabel = '⚡ Liq'; outcomeColor = '#f97316'; }
                        else if (entry.tpHit) { outcomeLabel = '✓ TP'; outcomeColor = '#10b981'; }
                        else if (entry.slHit) { outcomeLabel = '✗ SL'; outcomeColor = '#ef4444'; }
                        else if (gross !== undefined && gross > 0) { outcomeLabel = '+ Win'; outcomeColor = '#10b981'; }
                        else if (gross !== undefined && gross < 0) { outcomeLabel = '− Loss'; outcomeColor = '#ef4444'; }
                        else if (gross !== undefined) { outcomeLabel = '= Even'; outcomeColor = '#94a3b8'; }
                        return (
                          <tr key={entry.positionId ?? idx} style={{
                            borderBottom: '1px solid #1e293b',
                            background: idx % 2 === 0 ? 'transparent' : 'rgba(255,255,255,0.015)',
                          }}>
                            <td style={{ padding: '0.55rem 0.6rem', color: '#475569', whiteSpace: 'nowrap', fontSize: '0.75rem' }}>{dateStr}</td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, whiteSpace: 'nowrap' }}>{entry.pair}</td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: isBull ? '#10b981' : '#ef4444', whiteSpace: 'nowrap' }}>
                              {isBull ? '▲ Long' : '▼ Short'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', whiteSpace: 'nowrap' }}>
                              {entry.entryPrice ? `$${fmt(entry.entryPrice)}` : '—'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', whiteSpace: 'nowrap' }}>
                              {entry.exitPrice ? `$${fmt(entry.exitPrice)}` : '—'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', whiteSpace: 'nowrap' }}>{fmt(entry.quantity, 4)}</td>
                            <td style={{ padding: '0.55rem 0.6rem', color: '#f59e0b' }}>
                              {entry.leverage ? `${entry.leverage}×` : '—'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', color: '#10b981', whiteSpace: 'nowrap' }}>
                              {entry.tpPrice ? `$${fmt(entry.tpPrice)}` : '—'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', color: '#ef4444', whiteSpace: 'nowrap' }}>
                              {entry.slPrice ? `$${fmt(entry.slPrice)}` : '—'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: outcomeColor, whiteSpace: 'nowrap' }}>
                              {outcomeLabel}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: pnlColor(gross), whiteSpace: 'nowrap' }}>
                              {gross !== undefined ? `${gross >= 0 ? '+' : ''}${fmt(gross, 4)}` : '—'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', color: '#f59e0b', whiteSpace: 'nowrap' }}>
                              {entry.fees ? `-${fmt(entry.fees, 4)}` : '0.0000'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: pnlColor(net), whiteSpace: 'nowrap' }}>
                              {net !== undefined ? `${net >= 0 ? '+' : ''}${fmt(net, 4)}` : '—'}
                            </td>
                            <td style={{ padding: '0.55rem 0.6rem', color: pnlColor(entry.roi), whiteSpace: 'nowrap' }}>
                              {entry.roi !== undefined ? `${entry.roi >= 0 ? '+' : ''}${fmt(entry.roi, 2)}%` : '—'}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                    {/* Footer totals row */}
                    <tfoot>
                      <tr style={{ borderTop: '2px solid #334155', background: 'rgba(59,130,246,0.05)' }}>
                        <td colSpan={10} style={{ padding: '0.55rem 0.6rem', color: '#64748b', fontWeight: 700, fontSize: '0.75rem' }}>
                          TOTAL ({tradeLog.length} trades)
                        </td>
                        <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, whiteSpace: 'nowrap',
                          color: pnlColor(tradeLog.reduce((s, e) => s + (e.realizedPnl ?? 0), 0)) }}>
                          {fmt(tradeLog.reduce((s, e) => s + (e.realizedPnl ?? 0), 0), 4)}
                        </td>
                        <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, color: '#f59e0b', whiteSpace: 'nowrap' }}>
                          -{fmt(tradeLog.reduce((s, e) => s + (e.fees ?? 0), 0), 4)}
                        </td>
                        <td style={{ padding: '0.55rem 0.6rem', fontWeight: 700, whiteSpace: 'nowrap',
                          color: pnlColor(tradeLog.reduce((s, e) => s + (e.netPnl ?? 0), 0)) }}>
                          {fmt(tradeLog.reduce((s, e) => s + (e.netPnl ?? 0), 0), 4)}
                        </td>
                        <td style={{ padding: '0.55rem 0.6rem' }} />
                      </tr>
                    </tfoot>
                  </table>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CoinDCXTradingPage;
