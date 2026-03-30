"""
Load historical candlestick data for all timeframes into MySQL.

Supports two data sources per pair (set via PAIRS source field):

  coindcx  — CoinDCX futures API (https://public.coindcx.com)
    1m  -> resolution='1'
    5m  -> resolution='5'
    1h  -> resolution='60'
    1d  -> resolution='1D'

  binance  — Binance USDM Futures API (https://fapi.binance.com)
    1m  -> interval='1m'
    5m  -> interval='5m'
    1h  -> interval='1h'
    1d  -> interval='1d'

Aggregated from loaded data (both sources):
  3m  -> aggregate 3 x 1m candles
  15m -> aggregate 3 x 5m candles
  30m -> aggregate 6 x 5m candles
  2h  -> aggregate 2 x 1h candles
  4h  -> aggregate 4 x 1h candles
  1w  -> aggregate 7 x 1d candles
"""

import mysql.connector
import requests
import json
import time
from datetime import datetime, timezone, timedelta

# ─── Configuration ───────────────────────────────────────────────────────────
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'database': 'coindcx',
    'user': 'root',
    'password': 'Kakri@1234',
}

# All pairs to sync — each entry: (coindcx_pair, symbol, source)
# source = 'coindcx'  ->  CoinDCX futures API  (pcode=f)
# source = 'binance'  ->  Binance USDM futures API (fapi.binance.com)
PAIRS = [
    ('B-BTC_USDT', 'BTCUSDT',  'coindcx'),
    ('B-ETH_USDT', 'ETHUSDT',  'coindcx'),
    ('B-XAG_USDT', 'XAGUSDT',  'coindcx'),
    ('B-XAU_USDT', 'XAUSDT',   'coindcx'),
    ('B-SPX_USDT', 'SPXUSDT',  'coindcx'),   # SPX6900 futures
    # Example Binance-sourced pair (uncomment / add new ones as needed):
    # ('B-BTC_USDT', 'BTCUSDT',  'binance'),
]

# Active pair (updated in the main loop)
PAIR   = PAIRS[0][0]
SYMBOL = PAIRS[0][1]

START_TS = int(datetime(2026, 3, 15, tzinfo=timezone.utc).timestamp())   # epoch seconds
END_TS   = int(datetime.now(timezone.utc).timestamp())

API_BASE             = 'https://public.coindcx.com/market_data/candlesticks'
BINANCE_FUTURES_BASE = 'https://fapi.binance.com/fapi/v1/klines'

# ─── Timeframe definitions ────────────────────────────────────────────────────

# Loaded directly from the API (CoinDCX or Binance depending on source)
# binance_interval : Binance interval string
# binance_chunk    : seconds per Binance request (max 1500 candles per call)
DIRECT_TIMEFRAMES = [
    # label  coindcx-res  duration  interval  sec     coindcx-chunk  binance-interval  binance-chunk
    {'label': '1m',  'resolution': '1',   'duration': '1',   'interval': '1',   'sec': 60,    'chunk': 86_400,    'binance_interval': '1m', 'binance_chunk': 86_400},
    {'label': '5m',  'resolution': '5',   'duration': '5',   'interval': '5',   'sec': 300,   'chunk': 604_800,   'binance_interval': '5m', 'binance_chunk': 345_600},
    {'label': '1h',  'resolution': '60',  'duration': '60',  'interval': '60',  'sec': 3_600, 'chunk': 2_592_000, 'binance_interval': '1h', 'binance_chunk': 2_592_000},
    {'label': '1d',  'resolution': '1D',  'duration': '1D',  'interval': '1D',  'sec': 86_400,'chunk': 31_536_000,'binance_interval': '1d', 'binance_chunk': 31_536_000},
]

# Aggregated in Python from the source timeframe already in DB
AGGREGATE_TIMEFRAMES = [
    # label, duration stored, bucket_sec, source_duration already in DB, source_sec
    {'label': '3m',  'duration': '3',   'interval': '3',   'sec': 180,     'source_dur': '1',   'source_sec': 60},
    {'label': '15m', 'duration': '15',  'interval': '15',  'sec': 900,     'source_dur': '5',   'source_sec': 300},
    {'label': '30m', 'duration': '30',  'interval': '30',  'sec': 1_800,   'source_dur': '5',   'source_sec': 300},
    {'label': '2h',  'duration': '120', 'interval': '120', 'sec': 7_200,   'source_dur': '60',  'source_sec': 3_600},
    {'label': '4h',  'duration': '240', 'interval': '240', 'sec': 14_400,  'source_dur': '60',  'source_sec': 3_600},
    {'label': '1w',  'duration': '1W',  'interval': '1W',  'sec': 604_800, 'source_dur': '1D',  'source_sec': 86_400},
]

# ─── Helpers ────────────────────────────────────────────────────────────────

def fetch_candles(resolution, from_ts, to_ts, retries=3):
    """Fetch candles from CoinDCX futures API. Returns list of dicts."""
    url = (
        f"{API_BASE}?pair={PAIR}&resolution={resolution}"
        f"&from={from_ts}&to={to_ts}&pcode=f"
    )
    for attempt in range(retries):
        try:
            resp = requests.get(url, timeout=30)
            resp.raise_for_status()
            data = resp.json()
            if data.get('s') == 'ok' and data.get('data'):
                return data['data']
            return []
        except Exception as e:
            if attempt < retries - 1:
                time.sleep(2 ** attempt)
            else:
                print(f"    [WARN] fetch failed {url}: {e}")
                return []


def fetch_candles_binance(binance_interval, from_ts, to_ts, retries=3):
    """Fetch candles from Binance USDM Futures API.

    Args:
        binance_interval: Binance interval string e.g. '1m', '5m', '1h', '1d'
        from_ts / to_ts:  epoch seconds (same convention as fetch_candles)

    Returns list of dicts with keys: time (ms), open, high, low, close, volume
    """
    params = {
        'symbol':    SYMBOL,
        'interval':  binance_interval,
        'startTime': from_ts * 1000,   # Binance expects milliseconds
        'endTime':   to_ts   * 1000,
        'limit':     1500,
    }
    for attempt in range(retries):
        try:
            resp = requests.get(BINANCE_FUTURES_BASE, params=params, timeout=30)
            resp.raise_for_status()
            klines = resp.json()
            if not isinstance(klines, list):
                return []
            # Binance kline format:
            # [openTime, open, high, low, close, volume, closeTime, quoteVol, trades, ...]
            return [
                {
                    'time':   int(k[0]),        # already milliseconds
                    'open':   float(k[1]),
                    'high':   float(k[2]),
                    'low':    float(k[3]),
                    'close':  float(k[4]),
                    'volume': float(k[5]),
                }
                for k in klines
            ]
        except Exception as e:
            if attempt < retries - 1:
                time.sleep(2 ** attempt)
            else:
                print(f"    [WARN] Binance fetch failed (symbol={SYMBOL} interval={binance_interval}): {e}")
                return []


def get_existing_open_times(conn, duration, from_ms, to_ms):
    """Return a set of open_time (ms) already in DB for this pair+duration in the time range."""
    cur = conn.cursor()
    cur.execute(
        "SELECT open_time FROM websocket_futures_candlestick_data "
        "WHERE pair=%s AND duration=%s AND open_time>=%s AND open_time<=%s",
        (PAIR, duration, from_ms, to_ms)
    )
    rows = cur.fetchall()
    cur.close()
    return {r[0] for r in rows}


INSERT_SQL = """
    INSERT INTO websocket_futures_candlestick_data
        (channel_name, close, close_time, duration, ets, high, `interval`,
         low, open, open_time, pair, product, quote_volume, raw_data,
         record_timestamp, symbol, volume)
    VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
"""

def build_row(c, duration, interval_val):
    """Build a DB row tuple from a candle dict (API format)."""
    open_time_ms = int(c['time'])          # API returns ms
    close_time   = open_time_ms + (duration_to_ms(duration) - 1)
    now_str      = datetime.now(timezone.utc).strftime('%Y-%m-%d %H:%M:%S')
    return (
        'futures_candlestick',             # channel_name
        str(c['close']),                   # close
        float(close_time),                 # close_time
        duration,                          # duration
        open_time_ms,                      # ets
        str(c['high']),                    # high
        interval_val,                      # interval
        str(c['low']),                     # low
        str(c['open']),                    # open
        open_time_ms,                      # open_time
        PAIR,                              # pair
        'futures',                         # product
        '0',                               # quote_volume (not in futures candle API)
        json.dumps(c),                     # raw_data
        now_str,                           # record_timestamp
        SYMBOL,                            # symbol
        str(c['volume']),                  # volume
    )


def build_agg_row(bucket_ms, o, h, l, c, vol, duration, interval_val):
    """Build a DB row tuple from aggregated OHLCV values."""
    close_time = bucket_ms + duration_to_ms(duration) - 1
    now_str    = datetime.now(timezone.utc).strftime('%Y-%m-%d %H:%M:%S')
    raw = {'time': bucket_ms, 'open': o, 'high': h, 'low': l, 'close': c, 'volume': vol, 'aggregated': True}
    return (
        'futures_candlestick',
        str(c),
        float(close_time),
        duration,
        bucket_ms,
        str(h),
        interval_val,
        str(l),
        str(o),
        bucket_ms,
        PAIR,
        'futures',
        '0',
        json.dumps(raw),
        now_str,
        SYMBOL,
        str(vol),
    )


DURATION_MS = {
    '1':   60_000,
    '3':   180_000,
    '5':   300_000,
    '15':  900_000,
    '30':  1_800_000,
    '60':  3_600_000,
    '120': 7_200_000,
    '240': 14_400_000,
    '1D':  86_400_000,
    '1W':  604_800_000,
}

def duration_to_ms(duration):
    return DURATION_MS.get(duration, 60_000)


def insert_batch(conn, rows):
    if not rows:
        return 0
    cur = conn.cursor()
    cur.executemany(INSERT_SQL, rows)
    conn.commit()
    cur.close()
    return len(rows)


# ─── Direct load ─────────────────────────────────────────────────────────────

def load_direct(conn, tf, source='coindcx'):
    label      = tf['label']
    resolution = tf['resolution']
    duration   = tf['duration']
    interval_v = tf['interval']
    chunk_sec  = tf['binance_chunk'] if source == 'binance' else tf['chunk']

    src_label = f"Binance ({tf['binance_interval']})" if source == 'binance' else f"CoinDCX (resolution={resolution})"
    print(f"\n{'='*60}")
    print(f"  Loading {label} from {src_label}")
    print(f"{'='*60}")

    total_inserted = 0
    total_skipped  = 0
    chunk_from = START_TS

    while chunk_from < END_TS:
        chunk_to = min(chunk_from + chunk_sec, END_TS)

        if source == 'binance':
            candles = fetch_candles_binance(tf['binance_interval'], chunk_from, chunk_to)
        else:
            candles = fetch_candles(resolution, chunk_from, chunk_to)
        if not candles:
            chunk_from = chunk_to
            continue

        from_ms = chunk_from * 1000
        to_ms   = chunk_to   * 1000
        existing = get_existing_open_times(conn, duration, from_ms, to_ms)

        rows = []
        for c in candles:
            ot = int(c['time'])
            if ot in existing:
                total_skipped += 1
            else:
                rows.append(build_row(c, duration, interval_v))

        inserted = insert_batch(conn, rows)
        total_inserted += inserted

        from_dt = datetime.utcfromtimestamp(chunk_from).strftime('%Y-%m-%d')
        to_dt   = datetime.utcfromtimestamp(chunk_to).strftime('%Y-%m-%d')
        print(f"  [{from_dt} → {to_dt}]  fetched={len(candles)}  new={inserted}  skipped={len(candles)-inserted}  total_so_far={total_inserted}")

        chunk_from = chunk_to
        time.sleep(0.3)   # be nice to the API

    print(f"  DONE {label}: inserted={total_inserted}, skipped={total_skipped}")
    return total_inserted


# ─── Aggregate load ──────────────────────────────────────────────────────────

def load_aggregate(conn, tf):
    label      = tf['label']
    duration   = tf['duration']
    interval_v = tf['interval']
    bucket_ms  = tf['sec'] * 1000
    source_dur = tf['source_dur']

    print(f"\n{'='*60}")
    print(f"  Aggregating {label} from {source_dur}-duration source data")
    print(f"{'='*60}")

    # Fetch ALL source candles (ordered by open_time ASC)
    cur = conn.cursor(dictionary=True)
    cur.execute(
        "SELECT open_time, open, high, low, close, volume "
        "FROM websocket_futures_candlestick_data "
        "WHERE pair=%s AND duration=%s AND open_time>=%s "
        "ORDER BY open_time ASC",
        (PAIR, source_dur, START_TS * 1000)
    )
    source_rows = cur.fetchall()
    cur.close()

    if not source_rows:
        print(f"  [SKIP] No source data found for duration={source_dur}")
        return 0

    print(f"  Source candles fetched: {len(source_rows)}")

    # Bucket them
    buckets = {}   # bucket_start_ms -> list of rows (already sorted ASC)
    for row in source_rows:
        ot = int(row['open_time'])
        bucket_start = (ot // bucket_ms) * bucket_ms
        buckets.setdefault(bucket_start, []).append(row)

    # Get existing buckets already in DB
    all_existing = get_existing_open_times(
        conn, duration,
        START_TS * 1000,
        END_TS   * 1000 + bucket_ms
    )

    rows_to_insert = []
    skipped = 0
    for bucket_start, candles in sorted(buckets.items()):
        if bucket_start in all_existing:
            skipped += 1
            continue

        o   = float(candles[0]['open'])
        c   = float(candles[-1]['close'])
        h   = max(float(x['high'])   for x in candles)
        l   = min(float(x['low'])    for x in candles)
        vol = sum(float(x['volume']) for x in candles)

        rows_to_insert.append(build_agg_row(bucket_start, o, h, l, c, vol, duration, interval_v))

    # Insert in batches of 500
    BATCH = 500
    total_inserted = 0
    for i in range(0, len(rows_to_insert), BATCH):
        batch = rows_to_insert[i:i+BATCH]
        total_inserted += insert_batch(conn, batch)

    print(f"  DONE {label}: buckets_built={len(buckets)}  inserted={total_inserted}  skipped={skipped}")
    return total_inserted


# ─── Main ────────────────────────────────────────────────────────────────────

def run_pair(conn, pair, symbol, source='coindcx'):
    """Load/sync all timeframes for a single pair.

    Args:
        source: 'coindcx' (default) or 'binance'
    """
    global PAIR, SYMBOL
    PAIR   = pair
    SYMBOL = symbol

    print(f"\n{'#'*60}")
    print(f"  PAIR: {pair}  ({symbol})  source={source}")
    print(f"  Start={datetime.utcfromtimestamp(START_TS).strftime('%Y-%m-%d')} End={datetime.utcfromtimestamp(END_TS).strftime('%Y-%m-%d %H:%M:%S')} UTC")
    print(f"{'#'*60}")

    pair_total = 0

    # Step 1: Direct loads
    for tf in DIRECT_TIMEFRAMES:
        pair_total += load_direct(conn, tf, source)

    # Step 2: Aggregated loads
    for tf in AGGREGATE_TIMEFRAMES:
        pair_total += load_aggregate(conn, tf)

    print(f"\n  {pair} — new rows this run: {pair_total}")

    # Per-timeframe count for this pair
    print(f"\n  Final counts for {pair}:")
    cur = conn.cursor()
    cur.execute(
        "SELECT duration, COUNT(*) as cnt, "
        "MIN(open_time) as earliest, MAX(open_time) as latest "
        "FROM websocket_futures_candlestick_data "
        "WHERE pair=%s "
        "GROUP BY duration ORDER BY duration",
        (pair,)
    )
    for row in cur.fetchall():
        dur, cnt, earliest, latest = row
        e_str = datetime.utcfromtimestamp(earliest/1000).strftime('%Y-%m-%d %H:%M') if earliest else '-'
        l_str = datetime.utcfromtimestamp(latest/1000).strftime('%Y-%m-%d %H:%M') if latest else '-'
        print(f"    duration={dur:>5}  count={cnt:>8}  earliest={e_str}  latest={l_str}")
    cur.close()
    return pair_total


def main():
    print(f"Connecting to MySQL...")
    conn = mysql.connector.connect(**DB_CONFIG)
    print(f"Connected.")

    grand_total = 0
    for pair, symbol, source in PAIRS:
        grand_total += run_pair(conn, pair, symbol, source)

    print(f"\n{'='*60}")
    print(f"  ALL PAIRS COMPLETE — grand total new rows: {grand_total}")
    print(f"{'='*60}")

    conn.close()
    print("\nDone.")


if __name__ == '__main__':
    main()
