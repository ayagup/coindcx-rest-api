<<<<<<< HEAD
import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ReactECharts from 'echarts-for-react';
import { apiService } from '../services/api';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { ArrowLeft, RefreshCw, TrendingUp } from 'lucide-react';

interface Timeframe {
  label: string;
  seconds: number;
}

const TIMEFRAMES: Timeframe[] = [
  { label: '1m',  seconds: 60 },
  { label: '3m',  seconds: 180 },
  { label: '5m',  seconds: 300 },
  { label: '15m', seconds: 900 },
  { label: '30m', seconds: 1800 },
  { label: '1h',  seconds: 3600 },
  { label: '2h',  seconds: 7200 },
  { label: '4h',  seconds: 14400 },
  { label: '1d',  seconds: 86400 },
  { label: '1w',  seconds: 604800 },
];

type TickItem = { markPrice: number; eventTimestamp?: number; recordTimestamp?: string };
type OHLCVItem = {
  open: number; high: number; low: number; close: number; volume?: number;
  time?: number; timestamp?: number; openTime?: number;
  eventTimestamp?: number; recordTimestamp?: string;
};

interface OHLCVBar {
=======
import React, { useEffect, useRef, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as echarts from 'echarts';
import { apiService } from '../services/api';
import ErrorMessage from '../components/ErrorMessage';
import { ArrowLeft, RefreshCw } from 'lucide-react';

const FALLBACK_INTERVALS = ['1m', '3m', '5m', '15m', '30m', '1h', '2h', '4h', '6h', '12h', '1d', '1w'];

interface Bar {
>>>>>>> 84333d70095238b68805b30cb677b4043c137ddf
  time: number;
  open: number;
  high: number;
  low: number;
  close: number;
<<<<<<< HEAD
  volume?: number;
}

// ── Indicator IDs ─────────────────────────────────────────────────────────────
type IndicatorId = 'SMA5' | 'SMA10' | 'SMA20' | 'EMA21' | 'BB' | 'VWMA21' | 'VWMA50' | 'VWMA200' | 'MSBOB' | 'DIV' | 'Volume' | 'RSI' | 'MACD';

const INDICATOR_LABELS: Record<IndicatorId, string> = {
  SMA5:    'SMA 5',
  SMA10:   'SMA 10',
  SMA20:   'SMA 20',
  EMA21:   'EMA 21',
  BB:      'Bollinger',
  VWMA21:  'VWMA 21',
  VWMA50:  'VWMA 50',
  VWMA200: 'VWMA 200',
  MSBOB:   'MSB-OB',
  DIV:     'Divergence',
  Volume:  'Volume',
  RSI:     'RSI 14',
  MACD:    'MACD',
};

const SUB_INDICATORS: IndicatorId[] = ['Volume', 'RSI', 'MACD'];

// ── Indicator math ────────────────────────────────────────────────────────────

function calcSMA(closes: number[], period: number): (number | null)[] {
  return closes.map((_, i) => {
    if (i < period - 1) return null;
    const sum = closes.slice(i - period + 1, i + 1).reduce((a, b) => a + b, 0);
    return sum / period;
  });
}

function calcEMA(closes: number[], period: number): (number | null)[] {
  const k = 2 / (period + 1);
  const result: (number | null)[] = new Array(closes.length).fill(null);
  let seed = -1;
  for (let i = 0; i < closes.length; i++) {
    if (i < period - 1) continue;
    if (seed === -1) {
      // first EMA = SMA of first `period` values
      result[i] = closes.slice(0, period).reduce((a, b) => a + b, 0) / period;
      seed = i;
    } else {
      result[i] = closes[i] * k + (result[i - 1] as number) * (1 - k);
    }
  }
  return result;
}

/** Volume-Weighted Moving Average: sum(close*vol, n) / sum(vol, n) */
function calcVWMA(closes: number[], volumes: number[], period: number): (number | null)[] {
  return closes.map((_, i) => {
    if (i < period - 1) return null;
    let sumPV = 0, sumV = 0;
    for (let j = i - period + 1; j <= i; j++) {
      sumPV += closes[j] * (volumes[j] || 0);
      sumV  += volumes[j] || 0;
    }
    return sumV === 0 ? null : sumPV / sumV;
  });
}

interface BBPoint { upper: number | null; middle: number | null; lower: number | null }

function calcBB(closes: number[], period = 20, stdMult = 2): BBPoint[] {
  return closes.map((_, i) => {
    if (i < period - 1) return { upper: null, middle: null, lower: null };
    const slice = closes.slice(i - period + 1, i + 1);
    const mean  = slice.reduce((a, b) => a + b, 0) / period;
    const variance = slice.reduce((a, b) => a + (b - mean) ** 2, 0) / period;
    const std = Math.sqrt(variance);
    return { upper: mean + stdMult * std, middle: mean, lower: mean - stdMult * std };
  });
}

function calcRSI(closes: number[], period = 14): (number | null)[] {
  const result: (number | null)[] = new Array(closes.length).fill(null);
  if (closes.length < period + 1) return result;

  let gains = 0, losses = 0;
  for (let i = 1; i <= period; i++) {
    const diff = closes[i] - closes[i - 1];
    if (diff >= 0) gains += diff; else losses -= diff;
  }
  let avgGain = gains / period;
  let avgLoss = losses / period;
  result[period] = avgLoss === 0 ? 100 : 100 - 100 / (1 + avgGain / avgLoss);

  for (let i = period + 1; i < closes.length; i++) {
    const diff = closes[i] - closes[i - 1];
    avgGain = (avgGain * (period - 1) + Math.max(diff, 0)) / period;
    avgLoss = (avgLoss * (period - 1) + Math.max(-diff, 0)) / period;
    result[i] = avgLoss === 0 ? 100 : 100 - 100 / (1 + avgGain / avgLoss);
  }
  return result;
}

interface MACDPoint { macd: number | null; signal: number | null; hist: number | null }

function calcMACD(closes: number[], fast = 12, slow = 26, signalPeriod = 9): MACDPoint[] {
  const emaFast   = calcEMA(closes, fast);
  const emaSlow   = calcEMA(closes, slow);
  const macdLine  = closes.map((_, i) =>
    emaFast[i] !== null && emaSlow[i] !== null ? (emaFast[i] as number) - (emaSlow[i] as number) : null,
  );
  // signal = EMA of macdLine (only non-null values)
  const macdValues = macdLine.filter((v): v is number => v !== null);
  const signalRaw  = calcEMA(macdValues, signalPeriod);

  // Map signalRaw back to full-length array aligned with macdLine
  const result: MACDPoint[] = new Array(closes.length).fill(null).map(() => ({
    macd: null, signal: null, hist: null,
  }));
  let nonNullIdx = 0;
  for (let i = 0; i < closes.length; i++) {
    if (macdLine[i] !== null) {
      const sig = signalRaw[nonNullIdx] ?? null;
      const m   = macdLine[i] as number;
      result[i] = {
        macd:   m,
        signal: sig,
        hist:   sig !== null ? m - sig : null,
      };
      nonNullIdx++;
    }
  }
  return result;
}

// ── Extra indicator math (used by Divergence) ─────────────────────────────────

function calcStoch(
  closes: number[], highs: number[], lows: number[],
  kPeriod = 14, smooth = 3,
): (number | null)[] {
  const k: (number | null)[] = closes.map((c, i) => {
    if (i < kPeriod - 1) return null;
    let hh = -Infinity, ll = Infinity;
    for (let j = i - kPeriod + 1; j <= i; j++) {
      if (highs[j] > hh) hh = highs[j];
      if (lows[j]  < ll) ll = lows[j];
    }
    return hh === ll ? 0 : (c - ll) / (hh - ll) * 100;
  });
  return k.map((_, i) => {
    const sl = k.slice(Math.max(0, i - smooth + 1), i + 1).filter((v): v is number => v !== null);
    return sl.length < smooth ? null : sl.reduce((a, b) => a + b, 0) / smooth;
  });
}

function calcCCI(closes: number[], highs: number[], lows: number[], period = 10): (number | null)[] {
  const tp = closes.map((c, i) => (highs[i] + lows[i] + c) / 3);
  return tp.map((_, i) => {
    if (i < period - 1) return null;
    const sl   = tp.slice(i - period + 1, i + 1);
    const mean = sl.reduce((a, b) => a + b, 0) / period;
    const md   = sl.reduce((a, b) => a + Math.abs(b - mean), 0) / period;
    return md === 0 ? 0 : (tp[i] - mean) / (0.015 * md);
  });
}

function calcMOM(closes: number[], period = 10): (number | null)[] {
  return closes.map((c, i) => i < period ? null : c - closes[i - period]);
}

function calcOBV(closes: number[], volumes: number[]): number[] {
  const obv = [0];
  for (let i = 1; i < closes.length; i++) {
    const prev = obv[i - 1];
    if      (closes[i] > closes[i - 1]) obv.push(prev + (volumes[i] ?? 0));
    else if (closes[i] < closes[i - 1]) obv.push(prev - (volumes[i] ?? 0));
    else obv.push(prev);
  }
  return obv;
}

function calcVWMACD(closes: number[], volumes: number[], fast = 12, slow = 26): (number | null)[] {
  const vwFast = calcVWMA(closes, volumes, fast);
  const vwSlow = calcVWMA(closes, volumes, slow);
  return closes.map((_, i) =>
    vwFast[i] !== null && vwSlow[i] !== null
      ? (vwFast[i] as number) - (vwSlow[i] as number)
      : null,
  );
}

function calcCMF(
  closes: number[], highs: number[], lows: number[], volumes: number[], period = 21,
): (number | null)[] {
  const cmfv = closes.map((c, i) => {
    const hl = highs[i] - lows[i];
    return (hl === 0 ? 0 : ((c - lows[i]) - (highs[i] - c)) / hl) * (volumes[i] ?? 0);
  });
  return closes.map((_, i) => {
    if (i < period - 1) return null;
    const sumV    = volumes.slice(i - period + 1, i + 1).reduce((a, b) => a + (b ?? 0), 0);
    const sumCMFV = cmfv.slice(i - period + 1, i + 1).reduce((a, b) => a + b, 0);
    return sumV === 0 ? null : sumCMFV / sumV;
  });
}

function calcMFI(
  closes: number[], highs: number[], lows: number[], volumes: number[], period = 14,
): (number | null)[] {
  const tp = closes.map((c, i) => (highs[i] + lows[i] + c) / 3);
  const mf = tp.map((t, i) => t * (volumes[i] ?? 0));
  return closes.map((_, i) => {
    if (i < period) return null;
    let pos = 0, neg = 0;
    for (let j = i - period + 1; j <= i; j++) {
      if (tp[j] > tp[j - 1]) pos += mf[j];
      else if (tp[j] < tp[j - 1]) neg += mf[j];
    }
    return neg === 0 ? 100 : 100 - 100 / (1 + pos / neg);
  });
}

// ── Divergence for Many Indicators ───────────────────────────────────────────

interface DivSettings {
  prd:         number;
  source:      'Close' | 'High/Low';
  searchdiv:   'Regular' | 'Hidden' | 'Regular/Hidden';
  showindis:   'Full' | 'First Letter' | "Don't Show";
  showlimit:   number;
  maxpp:       number;
  maxbars:     number;
  shownum:     boolean;
  showlast:    boolean;
  dontconfirm: boolean;
  showlines:   boolean;
  showpivot:   boolean;
  calcmacd:    boolean;
  calcmacda:   boolean;
  calcrsi:     boolean;
  calcstoc:    boolean;
  calccci:     boolean;
  calcmom:     boolean;
  calcobv:     boolean;
  calcvwmacd:  boolean;
  calccmf:     boolean;
  calcmfi:     boolean;
  posRegDivColor: string;
  negRegDivColor: string;
  posHidDivColor: string;
  negHidDivColor: string;
  posTextColor:   string;
  negTextColor:   string;
  regLineStyle:   'Solid' | 'Dashed' | 'Dotted';
  hidLineStyle:   'Solid' | 'Dashed' | 'Dotted';
  regLineWidth:   number;
  hidLineWidth:   number;
  showMAs:   boolean;
  ma1Color:  string;
  ma2Color:  string;
}

const DEFAULT_DIV: DivSettings = {
  prd: 5,  source: 'Close',  searchdiv: 'Regular',
  showindis: 'Full',  showlimit: 1,  maxpp: 10,  maxbars: 100,
  shownum: true,  showlast: false,  dontconfirm: false,
  showlines: true,  showpivot: false,
  calcmacd: true,  calcmacda: true,  calcrsi: true,  calcstoc: true,
  calccci: true,   calcmom: true,   calcobv: true,  calcvwmacd: true,
  calccmf: true,   calcmfi: true,
  posRegDivColor: '#eab308',  negRegDivColor: '#1e40af',
  posHidDivColor: '#22c55e',  negHidDivColor: '#ef4444',
  posTextColor: '#0f172a',    negTextColor: '#f8fafc',
  regLineStyle: 'Solid',  hidLineStyle: 'Dashed',
  regLineWidth: 2,  hidLineWidth: 1,
  showMAs: false,  ma1Color: '#22c55e',  ma2Color: '#ef4444',
};

interface DivLine  { x1: number; y1: number; x2: number; y2: number; color: string; ls: 'solid'|'dashed'|'dotted'; lw: number }
interface DivLabel { x: number; y: number; text: string; bgColor: string; textColor: string; above: boolean }
interface DivResult { lines: DivLine[]; posLabels: DivLabel[]; negLabels: DivLabel[]; phIdxs: number[]; plIdxs: number[] }

function calcDivergences(data: OHLCVBar[], s: DivSettings): DivResult {
  const n = data.length;
  const empty: DivResult = { lines: [], posLabels: [], negLabels: [], phIdxs: [], plIdxs: [] };
  if (n < s.prd * 3 + 5) return empty;

  const closes  = data.map(d => d.close);
  const highs   = data.map(d => d.high);
  const lows    = data.map(d => d.low);
  const volumes = data.map(d => d.volume ?? 0);

  // ── Compute all indicator arrays ─────────────────────────────────────────
  const rsiVals    = calcRSI(closes);
  const macdArr    = calcMACD(closes);
  const macdVals   = macdArr.map(m => m.macd);
  const macdHVals  = macdArr.map(m => m.hist);
  const stochVals  = calcStoch(closes, highs, lows);
  const cciVals    = calcCCI(closes, highs, lows);
  const momVals    = calcMOM(closes);
  const obvVals: (number | null)[] = calcOBV(closes, volumes);
  const vwmVals    = calcVWMACD(closes, volumes);
  const cmfVals    = calcCMF(closes, highs, lows, volumes);
  const mfiVals    = calcMFI(closes, highs, lows, volumes);

  const indiList: Array<{ name: string; vals: (number | null)[]; on: boolean }> = [
    { name: 'MACD',   vals: macdVals,  on: s.calcmacd   },
    { name: 'Hist',   vals: macdHVals, on: s.calcmacda  },
    { name: 'RSI',    vals: rsiVals,   on: s.calcrsi    },
    { name: 'Stoch',  vals: stochVals, on: s.calcstoc   },
    { name: 'CCI',    vals: cciVals,   on: s.calccci    },
    { name: 'MOM',    vals: momVals,   on: s.calcmom    },
    { name: 'OBV',    vals: obvVals,   on: s.calcobv    },
    { name: 'VWMACD', vals: vwmVals,   on: s.calcvwmacd },
    { name: 'CMF',    vals: cmfVals,   on: s.calccmf    },
    { name: 'MFI',    vals: mfiVals,   on: s.calcmfi    },
  ];

  // ── Find confirmed pivot highs / lows ────────────────────────────────────
  const phSrc = s.source === 'Close' ? closes : highs;
  const plSrc = s.source === 'Close' ? closes : lows;
  const conf  = s.dontconfirm ? 0 : s.prd;
  const phArr: number[] = [];
  const plArr: number[] = [];

  for (let i = s.prd; i < n - conf; i++) {
    let isH = true, isL = true;
    for (let j = i - s.prd; j <= i + s.prd && j < n; j++) {
      if (j === i) continue;
      if (phSrc[j] >= phSrc[i]) isH = false;
      if (plSrc[j] <= plSrc[i]) isL = false;
    }
    if (isH) phArr.push(i);
    if (isL) plArr.push(i);
  }

  // ── Helpers ───────────────────────────────────────────────────────────────
  const lsOf = (st: 'Solid'|'Dashed'|'Dotted'): 'solid'|'dashed'|'dotted' =>
    st === 'Solid' ? 'solid' : st === 'Dashed' ? 'dashed' : 'dotted';

  const indiLabel = (name: string) =>
    s.showindis === 'Full' ? name : s.showindis === 'First Letter' ? name[0] : '';

  /** Check that no intermediate bar's value crosses the slope line in the wrong direction */
  const intact = (
    vals: (number|null)[], x1: number, v1: number, x2: number, v2: number, mustBeAbove: boolean,
  ): boolean => {
    const span = x2 - x1; if (span <= 1) return true;
    const slope = (v2 - v1) / span;
    for (let k = 1; k < span; k++) {
      const v = vals[x1 + k]; if (v === null || v === undefined) continue;
      const lv = v1 + slope * k;
      if (mustBeAbove  && (v as number) < lv) return false;
      if (!mustBeAbove && (v as number) > lv) return false;
    }
    return true;
  };

  // posGroups / negGroups: key = `${curBIdx}-${oldBIdx}` → {line, names}
  type DKey = string;
  type DEntry = { line: DivLine; names: string[]; barIdx: number };
  const posG = new Map<DKey, DEntry>();
  const negG = new Map<DKey, DEntry>();

  const tryDiv = (
    map: Map<DKey, DEntry>,
    regular: boolean,
    curIdx: number, oldIdx: number,
    curY: number,  oldY: number,
    curV: number,  oldV: number,
    mustBeAbove: boolean,
    color: string, ls: 'solid'|'dashed'|'dotted', lw: number,
    indiArr: (number|null)[],
    name: string,
  ) => {
    // Line cross integrity
    if (!intact(indiArr,   oldIdx, oldV, curIdx, curV, mustBeAbove)) return;
    if (!intact(closes as (number|null)[], oldIdx, closes[oldIdx], curIdx, closes[curIdx], mustBeAbove)) return;

    const key = `${curIdx}-${oldIdx}`;
    if (!map.has(key)) {
      map.set(key, {
        line: s.showlines ? { x1: oldIdx, y1: oldY, x2: curIdx, y2: curY, color, ls, lw } as DivLine
                          : null as any,
        names: [], barIdx: curIdx,
      });
    }
    const entry = map.get(key)!;
    const lbl = indiLabel(name);
    if (lbl && !entry.names.includes(lbl)) entry.names.push(lbl);
    // update color from regular type (regular wins over hidden)
    if (regular && entry.line) entry.line.color = color;
  };

  // ── Bullish divergences at pivot LOWS (+ patterns) ───────────────────────
  for (let pi = 1; pi < plArr.length; pi++) {
    const curIdx = plArr[pi];
    const curP   = plSrc[curIdx];
    let checked  = 0;
    for (let pj = pi - 1; pj >= 0 && checked < s.maxpp; pj--, checked++) {
      const oldIdx = plArr[pj];
      const oldP   = plSrc[oldIdx];
      const dist   = curIdx - oldIdx;
      if (dist > s.maxbars || dist < 5) continue;

      for (const ind of indiList) {
        if (!ind.on) continue;
        const curV = ind.vals[curIdx] as number; if (curV == null) continue;
        const oldV = ind.vals[oldIdx] as number; if (oldV == null) continue;

        // Positive Regular: indicator higher-low, price lower-low
        if ((s.searchdiv === 'Regular' || s.searchdiv === 'Regular/Hidden') && curV > oldV && curP < oldP)
          tryDiv(posG, true,  curIdx, oldIdx, curP, oldP, curV, oldV, true,
            s.posRegDivColor, lsOf(s.regLineStyle), s.regLineWidth, ind.vals, ind.name);

        // Negative Hidden: indicator lower-low, price higher-low
        if ((s.searchdiv === 'Hidden' || s.searchdiv === 'Regular/Hidden') && curV < oldV && curP > oldP)
          tryDiv(posG, false, curIdx, oldIdx, curP, oldP, curV, oldV, false,
            s.negHidDivColor, lsOf(s.hidLineStyle), s.hidLineWidth, ind.vals, ind.name);
      }
    }
  }

  // ── Bearish divergences at pivot HIGHS (- patterns) ──────────────────────
  for (let pi = 1; pi < phArr.length; pi++) {
    const curIdx = phArr[pi];
    const curP   = phSrc[curIdx];
    let checked  = 0;
    for (let pj = pi - 1; pj >= 0 && checked < s.maxpp; pj--, checked++) {
      const oldIdx = phArr[pj];
      const oldP   = phSrc[oldIdx];
      const dist   = curIdx - oldIdx;
      if (dist > s.maxbars || dist < 5) continue;

      for (const ind of indiList) {
        if (!ind.on) continue;
        const curV = ind.vals[curIdx] as number; if (curV == null) continue;
        const oldV = ind.vals[oldIdx] as number; if (oldV == null) continue;

        // Negative Regular: indicator lower-high, price higher-high
        if ((s.searchdiv === 'Regular' || s.searchdiv === 'Regular/Hidden') && curV < oldV && curP > oldP)
          tryDiv(negG, true,  curIdx, oldIdx, curP, oldP, curV, oldV, false,
            s.negRegDivColor, lsOf(s.regLineStyle), s.regLineWidth, ind.vals, ind.name);

        // Positive Hidden: indicator higher-high, price lower-high
        if ((s.searchdiv === 'Hidden' || s.searchdiv === 'Regular/Hidden') && curV > oldV && curP < oldP)
          tryDiv(negG, false, curIdx, oldIdx, curP, oldP, curV, oldV, true,
            s.posHidDivColor, lsOf(s.hidLineStyle), s.hidLineWidth, ind.vals, ind.name);
      }
    }
  }

  // ── Apply showlimit filter (minimum divergences per bar) ──────────────────
  // Count total divergence entries at each bar index; if < showlimit, discard
  if (s.showlimit > 1) {
    const barCount = new Map<number, number>();
    const countMap = (m: Map<DKey, DEntry>) => {
      m.forEach(e => barCount.set(e.barIdx, (barCount.get(e.barIdx) ?? 0) + 1));
    };
    countMap(posG); countMap(negG);
    const prune = (m: Map<DKey, DEntry>) => {
      m.forEach((e, k) => { if ((barCount.get(e.barIdx) ?? 0) < s.showlimit) m.delete(k); });
    };
    prune(posG); prune(negG);
  }

  // ── Apply showlast (only most recent divergence group per direction) ──────
  if (s.showlast) {
    const keepLast = (m: Map<DKey, DEntry>) => {
      let maxBar = -1;
      m.forEach(e => { if (e.barIdx > maxBar) maxBar = e.barIdx; });
      m.forEach((_, k) => { if (m.get(k)!.barIdx < maxBar) m.delete(k); });
    };
    keepLast(posG); keepLast(negG);
  }

  // ── Collect results ───────────────────────────────────────────────────────
  const lines: DivLine[] = [];
  const posLabels: DivLabel[] = [];
  const negLabels: DivLabel[] = [];

  // Aggregate labels per barIdx
  const posBarMap = new Map<number, DivLabel>();
  posG.forEach(e => {
    if (e.line) lines.push(e.line);
    const numStr = s.shownum ? String(e.names.length) : '';
    const text   = e.names.join('\n') + (numStr ? (e.names.length ? '\n' : '') + numStr : '');
    if (!posBarMap.has(e.barIdx)) {
      posBarMap.set(e.barIdx, {
        x: e.barIdx, y: lows[e.barIdx], text, bgColor: s.posRegDivColor, textColor: s.posTextColor, above: false,
      });
    } else {
      posBarMap.get(e.barIdx)!.text += '\n' + text;
    }
  });
  posBarMap.forEach(l => posLabels.push(l));

  const negBarMap = new Map<number, DivLabel>();
  negG.forEach(e => {
    if (e.line) lines.push(e.line);
    const numStr = s.shownum ? String(e.names.length) : '';
    const text   = e.names.join('\n') + (numStr ? (e.names.length ? '\n' : '') + numStr : '');
    if (!negBarMap.has(e.barIdx)) {
      negBarMap.set(e.barIdx, {
        x: e.barIdx, y: highs[e.barIdx], text, bgColor: s.negRegDivColor, textColor: s.negTextColor, above: true,
      });
    } else {
      negBarMap.get(e.barIdx)!.text += '\n' + text;
    }
  });
  negBarMap.forEach(l => negLabels.push(l));

  return { lines, posLabels, negLabels, phIdxs: phArr, plIdxs: plArr };
}

// ── MSB-OB ───────────────────────────────────────────────────────────────────

interface MSBOBSettings {
  zigzagLen: number;
  showZigzag: boolean;
  fibFactor: number;
  deleteBoxes: boolean;
  buOBColor: string;       // hex
  buOBBorderColor: string; // hex
  beOBColor: string;
  beOBBorderColor: string;
  buBBColor: string;
  buBBBorderColor: string;
  beBBColor: string;
  beBBBorderColor: string;
}

const DEFAULT_MSBOB: MSBOBSettings = {
  zigzagLen:       9,
  showZigzag:      true,
  fibFactor:       0.33,
  deleteBoxes:     true,
  buOBColor:       '#10b981',
  buOBBorderColor: '#10b981',
  beOBColor:       '#ef4444',
  beOBBorderColor: '#ef4444',
  buBBColor:       '#22d3ee',
  buBBBorderColor: '#22d3ee',
  beBBColor:       '#f97316',
  beBBBorderColor: '#f97316',
};

interface MSBZZLine  { x1: number; y1: number; x2: number; y2: number }
interface MSBLine    { x1: number; x2: number; y: number; color: string; midX: number; label: string }
interface MSBBox     { leftIdx: number; top: number; rightIdx: number; bottom: number; label: string; fillColor: string; borderColor: string }
interface MSBOBResult { zzLines: MSBZZLine[]; msbLines: MSBLine[]; buOB: MSBBox[]; beOB: MSBBox[]; buBB: MSBBox[]; beBB: MSBBox[] }

function calcMSBOB(data: OHLCVBar[], s: MSBOBSettings): MSBOBResult {
  const result: MSBOBResult = { zzLines: [], msbLines: [], buOB: [], beOB: [], buBB: [], beBB: [] };
  const n = data.length;
  if (n < s.zigzagLen * 3) return result;

  // Precompute rolling highest high / lowest low
  const rollingHigh = data.map((_, i) => {
    let m = -Infinity;
    for (let j = Math.max(0, i - s.zigzagLen + 1); j <= i; j++) if (data[j].high > m) m = data[j].high;
    return m;
  });
  const rollingLow = data.map((_, i) => {
    let m = Infinity;
    for (let j = Math.max(0, i - s.zigzagLen + 1); j <= i; j++) if (data[j].low < m) m = data[j].low;
    return m;
  });

  const toUp   = data.map((d, i) => d.high >= rollingHigh[i]);
  const toDown = data.map((d, i) => d.low  <= rollingLow[i]);

  // Pine Script array.new_box(5) — rolling queue, max 5 active boxes per type
  const MAX_BOXES = 5;
  // Live box queues (mutated during simulation)
  const livebuOB: MSBBox[] = [];
  const livebeOB: MSBBox[] = [];
  const livebuBB: MSBBox[] = [];
  const livebeBB: MSBBox[] = [];

  const pushBox = (arr: MSBBox[], box: MSBBox) => {
    if (arr.length >= MAX_BOXES) arr.shift(); // evict oldest (like array.shift in Pine)
    arr.push(box);
  };

  let trend  = 1;
  let market = 1;
  const hPts: number[] = [], hIdx: number[] = [];
  const lPts: number[] = [], lIdx: number[] = [];
  const push5 = (arr: number[], v: number) => { arr.push(v); if (arr.length > 5) arr.shift(); };
  let lastMktL0 = NaN;
  let lastMktH0 = NaN;

  for (let i = 1; i < n; i++) {
    const cl = data[i].close;

    // ── Per-bar: extend surviving boxes to current bar & prune broken ones ──
    const pruneOrExtend = (arr: MSBBox[], broken: (b: MSBBox) => boolean) => {
      for (let k = arr.length - 1; k >= 0; k--) {
        if (s.deleteBoxes && broken(arr[k])) {
          arr.splice(k, 1);
        } else {
          arr[k].rightIdx = i; // extend right edge to current bar
        }
      }
    };
    pruneOrExtend(livebuOB, b => cl < b.bottom);  // bull OB broken when close < bottom
    pruneOrExtend(livebeOB, b => cl > b.top);     // bear OB broken when close > top
    pruneOrExtend(livebuBB, b => cl < b.bottom);
    pruneOrExtend(livebeBB, b => cl > b.top);

    // ── Trend detection ─────────────────────────────────────────────────────
    const prevTrend = trend;
    if      (trend === 1  && toDown[i]) trend = -1;
    else if (trend === -1 && toUp[i])   trend =  1;

    if (trend !== prevTrend) {
      if (trend === -1) {
        let si = i - 1;
        while (si > 0 && !toUp[si]) si--;
        let mxH = -Infinity, mxHI = si;
        for (let j = si; j <= i; j++) if (data[j].high > mxH) { mxH = data[j].high; mxHI = j; }
        push5(hPts, mxH); push5(hIdx, mxHI);
        if (s.showZigzag && lPts.length > 0)
          result.zzLines.push({ x1: lIdx[lIdx.length - 1], y1: lPts[lPts.length - 1], x2: mxHI, y2: mxH });
      } else {
        let si = i - 1;
        while (si > 0 && !toDown[si]) si--;
        let mnL = Infinity, mnLI = si;
        for (let j = si; j <= i; j++) if (data[j].low < mnL) { mnL = data[j].low; mnLI = j; }
        push5(lPts, mnL); push5(lIdx, mnLI);
        if (s.showZigzag && hPts.length > 0)
          result.zzLines.push({ x1: hIdx[hIdx.length - 1], y1: hPts[hPts.length - 1], x2: mnLI, y2: mnL });
      }
    }

    if (hPts.length < 2 || lPts.length < 2) continue;

    const h0 = hPts[hPts.length - 1], h0i = hIdx[hIdx.length - 1];
    const h1 = hPts[hPts.length - 2], h1i = hIdx[hIdx.length - 2];
    const l0 = lPts[lPts.length - 1], l0i = lIdx[lIdx.length - 1];
    const l1 = lPts[lPts.length - 2], l1i = lIdx[lIdx.length - 2];

    // Guard: skip if pivots haven't changed since last market event
    if (l0 === lastMktL0 || h0 === lastMktH0) continue;

    let mktChanged = false;
    if (market === 1  && l0 < l1 && l0 < l1 - Math.abs(h0 - l1) * s.fibFactor) {
      market = -1; mktChanged = true;
    } else if (market === -1 && h0 > h1 && h0 > h1 + Math.abs(h1 - l0) * s.fibFactor) {
      market = 1;  mktChanged = true;
    }
    if (!mktChanged) continue;

    lastMktL0 = l0; lastMktH0 = h0;

    if (market === 1) {
      // Bullish MSB
      result.msbLines.push({ x1: h1i, x2: l0i, y: h1, color: '#10b981', midX: Math.floor((h1i + l0i) / 2), label: 'MSB' });

      let buOBI = -1;
      for (let j = Math.min(h1i, l0i); j <= Math.max(h1i, l0i); j++)
        if (data[j].open > data[j].close) buOBI = j;
      if (buOBI >= 0)
        pushBox(livebuOB, { leftIdx: buOBI, top: data[buOBI].high, rightIdx: i, bottom: data[buOBI].low, label: 'Bu-OB', fillColor: s.buOBColor, borderColor: s.buOBBorderColor });

      let buBBI = -1;
      for (let j = Math.max(0, l1i - s.zigzagLen); j <= h1i; j++)
        if (data[j].open < data[j].close) buBBI = j;
      if (buBBI >= 0)
        pushBox(livebuBB, { leftIdx: buBBI, top: data[buBBI].high, rightIdx: i, bottom: data[buBBI].low, label: l0 < l1 ? 'Bu-BB' : 'Bu-MB', fillColor: s.buBBColor, borderColor: s.buBBBorderColor });

    } else {
      // Bearish MSB
      result.msbLines.push({ x1: l1i, x2: h0i, y: l1, color: '#ef4444', midX: Math.floor((l1i + h0i) / 2), label: 'MSB' });

      let beOBI = -1;
      for (let j = Math.min(l1i, h0i); j <= Math.max(l1i, h0i); j++)
        if (data[j].open < data[j].close) beOBI = j;
      if (beOBI >= 0)
        pushBox(livebeOB, { leftIdx: beOBI, top: data[beOBI].high, rightIdx: i, bottom: data[beOBI].low, label: 'Be-OB', fillColor: s.beOBColor, borderColor: s.beOBBorderColor });

      let beBBI = -1;
      for (let j = Math.max(0, h1i - s.zigzagLen); j <= l1i; j++)
        if (data[j].open > data[j].close) beBBI = j;
      if (beBBI >= 0)
        pushBox(livebeBB, { leftIdx: beBBI, top: data[beBBI].high, rightIdx: i, bottom: data[beBBI].low, label: h0 > h1 ? 'Be-BB' : 'Be-MB', fillColor: s.beBBColor, borderColor: s.beBBBorderColor });
    }
  }

  // Snapshot surviving live boxes into result
  result.buOB = [...livebuOB];
  result.beOB = [...livebeOB];
  result.buBB = [...livebuBB];
  result.beBB = [...livebeBB];

  return result;
}

/** Returns true if the backend sent pre-aggregated OHLCV bars (timeframe was honoured). */
function isOHLCVResponse(items: any[]): boolean {
  if (!items.length) return false;
  const first = items[0];
  return first.open !== undefined && first.close !== undefined;
}

/** Map backend OHLCV items to chart-ready bars. */
function mapOHLCV(items: OHLCVItem[]): OHLCVBar[] {
  return items
    .map((item) => {
      const tsRaw =
        item.time        ??
        item.timestamp   ??
        item.openTime    ??
        item.eventTimestamp ??
        (item.recordTimestamp ? new Date(item.recordTimestamp).getTime() : null);
      if (tsRaw === null || tsRaw === undefined) return null;
      // Backend may return seconds or milliseconds — normalise to seconds for lightweight-charts
      const timeSec = tsRaw > 1e10 ? Math.floor(tsRaw / 1000) : tsRaw;
      return {
        time:   timeSec,
        open:   Number(item.open),
        high:   Number(item.high),
        low:    Number(item.low),
        close:  Number(item.close),
        volume: item.volume != null ? Number(item.volume) : undefined,
      };
    })
    .filter((b): b is NonNullable<typeof b> => b !== null && b.open > 0)
    .sort((a, b) => a.time - b.time);
}

function aggregateTicks(ticks: TickItem[], bucketSeconds: number): OHLCVBar[] {
  const buckets: Record<number, OHLCVBar> = {};

  ticks.forEach((item) => {
    const price = Number(item.markPrice);
    const tsMs = item.eventTimestamp
      ? item.eventTimestamp
      : new Date(item.recordTimestamp!).getTime();
    const bucket = Math.floor(tsMs / (bucketSeconds * 1000)) * bucketSeconds;

    if (!buckets[bucket]) {
      buckets[bucket] = { time: bucket, open: price, high: price, low: price, close: price };
    } else {
      const b = buckets[bucket];
      if (price > b.high) b.high = price;
      if (price < b.low) b.low = price;
      b.close = price;
    }
  });

  return Object.values(buckets).sort((a, b) => a.time - b.time);
=======
  volume: number;
>>>>>>> 84333d70095238b68805b30cb677b4043c137ddf
}

const ChartPage: React.FC = () => {
  const { marketType, symbol } = useParams<{ marketType: string; symbol: string }>();
  const navigate = useNavigate();
<<<<<<< HEAD
  const rawTicksRef = useRef<TickItem[]>([]);
  const echartsRef = useRef<any>(null);
  const timeframeRef = useRef<Timeframe>(TIMEFRAMES[0]);
  const fetchRef = useRef<(tf?: Timeframe) => Promise<void>>(async () => {});

  const [loading,       setLoading]       = useState(true);
  const [error,         setError]         = useState<string | null>(null);
  const [chartData,     setChartData]     = useState<OHLCVBar[]>([]);
  const [timeframe,     setTimeframe]     = useState<Timeframe>(TIMEFRAMES[0]);
  const [indicators,    setIndicators]    = useState<Set<IndicatorId>>(new Set<IndicatorId>());
  const [msbobSettings, setMsbobSettings] = useState<MSBOBSettings>(DEFAULT_MSBOB);
  const [divSettings,   setDivSettings]   = useState<DivSettings>(DEFAULT_DIV);

  const toggleIndicator = useCallback((id: IndicatorId) => {
    setIndicators((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id); else next.add(id);
      return next;
    });
  }, []);

  // ── ECharts option builder ────────────────────────────────────────────────

  const buildOption = useCallback((
    data: OHLCVBar[],
    tf: Timeframe,
    activeIndicators: Set<IndicatorId>,
    msbobCfg: MSBOBSettings,
    divCfg: DivSettings,
  ) => {
    const closes  = data.map((d) => d.close);
    const volumes = data.map((d) => d.volume ?? 0);

    // ── x-axis labels (+ 50 empty slots for right-side scroll room) ───────
    // Use unique index strings as category keys to prevent ECharts from
    // collapsing bars that share the same display label (e.g. all '29 Mar 15:30')
    const RIGHT_PAD = 50;
    const displayLabels: string[] = data.map((d) => {
      const ms = d.time > 1e10 ? d.time : d.time * 1000;
      const dt = new Date(ms);
      if (tf.seconds >= 86400) return dt.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: '2-digit' });
      if (tf.seconds >= 3600)  return dt.toLocaleDateString('en-GB', { day: '2-digit', month: 'short' }) + ' ' + dt.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
      return dt.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: tf.seconds < 60 ? '2-digit' : undefined });
    });
    // xData values are guaranteed unique (just the bar index as a string)
    const xData = [
      ...data.map((_, i) => String(i)),
      ...Array.from({ length: RIGHT_PAD }, (_, i) => `_${i}`),
    ];
    const totalLen = xData.length;
    const xLabelFormatter = (val: string) => {
      const idx = Number(val);
      return isNaN(idx) ? '' : (displayLabels[idx] ?? '');
    };

    // ECharts candlestick: [open, close, low, high]
    const ohlc = data.map((d) => [d.open, d.close, d.low, d.high]);

    const upColor   = '#10b981';
    const downColor = '#ef4444';

    // ── Sub-panel layout ───────────────────────────────────────────────────
    const subPanels = SUB_INDICATORS.filter((id) => activeIndicators.has(id));
    const SUB_H  = 16; // % height per sub-panel
    const GAP    = 2;  // % gap
    const ZOOM_H = 6;  // % for dataZoom slider
    const subTotalH = subPanels.length * (SUB_H + GAP);
    const priceBottom = subTotalH + ZOOM_H + 2;

    // Build grids: [0] = price, [1..N] = sub-panels
    const grids: any[] = [
      { left: '1%', right: '6%', top: '3%', bottom: `${priceBottom}%`, containLabel: true },
    ];
    const xAxes: any[]  = [];
    const yAxes: any[]  = [];
    const axisPtrLinks: any[] = [];

    const baseAxisCfg = (gridIndex: number) => ({
      type: 'category',
      data: xData,
      gridIndex,
      axisLine:  { lineStyle: { color: '#334155' } },
      axisTick:  { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#64748b', fontSize: 10, show: gridIndex === grids.length - 1, formatter: xLabelFormatter },
      splitLine: { show: false },
      axisPointer: { label: { show: false } },
    });

    // Price xAxis (hidden labels if sub-panels exist)
    xAxes.push({
      ...baseAxisCfg(0),
      axisLabel: { color: '#64748b', fontSize: 10, show: subPanels.length === 0, formatter: xLabelFormatter },
    });
    yAxes.push({
      scale: true, position: 'right', gridIndex: 0,
      axisLine:  { lineStyle: { color: '#334155' } },
      axisTick:  { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#64748b', fontSize: 11 },
      splitLine: { lineStyle: { color: '#1e3a4a', type: 'dashed' } },
    });
    axisPtrLinks.push({ xAxisIndex: 0 });

    subPanels.forEach((id, idx) => {
      const gi     = idx + 1;
      const bottom = ZOOM_H + 2 + (subPanels.length - 1 - idx) * (SUB_H + GAP);
      grids.push({ left: '1%', right: '6%', bottom: `${bottom}%`, height: `${SUB_H}%`, containLabel: true });

      xAxes.push({ ...baseAxisCfg(gi), axisLabel: { color: '#64748b', fontSize: 10, show: idx === subPanels.length - 1, formatter: xLabelFormatter } });

      const yMin = id === 'RSI' ? 0 : undefined;
      const yMax = id === 'RSI' ? 100 : undefined;
      yAxes.push({
        scale: id !== 'RSI',
        min: yMin, max: yMax,
        position: 'right',
        gridIndex: gi,
        axisLine:  { lineStyle: { color: '#334155' } },
        axisTick:  { lineStyle: { color: '#334155' } },
        axisLabel: { color: '#64748b', fontSize: 10 },
        splitLine: { lineStyle: { color: '#1e3a4a', type: 'dashed' } },
        splitNumber: 3,
      });
      axisPtrLinks.push({ xAxisIndex: gi });
    });

    // ── Series ─────────────────────────────────────────────────────────────
    const series: any[] = [
      {
        name: 'Price',
        type: 'candlestick',
        xAxisIndex: 0, yAxisIndex: 0,
        data: ohlc,
        itemStyle: {
          color: upColor, color0: downColor,
          borderColor: upColor, borderColor0: downColor,
          borderWidth: 1,
        },
        z: 10,
      },
    ];

    // ── Overlay indicators on price panel ─────────────────────────────────
    const overlayColors: Record<string, string> = {
      SMA5:    '#f59e0b', SMA10:   '#8b5cf6', SMA20:   '#3b82f6', EMA21: '#ec4899',
      VWMA21:  '#06b6d4', VWMA50:  '#a3e635', VWMA200: '#fb923c',
    };

    if (activeIndicators.has('SMA5'))  {
      const sma = calcSMA(closes, 5);
      series.push({ name: 'SMA 5', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: sma, smooth: false, symbol: 'none', lineStyle: { color: overlayColors.SMA5, width: 1.2 }, z: 5 });
    }
    if (activeIndicators.has('SMA10')) {
      const sma = calcSMA(closes, 10);
      series.push({ name: 'SMA 10', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: sma, smooth: false, symbol: 'none', lineStyle: { color: overlayColors.SMA10, width: 1.2 }, z: 5 });
    }
    if (activeIndicators.has('SMA20')) {
      const sma = calcSMA(closes, 20);
      series.push({ name: 'SMA 20', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: sma, smooth: false, symbol: 'none', lineStyle: { color: overlayColors.SMA20, width: 1.2 }, z: 5 });
    }
    if (activeIndicators.has('EMA21')) {
      const ema = calcEMA(closes, 21);
      series.push({ name: 'EMA 21', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: ema, smooth: false, symbol: 'none', lineStyle: { color: overlayColors.EMA21, width: 1.4, type: 'dashed' }, z: 5 });
    }
    if (activeIndicators.has('VWMA21')) {
      const vwma = calcVWMA(closes, volumes, 21);
      series.push({ name: 'VWMA 21', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: vwma, smooth: false, symbol: 'none', lineStyle: { color: overlayColors.VWMA21, width: 1.4 }, z: 5 });
    }
    if (activeIndicators.has('VWMA50')) {
      const vwma = calcVWMA(closes, volumes, 50);
      series.push({ name: 'VWMA 50', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: vwma, smooth: false, symbol: 'none', lineStyle: { color: overlayColors.VWMA50, width: 1.6 }, z: 5 });
    }
    if (activeIndicators.has('VWMA200')) {
      const vwma = calcVWMA(closes, volumes, 200);
      series.push({ name: 'VWMA 200', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: vwma, smooth: false, symbol: 'none', lineStyle: { color: overlayColors.VWMA200, width: 2 }, z: 5 });
    }
    if (activeIndicators.has('MSBOB')) {
      const msbob = calcMSBOB(data, msbobCfg);
      // hex colour → rgba helper
      const ha = (hex: string, alpha: number) => {
        const r = parseInt(hex.slice(1, 3), 16);
        const g = parseInt(hex.slice(3, 5), 16);
        const b = parseInt(hex.slice(5, 7), 16);
        return `rgba(${r},${g},${b},${alpha})`;
      };
      // ZigZag lines
      if (msbob.zzLines.length > 0) {
        series.push({
          name: 'ZigZag', type: 'scatter', data: [], xAxisIndex: 0, yAxisIndex: 0, z: 3,
          markLine: {
            silent: true, symbol: ['none', 'none'],
            lineStyle: { color: '#64748b', width: 1 },
            label: { show: false },
            data: msbob.zzLines.map(l => [{ coord: [l.x1, l.y1] }, { coord: [l.x2, l.y2] }]),
          },
        });
      }
      // MSB horizontal lines
      if (msbob.msbLines.length > 0) {
        series.push({
          name: 'MSB Lines', type: 'scatter', data: [], xAxisIndex: 0, yAxisIndex: 0, z: 6,
          markLine: {
            silent: true, symbol: ['none', 'none'],
            data: msbob.msbLines.map(l => [
              { coord: [l.x1, l.y], lineStyle: { color: l.color, width: 2 }, label: { show: false } },
              { coord: [l.x2, l.y], label: { show: true, formatter: l.label, color: l.color, fontSize: 10, fontWeight: 700, position: 'insideEndTop', backgroundColor: 'transparent', borderWidth: 0 } },
            ]),
          },
        });
      }
      // Box helper
      const boxSeries = (boxes: MSBBox[], alpha: number, name: string) => {
        if (!boxes.length) return null;
        return {
          name, type: 'scatter', data: [], xAxisIndex: 0, yAxisIndex: 0, z: 2,
          markArea: {
            silent: true,
            data: boxes.map(b => [
              {
                xAxis: b.leftIdx, yAxis: b.top,
                itemStyle: { color: ha(b.fillColor, alpha), borderColor: b.borderColor, borderWidth: 1 },
                label: { show: true, formatter: b.label, color: b.borderColor, fontSize: 10, fontWeight: 700, position: 'insideTopRight', backgroundColor: 'transparent', padding: [1, 3] },
              },
              { xAxis: Math.min(b.rightIdx, data.length - 1), yAxis: b.bottom },
            ]),
          },
        };
      };
      [
        boxSeries(msbob.buOB, 0.25, 'Bu-OB'),
        boxSeries(msbob.beOB, 0.25, 'Be-OB'),
        boxSeries(msbob.buBB, 0.20, 'Bu-BB'),
        boxSeries(msbob.beBB, 0.20, 'Be-BB'),
      ].forEach(s => s && series.push(s));
    }
    if (activeIndicators.has('BB')) {
      const bb = calcBB(closes);
      series.push(
        { name: 'BB Upper', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: bb.map((b) => b.upper), symbol: 'none', lineStyle: { color: '#94a3b8', width: 1, type: 'dashed' }, z: 4 },
        { name: 'BB Middle', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: bb.map((b) => b.middle), symbol: 'none', lineStyle: { color: '#94a3b8', width: 1 }, z: 4 },
        {
          name: 'BB Lower', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: bb.map((b) => b.lower), symbol: 'none',
          lineStyle: { color: '#94a3b8', width: 1, type: 'dashed' },
          areaStyle: { color: 'rgba(148,163,184,0.06)' },
          z: 4,
        },
      );
    }

    // ── Divergence ─────────────────────────────────────────────────────────
    if (activeIndicators.has('DIV')) {
      const div = calcDivergences(data, divCfg);

      // Divergence lines
      if (div.lines.length > 0) {
        series.push({
          name: 'DivLines', type: 'scatter', data: [], xAxisIndex: 0, yAxisIndex: 0, z: 7,
          markLine: {
            silent: true,
            symbol: ['none', 'none'],
            lineStyle: { type: 'solid' },
            label: { show: false },
            data: div.lines.map(l => [
              { coord: [l.x1, l.y1], lineStyle: { color: l.color, type: l.ls, width: l.lw } },
              { coord: [l.x2, l.y2], lineStyle: { color: l.color, type: l.ls, width: l.lw } },
            ]),
          },
        });
      }

      // Bullish labels (below bar)
      if (div.posLabels.length > 0) {
        series.push({
          name: 'DivLabelPos', type: 'scatter', data: [], xAxisIndex: 0, yAxisIndex: 0, z: 8,
          markPoint: {
            silent: true,
            symbol: 'pin',
            symbolSize: 0,
            data: div.posLabels.map(l => ({
              coord: [l.x, l.y],
              itemStyle: { color: l.bgColor },
              label: {
                show: divCfg.showindis !== "Don't Show" || divCfg.shownum,
                formatter: l.text,
                color: l.textColor,
                backgroundColor: l.bgColor,
                borderRadius: 3,
                padding: [2, 4],
                fontSize: 10,
                fontWeight: 700 as any,
                position: 'bottom',
              },
            })),
          },
        });
      }

      // Bearish labels (above bar)
      if (div.negLabels.length > 0) {
        series.push({
          name: 'DivLabelNeg', type: 'scatter', data: [], xAxisIndex: 0, yAxisIndex: 0, z: 8,
          markPoint: {
            silent: true,
            symbol: 'pin',
            symbolSize: 0,
            data: div.negLabels.map(l => ({
              coord: [l.x, l.y],
              itemStyle: { color: l.bgColor },
              label: {
                show: divCfg.showindis !== "Don't Show" || divCfg.shownum,
                formatter: l.text,
                color: l.textColor,
                backgroundColor: l.bgColor,
                borderRadius: 3,
                padding: [2, 4],
                fontSize: 10,
                fontWeight: 700 as any,
                position: 'top',
              },
            })),
          },
        });
      }

      // Pivot markers
      if (divCfg.showpivot) {
        const pivotPts: any[] = [
          ...div.phIdxs.map(i => ({ coord: [i, data[i].high], label: { show: true, formatter: 'H', color: '#ef4444', position: 'top', fontSize: 10, fontWeight: 700 }, itemStyle: { color: 'transparent' } })),
          ...div.plIdxs.map(i => ({ coord: [i, data[i].low],  label: { show: true, formatter: 'L', color: '#22c55e', position: 'bottom', fontSize: 10, fontWeight: 700 }, itemStyle: { color: 'transparent' } })),
        ];
        series.push({
          name: 'DivPivots', type: 'scatter', data: [], xAxisIndex: 0, yAxisIndex: 0, z: 6,
          markPoint: { silent: true, symbol: 'circle', symbolSize: 4, data: pivotPts },
        });
      }

      // MA 50 / 200 overlay
      if (divCfg.showMAs) {
        const sma50  = calcSMA(closes, 50);
        const sma200 = calcSMA(closes, 200);
        series.push(
          { name: 'MA50',  type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: sma50,  symbol: 'none', lineStyle: { color: divCfg.ma1Color, width: 1.2 }, z: 5 },
          { name: 'MA200', type: 'line', xAxisIndex: 0, yAxisIndex: 0, data: sma200, symbol: 'none', lineStyle: { color: divCfg.ma2Color, width: 1.4 }, z: 5 },
        );
      }
    }

    // ── Sub-panel series ───────────────────────────────────────────────────
    subPanels.forEach((id, idx) => {
      const gi = idx + 1;

      if (id === 'Volume') {
        series.push({
          name: 'Volume',
          type: 'bar',
          xAxisIndex: gi, yAxisIndex: gi,
          data: volumes.map((v, i) => ({
            value: v,
            itemStyle: { color: data[i].close >= data[i].open ? 'rgba(16,185,129,0.6)' : 'rgba(239,68,68,0.6)' },
          })),
          barMaxWidth: 6,
        });
      }

      if (id === 'RSI') {
        const rsi = calcRSI(closes);
        series.push(
          { name: 'RSI', type: 'line', xAxisIndex: gi, yAxisIndex: gi, data: rsi, symbol: 'none', lineStyle: { color: '#f59e0b', width: 1.5 }, z: 5 },
          { name: 'RSI 70', type: 'line', xAxisIndex: gi, yAxisIndex: gi, data: new Array(data.length).fill(70), symbol: 'none', lineStyle: { color: '#ef4444', width: 0.8, type: 'dashed' }, z: 4 },
          { name: 'RSI 30', type: 'line', xAxisIndex: gi, yAxisIndex: gi, data: new Array(data.length).fill(30), symbol: 'none', lineStyle: { color: '#10b981', width: 0.8, type: 'dashed' }, z: 4 },
        );
      }

      if (id === 'MACD') {
        const macdArr = calcMACD(closes);
        series.push(
          { name: 'MACD',   type: 'line', xAxisIndex: gi, yAxisIndex: gi, data: macdArr.map((m) => m.macd),   symbol: 'none', lineStyle: { color: '#3b82f6', width: 1.5 }, z: 5 },
          { name: 'Signal', type: 'line', xAxisIndex: gi, yAxisIndex: gi, data: macdArr.map((m) => m.signal), symbol: 'none', lineStyle: { color: '#ec4899', width: 1.2 }, z: 5 },
          {
            name: 'Histogram', type: 'bar', xAxisIndex: gi, yAxisIndex: gi,
            data: macdArr.map((m) => ({
              value: m.hist,
              itemStyle: { color: (m.hist ?? 0) >= 0 ? 'rgba(16,185,129,0.7)' : 'rgba(239,68,68,0.7)' },
            })),
            barMaxWidth: 4,
            z: 4,
          },
        );
      }
    });

    // ── dataZoom ───────────────────────────────────────────────────────────
    // Default view: last ~120 real candles + the full right-pad visible
    const zoomStart = Math.max(0, 100 - Math.round(100 * (120 + RIGHT_PAD) / Math.max(totalLen, 1)));
    const allXIndexes = xAxes.map((_, i) => i);

    const dataZoom: any[] = [
      { type: 'inside', xAxisIndex: allXIndexes, start: zoomStart, end: 100, minValueSpan: 10 },
      {
        type: 'slider', xAxisIndex: allXIndexes,
        bottom: 2, height: 18,
        borderColor: '#334155',
        fillerColor: 'rgba(59,130,246,0.12)',
        handleStyle: { color: '#3b82f6' },
        textStyle: { color: '#64748b' },
        start: zoomStart, end: 100,
        labelFormatter: (val: any) => xLabelFormatter(String(val)),
      },
    ];

    // ── Tooltip ────────────────────────────────────────────────────────────
    const tooltip: any = {
      trigger: 'axis',
      axisPointer: { type: 'cross', link: axisPtrLinks },
      backgroundColor: '#0f172a',
      borderColor: '#334155',
      textStyle: { color: '#e2e8f0', fontSize: 12 },
      formatter: (params: any[]) => {
        const c = params.find((p: any) => p.seriesType === 'candlestick');
        if (!c) return '';
        const [o, cl, l, h] = c.value;
        const col = cl >= o ? upColor : downColor;
        const barLabel = displayLabels[Number(c.axisValue)] ?? c.name;
        let html = `<div style="font-size:12px;line-height:1.8"><b style="color:#94a3b8">${barLabel}</b><br/>
          <span style="color:#94a3b8">O</span> <b style="color:${col}">${Number(o).toLocaleString()}</b>&nbsp;
          <span style="color:#94a3b8">H</span> <b style="color:${col}">${Number(h).toLocaleString()}</b><br/>
          <span style="color:#94a3b8">L</span> <b style="color:${col}">${Number(l).toLocaleString()}</b>&nbsp;
          <span style="color:#94a3b8">C</span> <b style="color:${col}">${Number(cl).toLocaleString()}</b>`;

        // Append overlay values
        params.forEach((p: any) => {
          if (p.seriesType !== 'candlestick' && p.seriesName !== 'BB Lower' && p.seriesName !== 'BB Upper'
              && p.seriesName !== 'RSI 70' && p.seriesName !== 'RSI 30' && p.value != null) {
            const val = typeof p.value === 'number' ? p.value.toFixed(2) : (p.value?.value ?? '');
            if (val !== '' && val !== 'null') {
              html += `<br/><span style="color:${p.color}">${p.seriesName}</span> <b>${val}</b>`;
            }
          }
        });
        html += '</div>';
        return html;
      },
    };

    return {
      backgroundColor: '#1e293b',
      animation: false,
      tooltip,
      axisPointer: { link: axisPtrLinks },
      grid:  grids,
      xAxis: xAxes,
      yAxis: yAxes,
      dataZoom,
      series,
    };
  }, []);

  // ── Data fetcher ──────────────────────────────────────────────────────────

  const fetchChartData = useCallback(async (tf?: Timeframe) => {
    if (!symbol || !marketType) return;
    const activeTf = tf ?? timeframe;

    try {
      setLoading(true);
      setError(null);

      const response = marketType === 'spot'
        ? await apiService.getSpotMarketData(symbol, 20000, activeTf.label)
        : await apiService.getFuturesMarketData(symbol, 20000, activeTf.label);

      if (response.data && Array.isArray(response.data) && response.data.length > 0) {
        let candles: OHLCVBar[];

        if (isOHLCVResponse(response.data)) {
          candles = mapOHLCV(response.data);
        } else {
          const validTicks = response.data.filter(
            (item: any) => item.markPrice != null && Number(item.markPrice) > 0,
          ) as TickItem[];
          if (validTicks.length === 0) {
            setError('No price data available yet for this market');
            setLoading(false);
            return;
          }
          rawTicksRef.current = validTicks;
          candles = aggregateTicks(validTicks, activeTf.seconds);
        }

        if (candles.length === 0) {
          setError('No price data available yet for this market');
          setLoading(false);
          return;
        }

        setChartData(candles);

        // Push option directly to existing chart instance to avoid full re-render on refresh
        if (echartsRef.current) {
          echartsRef.current.getEchartsInstance().setOption(
            buildOption(candles, activeTf, indicators, msbobSettings, divSettings),
            { notMerge: true },
          );
        }
      } else {
        setError("No chart data available. The WebSocket service hasn't collected enough data yet.");
      }
    } catch (err: any) {
      console.error('Chart data fetch error:', err);
      setError(err.response?.data?.message || 'Failed to fetch chart data');
    } finally {
      setLoading(false);
    }
  }, [symbol, marketType, timeframe, indicators, msbobSettings, divSettings, buildOption]);

  // Keep refs in sync so stale closures always see latest values
  useEffect(() => { timeframeRef.current = timeframe; }, [timeframe]);
  useEffect(() => { fetchRef.current = fetchChartData; }, [fetchChartData]);

  // Initial load + 30s auto-refresh — interval uses refs to avoid stale closure
  useEffect(() => {
    fetchRef.current(timeframeRef.current);
    const interval = setInterval(() => fetchRef.current(timeframeRef.current), 30000);
    return () => clearInterval(interval);
  }, [symbol, marketType]); // eslint-disable-line react-hooks/exhaustive-deps

  // Timeframe switch — re-fetch with new resolution
  useEffect(() => {
    fetchChartData(timeframe);
  }, [timeframe]); // eslint-disable-line react-hooks/exhaustive-deps

  // Indicator toggle / MSB-OB settings change — rebuild option with current data
  useEffect(() => {
    if (chartData.length === 0 || !echartsRef.current) return;
    echartsRef.current.getEchartsInstance().setOption(
      buildOption(chartData, timeframe, indicators, msbobSettings, divSettings),
      { notMerge: true },
    );
  }, [indicators, msbobSettings, divSettings]); // eslint-disable-line react-hooks/exhaustive-deps

  if (loading && chartData.length === 0) return <Loading />;

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <button onClick={() => navigate('/')} className="btn btn-secondary" style={{ padding: '0.5rem' }}>
            <ArrowLeft size={18} />
          </button>
          <div>
            <h1 style={{ marginBottom: '0.25rem' }}>{symbol}</h1>
            <p style={{ color: '#64748b', fontSize: '0.875rem', margin: 0 }}>
              {marketType === 'spot' ? 'Spot Market' : 'Futures Contract'}
            </p>
          </div>
        </div>
        <button onClick={() => fetchChartData(timeframe)} className="btn btn-secondary" disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} />
=======
  const chartRef = useRef<HTMLDivElement>(null);
  const chartInstance = useRef<echarts.ECharts | null>(null);

  const [availableIntervals, setAvailableIntervals] = useState<string[]>([]);
  const [selectedInterval, setSelectedInterval] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [barCount, setBarCount] = useState(0);

  const renderChart = useCallback((bars: Bar[]) => {
    if (!chartRef.current) return;
    if (!chartInstance.current) {
      chartInstance.current = echarts.init(chartRef.current, 'dark');
    }
    const dates = bars.map(b => new Date(b.time).toISOString().replace('T', ' ').slice(0, 16));
    const candleData = bars.map(b => [b.open, b.close, b.low, b.high]);
    const volumeData = bars.map(b => ({
      value: b.volume,
      itemStyle: { color: b.close >= b.open ? '#26a69a' : '#ef5350' },
    }));
    const option: echarts.EChartsOption = {
      backgroundColor: '#0f172a',
      animation: false,
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' },
        formatter: (params: any) => {
          const candle = params.find((p: any) => p.seriesName === 'OHLC');
          const vol = params.find((p: any) => p.seriesName === 'Volume');
          if (!candle) return '';
          const [o, c, l, h] = candle.value;
          const color = c >= o ? '#26a69a' : '#ef5350';
          return (
            '<div style="font-size:12px">' +
            '<div style="margin-bottom:4px">' + candle.axisValue + '</div>' +
            '<div>O: <b style="color:' + color + '">' + o + '</b></div>' +
            '<div>H: <b style="color:' + color + '">' + h + '</b></div>' +
            '<div>L: <b style="color:' + color + '">' + l + '</b></div>' +
            '<div>C: <b style="color:' + color + '">' + c + '</b></div>' +
            (vol ? '<div>Vol: ' + Number(vol.value).toLocaleString(undefined, { maximumFractionDigits: 2 }) + '</div>' : '') +
            '</div>'
          );
        },
      },
      axisPointer: { link: [{ xAxisIndex: 'all' }] },
      dataZoom: [
        { type: 'inside', xAxisIndex: [0, 1], start: 85, end: 100 },
        { show: true, xAxisIndex: [0, 1], type: 'slider', bottom: 10, height: 30 },
      ],
      grid: [
        { left: '5%', right: '3%', top: 20, bottom: 130, height: '58%' },
        { left: '5%', right: '3%', bottom: 70, height: '18%' },
      ],
      xAxis: [
        {
          type: 'category',
          data: dates,
          boundaryGap: false,
          axisLine: { onZero: false },
          splitLine: { show: false },
          axisLabel: { fontSize: 10 },
        },
        {
          type: 'category',
          gridIndex: 1,
          data: dates,
          boundaryGap: false,
          axisLine: { onZero: false },
          splitLine: { show: false },
          axisLabel: { show: false },
        },
      ],
      yAxis: [
        { scale: true, splitArea: { show: true }, axisLabel: { fontSize: 10 } },
        {
          scale: true,
          gridIndex: 1,
          splitNumber: 2,
          axisLabel: { show: true, fontSize: 9 },
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { show: false },
        },
      ],
      series: [
        {
          name: 'OHLC',
          type: 'candlestick',
          data: candleData,
          itemStyle: {
            color: '#26a69a',
            color0: '#ef5350',
            borderColor: '#26a69a',
            borderColor0: '#ef5350',
          },
        },
        {
          name: 'Volume',
          type: 'bar',
          xAxisIndex: 1,
          yAxisIndex: 1,
          data: volumeData,
        },
      ],
    };
    chartInstance.current.setOption(option, true);
    requestAnimationFrame(() => chartInstance.current?.resize());
  }, []);

  const fetchAndRender = useCallback(async () => {
    if (!symbol || !selectedInterval) return;
    const decoded = decodeURIComponent(symbol);
    setLoading(true);
    setError(null);
    try {
      let bars: Bar[] = [];
      if (marketType === 'spot') {
        const res = await apiService.getSpotCandlesticks(decoded, selectedInterval, 20000);
        bars = (res.data || []).map((d: any) => ({
          time: d.startTimestamp < 1e10 ? d.startTimestamp * 1000 : d.startTimestamp,
          open: parseFloat(d.openPrice),
          close: parseFloat(d.closePrice),
          high: parseFloat(d.highPrice),
          low: parseFloat(d.lowPrice),
          volume: parseFloat(d.baseAssetVolume || '0'),
        }));
      } else {
        const res = await apiService.getFuturesCandlesticks(decoded, selectedInterval, 20000);
        bars = (res.data || []).map((d: any) => ({
          time: d.openTime < 1e10 ? d.openTime * 1000 : d.openTime,
          open: Number(d.open),
          close: Number(d.close),
          high: Number(d.high),
          low: Number(d.low),
          volume: Number(d.volume || 0),
        }));
      }
      bars = bars
        .filter(b => b.time && !isNaN(b.open) && !isNaN(b.close))
        .sort((a, b) => a.time - b.time);
      setBarCount(bars.length);
      renderChart(bars);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch candlestick data');
    } finally {
      setLoading(false);
    }
  }, [symbol, selectedInterval, marketType, renderChart]);

  // Fetch available intervals from API on mount, auto-select first
  useEffect(() => {
    if (!symbol) return;
    const decoded = decodeURIComponent(symbol);
    (async () => {
      try {
        let intervals: string[] = [];
        if (marketType === 'spot') {
          const res = await apiService.getSpotCandlestickIntervals(decoded);
          intervals = Array.isArray(res.data) ? res.data : [];
        } else {
          const res = await apiService.getFuturesCandlestickDurations();
          intervals = Array.isArray(res.data) ? res.data : [];
        }
        if (intervals.length === 0) intervals = [...FALLBACK_INTERVALS];
        setAvailableIntervals(intervals);
        setSelectedInterval(intervals[0]);
      } catch {
        setAvailableIntervals([...FALLBACK_INTERVALS]);
        setSelectedInterval(FALLBACK_INTERVALS[0]);
      }
    })();
  }, [symbol, marketType]);

  // Fetch data whenever selected interval changes
  useEffect(() => {
    if (selectedInterval) fetchAndRender();
  }, [fetchAndRender]);

  // Resize chart on window resize
  useEffect(() => {
    const handleResize = () => chartInstance.current?.resize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Dispose chart on unmount
  useEffect(() => {
    return () => {
      chartInstance.current?.dispose();
      chartInstance.current = null;
    };
  }, []);

  const displaySymbol = symbol ? decodeURIComponent(symbol) : '';

  return (
    <div className="page">
      <div className="page-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', flexWrap: 'wrap' }}>
          <button onClick={() => navigate(-1)} className="btn btn-secondary">
            <ArrowLeft size={18} />
            Back
          </button>
          <h1 style={{ margin: 0 }}>{displaySymbol}</h1>
          <span style={{
            padding: '0.2rem 0.6rem',
            background: marketType === 'futures' ? 'rgba(245,158,11,0.15)' : 'rgba(59,130,246,0.15)',
            color: marketType === 'futures' ? '#f59e0b' : '#3b82f6',
            borderRadius: '0.25rem',
            fontSize: '0.75rem',
            fontWeight: 600,
            textTransform: 'uppercase' as const,
          }}>
            {marketType}
          </span>
          {barCount > 0 && (
            <span style={{ color: '#64748b', fontSize: '0.875rem' }}>
              {barCount.toLocaleString()} bars
            </span>
          )}
        </div>
        <button onClick={fetchAndRender} className="btn btn-secondary" disabled={loading}>
          <RefreshCw size={18} />
>>>>>>> 84333d70095238b68805b30cb677b4043c137ddf
          Refresh
        </button>
      </div>

      {error && <ErrorMessage message={error} onClose={() => setError(null)} />}

<<<<<<< HEAD
      {/* Timeframe selector */}
      <div style={{
        display: 'flex', gap: '0.25rem', marginTop: '1rem',
        padding: '0.5rem', background: '#1e293b', borderRadius: '0.5rem', flexWrap: 'wrap',
      }}>
        {TIMEFRAMES.map((tf) => (
          <button
            key={tf.label}
            onClick={() => setTimeframe(tf)}
            style={{
              padding: '0.35rem 0.65rem', borderRadius: '0.375rem', border: 'none',
              cursor: 'pointer', fontSize: '0.8rem', fontWeight: '600',
              transition: 'background 0.15s, color 0.15s',
              background: timeframe.label === tf.label ? '#3b82f6' : 'transparent',
              color:      timeframe.label === tf.label ? '#fff'    : '#94a3b8',
            }}
          >
            {tf.label}
=======
      {/* Interval buttons — populated from API */}
      <div style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap', marginBottom: '1rem' }}>
        {availableIntervals.map(iv => (
          <button
            key={iv}
            onClick={() => setSelectedInterval(iv)}
            className={`btn ${selectedInterval === iv ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '0.25rem 0.65rem', fontSize: '0.78rem', minWidth: 0 }}
          >
            {iv}
>>>>>>> 84333d70095238b68805b30cb677b4043c137ddf
          </button>
        ))}
      </div>

<<<<<<< HEAD
      {/* Indicator selector */}
      <div style={{
        display: 'flex', gap: '0.4rem', marginTop: '0.5rem',
        padding: '0.5rem 0.75rem', background: '#111827',
        borderRadius: '0.5rem', flexWrap: 'wrap', alignItems: 'center',
      }}>
        <TrendingUp size={14} style={{ color: '#64748b', flexShrink: 0 }} />
        <span style={{ fontSize: '0.75rem', color: '#64748b', marginRight: '0.25rem' }}>Indicators:</span>
        {(Object.keys(INDICATOR_LABELS) as IndicatorId[]).map((id) => {
          const active = indicators.has(id);
          const isSub  = SUB_INDICATORS.includes(id);
          return (
            <button
              key={id}
              onClick={() => toggleIndicator(id)}
              style={{
                padding: '0.2rem 0.55rem', borderRadius: '999px',
                border: `1px solid ${active ? (isSub ? '#f59e0b' : '#3b82f6') : '#334155'}`,
                cursor: 'pointer', fontSize: '0.72rem', fontWeight: '600',
                background: active ? (isSub ? 'rgba(245,158,11,0.15)' : 'rgba(59,130,246,0.15)') : 'transparent',
                color:      active ? (isSub ? '#f59e0b' : '#93c5fd') : '#64748b',
                transition: 'all 0.15s',
              }}
            >
              {INDICATOR_LABELS[id]}
            </button>
          );
        })}
      </div>

      {/* MSB-OB Settings panel */}
      {indicators.has('MSBOB') && (
        <div style={{
          marginTop: '0.5rem', padding: '0.75rem 1rem',
          background: '#0f172a', borderRadius: '0.5rem',
          border: '1px solid #1e3a4a', fontSize: '0.75rem',
        }}>
          <div style={{ color: '#94a3b8', fontWeight: 700, marginBottom: '0.6rem', letterSpacing: '0.04em' }}>MSB-OB Settings</div>
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>

            {/* ZigZag Length */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              ZigZag Len
              <input type="number" min={2} max={50} value={msbobSettings.zigzagLen}
                onChange={e => setMsbobSettings(p => ({ ...p, zigzagLen: Math.max(2, +e.target.value) }))}
                style={{ width: '3.5rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            {/* Fib Factor */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Fib Factor
              <input type="number" step={0.01} min={0} max={1} value={msbobSettings.fibFactor}
                onChange={e => setMsbobSettings(p => ({ ...p, fibFactor: Math.min(1, Math.max(0, +e.target.value)) }))}
                style={{ width: '4rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            {/* Show ZigZag */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', color: '#94a3b8', cursor: 'pointer' }}>
              <input type="checkbox" checked={msbobSettings.showZigzag}
                onChange={e => setMsbobSettings(p => ({ ...p, showZigzag: e.target.checked }))}
                style={{ accentColor: '#3b82f6' }}
              />
              Show ZigZag
            </label>

            {/* Delete Broken */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', color: '#94a3b8', cursor: 'pointer' }}>
              <input type="checkbox" checked={msbobSettings.deleteBoxes}
                onChange={e => setMsbobSettings(p => ({ ...p, deleteBoxes: e.target.checked }))}
                style={{ accentColor: '#3b82f6' }}
              />
              Delete Broken
            </label>

            {/* Separator */}
            <div style={{ width: '1px', height: '1.4rem', background: '#334155', flexShrink: 0 }} />

            {/* Bu-OB colors */}
            {([
              { key: 'buOBColor',       label: 'Bu-OB Fill' },
              { key: 'buOBBorderColor', label: 'Bu-OB Border' },
              { key: 'beOBColor',       label: 'Be-OB Fill' },
              { key: 'beOBBorderColor', label: 'Be-OB Border' },
              { key: 'buBBColor',       label: 'Bu-BB Fill' },
              { key: 'buBBBorderColor', label: 'Bu-BB Border' },
              { key: 'beBBColor',       label: 'Be-BB Fill' },
              { key: 'beBBBorderColor', label: 'Be-BB Border' },
            ] as { key: keyof MSBOBSettings; label: string }[]).map(({ key, label }) => (
              <label key={key} style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', color: '#64748b', cursor: 'pointer' }}>
                <input
                  type="color"
                  value={msbobSettings[key] as string}
                  onChange={e => setMsbobSettings(p => ({ ...p, [key]: e.target.value }))}
                  style={{ width: '1.4rem', height: '1.4rem', padding: 0, border: 'none', background: 'none', cursor: 'pointer', borderRadius: '0.2rem' }}
                />
                {label}
              </label>
            ))}

          </div>
        </div>
      )}

      {/* Divergence Settings panel */}
      {indicators.has('DIV') && (
        <div style={{
          marginTop: '0.5rem', padding: '0.75rem 1rem',
          background: '#0f172a', borderRadius: '0.5rem',
          border: '1px solid #1e3a4a', fontSize: '0.75rem',
        }}>
          <div style={{ color: '#94a3b8', fontWeight: 700, marginBottom: '0.6rem', letterSpacing: '0.04em' }}>
            Divergence Settings
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem 1.25rem', alignItems: 'center' }}>

            {/* Pivot Period */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Pivot Period
              <input type="number" min={1} max={50} value={divSettings.prd}
                onChange={e => setDivSettings(p => ({ ...p, prd: Math.max(1, Math.min(50, +e.target.value)) }))}
                style={{ width: '3rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            {/* Source */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Source
              <select value={divSettings.source}
                onChange={e => setDivSettings(p => ({ ...p, source: e.target.value as DivSettings['source'] }))}
                style={{ padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}>
                <option value="Close">Close</option>
                <option value="High/Low">High/Low</option>
              </select>
            </label>

            {/* Divergence Type */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Type
              <select value={divSettings.searchdiv}
                onChange={e => setDivSettings(p => ({ ...p, searchdiv: e.target.value as DivSettings['searchdiv'] }))}
                style={{ padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}>
                <option value="Regular">Regular</option>
                <option value="Hidden">Hidden</option>
                <option value="Regular/Hidden">Regular/Hidden</option>
              </select>
            </label>

            {/* Show Indicator Names */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Labels
              <select value={divSettings.showindis}
                onChange={e => setDivSettings(p => ({ ...p, showindis: e.target.value as DivSettings['showindis'] }))}
                style={{ padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}>
                <option value="Full">Full</option>
                <option value="First Letter">First Letter</option>
                <option value="Don't Show">Don't Show</option>
              </select>
            </label>

            {/* Min Divergences */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Min Divs
              <input type="number" min={1} max={11} value={divSettings.showlimit}
                onChange={e => setDivSettings(p => ({ ...p, showlimit: Math.max(1, Math.min(11, +e.target.value)) }))}
                style={{ width: '3rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            {/* Max Pivot Points */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Max Pivots
              <input type="number" min={1} max={20} value={divSettings.maxpp}
                onChange={e => setDivSettings(p => ({ ...p, maxpp: Math.max(1, Math.min(20, +e.target.value)) }))}
                style={{ width: '3rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            {/* Max Bars to Check */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Max Bars
              <input type="number" min={30} max={200} value={divSettings.maxbars}
                onChange={e => setDivSettings(p => ({ ...p, maxbars: Math.max(30, Math.min(200, +e.target.value)) }))}
                style={{ width: '3.5rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            {/* Div separator */}
            <div style={{ width: '1px', height: '1.4rem', background: '#334155', flexShrink: 0 }} />

            {/* Checkbox options */}
            {([
              { key: 'shownum',     label: 'Show #' },
              { key: 'showlast',    label: 'Last Only' },
              { key: 'dontconfirm',label: "No Wait" },
              { key: 'showlines',   label: 'Lines' },
              { key: 'showpivot',   label: 'Pivots' },
              { key: 'showMAs',     label: 'MA 50/200' },
            ] as { key: keyof DivSettings; label: string }[]).map(({ key, label }) => (
              <label key={key} style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', color: '#94a3b8', cursor: 'pointer' }}>
                <input type="checkbox" checked={divSettings[key] as boolean}
                  onChange={e => setDivSettings(p => ({ ...p, [key]: e.target.checked }))}
                  style={{ accentColor: '#3b82f6' }}
                />
                {label}
              </label>
            ))}

            {/* Div separator */}
            <div style={{ width: '1px', height: '1.4rem', background: '#334155', flexShrink: 0 }} />

            {/* Line styles */}
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Reg Line
              <select value={divSettings.regLineStyle}
                onChange={e => setDivSettings(p => ({ ...p, regLineStyle: e.target.value as DivSettings['regLineStyle'] }))}
                style={{ padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}>
                <option>Solid</option><option>Dashed</option><option>Dotted</option>
              </select>
              <input type="number" min={1} max={5} value={divSettings.regLineWidth}
                onChange={e => setDivSettings(p => ({ ...p, regLineWidth: Math.max(1, Math.min(5, +e.target.value)) }))}
                style={{ width: '2.5rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            <label style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#94a3b8' }}>
              Hid Line
              <select value={divSettings.hidLineStyle}
                onChange={e => setDivSettings(p => ({ ...p, hidLineStyle: e.target.value as DivSettings['hidLineStyle'] }))}
                style={{ padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}>
                <option>Solid</option><option>Dashed</option><option>Dotted</option>
              </select>
              <input type="number" min={1} max={5} value={divSettings.hidLineWidth}
                onChange={e => setDivSettings(p => ({ ...p, hidLineWidth: Math.max(1, Math.min(5, +e.target.value)) }))}
                style={{ width: '2.5rem', padding: '0.15rem 0.3rem', background: '#1e293b', border: '1px solid #334155', borderRadius: '0.25rem', color: '#e2e8f0', fontSize: '0.75rem' }}
              />
            </label>

            {/* Div separator */}
            <div style={{ width: '1px', height: '1.4rem', background: '#334155', flexShrink: 0 }} />

            {/* Colors */}
            {([
              { key: 'posRegDivColor', label: '+Reg' },
              { key: 'negRegDivColor', label: '-Reg' },
              { key: 'posHidDivColor', label: '+Hid' },
              { key: 'negHidDivColor', label: '-Hid' },
              { key: 'posTextColor',   label: '+Text' },
              { key: 'negTextColor',   label: '-Text' },
              { key: 'ma1Color',       label: 'MA50' },
              { key: 'ma2Color',       label: 'MA200' },
            ] as { key: keyof DivSettings; label: string }[]).map(({ key, label }) => (
              <label key={key} style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', color: '#64748b', cursor: 'pointer' }}>
                <input type="color" value={divSettings[key] as string}
                  onChange={e => setDivSettings(p => ({ ...p, [key]: e.target.value }))}
                  style={{ width: '1.4rem', height: '1.4rem', padding: 0, border: 'none', background: 'none', cursor: 'pointer', borderRadius: '0.2rem' }}
                />
                {label}
              </label>
            ))}

            {/* Div separator */}
            <div style={{ width: '1px', height: '1.4rem', background: '#334155', flexShrink: 0 }} />

            {/* Indicator toggles */}
            <span style={{ color: '#64748b', fontSize: '0.7rem' }}>Indicators:</span>
            {([
              { key: 'calcmacd',   label: 'MACD' },
              { key: 'calcmacda',  label: 'Hist' },
              { key: 'calcrsi',    label: 'RSI' },
              { key: 'calcstoc',   label: 'Stoch' },
              { key: 'calccci',    label: 'CCI' },
              { key: 'calcmom',    label: 'MOM' },
              { key: 'calcobv',    label: 'OBV' },
              { key: 'calcvwmacd', label: 'VWmacd' },
              { key: 'calccmf',    label: 'CMF' },
              { key: 'calcmfi',    label: 'MFI' },
            ] as { key: keyof DivSettings; label: string }[]).map(({ key, label }) => (
              <label key={key} style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', color: '#94a3b8', cursor: 'pointer' }}>
                <input type="checkbox" checked={divSettings[key] as boolean}
                  onChange={e => setDivSettings(p => ({ ...p, [key]: e.target.checked }))}
                  style={{ accentColor: '#a78bfa' }}
                />
                {label}
              </label>
            ))}

          </div>
        </div>
      )}

      {/* Chart */}
      {chartData.length > 0 && (
        <div style={{ marginTop: '1rem', borderRadius: '0.5rem', overflow: 'hidden' }}>
          <ReactECharts
            ref={echartsRef}
            option={buildOption(chartData, timeframe, indicators, msbobSettings, divSettings)}
            style={{ height: 520, width: '100%' }}
            notMerge={true}
            lazyUpdate={false}
            theme="dark"
          />
          <div style={{
            padding: '0.6rem 1rem',
            background: 'rgba(59, 130, 246, 0.08)',
            color: '#64748b', fontSize: '0.8rem',
          }}>
            📊 {chartData.length} candles · {timeframe.label} timeframe
            {loading && <span style={{ marginLeft: '0.75rem', color: '#3b82f6' }}>
              <RefreshCw size={12} className="spin" style={{ verticalAlign: 'middle', marginRight: 4 }} />
              Refreshing…
            </span>}
          </div>
        </div>
      )}

      {chartData.length === 0 && !loading && (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
          <p style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>No chart data available</p>
          <p style={{ fontSize: '0.9rem' }}>
            The WebSocket service hasn't collected enough data to display a chart yet.
          </p>
        </div>
      )}
=======
      {loading && (
        <div style={{ textAlign: 'center', padding: '1rem', color: '#64748b' }}>
          Loading candlestick data…
        </div>
      )}

      {/* Chart container always mounted so ECharts can measure dimensions */}
      <div style={{ position: 'relative', width: '100%' }}>
        <div
          ref={chartRef}
          style={{
            width: '100%',
            height: 'calc(100vh - 220px)',
            minHeight: '400px',
            background: '#0f172a',
            borderRadius: '0.75rem',
          }}
        />
        {!loading && barCount === 0 && !error && selectedInterval && (
          <div style={{
            position: 'absolute', inset: 0,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: '#64748b',
          }}>
            <p>No data for <strong>{displaySymbol}</strong> / <strong>{selectedInterval}</strong> yet.</p>
          </div>
        )}
      </div>
>>>>>>> 84333d70095238b68805b30cb677b4043c137ddf
    </div>
  );
};

export default ChartPage;
