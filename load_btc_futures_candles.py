"""
Load B-BTC_USDT 1-minute futures candlestick data from CoinDCX
into the MySQL websocket_futures_candlestick_data table.

Date range : 2026-01-01 00:00:00 UTC  →  today (2026-03-29)
Endpoint   : https://public.coindcx.com/market_data/candlesticks
             (same endpoint used by the CoinDCX MCP, pcode=f for futures)
"""

import json
import time
import requests
import mysql.connector
from datetime import datetime, timezone

# ──────────────────────────────────────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────────────────────────────────────
DB_CONFIG = {
    "host":     "localhost",
    "port":     3306,
    "user":     "root",
    "password": "Kakri@1234",
    "database": "coindcx",
}

PAIR        = "B-BTC_USDT"
SYMBOL      = "BTCUSDT"
RESOLUTION  = "1"          # 1-minute candles
PRODUCT     = "futures"
CHANNEL     = "futures_candlestick"

# 2026-01-01 00:00:00 UTC  (epoch seconds)
START_TS    = 1767225600
# 2026-03-30 00:00:00 UTC  (epoch seconds) – covers full day of 2026-03-29
END_TS      = 1774828800

CHUNK_SECS  = 86400        # fetch one day per request (1 440 × 1-min candles)
SLEEP_SEC   = 0.35         # polite delay between requests

API_URL = "https://public.coindcx.com/market_data/candlesticks"

# ──────────────────────────────────────────────────────────────────────────────
# Helpers
# ──────────────────────────────────────────────────────────────────────────────

def fetch_candles(from_ts: int, to_ts: int) -> list:
    params = {
        "pair":       PAIR,
        "resolution": RESOLUTION,
        "from":       from_ts,
        "to":         to_ts,
        "pcode":      "f",          # f = futures (as used by the MCP)
    }
    resp = requests.get(API_URL, params=params, timeout=30)
    resp.raise_for_status()
    result = resp.json()
    if result.get("s") != "ok":
        return []
    return result.get("data", [])


INSERT_SQL = """
INSERT INTO websocket_futures_candlestick_data
    (channel_name, close, close_time, duration, ets,
     high, `interval`, low, open, open_time,
     pair, product, quote_volume, raw_data, record_timestamp,
     symbol, volume)
VALUES
    (%s, %s, %s, %s, %s,
     %s, %s, %s, %s, %s,
     %s, %s, %s, %s, %s,
     %s, %s)
"""

EXISTING_SQL = """
SELECT open_time FROM websocket_futures_candlestick_data
WHERE pair = %s AND `interval` = %s AND open_time >= %s AND open_time < %s
"""

# ──────────────────────────────────────────────────────────────────────────────
# Main
# ──────────────────────────────────────────────────────────────────────────────

def main():
    cnx = mysql.connector.connect(**DB_CONFIG)
    cur = cnx.cursor()

    total_inserted = 0
    total_skipped  = 0
    chunk_from = START_TS

    print(f"\nLoading {PAIR} 1m futures candles from "
          f"{datetime.fromtimestamp(START_TS, tz=timezone.utc).date()} "
          f"to {datetime.fromtimestamp(END_TS,   tz=timezone.utc).date()}\n")

    while chunk_from < END_TS:
        chunk_to = min(chunk_from + CHUNK_SECS, END_TS)
        date_label = datetime.fromtimestamp(chunk_from, tz=timezone.utc).strftime("%Y-%m-%d")

        # open_time stored as milliseconds in the DB
        chunk_from_ms = chunk_from * 1000
        chunk_to_ms   = chunk_to   * 1000

        # Find already-existing open_times for this window (interval='1')
        cur.execute(EXISTING_SQL, (PAIR, RESOLUTION, chunk_from_ms, chunk_to_ms))
        existing_set = {row[0] for row in cur.fetchall()}

        candles = fetch_candles(chunk_from, chunk_to)
        inserted_this_chunk = 0

        if candles:
            now_dt  = datetime.now()
            ets_ms  = int(time.time() * 1000)
            rows    = []

            for c in candles:
                open_time_ms  = c["time"]           # milliseconds epoch
                if open_time_ms in existing_set:
                    total_skipped += 1
                    continue
                close_time_ms = open_time_ms + 59999 # end of the 1-min bar

                rows.append((
                    CHANNEL,           # channel_name
                    c["close"],        # close
                    float(close_time_ms),   # close_time  (double, ms)
                    RESOLUTION,        # duration
                    ets_ms,            # ets
                    c["high"],         # high
                    RESOLUTION,        # interval
                    c["low"],          # low
                    c["open"],         # open
                    open_time_ms,      # open_time (bigint, ms)
                    PAIR,              # pair
                    PRODUCT,           # product
                    0.0,               # quote_volume  (not in public feed)
                    json.dumps(c),     # raw_data
                    now_dt,            # record_timestamp
                    SYMBOL,            # symbol
                    c["volume"],       # volume
                ))

            if rows:
                cur.executemany(INSERT_SQL, rows)
                cnx.commit()
                inserted_this_chunk = len(rows)

        total_inserted += inserted_this_chunk

        print(f"  {date_label}  fetched={len(candles):4d}  "
              f"new={inserted_this_chunk:4d}  skipped(dup)={len(existing_set):3d}  "
              f"(total_inserted: {total_inserted:,})")

        chunk_from = chunk_to
        time.sleep(SLEEP_SEC)

    cur.close()
    cnx.close()

    print(f"\n{'='*60}")
    print(f"  Done.  Total inserted : {total_inserted:,}")
    print(f"         Total skipped  : {total_skipped:,}  (already present)")
    print(f"{'='*60}")


if __name__ == "__main__":
    main()
