"""
Flask server — MetaTrader 5 trade gateway.

Endpoints
---------
GET  /health                  → liveness check
GET  /account                 → full account snapshot
GET  /account/balance         → wallet balance details
GET  /account/leverage        → account leverage
GET  /history/trades          → executed deal log
                                 ?from=YYYY-MM-DD  &to=YYYY-MM-DD  &symbol=
GET  /history/pnl             → closed-position PnL summary
                                 ?from=YYYY-MM-DD  &to=YYYY-MM-DD  &symbol=

GET  /symbols                 → all tradable symbols  (?search= optional)
GET  /candlesticks            → OHLCV bars for a symbol
                                 ?symbol=  &timeframe=H1  &count=100
                                 &from=YYYY-MM-DD  &to=YYYY-MM-DD
GET  /ticks                   → raw ticks for a symbol
                                 ?symbol=  &count=100  &flags=all
                                 &from=YYYY-MM-DDTHH:MM:SS  &to=YYYY-MM-DDTHH:MM:SS
GET  /market/trades           → recent executed trade ticks (last price + volume)
                                 ?symbol=  &count=100
GET  /market/orderbook        → Depth of Market (DOM) snapshot
                                 ?symbol=

GET  /docs/                   → Swagger UI (interactive API docs)
GET  /apispec_1.json          → raw OpenAPI 2.0 spec (JSON)

GET  /positions               → all open positions  (?symbol= optional)
GET  /orders                  → all pending orders  (?symbol= optional)

POST /trade/open              → open a market position on any symbol
     body (JSON):
       {
         "symbol":      "XAUUSD",   // required
         "direction":   "long",     // "long" | "short"
         "margin_usdt": 5.0         // margin in account currency (default 5.0)
       }

POST /trade/tpsl              → set / update TP and/or SL on an open position
     body (JSON):
       {
         "ticket": 123456789,       // required — position ticket
         "tp":     3500.00,         // optional — take-profit price
         "sl":     3400.00          // optional — stop-loss price
       }

# legacy kept for backward compatibility
POST /trade/open-long-xauusd
"""

import os
import logging
from flask import Flask, jsonify, request
from dotenv import load_dotenv
from flasgger import Swagger
import mt5_service as mt5svc

load_dotenv()

# ---------------------------------------------------------------------------
# logging
# ---------------------------------------------------------------------------

log_level = logging.DEBUG if os.getenv("FLASK_DEBUG", "false").lower() == "true" else logging.INFO
logging.basicConfig(
    level=log_level,
    format="%(asctime)s  %(levelname)-8s  %(name)s  %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# ---------------------------------------------------------------------------
# Swagger / OpenAPI 2.0  — top-level metadata only.
# Per-endpoint specs live in each route's docstring (YAML after the ---)
# so they stay co-located with the code and update automatically.
# ---------------------------------------------------------------------------

_SWAGGER_TEMPLATE = {
    "swagger": "2.0",
    "info": {
        "title": "MetaTrader 5 REST Gateway",
        "description": "Flask REST API that wraps the MetaTrader 5 Python SDK.",
        "version": "1.0.0",
    },
    "basePath": "/",
    "schemes": ["http", "https"],
    "consumes": ["application/json"],
    "produces": ["application/json"],
    "definitions": {
        "Error": {
            "type": "object",
            "properties": {
                "ok":    {"type": "boolean", "example": False},
                "error": {"type": "string",  "example": "symbol is required"},
            },
        },
        "OpenPositionBody": {
            "type": "object",
            "required": ["symbol"],
            "properties": {
                "symbol":      {"type": "string",  "example": "XAUUSD"},
                "direction":   {"type": "string",  "example": "long",
                                "enum": ["long", "short"], "default": "long"},
                "margin_usdt": {"type": "number",  "example": 5.0},
            },
        },
        "TpSlBody": {
            "type": "object",
            "required": ["ticket"],
            "properties": {
                "ticket": {"type": "integer", "example": 123456789},
                "tp":     {"type": "number",  "example": 3500.00},
                "sl":     {"type": "number",  "example": 3400.00},
            },
        },
    },
    # paths intentionally empty — flasgger merges them from route docstrings
    "paths": {},
}

Swagger(
    app,
    template=_SWAGGER_TEMPLATE,
    config={
        "headers": [],
        "specs": [
            {
                "endpoint": "apispec_1",
                "route": "/apispec_1.json",
                "rule_filter": lambda rule: True,
                "model_filter": lambda tag: True,
            }
        ],
        "static_url_path": "/flasgger_static",
        "swagger_ui": True,
        "specs_route": "/docs/",
    },
)


# ---------------------------------------------------------------------------
# helpers
# ---------------------------------------------------------------------------

def _json_error(message: str, status: int = 400):
    return jsonify({"ok": False, "error": message}), status


def _connected():
    """
    Connect to MT5.
    Returns None on success, or an error Response tuple on failure.
    """
    logger.debug("Requesting MT5 connection")
    result = mt5svc.connect()
    if not result["ok"]:
        logger.debug("MT5 connection failed: %s", result["error"])
        return _json_error(result["error"], 503)
    logger.debug("MT5 connection established")
    return None


# ---------------------------------------------------------------------------
# routes
# ---------------------------------------------------------------------------

@app.get("/health")
def health():
    """
    Liveness check.
    ---
    tags:
      - Utility
    summary: Liveness check
    responses:
      200:
        description: Server is alive
        schema:
          type: object
          properties:
            status:
              type: string
              example: ok
    """
    return jsonify({"status": "ok"}), 200


@app.get("/account")
def account():
    """
    Full MT5 account snapshot.
    ---
    tags:
      - Account
    summary: Full account snapshot
    responses:
      200:
        description: Account info returned
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    logger.debug("GET /account called")
    err = _connected()
    if err:
        return err
    try:
        data = mt5svc.account_snapshot()
        logger.debug("GET /account result: ok=%s", data["ok"])
        return jsonify(data), 200 if data["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.get("/account/balance")
def account_balance():
    """
    Wallet balance details.
    ---
    tags:
      - Account
    summary: Wallet balance details
    description: Returns balance, equity, margin, free_margin, margin_level, and floating profit.
    responses:
      200:
        description: Balance returned
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    logger.debug("GET /account/balance called")
    err = _connected()
    if err:
        return err
    try:
        data = mt5svc.get_balance()
        logger.debug("GET /account/balance result: ok=%s balance=%s", data.get("ok"), data.get("balance"))
        return jsonify(data), 200 if data["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.get("/account/leverage")
def account_leverage():
    """
    Account leverage.
    ---
    tags:
      - Account
    summary: Account leverage
    description: Returns leverage as an integer and as a display string e.g. "1:50".
    responses:
      200:
        description: Leverage returned
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    logger.debug("GET /account/leverage called")
    err = _connected()
    if err:
        return err
    try:
        data = mt5svc.get_leverage()
        logger.debug("GET /account/leverage result: ok=%s leverage=%s", data.get("ok"), data.get("leverage"))
        return jsonify(data), 200 if data["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.get("/positions")
def positions():
    """
    All open positions.
    ---
    tags:
      - Trading
    summary: All open positions
    parameters:
      - name: symbol
        in: query
        type: string
        required: false
        description: Filter by symbol, e.g. XAUUSD
    responses:
      200:
        description: Position list returned
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    symbol = request.args.get("symbol", "").strip() or None
    logger.debug("GET /positions symbol=%s", symbol)
    err = _connected()
    if err:
        return err
    try:
        data = mt5svc.get_open_positions(symbol=symbol)
        logger.debug("GET /positions result: ok=%s count=%s", data.get("ok"), data.get("count"))
        return jsonify(data), 200 if data["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.get("/orders")
def orders():
    """
    All pending orders.
    ---
    tags:
      - Trading
    summary: All pending orders
    parameters:
      - name: symbol
        in: query
        type: string
        required: false
        description: Filter by symbol, e.g. XAUUSD
    responses:
      200:
        description: Order list returned
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    symbol = request.args.get("symbol", "").strip() or None
    logger.debug("GET /orders symbol=%s", symbol)
    err = _connected()
    if err:
        return err
    try:
        data = mt5svc.get_open_orders(symbol=symbol)
        logger.debug("GET /orders result: ok=%s count=%s", data.get("ok"), data.get("count"))
        return jsonify(data), 200 if data["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.get("/history/trades")
def history_trades():
    """
    Executed deal log.
    ---
    tags:
      - History
    summary: Executed deal log
    description: Returns all executed deals in the given date range.
    parameters:
      - name: from
        in: query
        type: string
        description: Start date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS (default 30 days ago)
      - name: to
        in: query
        type: string
        description: End date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS (default now)
      - name: symbol
        in: query
        type: string
        description: Optional symbol filter, e.g. XAUUSD
    responses:
      200:
        description: Deal list returned
      400:
        description: Invalid date format
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    date_from = request.args.get("from", "").strip() or None
    date_to   = request.args.get("to",   "").strip() or None
    symbol    = request.args.get("symbol", "").strip() or None
    logger.debug("GET /history/trades from=%s to=%s symbol=%s", date_from, date_to, symbol)

    try:
        err = _connected()
        if err:
            return err
        try:
            data = mt5svc.get_trade_history(date_from=date_from, date_to=date_to, symbol=symbol)
            logger.debug("GET /history/trades result: ok=%s count=%s", data.get("ok"), data.get("count"))
            return jsonify(data), 200 if data["ok"] else 502
        finally:
            mt5svc.disconnect()
    except ValueError as exc:
        return _json_error(str(exc))


@app.get("/history/pnl")
def history_pnl():
    """
    Closed-position PnL summary.
    ---
    tags:
      - History
    summary: Closed-position PnL summary
    description: Aggregates deals by position ID and returns net PnL per closed position.
    parameters:
      - name: from
        in: query
        type: string
        description: Start date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS (default 30 days ago)
      - name: to
        in: query
        type: string
        description: End date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS (default now)
      - name: symbol
        in: query
        type: string
        description: Optional symbol filter, e.g. XAUUSD
    responses:
      200:
        description: PnL summary returned
      400:
        description: Invalid date format
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    date_from = request.args.get("from", "").strip() or None
    date_to   = request.args.get("to",   "").strip() or None
    symbol    = request.args.get("symbol", "").strip() or None
    logger.debug("GET /history/pnl from=%s to=%s symbol=%s", date_from, date_to, symbol)

    try:
        err = _connected()
        if err:
            return err
        try:
            data = mt5svc.get_pnl_history(date_from=date_from, date_to=date_to, symbol=symbol)
            logger.debug(
                "GET /history/pnl result: ok=%s count=%s total_net_pnl=%s",
                data.get("ok"), data.get("count"), data.get("total_net_pnl"),
            )
            return jsonify(data), 200 if data["ok"] else 502
        finally:
            mt5svc.disconnect()
    except ValueError as exc:
        return _json_error(str(exc))


@app.get("/history/position")
def history_position():
    """
    All deals for a specific position.
    ---
    tags:
      - History
    summary: Position transaction history
    description: |
      Returns every deal linked to the given position ticket using
      `history_deals_get(position=ticket)`. Covers the full lifecycle:
      opening fill, partial closes, swap/commission charges, final close.
    parameters:
      - name: ticket
        in: query
        type: integer
        required: true
        description: Position ticket
        example: 123456789
    responses:
      200:
        description: Deal list returned
        schema:
          type: object
          properties:
            ok:     {type: boolean}
            ticket: {type: integer}
            count:  {type: integer}
            deals:  {type: array, items: {type: object}}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    raw = request.args.get("ticket", "").strip()
    if not raw:
        return _json_error("ticket query parameter is required")
    try:
        ticket = int(raw)
    except ValueError:
        return _json_error("ticket must be an integer")

    logger.debug("GET /history/position ticket=%s", ticket)

    err = _connected()
    if err:
        return err

    try:
        data = mt5svc.get_position_transactions(ticket)
        logger.debug(
            "GET /history/position result: ok=%s count=%s",
            data.get("ok"), data.get("count"),
        )
        return jsonify(data), 200 if data["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/open")
def trade_open():
    """
    Open a market position on any symbol.
    ---
    tags:
      - Trading
    summary: Open a market position
    description: Opens a BUY or SELL market order using a margin-based lot calculation.
    parameters:
      - name: body
        in: body
        required: true
        schema:
          $ref: '#/definitions/OpenPositionBody'
    responses:
      200:
        description: Position opened
        schema:
          type: object
          properties:
            ok:     {type: boolean}
            ticket: {type: integer}
            lot:    {type: number}
            price:  {type: number}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 order rejected
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}

    symbol = body.get("symbol", "").strip().upper()
    if not symbol:
        return _json_error("symbol is required")

    direction = body.get("direction", "long").strip().lower()
    if direction not in ("long", "short"):
        return _json_error("direction must be 'long' or 'short'")

    try:
        margin_usdt = float(body.get("margin_usdt", 5.0))
    except (TypeError, ValueError):
        return _json_error("margin_usdt must be a number")
    if margin_usdt <= 0:
        return _json_error("margin_usdt must be positive")

    logger.debug("POST /trade/open symbol=%s direction=%s margin_usdt=%s", symbol, direction, margin_usdt)

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.open_position(symbol, direction, margin_usdt)
        if result["ok"]:
            logger.debug("trade_open result: ok=True ticket=%s lot=%s price=%s",
                         result.get("ticket"), result.get("lot"), result.get("price"))
        else:
            logger.debug("trade_open result: ok=False error=%r retcode=%s comment=%r",
                         result.get("error"), result.get("retcode"), result.get("comment"))
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/tpsl")
def trade_tpsl():
    """
    Set or update TP and SL on an open position.
    ---
    tags:
      - Trading
    summary: Set / update TP and SL
    description: Modifies take-profit and/or stop-loss of an open position. Omit a field to leave it unchanged.
    parameters:
      - name: body
        in: body
        required: true
        schema:
          $ref: '#/definitions/TpSlBody'
    responses:
      200:
        description: TP/SL updated
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 order rejected
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}

    if "ticket" not in body:
        return _json_error("ticket is required")
    try:
        ticket = int(body["ticket"])
    except (TypeError, ValueError):
        return _json_error("ticket must be an integer")

    tp = body.get("tp")
    sl = body.get("sl")

    if tp is None and sl is None:
        return _json_error("At least one of 'tp' or 'sl' must be provided")

    try:
        tp = float(tp) if tp is not None else None
        sl = float(sl) if sl is not None else None
    except (TypeError, ValueError):
        return _json_error("tp and sl must be numbers")

    logger.debug("POST /trade/tpsl ticket=%s tp=%s sl=%s", ticket, tp, sl)

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.set_tpsl(ticket, tp=tp, sl=sl)
        if result["ok"]:
            logger.debug("trade_tpsl result: ok=True ticket=%s tp=%s sl=%s",
                         result.get("ticket"), result.get("tp"), result.get("sl"))
        else:
            logger.debug("trade_tpsl result: ok=False error=%r retcode=%s comment=%r",
                         result.get("error"), result.get("retcode"), result.get("comment"))
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/close")
def trade_close():
    """
    Close an open position and cancel all pending orders on the same symbol.
    ---
    tags:
      - Trading
    summary: Close a position
    description: |
      Closes the open position identified by ticket by sending a market order
      in the opposite direction, then cancels every pending order on the same symbol.
    parameters:
      - name: body
        in: body
        required: true
        schema:
          type: object
          required: [ticket]
          properties:
            ticket:
              type: integer
              example: 123456789
              description: Ticket of the open position to close
    responses:
      200:
        description: Position closed
        schema:
          type: object
          properties:
            ok:                 {type: boolean}
            ticket:             {type: integer}
            symbol:             {type: string}
            close_price:        {type: number}
            volume:             {type: number}
            orders_cancelled:
              type: array
              items: {type: integer}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 order rejected
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}

    if "ticket" not in body:
        return _json_error("ticket is required")
    try:
        ticket = int(body["ticket"])
    except (TypeError, ValueError):
        return _json_error("ticket must be an integer")

    logger.debug("POST /trade/close ticket=%s", ticket)

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.close_position(ticket)
        if result["ok"]:
            logger.debug(
                "trade_close result: ok=True ticket=%s symbol=%s close_price=%s "
                "volume=%s orders_cancelled=%s",
                result.get("ticket"), result.get("symbol"), result.get("close_price"),
                result.get("volume"), result.get("orders_cancelled"),
            )
        else:
            logger.debug(
                "trade_close result: ok=False error=%r retcode=%s comment=%r",
                result.get("error"), result.get("retcode"), result.get("comment"),
            )
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/cancel-order")
def trade_cancel_order():
    """
    Cancel a pending order.
    ---
    tags:
      - Trading
    summary: Cancel a pending order
    description: |
      Cancels a single pending order (limit, stop, stop-limit, etc.) by ticket
      using TRADE_ACTION_REMOVE. Only works on pending orders — not open positions.
    parameters:
      - name: body
        in: body
        required: true
        schema:
          type: object
          required: [ticket]
          properties:
            ticket:
              type: integer
              example: 987654321
              description: Ticket of the pending order to cancel
    responses:
      200:
        description: Order cancelled
        schema:
          type: object
          properties:
            ok:     {type: boolean}
            ticket: {type: integer}
            symbol: {type: string}
            type:   {type: string, description: "BUY_LIMIT / SELL_STOP / etc."}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 order rejected
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}

    if "ticket" not in body:
        return _json_error("ticket is required")
    try:
        ticket = int(body["ticket"])
    except (TypeError, ValueError):
        return _json_error("ticket must be an integer")

    logger.debug("POST /trade/cancel-order ticket=%s", ticket)

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.cancel_order(ticket)
        if result["ok"]:
            logger.debug(
                "trade_cancel_order result: ok=True ticket=%s symbol=%s type=%s",
                result.get("ticket"), result.get("symbol"), result.get("type"),
            )
        else:
            logger.debug(
                "trade_cancel_order result: ok=False error=%r retcode=%s comment=%r",
                result.get("error"), result.get("retcode"), result.get("comment"),
            )
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/add-margin")
def trade_add_margin():
    """
    Add margin to an existing open position.
    ---
    tags:
      - Trading
    summary: Add margin to a position
    description: |
      Extends an open position by opening an additional deal on the same symbol
      and direction, sized by `margin_usdt`. Because MetaTrader 5 backs all
      positions from the account's free margin (no per-position deposit), this
      endpoint expresses *adding margin* as increasing the position's volume.
      The broker merges the new deal into the existing position (netting mode)
      or creates a linked position (hedging mode).
    parameters:
      - name: body
        in: body
        required: true
        schema:
          type: object
          required: [ticket, margin_usdt]
          properties:
            ticket:
              type: integer
              example: 123456789
              description: Ticket of the open position to extend
            margin_usdt:
              type: number
              example: 5
              description: Extra margin in USDT to allocate
    responses:
      200:
        description: Margin added (position extended)
        schema:
          type: object
          properties:
            ok:           {type: boolean}
            ticket:       {type: integer}
            symbol:       {type: string}
            added_volume: {type: number}
            added_margin: {type: number}
            price:        {type: number}
            new_order:    {type: integer}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 order rejected
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}

    if "ticket" not in body:
        return _json_error("ticket is required")
    if "margin_usdt" not in body:
        return _json_error("margin_usdt is required")
    try:
        ticket = int(body["ticket"])
    except (TypeError, ValueError):
        return _json_error("ticket must be an integer")
    try:
        margin_usdt = float(body["margin_usdt"])
        if margin_usdt <= 0:
            raise ValueError
    except (TypeError, ValueError):
        return _json_error("margin_usdt must be a positive number")

    logger.debug("POST /trade/add-margin ticket=%s margin_usdt=%s", ticket, margin_usdt)

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.add_margin(ticket, margin_usdt)
        if result["ok"]:
            logger.debug(
                "trade_add_margin result: ok=True ticket=%s symbol=%s "
                "added_volume=%s price=%s new_order=%s",
                result.get("ticket"), result.get("symbol"),
                result.get("added_volume"), result.get("price"), result.get("new_order"),
            )
        else:
            logger.debug(
                "trade_add_margin result: ok=False error=%r retcode=%s comment=%r",
                result.get("error"), result.get("retcode"), result.get("comment"),
            )
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/remove-margin")
def trade_remove_margin():
    """
    Remove margin from an open position (partial close).
    ---
    tags:
      - Trading
    summary: Remove margin from a position
    description: |
      Reduces an open position's volume by partially closing it — an
      opposite-direction deal linked to the original position via the ticket.
      This frees the margin backing the closed portion.

      If the requested `margin_usdt` maps to a lot >= the full position
      volume, or the remaining volume after the partial close would fall below
      the symbol's `volume_min`, the entire position is closed automatically
      and `fully_closed` is set to `true`.
    parameters:
      - name: body
        in: body
        required: true
        schema:
          type: object
          required: [ticket, margin_usdt]
          properties:
            ticket:
              type: integer
              example: 123456789
              description: Ticket of the open position to partially close
            margin_usdt:
              type: number
              example: 5
              description: Margin in USDT to remove (free up) from the position
    responses:
      200:
        description: Margin removed (position partially or fully closed)
        schema:
          type: object
          properties:
            ok:             {type: boolean}
            ticket:         {type: integer}
            symbol:         {type: string}
            removed_volume: {type: number}
            removed_margin: {type: number}
            price:          {type: number}
            close_order:    {type: integer}
            fully_closed:   {type: boolean}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 order rejected
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}

    if "ticket" not in body:
        return _json_error("ticket is required")
    if "margin_usdt" not in body:
        return _json_error("margin_usdt is required")
    try:
        ticket = int(body["ticket"])
    except (TypeError, ValueError):
        return _json_error("ticket must be an integer")
    try:
        margin_usdt = float(body["margin_usdt"])
        if margin_usdt <= 0:
            raise ValueError
    except (TypeError, ValueError):
        return _json_error("margin_usdt must be a positive number")

    logger.debug("POST /trade/remove-margin ticket=%s margin_usdt=%s", ticket, margin_usdt)

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.remove_margin(ticket, margin_usdt)
        if result["ok"]:
            logger.debug(
                "trade_remove_margin result: ok=True ticket=%s symbol=%s "
                "removed_volume=%s price=%s close_order=%s fully_closed=%s",
                result.get("ticket"), result.get("symbol"),
                result.get("removed_volume"), result.get("price"),
                result.get("close_order"), result.get("fully_closed"),
            )
        else:
            logger.debug(
                "trade_remove_margin result: ok=False error=%r retcode=%s comment=%r",
                result.get("error"), result.get("retcode"), result.get("comment"),
            )
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/cancel-position-orders")
def trade_cancel_position_orders():
    """
    Cancel all pending orders for a position.
    ---
    tags:
      - Trading
    summary: Cancel all orders for a position
    description: |
      Resolves the open position identified by `ticket` to its symbol, then
      cancels every pending order (limit, stop, stop-limit, etc.) on that
      symbol using TRADE_ACTION_REMOVE.
      Returns two lists: `cancelled` (tickets successfully removed) and
      `failed` (tickets that could not be cancelled).
    parameters:
      - name: body
        in: body
        required: true
        schema:
          type: object
          required: [ticket]
          properties:
            ticket:
              type: integer
              example: 123456789
              description: Ticket of the open position whose orders should be cancelled
    responses:
      200:
        description: Cancellation attempted (check cancelled/failed lists)
        schema:
          type: object
          properties:
            ok:        {type: boolean}
            ticket:    {type: integer}
            symbol:    {type: string}
            cancelled:
              type: array
              items: {type: integer}
            failed:
              type: array
              items: {type: integer}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: Position not found or MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}

    if "ticket" not in body:
        return _json_error("ticket is required")
    try:
        ticket = int(body["ticket"])
    except (TypeError, ValueError):
        return _json_error("ticket must be an integer")

    logger.debug("POST /trade/cancel-position-orders ticket=%s", ticket)

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.cancel_position_orders(ticket)
        if result["ok"]:
            logger.debug(
                "trade_cancel_position_orders result: ok=True ticket=%s symbol=%s "
                "cancelled=%s failed=%s",
                result.get("ticket"), result.get("symbol"),
                result.get("cancelled"), result.get("failed"),
            )
        else:
            logger.debug(
                "trade_cancel_position_orders result: ok=False error=%r",
                result.get("error"),
            )
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.post("/trade/cancel-all-orders")
def trade_cancel_all_orders():
    """
    Cancel all pending orders across every open position.
    ---
    tags:
      - Trading
    summary: Cancel all orders (all positions)
    description: |
      Collects the unique set of symbols from all open positions, then cancels
      every pending order on those symbols via TRADE_ACTION_REMOVE.
      Returns `cancelled` (tickets successfully removed) and `failed`
      (tickets that could not be cancelled) along with a count of open
      positions and the list of symbols scanned.
      If there are no open positions the call succeeds with empty lists.
    responses:
      200:
        description: Cancellation attempted
        schema:
          type: object
          properties:
            ok:        {type: boolean}
            positions: {type: integer, description: Number of open positions found}
            symbols:
              type: array
              items: {type: string}
            cancelled:
              type: array
              items: {type: integer}
            failed:
              type: array
              items: {type: integer}
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    logger.debug("POST /trade/cancel-all-orders")

    err = _connected()
    if err:
        return err

    try:
        result = mt5svc.cancel_all_orders()
        if result["ok"]:
            logger.debug(
                "trade_cancel_all_orders result: ok=True positions=%s symbols=%s "
                "cancelled=%s failed=%s",
                result.get("positions"), result.get("symbols"),
                result.get("cancelled"), result.get("failed"),
            )
        else:
            logger.debug(
                "trade_cancel_all_orders result: ok=False error=%r",
                result.get("error"),
            )
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


# ---------------------------------------------------------------------------
# legacy endpoint  (kept for backward compatibility)
# ---------------------------------------------------------------------------

@app.post("/trade/open-long-xauusd")
def open_long_xauusd():
    """
    Open XAUUSD long — legacy endpoint.
    ---
    tags:
      - Trading (Legacy)
    summary: Open XAUUSD long (legacy)
    description: Kept for backward compatibility. Prefer POST /trade/open.
    parameters:
      - name: body
        in: body
        required: false
        schema:
          type: object
          properties:
            margin_usdt:
              type: number
              example: 5.0
    responses:
      200:
        description: Position opened
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 order rejected
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    body = request.get_json(silent=True) or {}
    try:
        margin_usdt = float(body.get("margin_usdt", 5.0))
    except (TypeError, ValueError):
        return _json_error("margin_usdt must be a number")
    if margin_usdt <= 0:
        return _json_error("margin_usdt must be positive")
    logger.debug("POST /trade/open-long-xauusd (legacy) margin_usdt=%s", margin_usdt)
    err = _connected()
    if err:
        return err
    try:
        result = mt5svc.open_long_xauusd(margin_usdt=margin_usdt)
        return jsonify(result), 200 if result["ok"] else 502
    finally:
        mt5svc.disconnect()


# ---------------------------------------------------------------------------
# market data
# ---------------------------------------------------------------------------

@app.get("/symbols")
def symbols():
    """
    All fully-tradeable symbols.
    ---
    tags:
      - Market Data
    summary: All tradeable symbols
    description: |
      Returns only symbols with SYMBOL_TRADE_MODE_FULL (full two-way trading allowed).
      Use ?search= for a case-insensitive substring filter on the symbol name.
    parameters:
      - name: search
        in: query
        type: string
        required: false
        description: Case-insensitive substring filter, e.g. XAU
    responses:
      200:
        description: Symbol list returned
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    search = request.args.get("search", "").strip() or None
    logger.debug("GET /symbols search=%s", search)
    err = _connected()
    if err:
        return err
    try:
        data = mt5svc.get_symbols(search=search)
        logger.debug("GET /symbols result: ok=%s count=%s", data.get("ok"), data.get("count"))
        return jsonify(data), 200 if data["ok"] else 502
    finally:
        mt5svc.disconnect()


@app.get("/candlesticks")
def candlesticks():
    """
    OHLCV candlestick bars for a symbol.
    ---
    tags:
      - Market Data
    summary: OHLCV candlestick bars
    description: |
      Returns OHLCV bars for the given symbol and timeframe.
      When 'from' is omitted the latest 'count' bars are returned.
      Supported timeframes: M1 M2 M3 M4 M5 M6 M10 M12 M15 M20 M30 H1 H2 H3 H4 H6 H8 H12 D1 W1 MN1.
    parameters:
      - name: symbol
        in: query
        type: string
        required: true
        description: Symbol name, e.g. XAUUSD
      - name: timeframe
        in: query
        type: string
        default: H1
        description: Timeframe (default H1)
      - name: count
        in: query
        type: integer
        default: 100
        description: Number of bars (default 100, max 5000)
      - name: from
        in: query
        type: string
        description: Start date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS
      - name: to
        in: query
        type: string
        description: End date YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS
    responses:
      200:
        description: Candlestick bars returned
      400:
        description: Invalid parameters
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    symbol    = request.args.get("symbol", "").strip()
    if not symbol:
        return _json_error("symbol is required")

    timeframe = request.args.get("timeframe", "H1").strip()
    date_from = request.args.get("from", "").strip() or None
    date_to   = request.args.get("to",   "").strip() or None

    try:
        count = int(request.args.get("count", 100))
    except ValueError:
        return _json_error("count must be an integer")

    logger.debug(
        "GET /candlesticks symbol=%s timeframe=%s count=%s from=%s to=%s",
        symbol, timeframe, count, date_from, date_to,
    )

    try:
        err = _connected()
        if err:
            return err
        try:
            data = mt5svc.get_candlesticks(
                symbol=symbol,
                timeframe=timeframe,
                count=count,
                date_from=date_from,
                date_to=date_to,
            )
            logger.debug(
                "GET /candlesticks result: ok=%s count=%s",
                data.get("ok"), data.get("count"),
            )
            return jsonify(data), 200 if data["ok"] else 502
        finally:
            mt5svc.disconnect()
    except ValueError as exc:
        return _json_error(str(exc))


@app.get("/ticks")
def ticks():
    """
    Raw tick data for a symbol.
    ---
    tags:
      - Market Data
    summary: Raw tick data
    description: |
      Returns raw ticks for the given symbol.
      When 'from' is omitted the latest 'count' ticks are returned.
      When 'from' alone is given, 'count' ticks starting from that timestamp.
      When both 'from' and 'to' are given, all ticks in that range.
    parameters:
      - name: symbol
        in: query
        type: string
        required: true
        description: Symbol name, e.g. XAUUSD
      - name: count
        in: query
        type: integer
        default: 100
        description: Number of ticks (default 100, max 10000)
      - name: flags
        in: query
        type: string
        default: all
        enum: [all, info, trade]
        description: Tick type filter (default all)
      - name: from
        in: query
        type: string
        description: Start timestamp YYYY-MM-DDTHH:MM:SS
      - name: to
        in: query
        type: string
        description: End timestamp YYYY-MM-DDTHH:MM:SS
    responses:
      200:
        description: Tick list returned
      400:
        description: Invalid parameters
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    symbol = request.args.get("symbol", "").strip()
    if not symbol:
        return _json_error("symbol is required")

    flags     = request.args.get("flags", "all").strip()
    date_from = request.args.get("from", "").strip() or None
    date_to   = request.args.get("to",   "").strip() or None

    try:
        count = int(request.args.get("count", 100))
    except ValueError:
        return _json_error("count must be an integer")

    logger.debug(
        "GET /ticks symbol=%s count=%s flags=%s from=%s to=%s",
        symbol, count, flags, date_from, date_to,
    )

    try:
        err = _connected()
        if err:
            return err
        try:
            data = mt5svc.get_ticks(
                symbol=symbol,
                count=count,
                date_from=date_from,
                date_to=date_to,
                flags=flags,
            )
            logger.debug(
                "GET /ticks result: ok=%s count=%s",
                data.get("ok"), data.get("count"),
            )
            return jsonify(data), 200 if data["ok"] else 502
        finally:
            mt5svc.disconnect()
    except ValueError as exc:
        return _json_error(str(exc))


@app.get("/market/trades")
def market_trades():
    """
    Recent market trade ticks.
    ---
    tags:
      - Market Data
    summary: Recent market trades
    description: |
      Returns the most recent executed trade ticks (last price + volume) for
      the given symbol using `copy_ticks_from` with `COPY_TICKS_TRADE`.
      Unlike `/ticks`, this endpoint is focused on actual executed trades
      rather than quote (bid/ask) changes.
    parameters:
      - name: symbol
        in: query
        type: string
        required: true
        description: Symbol name, e.g. XAUUSD
      - name: count
        in: query
        type: integer
        default: 100
        description: Number of trade ticks to return (default 100, max 10000)
    responses:
      200:
        description: Trade tick list returned
        schema:
          type: object
          properties:
            ok:     {type: boolean}
            symbol: {type: string}
            count:  {type: integer}
            trades: {type: array, items: {type: object}}
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    symbol = request.args.get("symbol", "").strip()
    if not symbol:
        return _json_error("symbol is required")
    try:
        count = int(request.args.get("count", 100))
    except ValueError:
        return _json_error("count must be an integer")

    logger.debug("GET /market/trades symbol=%s count=%s", symbol, count)

    try:
        err = _connected()
        if err:
            return err
        try:
            data = mt5svc.get_market_trades(symbol=symbol, count=count)
            logger.debug(
                "GET /market/trades result: ok=%s count=%s",
                data.get("ok"), data.get("count"),
            )
            return jsonify(data), 200 if data["ok"] else 502
        finally:
            mt5svc.disconnect()
    except ValueError as exc:
        return _json_error(str(exc))


@app.get("/market/orderbook")
def market_orderbook():
    """
    Depth of Market (order book).
    ---
    tags:
      - Market Data
    summary: Order book / market depth
    description: |
      Returns the current Depth of Market snapshot for the given symbol.
      Subscribes via `market_book_add`, reads with `market_book_get`, then
      releases with `market_book_release`.

      **Note:** The broker must enable DOM data for the symbol; not all
      instruments or broker accounts expose level-2 depth. If the symbol has
      no DOM data the bids/asks lists will be empty.

      Bids are sorted highest-price-first; asks lowest-price-first.
    parameters:
      - name: symbol
        in: query
        type: string
        required: true
        description: Symbol name, e.g. XAUUSD
    responses:
      200:
        description: Order book snapshot returned
        schema:
          type: object
          properties:
            ok:     {type: boolean}
            symbol: {type: string}
            bids:
              type: array
              items: {type: object}
              description: Bid entries sorted highest price first
            asks:
              type: array
              items: {type: object}
              description: Ask entries sorted lowest price first
      400:
        description: Validation error
        schema:
          $ref: '#/definitions/Error'
      502:
        description: MT5 error
        schema:
          $ref: '#/definitions/Error'
      503:
        description: Cannot connect to MT5
        schema:
          $ref: '#/definitions/Error'
    """
    symbol = request.args.get("symbol", "").strip()
    if not symbol:
        return _json_error("symbol is required")

    logger.debug("GET /market/orderbook symbol=%s", symbol)

    try:
        err = _connected()
        if err:
            return err
        try:
            data = mt5svc.get_orderbook(symbol=symbol)
            logger.debug(
                "GET /market/orderbook result: ok=%s bids=%s asks=%s",
                data.get("ok"), len(data.get("bids", [])), len(data.get("asks", [])),
            )
            return jsonify(data), 200 if data["ok"] else 502
        finally:
            mt5svc.disconnect()
    except ValueError as exc:
        return _json_error(str(exc))


# ---------------------------------------------------------------------------
# entry point
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    host  = os.getenv("FLASK_HOST", "0.0.0.0")
    port  = int(os.getenv("FLASK_PORT", "5000"))
    debug = os.getenv("FLASK_DEBUG", "false").lower() == "true"

    print(f"Starting Flask on {host}:{port}  debug={debug}")
    app.run(host=host, port=port, debug=debug)
