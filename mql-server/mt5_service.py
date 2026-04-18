"""
MetaTrader 5 service module.
Handles connection and trade operations via the MT5 Python SDK.
"""

import os
import math
import logging
import datetime
import MetaTrader5 as mt5
from dotenv import load_dotenv

logger = logging.getLogger(__name__)

load_dotenv()

MT5_LOGIN   = int(os.getenv("MT5_LOGIN", "0"))
MT5_PASSWORD = os.getenv("MT5_PASSWORD", "")
MT5_SERVER  = os.getenv("MT5_SERVER", "")
MT5_PATH    = os.getenv("MT5_PATH", "")


# ---------------------------------------------------------------------------
# connection helpers
# ---------------------------------------------------------------------------

def connect() -> dict:
    """Initialise and log into MT5.  Returns a status dict."""
    kwargs = {}
    if MT5_PATH:
        kwargs["path"] = MT5_PATH

    logger.debug("MT5 connect: calling initialize() path=%r", MT5_PATH or "<default>")
    if not mt5.initialize(**kwargs):
        err = mt5.last_error()
        logger.debug("MT5 connect: initialize() failed — %s", err)
        return {"ok": False, "error": f"initialize() failed: {err}"}

    logger.debug("MT5 connect: initialize() succeeded")

    if MT5_LOGIN:
        logger.debug("MT5 connect: logging in as account=%s server=%r", MT5_LOGIN, MT5_SERVER)
        authorised = mt5.login(MT5_LOGIN, password=MT5_PASSWORD, server=MT5_SERVER)
        if not authorised:
            err = mt5.last_error()
            logger.debug("MT5 connect: login() failed — %s", err)
            mt5.shutdown()
            return {"ok": False, "error": f"login() failed: {err}"}
        logger.debug("MT5 connect: login() succeeded")

    info = mt5.account_info()
    if info is None:
        err = mt5.last_error()
        logger.debug("MT5 connect: account_info() returned None — %s", err)
        mt5.shutdown()
        return {"ok": False, "error": "account_info() returned None"}

    logger.debug(
        "MT5 connect: ready — account=%s server=%r balance=%s %s leverage=1:%s",
        info.login, info.server, info.balance, info.currency, info.leverage,
    )
    return {
        "ok": True,
        "account": info.login,
        "server": info.server,
        "currency": info.currency,
        "balance": info.balance,
        "leverage": info.leverage,
    }


def disconnect():
    logger.debug("MT5 disconnect: calling shutdown()")
    mt5.shutdown()
    logger.debug("MT5 disconnect: shutdown() done")


# ---------------------------------------------------------------------------
# lot-size calculation
# ---------------------------------------------------------------------------

def _lot_for_margin(symbol: str, margin_usdt: float, order_type: int) -> float | None:
    """
    Return the smallest lot that requires AT LEAST *margin_usdt* USD of margin.
    Uses MT5's order_calc_margin() to avoid hard-coded contract specs.

    Returns None on failure.
    """
    info = mt5.symbol_info(symbol)
    if info is None:
        logger.debug("_lot_for_margin: symbol_info(%s) returned None — %s", symbol, mt5.last_error())
        return None

    step    = info.volume_step
    min_lot = info.volume_min
    max_lot = info.volume_max

    tick = mt5.symbol_info_tick(symbol)
    if tick is None:
        logger.debug("_lot_for_margin: symbol_info_tick(%s) returned None — %s", symbol, mt5.last_error())
        return None
    price = tick.ask

    logger.debug(
        "_lot_for_margin: symbol=%s ask=%s step=%s min=%s max=%s target_margin=%s",
        symbol, price, step, min_lot, max_lot, margin_usdt,
    )

    # Sanity-check that order_calc_margin works at all
    probe = mt5.order_calc_margin(order_type, symbol, min_lot, price)
    if probe is None:
        logger.debug("_lot_for_margin: order_calc_margin probe failed — %s", mt5.last_error())
        return None
    logger.debug("_lot_for_margin: margin for min_lot(%s) = %s", min_lot, probe)

    # If even the minimum lot already covers the requested margin, use it
    if probe >= margin_usdt:
        logger.debug("_lot_for_margin: min_lot margin %s >= target %s, using min_lot=%s", probe, margin_usdt, min_lot)
        return min_lot

    # Binary search between min_lot and max_lot
    lo, hi = min_lot, max_lot
    candidate = None

    for _ in range(60):
        mid = round(round((lo + hi) / 2 / step) * step, 8)
        m   = mt5.order_calc_margin(order_type, symbol, mid, price)
        if m is None:
            logger.debug("_lot_for_margin: order_calc_margin(mid=%s) returned None — %s", mid, mt5.last_error())
            return None
        if m >= margin_usdt:
            candidate = mid
            hi = mid
        else:
            lo = mid
        if hi - lo < step * 0.5:
            break

    if candidate is None:
        candidate = min_lot

    candidate = max(min_lot, min(max_lot, candidate))
    candidate = round(round(candidate / step) * step, 8)
    logger.debug("_lot_for_margin: chosen lot=%s", candidate)
    return candidate


# ---------------------------------------------------------------------------
# filling mode detection
# ---------------------------------------------------------------------------

def _filling_mode(sym_info) -> int:
    """
    Return the best ORDER_FILLING_* constant supported by the symbol.
    MT5 encodes allowed filling modes as a bitmask in filling_mode:
      bit 0 → FOK   (1)
      bit 1 → IOC   (2)
      bit 2 → RETURN (4 / BOC on some builds)
    We prefer RETURN → IOC → FOK in that order (RETURN works on most demo servers).
    """
    mask = sym_info.filling_mode
    logger.debug("_filling_mode: symbol=%s filling_mode_mask=%s", sym_info.name, mask)
    if mask & 4:   # RETURN
        return mt5.ORDER_FILLING_RETURN
    if mask & 2:   # IOC
        return mt5.ORDER_FILLING_IOC
    return mt5.ORDER_FILLING_FOK


# ---------------------------------------------------------------------------
# open position  (generic — any symbol, long or short)
# ---------------------------------------------------------------------------

def open_position(
    symbol: str,
    direction: str,       # "long" or "short"
    margin_usdt: float,
) -> dict:
    """
    Open a market position on *symbol* in *direction* ("long"/"short").
    Lot size is calculated so that the required margin ≈ *margin_usdt* USD.
    """
    symbol    = symbol.upper()
    direction = direction.lower()
    if direction not in ("long", "short"):
        return {"ok": False, "error": "direction must be 'long' or 'short'"}

    order_type = mt5.ORDER_TYPE_BUY if direction == "long" else mt5.ORDER_TYPE_SELL

    # Ensure symbol is in MarketWatch
    if not mt5.symbol_select(symbol, True):
        err = mt5.last_error()
        logger.debug("open_position: symbol_select(%s) failed — %s", symbol, err)
        return {"ok": False, "error": f"symbol_select({symbol}) failed: {err}"}

    sym_info = mt5.symbol_info(symbol)
    if sym_info is None:
        err = mt5.last_error()
        logger.debug("open_position: symbol_info(%s) returned None — %s", symbol, err)
        return {"ok": False, "error": f"symbol_info({symbol}) returned None"}

    tick = mt5.symbol_info_tick(symbol)
    if tick is None:
        err = mt5.last_error()
        logger.debug("open_position: symbol_info_tick(%s) returned None — %s", symbol, err)
        return {"ok": False, "error": f"symbol_info_tick({symbol}) returned None"}

    price = tick.ask if direction == "long" else tick.bid
    logger.debug("open_position: symbol=%s direction=%s price=%s", symbol, direction, price)

    lot = _lot_for_margin(symbol, margin_usdt, order_type)
    if lot is None or lot <= 0:
        logger.debug("open_position: lot calculation failed (got %s)", lot)
        return {"ok": False, "error": "Could not calculate lot size from margin"}

    required_margin = mt5.order_calc_margin(order_type, symbol, lot, price)
    logger.debug("open_position: lot=%s required_margin=%s", lot, required_margin)

    filling_type = _filling_mode(sym_info)
    logger.debug("open_position: filling mode=%s", filling_type)

    req = {
        "action":       mt5.TRADE_ACTION_DEAL,
        "symbol":       symbol,
        "volume":       lot,
        "type":         order_type,
        "price":        price,
        "deviation":    20,
        "magic":        20260413,
        "comment":      f"flask {direction} {margin_usdt}usdt",
        "type_time":    mt5.ORDER_TIME_GTC,
        "type_filling": filling_type,
    }
    logger.debug("open_position: order_send request=%s", req)

    result = mt5.order_send(req)
    if result is None:
        err = mt5.last_error()
        logger.debug("open_position: order_send() returned None — %s", err)
        return {"ok": False, "error": f"order_send() returned None: {err}"}

    logger.debug(
        "open_position: order_send retcode=%s comment=%r order=%s",
        result.retcode, result.comment, result.order,
    )

    if result.retcode != mt5.TRADE_RETCODE_DONE:
        return {
            "ok":      False,
            "retcode": result.retcode,
            "comment": result.comment,
            "error":   f"order_send failed (retcode={result.retcode})",
        }

    return {
        "ok":              True,
        "ticket":          result.order,
        "symbol":          symbol,
        "direction":       direction,
        "lot":             lot,
        "price":           result.price,
        "margin_usdt":     margin_usdt,
        "required_margin": required_margin,
        "comment":         result.comment,
    }


# backward-compat shim
def open_long_xauusd(margin_usdt: float = 5.0) -> dict:
    return open_position("XAUUSD", "long", margin_usdt)


# ---------------------------------------------------------------------------
# TP / SL  (modify an existing open position)
# ---------------------------------------------------------------------------

def set_tpsl(
    ticket: int,
    tp: float | None = None,
    sl: float | None = None,
) -> dict:
    """
    Set / update take-profit and/or stop-loss on an open position identified
    by *ticket*.  Pass None to leave that level unchanged.
    At least one of tp/sl must be provided.
    """
    if tp is None and sl is None:
        return {"ok": False, "error": "At least one of tp or sl must be provided"}

    # Locate the open position
    positions = mt5.positions_get(ticket=ticket)
    if positions is None or len(positions) == 0:
        err = mt5.last_error()
        logger.debug("set_tpsl: positions_get(ticket=%s) returned nothing — %s", ticket, err)
        return {"ok": False, "error": f"No open position found with ticket {ticket}"}

    pos = positions[0]
    logger.debug(
        "set_tpsl: found position ticket=%s symbol=%s type=%s sl=%s tp=%s",
        pos.ticket, pos.symbol, pos.type, pos.sl, pos.tp,
    )

    # Get symbol info to normalise price precision
    sym_info = mt5.symbol_info(pos.symbol)
    if sym_info is None:
        err = mt5.last_error()
        logger.debug("set_tpsl: symbol_info(%s) returned None — %s", pos.symbol, err)
        return {"ok": False, "error": f"symbol_info({pos.symbol}) returned None: {err}"}
    digits = sym_info.digits

    # Keep existing value when caller passes None; round to symbol precision
    new_sl = round(sl, digits) if sl is not None else pos.sl
    new_tp = round(tp, digits) if tp is not None else pos.tp

    req = {
        "action":   mt5.TRADE_ACTION_SLTP,
        "position": ticket,
        "symbol":   pos.symbol,
        "sl":       new_sl,
        "tp":       new_tp,
    }
    logger.debug("set_tpsl: order_send request=%s", req)

    result = mt5.order_send(req)
    if result is None:
        err = mt5.last_error()
        logger.debug("set_tpsl: order_send() returned None — %s", err)
        return {"ok": False, "error": f"order_send() returned None: {err}"}

    logger.debug(
        "set_tpsl: order_send retcode=%s comment=%r",
        result.retcode, result.comment,
    )

    if result.retcode != mt5.TRADE_RETCODE_DONE:
        return {
            "ok":      False,
            "retcode": result.retcode,
            "comment": result.comment,
            "error":   f"order_send failed (retcode={result.retcode})",
        }

    return {
        "ok":     True,
        "ticket": ticket,
        "symbol": pos.symbol,
        "sl":     new_sl,
        "tp":     new_tp,
    }


# ---------------------------------------------------------------------------
# account snapshot (utility)
# ---------------------------------------------------------------------------

def account_snapshot() -> dict:
    info = mt5.account_info()
    if info is None:
        return {"ok": False, "error": "account_info() returned None"}
    return {
        "ok":        True,
        "login":     info.login,
        "server":    info.server,
        "currency":  info.currency,
        "balance":   info.balance,
        "equity":    info.equity,
        "margin":    info.margin,
        "free_margin": info.margin_free,
        "leverage":  info.leverage,
    }


# ---------------------------------------------------------------------------
# balance
# ---------------------------------------------------------------------------

def get_balance() -> dict:
    """Return wallet balance details from the account."""
    info = mt5.account_info()
    if info is None:
        err = mt5.last_error()
        logger.debug("get_balance: account_info() returned None — %s", err)
        return {"ok": False, "error": f"account_info() returned None: {err}"}
    logger.debug("get_balance: balance=%s equity=%s margin_free=%s", info.balance, info.equity, info.margin_free)
    return {
        "ok":          True,
        "currency":    info.currency,
        "balance":     info.balance,
        "equity":      info.equity,
        "margin":      info.margin,
        "free_margin": info.margin_free,
        "margin_level": info.margin_level,
        "profit":      info.profit,
    }


# ---------------------------------------------------------------------------
# leverage
# ---------------------------------------------------------------------------

def get_leverage() -> dict:
    """Return account leverage."""
    info = mt5.account_info()
    if info is None:
        err = mt5.last_error()
        logger.debug("get_leverage: account_info() returned None — %s", err)
        return {"ok": False, "error": f"account_info() returned None: {err}"}
    logger.debug("get_leverage: leverage=1:%s", info.leverage)
    return {
        "ok":       True,
        "leverage": info.leverage,
        "display":  f"1:{info.leverage}",
    }


# ---------------------------------------------------------------------------
# open positions
# ---------------------------------------------------------------------------

def get_open_positions(symbol: str | None = None) -> dict:
    """
    Return all open positions, optionally filtered by *symbol*.
    """
    kwargs = {}
    if symbol:
        symbol = symbol.upper()
        if not mt5.symbol_select(symbol, True):
            logger.debug("get_open_positions: symbol_select(%s) failed", symbol)
        kwargs["symbol"] = symbol

    positions = mt5.positions_get(**kwargs)
    if positions is None:
        err = mt5.last_error()
        logger.debug("get_open_positions: positions_get() failed — %s", err)
        return {"ok": False, "error": f"positions_get() failed: {err}"}

    logger.debug("get_open_positions: found %d position(s)", len(positions))

    _type_map = {mt5.POSITION_TYPE_BUY: "buy", mt5.POSITION_TYPE_SELL: "sell"}

    return {
        "ok":    True,
        "count": len(positions),
        "positions": [
            {
                "ticket":      p.ticket,
                "symbol":      p.symbol,
                "type":        _type_map.get(p.type, str(p.type)),
                "volume":      p.volume,
                "open_price":  p.price_open,
                "current_price": p.price_current,
                "sl":          p.sl,
                "tp":          p.tp,
                "profit":      p.profit,
                "swap":        p.swap,
                "margin":      None,  # not exposed by positions_get
                "comment":     p.comment,
                "magic":       p.magic,
                "open_time":   p.time,
            }
            for p in positions
        ],
    }


# ---------------------------------------------------------------------------
# open (pending) orders
# ---------------------------------------------------------------------------

def get_open_orders(symbol: str | None = None) -> dict:
    """
    Return all pending orders, optionally filtered by *symbol*.
    """
    kwargs = {}
    if symbol:
        symbol = symbol.upper()
        if not mt5.symbol_select(symbol, True):
            logger.debug("get_open_orders: symbol_select(%s) failed", symbol)
        kwargs["symbol"] = symbol

    orders = mt5.orders_get(**kwargs)
    if orders is None:
        err = mt5.last_error()
        logger.debug("get_open_orders: orders_get() failed — %s", err)
        return {"ok": False, "error": f"orders_get() failed: {err}"}

    logger.debug("get_open_orders: found %d order(s)", len(orders))

    _type_map = {
        mt5.ORDER_TYPE_BUY:             "buy_market",
        mt5.ORDER_TYPE_SELL:            "sell_market",
        mt5.ORDER_TYPE_BUY_LIMIT:       "buy_limit",
        mt5.ORDER_TYPE_SELL_LIMIT:      "sell_limit",
        mt5.ORDER_TYPE_BUY_STOP:        "buy_stop",
        mt5.ORDER_TYPE_SELL_STOP:       "sell_stop",
        mt5.ORDER_TYPE_BUY_STOP_LIMIT:  "buy_stop_limit",
        mt5.ORDER_TYPE_SELL_STOP_LIMIT: "sell_stop_limit",
    }

    return {
        "ok":    True,
        "count": len(orders),
        "orders": [
            {
                "ticket":       o.ticket,
                "symbol":       o.symbol,
                "type":         _type_map.get(o.type, str(o.type)),
                "volume":       o.volume_current,
                "volume_init":  o.volume_initial,
                "open_price":   o.price_open,
                "stop_limit":   o.price_stoplimit,
                "sl":           o.sl,
                "tp":           o.tp,
                "comment":      o.comment,
                "magic":        o.magic,
                "setup_time":   o.time_setup,
                "expiry_time":  o.time_expiration,
            }
            for o in orders
        ],
    }


# ---------------------------------------------------------------------------
# helpers for history queries
# ---------------------------------------------------------------------------

def _parse_date(value: str | None, default: datetime.datetime) -> datetime.datetime:
    """Parse an ISO-8601 date/datetime string, or return *default*.

    The returned datetime is always timezone-aware (UTC), which is required
    by the MT5 Python API for history_deals_get / history_orders_get.
    """
    if not value:
        return default
    for fmt in ("%Y-%m-%dT%H:%M:%S", "%Y-%m-%d"):
        try:
            dt = datetime.datetime.strptime(value, fmt)
            return dt.replace(tzinfo=datetime.timezone.utc)
        except ValueError:
            continue
    raise ValueError(f"Cannot parse date: {value!r} (expected YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS)")


_DEAL_TYPE_MAP = {
    0:  "buy",
    1:  "sell",
    2:  "balance",
    3:  "credit",
    4:  "charge",
    5:  "correction",
    6:  "bonus",
    7:  "commission",
    8:  "commission_daily",
    9:  "commission_monthly",
    10: "commission_agent_daily",
    11: "commission_agent_monthly",
    12: "interest",
    13: "buy_cancelled",
    14: "sell_cancelled",
}

_DEAL_ENTRY_MAP = {
    0: "in",
    1: "out",
    2: "inout",
    3: "out_by",
}


# ---------------------------------------------------------------------------
# trade history  (all executed deals)
# ---------------------------------------------------------------------------

def get_trade_history(
    date_from: str | None = None,
    date_to:   str | None = None,
    symbol:    str | None = None,
) -> dict:
    """
    Return all executed deals (fills) in the requested date range.

    *date_from* / *date_to*: ISO-8601 strings "YYYY-MM-DD" or
    "YYYY-MM-DDTHH:MM:SS".  Default window is the last 30 days.
    """
    now     = datetime.datetime.now(datetime.timezone.utc)
    dt_from = _parse_date(date_from, now - datetime.timedelta(days=30))
    dt_to   = _parse_date(date_to,   now)

    logger.debug(
        "get_trade_history: from=%s to=%s symbol=%s",
        dt_from.isoformat(), dt_to.isoformat(), symbol,
    )

    kwargs: dict = {"date_from": dt_from, "date_to": dt_to}
    if symbol:
        kwargs["group"] = f"*{symbol.upper()}*"

    deals = mt5.history_deals_get(**kwargs)
    if deals is None:
        err = mt5.last_error()
        logger.debug("get_trade_history: history_deals_get() failed — %s", err)
        return {"ok": False, "error": f"history_deals_get() failed: {err}"}

    logger.debug("get_trade_history: found %d deal(s)", len(deals))

    return {
        "ok":    True,
        "count": len(deals),
        "from":  dt_from.isoformat(),
        "to":    dt_to.isoformat(),
        "deals": [
            {
                "ticket":       d.ticket,
                "order":        d.order,
                "position_id":  d.position_id,
                "symbol":       d.symbol,
                "type":         _DEAL_TYPE_MAP.get(d.type, str(d.type)),
                "entry":        _DEAL_ENTRY_MAP.get(d.entry, str(d.entry)),
                "volume":       d.volume,
                "price":        d.price,
                "sl":           d.sl,
                "tp":           d.tp,
                "profit":       d.profit,
                "swap":         d.swap,
                "commission":   d.commission,
                "fee":          d.fee,
                "comment":      d.comment,
                "magic":        d.magic,
                "time":         d.time,
            }
            for d in deals
        ],
    }


# ---------------------------------------------------------------------------
# PnL history  (closed positions, aggregated per position_id)
# ---------------------------------------------------------------------------


def get_position_transactions(ticket: int) -> dict:
    """
    Return every deal (transaction) associated with a specific position ticket.

    Uses ``history_deals_get(position=ticket)`` which searches MT5's deal
    history by ``position_id``.  This covers the entire life of the position
    (open fill, any partial closes, swap/commission charges, final close).

    Returns a dict with:
      ok          bool
      ticket      int          — position ticket queried
      count       int
      deals       list[dict]   — each deal row
    """
    logger.debug("get_position_transactions: position ticket=%s", ticket)

    deals = mt5.history_deals_get(position=ticket)
    if deals is None:
        err = mt5.last_error()
        logger.debug(
            "get_position_transactions: history_deals_get(position=%s) failed — %s",
            ticket, err,
        )
        return {"ok": False, "error": f"history_deals_get() failed: {err}"}

    logger.debug(
        "get_position_transactions: found %d deal(s) for position=%s",
        len(deals), ticket,
    )

    return {
        "ok":     True,
        "ticket": ticket,
        "count":  len(deals),
        "deals": [
            {
                "ticket":      d.ticket,
                "order":       d.order,
                "position_id": d.position_id,
                "symbol":      d.symbol,
                "type":        _DEAL_TYPE_MAP.get(d.type, str(d.type)),
                "entry":       _DEAL_ENTRY_MAP.get(d.entry, str(d.entry)),
                "volume":      d.volume,
                "price":       d.price,
                "sl":          d.sl,
                "tp":          d.tp,
                "profit":      d.profit,
                "swap":        d.swap,
                "commission":  d.commission,
                "fee":         d.fee,
                "comment":     d.comment,
                "magic":       d.magic,
                "time":        d.time,
            }
            for d in deals
        ],
    }


# ---------------------------------------------------------------------------
# PnL history  (closed positions, aggregated per position_id)
# ---------------------------------------------------------------------------

def get_pnl_history(
    date_from: str | None = None,
    date_to:   str | None = None,
    symbol:    str | None = None,
) -> dict:
    """
    Return closed-position PnL, aggregated by position_id.

    Each row shows the opening deal, the closing deal, and the net
    profit/swap/commission for that round-trip trade.
    """
    raw = get_trade_history(date_from=date_from, date_to=date_to, symbol=symbol)
    if not raw["ok"]:
        return raw

    # Group deals by position_id
    from collections import defaultdict
    by_pos: dict = defaultdict(list)
    for d in raw["deals"]:
        if d["symbol"]:          # skip balance/credit lines with no symbol
            by_pos[d["position_id"]].append(d)

    closed_trades = []
    total_profit = 0.0

    for pos_id, deals in by_pos.items():
        entry_deal  = next((d for d in deals if d["entry"] == "in"),    None)
        exit_deal   = next((d for d in deals if d["entry"] == "out"),   None)
        # Some brokers use inout for hedging
        if exit_deal is None:
            exit_deal = next((d for d in deals if d["entry"] == "inout"), None)

        if entry_deal is None:
            continue                 # skip if no opening deal in range

        net_profit     = sum(d["profit"]     for d in deals)
        net_swap       = sum(d["swap"]       for d in deals)
        net_commission = sum(d["commission"] for d in deals)
        net_fee        = sum(d["fee"]        for d in deals)
        net_total      = net_profit + net_swap + net_commission + net_fee

        total_profit += net_total

        closed_trades.append({
            "position_id":   pos_id,
            "symbol":        entry_deal["symbol"],
            "direction":     entry_deal["type"],   # "buy" / "sell"
            "volume":        entry_deal["volume"],
            "open_price":    entry_deal["price"],
            "open_time":     entry_deal["time"],
            "close_price":   exit_deal["price"]  if exit_deal else None,
            "close_time":    exit_deal["time"]   if exit_deal else None,
            "is_closed":     exit_deal is not None,
            "profit":        net_profit,
            "swap":          net_swap,
            "commission":    net_commission,
            "fee":           net_fee,
            "net_total":     net_total,
        })

    # Sort by open_time descending
    closed_trades.sort(key=lambda t: t["open_time"] or 0, reverse=True)

    logger.debug(
        "get_pnl_history: %d position(s), total_net_pnl=%s",
        len(closed_trades), round(total_profit, 2),
    )

    return {
        "ok":            True,
        "count":         len(closed_trades),
        "total_net_pnl": round(total_profit, 2),
        "from":          raw["from"],
        "to":            raw["to"],
        "trades":        closed_trades,
    }


# ---------------------------------------------------------------------------
# tradable symbols
# ---------------------------------------------------------------------------

def get_symbols(search: str | None = None) -> dict:
    """
    Return only fully-tradeable symbols (SYMBOL_TRADE_MODE_FULL).
    Optionally filter by *search* (case-insensitive substring match on name).
    """
    symbols = mt5.symbols_get()
    if symbols is None:
        err = mt5.last_error()
        logger.debug("get_symbols: symbols_get() failed — %s", err)
        return {"ok": False, "error": f"symbols_get() failed: {err}"}

    # Keep only symbols that allow full two-way trading
    symbols = [s for s in symbols if s.trade_mode == mt5.SYMBOL_TRADE_MODE_FULL]

    if search:
        needle = search.upper()
        symbols = [s for s in symbols if needle in s.name.upper()]

    logger.debug("get_symbols: returning %d tradeable symbol(s) search=%r", len(symbols), search)

    _trade_mode = {
        0: "disabled",
        1: "long_only",
        2: "short_only",
        4: "close_only",
        15: "full",
    }

    return {
        "ok":    True,
        "count": len(symbols),
        "symbols": [
            {
                "name":         s.name,
                "description":  s.description,
                "currency_base":   s.currency_base,
                "currency_profit": s.currency_profit,
                "currency_margin": s.currency_margin,
                "digits":       s.digits,
                "volume_min":   s.volume_min,
                "volume_max":   s.volume_max,
                "volume_step":  s.volume_step,
                "contract_size":s.trade_contract_size,
                "trade_mode":   _trade_mode.get(s.trade_mode, str(s.trade_mode)),
                "spread":       s.spread,
                "in_market_watch": bool(s.visible),
            }
            for s in symbols
        ],
    }


# ---------------------------------------------------------------------------
# candlesticks (OHLCV)
# ---------------------------------------------------------------------------

# MT5 timeframe constants
_TIMEFRAMES: dict[str, int] = {
    "M1":  mt5.TIMEFRAME_M1,
    "M2":  mt5.TIMEFRAME_M2,
    "M3":  mt5.TIMEFRAME_M3,
    "M4":  mt5.TIMEFRAME_M4,
    "M5":  mt5.TIMEFRAME_M5,
    "M6":  mt5.TIMEFRAME_M6,
    "M10": mt5.TIMEFRAME_M10,
    "M12": mt5.TIMEFRAME_M12,
    "M15": mt5.TIMEFRAME_M15,
    "M20": mt5.TIMEFRAME_M20,
    "M30": mt5.TIMEFRAME_M30,
    "H1":  mt5.TIMEFRAME_H1,
    "H2":  mt5.TIMEFRAME_H2,
    "H3":  mt5.TIMEFRAME_H3,
    "H4":  mt5.TIMEFRAME_H4,
    "H6":  mt5.TIMEFRAME_H6,
    "H8":  mt5.TIMEFRAME_H8,
    "H12": mt5.TIMEFRAME_H12,
    "D1":  mt5.TIMEFRAME_D1,
    "W1":  mt5.TIMEFRAME_W1,
    "MN1": mt5.TIMEFRAME_MN1,
}


def get_candlesticks(
    symbol:    str,
    timeframe: str  = "H1",
    count:     int  = 100,
    date_from: str | None = None,
    date_to:   str | None = None,
) -> dict:
    """
    Return OHLCV bars for *symbol* on *timeframe*.

    Either supply *count* (latest N bars from *date_to* / now),
    or supply *date_from* + *date_to* for a fixed date range.

    *timeframe*: M1 M2 M3 M4 M5 M6 M10 M12 M15 M20 M30
                 H1 H2 H3 H4 H6 H8 H12 D1 W1 MN1  (default H1)
    *count*: max bars to return (default 100, max 5000)
    """
    symbol = symbol.upper()
    tf_key = timeframe.upper()
    if tf_key not in _TIMEFRAMES:
        return {
            "ok":    False,
            "error": f"Unknown timeframe {timeframe!r}. Valid: {', '.join(_TIMEFRAMES)}",
        }
    tf = _TIMEFRAMES[tf_key]

    if not mt5.symbol_select(symbol, True):
        err = mt5.last_error()
        logger.debug("get_candlesticks: symbol_select(%s) failed — %s", symbol, err)
        return {"ok": False, "error": f"symbol_select({symbol}) failed: {err}"}

    count = min(max(1, count), 5000)

    if date_from:
        _now_utc = datetime.datetime.now(datetime.timezone.utc)
        dt_from = _parse_date(date_from, _now_utc - datetime.timedelta(days=30))
        dt_to   = _parse_date(date_to, _now_utc) if date_to else _now_utc
        logger.debug(
            "get_candlesticks: symbol=%s tf=%s from=%s to=%s",
            symbol, tf_key, dt_from.isoformat(), dt_to.isoformat(),
        )
        rates = mt5.copy_rates_range(symbol, tf, dt_from, dt_to)
    else:
        logger.debug("get_candlesticks: symbol=%s tf=%s count=%s", symbol, tf_key, count)
        rates = mt5.copy_rates_from_pos(symbol, tf, 0, count)

    if rates is None:
        err = mt5.last_error()
        logger.debug("get_candlesticks: copy_rates failed — %s", err)
        return {"ok": False, "error": f"copy_rates failed: {err}"}

    logger.debug("get_candlesticks: got %d bar(s)", len(rates))

    return {
        "ok":        True,
        "symbol":    symbol,
        "timeframe": tf_key,
        "count":     len(rates),
        "candles": [
            {
                "time":        int(r["time"]),
                "open":        float(r["open"]),
                "high":        float(r["high"]),
                "low":         float(r["low"]),
                "close":       float(r["close"]),
                "volume":      int(r["tick_volume"]),
                "real_volume": int(r["real_volume"]),
                "spread":      int(r["spread"]),
            }
            for r in rates
        ],
    }


# ---------------------------------------------------------------------------
# ticks
# ---------------------------------------------------------------------------

_TICK_FLAGS: dict[str, int] = {
    "all":   mt5.COPY_TICKS_ALL,
    "info":  mt5.COPY_TICKS_INFO,    # quote ticks (bid/ask)
    "trade": mt5.COPY_TICKS_TRADE,   # trade ticks (last/volume)
}


def get_ticks(
    symbol:    str,
    count:     int        = 100,
    date_from: str | None = None,
    date_to:   str | None = None,
    flags:     str        = "all",
) -> dict:
    """
    Return raw ticks for *symbol*.

    Either supply *count* (latest N ticks from *date_from* / now),
    or supply *date_from* + *date_to* for a fixed date range.

    *flags*: "all" | "info" (bid/ask) | "trade" (last/volume)  (default "all")
    *count*: max ticks (default 100, max 10 000)
    """
    symbol = symbol.upper()
    flag_key = flags.lower()
    if flag_key not in _TICK_FLAGS:
        return {
            "ok":    False,
            "error": f"Unknown flags {flags!r}. Valid: all, info, trade",
        }
    tick_flags = _TICK_FLAGS[flag_key]

    if not mt5.symbol_select(symbol, True):
        err = mt5.last_error()
        logger.debug("get_ticks: symbol_select(%s) failed — %s", symbol, err)
        return {"ok": False, "error": f"symbol_select({symbol}) failed: {err}"}

    count = min(max(1, count), 10_000)

    if date_from:
        _now_utc = datetime.datetime.now(datetime.timezone.utc)
        dt_from = _parse_date(date_from, _now_utc - datetime.timedelta(minutes=10))
        if date_to:
            dt_to = _parse_date(date_to, _now_utc)
            logger.debug(
                "get_ticks: symbol=%s flags=%s range %s → %s",
                symbol, flag_key, dt_from.isoformat(), dt_to.isoformat(),
            )
            ticks = mt5.copy_ticks_range(symbol, dt_from, dt_to, tick_flags)
        else:
            logger.debug(
                "get_ticks: symbol=%s flags=%s from=%s count=%s",
                symbol, flag_key, dt_from.isoformat(), count,
            )
            ticks = mt5.copy_ticks_from(symbol, dt_from, count, tick_flags)
    else:
        # latest N ticks
        dt_from = datetime.datetime.now(datetime.timezone.utc) - datetime.timedelta(seconds=1)
        logger.debug("get_ticks: symbol=%s flags=%s count=%s (latest)", symbol, flag_key, count)
        ticks = mt5.copy_ticks_from(symbol, dt_from, count, tick_flags)

    if ticks is None:
        err = mt5.last_error()
        logger.debug("get_ticks: copy_ticks failed — %s", err)
        return {"ok": False, "error": f"copy_ticks failed: {err}"}

    logger.debug("get_ticks: got %d tick(s)", len(ticks))

    return {
        "ok":     True,
        "symbol": symbol,
        "flags":  flag_key,
        "count":  len(ticks),
        "ticks": [
            {
                "time":      int(t["time"]),
                "time_msc":  int(t["time_msc"]),
                "bid":       float(t["bid"]),
                "ask":       float(t["ask"]),
                "last":      float(t["last"]),
                "volume":    int(t["volume"]),
                "volume_real": float(t["volume_real"]),
                "flags":     int(t["flags"]),
            }
            for t in ticks
        ],
    }


# ---------------------------------------------------------------------------
# close position
# ---------------------------------------------------------------------------


def get_market_trades(
    symbol: str,
    count:  int = 100,
) -> dict:
    """
    Return the most recent executed market trades (trade ticks) for *symbol*.

    A trade tick carries the ``last`` price and ``volume`` of each individual
    trade that took place on the exchange.  This is distinct from quote ticks
    (bid/ask changes) or the account's own deal history.

    Uses ``copy_ticks_from`` with ``COPY_TICKS_TRADE``.
    *count*: max trades to return (default 100, max 10 000).

    Returns a dict with:
      ok      bool
      symbol  str
      count   int
      trades  list[dict]  — time_msc, last, volume, volume_real, flags
    """
    symbol = symbol.upper()
    count  = min(max(1, count), 10_000)

    if not mt5.symbol_select(symbol, True):
        err = mt5.last_error()
        logger.debug("get_market_trades: symbol_select(%s) failed — %s", symbol, err)
        return {"ok": False, "error": f"symbol_select({symbol}) failed: {err}"}

    dt_from = datetime.datetime.now(datetime.timezone.utc) - datetime.timedelta(seconds=1)
    logger.debug(
        "get_market_trades: symbol=%s count=%s (latest trade ticks)", symbol, count
    )

    ticks = mt5.copy_ticks_from(symbol, dt_from, count, mt5.COPY_TICKS_TRADE)
    if ticks is None:
        err = mt5.last_error()
        logger.debug("get_market_trades: copy_ticks_from failed — %s", err)
        return {"ok": False, "error": f"copy_ticks_from failed: {err}"}

    logger.debug("get_market_trades: got %d trade tick(s)", len(ticks))

    return {
        "ok":     True,
        "symbol": symbol,
        "count":  len(ticks),
        "trades": [
            {
                "time":        int(t["time"]),
                "time_msc":    int(t["time_msc"]),
                "last":        float(t["last"]),
                "volume":      int(t["volume"]),
                "volume_real": float(t["volume_real"]),
                "flags":       int(t["flags"]),
            }
            for t in ticks
        ],
    }


# ---------------------------------------------------------------------------
# orderbook (Depth of Market)
# ---------------------------------------------------------------------------

_BOOK_TYPE_MAP: dict[int, str] = {
    1: "ask",           # BOOK_TYPE_SELL
    2: "bid",           # BOOK_TYPE_BUY
    3: "ask_market",    # BOOK_TYPE_SELL_MARKET
    4: "bid_market",    # BOOK_TYPE_BUY_MARKET
}


def get_orderbook(symbol: str) -> dict:
    """
    Return the current Depth of Market (DOM) for *symbol*.

    Subscribes to market depth with ``market_book_add``, reads the snapshot
    with ``market_book_get``, then releases the subscription.

    The response separates entries into ``bids`` (type buy / buy_market) and
    ``asks`` (type ask / ask_market), each sorted by price descending /
    ascending respectively.

    Returns a dict with:
      ok      bool
      symbol  str
      bids    list[dict]  — price, volume, volume_real, type
      asks    list[dict]  — price, volume, volume_real, type
    """
    symbol = symbol.upper()

    if not mt5.symbol_select(symbol, True):
        err = mt5.last_error()
        logger.debug("get_orderbook: symbol_select(%s) failed — %s", symbol, err)
        return {"ok": False, "error": f"symbol_select({symbol}) failed: {err}"}

    if not mt5.market_book_add(symbol):
        err = mt5.last_error()
        logger.debug("get_orderbook: market_book_add(%s) failed — %s", symbol, err)
        return {"ok": False, "error": f"market_book_add({symbol}) failed: {err}"}

    try:
        book = mt5.market_book_get(symbol)
        if book is None:
            err = mt5.last_error()
            logger.debug("get_orderbook: market_book_get(%s) failed — %s", symbol, err)
            return {"ok": False, "error": f"market_book_get({symbol}) failed: {err}"}

        logger.debug("get_orderbook: symbol=%s entries=%d", symbol, len(book))

        bids, asks = [], []
        for entry in book:
            row = {
                "price":       entry.price,
                "volume":      entry.volume,
                "volume_real": entry.volume_real,
                "type":        _BOOK_TYPE_MAP.get(entry.type, str(entry.type)),
            }
            if entry.type in (2, 4):   # bid / bid_market
                bids.append(row)
            else:                       # ask / ask_market
                asks.append(row)

        # bids: highest price first; asks: lowest price first
        bids.sort(key=lambda r: r["price"], reverse=True)
        asks.sort(key=lambda r: r["price"])

        return {
            "ok":     True,
            "symbol": symbol,
            "bids":   bids,
            "asks":   asks,
        }
    finally:
        mt5.market_book_release(symbol)


# ---------------------------------------------------------------------------
# close position

def close_position(ticket: int) -> dict:
    """
    Close an open position by ticket and cancel all pending orders on the
    same symbol.

    Steps:
      1. Look up the open position (symbol, type, volume, current price).
      2. Send a TRADE_ACTION_DEAL in the opposite direction to close it.
      3. Cancel every pending order on the same symbol.

    Returns a dict with:
      ok            bool
      ticket        int   — closed position ticket
      symbol        str
      close_price   float
      volume        float
      orders_cancelled  list[int]  — tickets of cancelled pending orders
    """
    # ---- 1. locate the position ----
    positions = mt5.positions_get(ticket=ticket)
    if not positions:
        err = mt5.last_error()
        logger.debug("close_position: positions_get(ticket=%s) returned nothing — %s", ticket, err)
        return {"ok": False, "error": f"No open position with ticket {ticket}"}

    pos = positions[0]
    symbol  = pos.symbol
    volume  = pos.volume
    pos_type = pos.type  # 0 = BUY, 1 = SELL

    logger.debug(
        "close_position: found ticket=%s symbol=%s type=%s volume=%s",
        ticket, symbol, pos_type, volume,
    )

    # ---- 2. choose closing price (opposite side) ----
    tick = mt5.symbol_info_tick(symbol)
    if tick is None:
        err = mt5.last_error()
        logger.debug("close_position: symbol_info_tick(%s) failed — %s", symbol, err)
        return {"ok": False, "error": f"symbol_info_tick() failed: {err}"}

    # To close a BUY we SELL at bid; to close a SELL we BUY at ask
    if pos_type == mt5.ORDER_TYPE_BUY:
        close_type  = mt5.ORDER_TYPE_SELL
        close_price = tick.bid
    else:
        close_type  = mt5.ORDER_TYPE_BUY
        close_price = tick.ask

    sym_info = mt5.symbol_info(symbol)
    if sym_info is None:
        return {"ok": False, "error": f"symbol_info({symbol}) returned None"}

    filling = _filling_mode(sym_info)

    req = {
        "action":      mt5.TRADE_ACTION_DEAL,
        "symbol":      symbol,
        "volume":      volume,
        "type":        close_type,
        "position":    ticket,          # links the close to the original position
        "price":       close_price,
        "deviation":   20,
        "magic":       20260413,
        "comment":     f"flask close {ticket}",
        "type_time":   mt5.ORDER_TIME_GTC,
        "type_filling": filling,
    }
    logger.debug("close_position: order_send request=%s", req)

    result = mt5.order_send(req)
    if result is None:
        err = mt5.last_error()
        logger.debug("close_position: order_send() returned None — %s", err)
        return {"ok": False, "error": f"order_send() returned None: {err}"}

    logger.debug(
        "close_position: order_send retcode=%s comment=%r order=%s",
        result.retcode, result.comment, result.order,
    )

    if result.retcode not in (mt5.TRADE_RETCODE_DONE, mt5.TRADE_RETCODE_PLACED):
        return {
            "ok":      False,
            "retcode": result.retcode,
            "comment": result.comment,
            "error":   f"Close order failed (retcode={result.retcode})",
        }

    # ---- 3. cancel all pending orders on the same symbol ----
    cancelled = []
    pending = mt5.orders_get(symbol=symbol)
    if pending:
        for order in pending:
            cancel_req = {
                "action": mt5.TRADE_ACTION_REMOVE,
                "order":  order.ticket,
            }
            logger.debug("close_position: cancelling pending order ticket=%s", order.ticket)
            cancel_result = mt5.order_send(cancel_req)
            if cancel_result is not None and cancel_result.retcode in (
                mt5.TRADE_RETCODE_DONE, mt5.TRADE_RETCODE_PLACED
            ):
                cancelled.append(order.ticket)
                logger.debug("close_position: cancelled order %s", order.ticket)
            else:
                err = mt5.last_error() if cancel_result is None else cancel_result.comment
                logger.debug(
                    "close_position: failed to cancel order %s — %s", order.ticket, err
                )

    return {
        "ok":               True,
        "ticket":           ticket,
        "symbol":           symbol,
        "close_price":      close_price,
        "volume":           volume,
        "orders_cancelled": cancelled,
    }


# ---------------------------------------------------------------------------
# cancel_order
# ---------------------------------------------------------------------------

def cancel_order(ticket: int) -> dict:
    """
    Cancel a single pending order by ticket (TRADE_ACTION_REMOVE).

    Returns a dict with:
      ok      bool
      ticket  int   — cancelled order ticket
      symbol  str
      type    str   — human-readable order type (e.g. BUY_LIMIT)
    """
    orders = mt5.orders_get(ticket=ticket)
    if not orders:
        err = mt5.last_error()
        logger.debug(
            "cancel_order: orders_get(ticket=%s) returned nothing — %s", ticket, err
        )
        return {"ok": False, "error": f"No pending order with ticket {ticket}"}

    order  = orders[0]
    symbol = order.symbol

    logger.debug(
        "cancel_order: found ticket=%s symbol=%s type=%s price=%s",
        ticket, symbol, order.type, order.price_open,
    )

    req = {
        "action": mt5.TRADE_ACTION_REMOVE,
        "order":  ticket,
    }
    logger.debug("cancel_order: order_send request=%s", req)

    result = mt5.order_send(req)
    if result is None:
        err = mt5.last_error()
        logger.debug("cancel_order: order_send() returned None — %s", err)
        return {"ok": False, "error": f"order_send() returned None: {err}"}

    logger.debug(
        "cancel_order: retcode=%s comment=%r", result.retcode, result.comment
    )

    if result.retcode not in (mt5.TRADE_RETCODE_DONE, mt5.TRADE_RETCODE_PLACED):
        return {
            "ok":      False,
            "retcode": result.retcode,
            "comment": result.comment,
            "error":   f"Cancel failed (retcode={result.retcode})",
        }

    _ORDER_TYPES = {
        mt5.ORDER_TYPE_BUY:              "BUY",
        mt5.ORDER_TYPE_SELL:             "SELL",
        mt5.ORDER_TYPE_BUY_LIMIT:        "BUY_LIMIT",
        mt5.ORDER_TYPE_SELL_LIMIT:       "SELL_LIMIT",
        mt5.ORDER_TYPE_BUY_STOP:         "BUY_STOP",
        mt5.ORDER_TYPE_SELL_STOP:        "SELL_STOP",
        mt5.ORDER_TYPE_BUY_STOP_LIMIT:   "BUY_STOP_LIMIT",
        mt5.ORDER_TYPE_SELL_STOP_LIMIT:  "SELL_STOP_LIMIT",
    }

    return {
        "ok":     True,
        "ticket": ticket,
        "symbol": symbol,
        "type":   _ORDER_TYPES.get(order.type, str(order.type)),
    }


# ---------------------------------------------------------------------------
# add_margin
# ---------------------------------------------------------------------------

def add_margin(ticket: int, margin_usdt: float) -> dict:
    """
    Extend an open position by opening an additional deal on the same symbol
    and direction, sized by *margin_usdt*.

    In MT5 the account's free margin backs all positions; there is no
    per-position margin deposit.  This endpoint "adds margin" expressed in
    USDT by increasing the position's volume — the broker will merge it
    (netting mode) or create a linked position (hedging mode).

    Returns a dict with:
      ok            bool
      ticket        int   — original position ticket used for lookup
      symbol        str
      added_volume  float — lot size opened
      added_margin  float — margin_usdt requested
      price         float — fill price
      new_order     int   — ticket of the newly created deal/order
    """
    positions = mt5.positions_get(ticket=ticket)
    if not positions:
        err = mt5.last_error()
        logger.debug(
            "add_margin: positions_get(ticket=%s) returned nothing — %s", ticket, err
        )
        return {"ok": False, "error": f"No open position with ticket {ticket}"}

    pos      = positions[0]
    symbol   = pos.symbol
    pos_type = pos.type   # 0 = BUY, 1 = SELL

    order_type = (
        mt5.ORDER_TYPE_BUY if pos_type == mt5.ORDER_TYPE_BUY else mt5.ORDER_TYPE_SELL
    )

    logger.debug(
        "add_margin: position ticket=%s symbol=%s type=%s; adding margin_usdt=%s",
        ticket, symbol, pos_type, margin_usdt,
    )

    lot = _lot_for_margin(symbol, margin_usdt, order_type)
    if lot is None or lot <= 0:
        return {
            "ok":    False,
            "error": f"Could not calculate lot for {margin_usdt} USDT on {symbol}",
        }

    tick = mt5.symbol_info_tick(symbol)
    if tick is None:
        err = mt5.last_error()
        return {"ok": False, "error": f"symbol_info_tick({symbol}) failed: {err}"}

    sym_info = mt5.symbol_info(symbol)
    if sym_info is None:
        return {"ok": False, "error": f"symbol_info({symbol}) returned None"}

    price   = tick.ask if order_type == mt5.ORDER_TYPE_BUY else tick.bid
    filling = _filling_mode(sym_info)

    req = {
        "action":       mt5.TRADE_ACTION_DEAL,
        "symbol":       symbol,
        "volume":       lot,
        "type":         order_type,
        "price":        price,
        "deviation":    20,
        "magic":        20260413,
        "comment":      f"flask addmargin {margin_usdt}usdt",
        "type_time":    mt5.ORDER_TIME_GTC,
        "type_filling": filling,
    }
    logger.debug("add_margin: order_send request=%s", req)

    result = mt5.order_send(req)
    if result is None:
        err = mt5.last_error()
        logger.debug("add_margin: order_send() returned None — %s", err)
        return {"ok": False, "error": f"order_send() returned None: {err}"}

    logger.debug(
        "add_margin: order_send retcode=%s comment=%r order=%s deal=%s",
        result.retcode, result.comment, result.order, result.deal,
    )

    if result.retcode not in (mt5.TRADE_RETCODE_DONE, mt5.TRADE_RETCODE_PLACED):
        return {
            "ok":      False,
            "retcode": result.retcode,
            "comment": result.comment,
            "error":   f"Add-margin order failed (retcode={result.retcode})",
        }

    return {
        "ok":           True,
        "ticket":       ticket,
        "symbol":       symbol,
        "added_volume": lot,
        "added_margin": margin_usdt,
        "price":        result.price,
        "new_order":    result.order,
    }


# ---------------------------------------------------------------------------
# remove_margin
# ---------------------------------------------------------------------------

def remove_margin(ticket: int, margin_usdt: float) -> dict:
    """
    Reduce an open position's volume by partially closing it.

    MT5 does not have a per-position margin deposit mechanism; "removing
    margin" is expressed as a partial close — an opposite-direction deal
    linked to the original position via ``"position": ticket``.  This frees
    the margin that was backing the closed portion.

    If *margin_usdt* corresponds to a lot >= the full position volume, or if
    the remaining volume after the partial close would fall below
    ``volume_min``, the entire position is closed automatically
    (``fully_closed=True``).

    Returns a dict with:
      ok               bool
      ticket           int   — original position ticket
      symbol           str
      removed_volume   float — lot size closed
      removed_margin   float — margin_usdt requested
      price            float — fill price
      close_order      int   — ticket of the closing deal/order
      fully_closed     bool  — True when the entire position was closed
    """
    positions = mt5.positions_get(ticket=ticket)
    if not positions:
        err = mt5.last_error()
        logger.debug(
            "remove_margin: positions_get(ticket=%s) returned nothing — %s", ticket, err
        )
        return {"ok": False, "error": f"No open position with ticket {ticket}"}

    pos      = positions[0]
    symbol   = pos.symbol
    pos_vol  = pos.volume
    pos_type = pos.type  # 0 = BUY, 1 = SELL

    # Closing direction is opposite to position direction
    close_type = (
        mt5.ORDER_TYPE_SELL if pos_type == mt5.ORDER_TYPE_BUY else mt5.ORDER_TYPE_BUY
    )

    logger.debug(
        "remove_margin: position ticket=%s symbol=%s type=%s volume=%s; removing margin_usdt=%s",
        ticket, symbol, pos_type, pos_vol, margin_usdt,
    )

    lot = _lot_for_margin(symbol, margin_usdt, close_type)
    if lot is None or lot <= 0:
        return {
            "ok":    False,
            "error": f"Could not calculate lot for {margin_usdt} USDT on {symbol}",
        }

    sym_info = mt5.symbol_info(symbol)
    if sym_info is None:
        return {"ok": False, "error": f"symbol_info({symbol}) returned None"}

    # Cap to full volume; snap to full close if remainder < volume_min
    fully_closed = False
    if lot >= pos_vol or (pos_vol - lot) < sym_info.volume_min:
        lot = pos_vol
        fully_closed = True
        logger.debug(
            "remove_margin: snapping to full close "
            "(requested_lot=%s pos_vol=%s volume_min=%s)",
            lot, pos_vol, sym_info.volume_min,
        )

    tick = mt5.symbol_info_tick(symbol)
    if tick is None:
        err = mt5.last_error()
        return {"ok": False, "error": f"symbol_info_tick({symbol}) failed: {err}"}

    # To close a BUY we SELL at bid; to close a SELL we BUY at ask
    price   = tick.bid if close_type == mt5.ORDER_TYPE_SELL else tick.ask
    filling = _filling_mode(sym_info)

    req = {
        "action":       mt5.TRADE_ACTION_DEAL,
        "symbol":       symbol,
        "volume":       lot,
        "type":         close_type,
        "position":     ticket,
        "price":        price,
        "deviation":    20,
        "magic":        20260413,
        "comment":      f"flask removemargin {margin_usdt}usdt",
        "type_time":    mt5.ORDER_TIME_GTC,
        "type_filling": filling,
    }
    logger.debug("remove_margin: order_send request=%s", req)

    result = mt5.order_send(req)
    if result is None:
        err = mt5.last_error()
        logger.debug("remove_margin: order_send() returned None — %s", err)
        return {"ok": False, "error": f"order_send() returned None: {err}"}

    logger.debug(
        "remove_margin: order_send retcode=%s comment=%r order=%s deal=%s",
        result.retcode, result.comment, result.order, result.deal,
    )

    if result.retcode not in (mt5.TRADE_RETCODE_DONE, mt5.TRADE_RETCODE_PLACED):
        return {
            "ok":      False,
            "retcode": result.retcode,
            "comment": result.comment,
            "error":   f"Remove-margin order failed (retcode={result.retcode})",
        }

    return {
        "ok":             True,
        "ticket":         ticket,
        "symbol":         symbol,
        "removed_volume": lot,
        "removed_margin": margin_usdt,
        "price":          result.price,
        "close_order":    result.order,
        "fully_closed":   fully_closed,
    }


# ---------------------------------------------------------------------------
# cancel_position_orders
# ---------------------------------------------------------------------------

def cancel_position_orders(ticket: int) -> dict:
    """
    Cancel all pending orders associated with an open position.

    Resolves the position's symbol from *ticket*, then cancels every pending
    order on that symbol via TRADE_ACTION_REMOVE.

    Returns a dict with:
      ok                bool
      ticket            int          — position ticket used for lookup
      symbol            str
      cancelled         list[int]    — tickets of successfully cancelled orders
      failed            list[int]    — tickets that could not be cancelled
    """
    positions = mt5.positions_get(ticket=ticket)
    if not positions:
        err = mt5.last_error()
        logger.debug(
            "cancel_position_orders: positions_get(ticket=%s) returned nothing — %s",
            ticket, err,
        )
        return {"ok": False, "error": f"No open position with ticket {ticket}"}

    symbol = positions[0].symbol
    logger.debug(
        "cancel_position_orders: position ticket=%s symbol=%s", ticket, symbol
    )

    pending = mt5.orders_get(symbol=symbol)
    if not pending:
        logger.debug(
            "cancel_position_orders: no pending orders found for symbol=%s", symbol
        )
        return {
            "ok":       True,
            "ticket":   ticket,
            "symbol":   symbol,
            "cancelled": [],
            "failed":    [],
        }

    cancelled, failed = [], []
    for order in pending:
        req = {
            "action": mt5.TRADE_ACTION_REMOVE,
            "order":  order.ticket,
        }
        logger.debug(
            "cancel_position_orders: cancelling order ticket=%s", order.ticket
        )
        result = mt5.order_send(req)
        if result is not None and result.retcode in (
            mt5.TRADE_RETCODE_DONE, mt5.TRADE_RETCODE_PLACED
        ):
            cancelled.append(order.ticket)
            logger.debug(
                "cancel_position_orders: cancelled order %s", order.ticket
            )
        else:
            err = mt5.last_error() if result is None else result.comment
            logger.debug(
                "cancel_position_orders: failed to cancel order %s — %s",
                order.ticket, err,
            )
            failed.append(order.ticket)

    return {
        "ok":        True,
        "ticket":    ticket,
        "symbol":    symbol,
        "cancelled": cancelled,
        "failed":    failed,
    }


# ---------------------------------------------------------------------------
# cancel_all_orders
# ---------------------------------------------------------------------------

def cancel_all_orders() -> dict:
    """
    Cancel every pending order across all open positions.

    Steps:
      1. Collect the unique set of symbols from all open positions.
      2. For each symbol, cancel every pending order via TRADE_ACTION_REMOVE.

    Returns a dict with:
      ok           bool
      positions    int          — number of open positions found
      symbols      list[str]    — unique symbols that were scanned
      cancelled    list[int]    — tickets successfully cancelled
      failed       list[int]    — tickets that could not be cancelled
    """
    positions = mt5.positions_get()
    if positions is None:
        err = mt5.last_error()
        logger.debug("cancel_all_orders: positions_get() failed — %s", err)
        return {"ok": False, "error": f"positions_get() failed: {err}"}

    # Unique symbols across all open positions
    symbols = list({p.symbol for p in positions})
    logger.debug(
        "cancel_all_orders: %d open position(s) across symbols=%s",
        len(positions), symbols,
    )

    cancelled, failed = [], []
    for symbol in symbols:
        pending = mt5.orders_get(symbol=symbol)
        if not pending:
            logger.debug(
                "cancel_all_orders: no pending orders for symbol=%s", symbol
            )
            continue
        for order in pending:
            req = {
                "action": mt5.TRADE_ACTION_REMOVE,
                "order":  order.ticket,
            }
            logger.debug(
                "cancel_all_orders: cancelling order ticket=%s symbol=%s",
                order.ticket, symbol,
            )
            result = mt5.order_send(req)
            if result is not None and result.retcode in (
                mt5.TRADE_RETCODE_DONE, mt5.TRADE_RETCODE_PLACED
            ):
                cancelled.append(order.ticket)
                logger.debug("cancel_all_orders: cancelled order %s", order.ticket)
            else:
                err = mt5.last_error() if result is None else result.comment
                logger.debug(
                    "cancel_all_orders: failed to cancel order %s — %s",
                    order.ticket, err,
                )
                failed.append(order.ticket)

    return {
        "ok":        True,
        "positions": len(positions),
        "symbols":   symbols,
        "cancelled": cancelled,
        "failed":    failed,
    }