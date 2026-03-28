import React, { useEffect, useRef, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as echarts from 'echarts';
import { apiService } from '../services/api';
import ErrorMessage from '../components/ErrorMessage';
import { ArrowLeft, RefreshCw } from 'lucide-react';

const FALLBACK_INTERVALS = ['1m', '3m', '5m', '15m', '30m', '1h', '2h', '4h', '6h', '12h', '1d', '1w'];

interface Bar {
  time: number;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

const ChartPage: React.FC = () => {
  const { marketType, symbol } = useParams<{ marketType: string; symbol: string }>();
  const navigate = useNavigate();
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
          Refresh
        </button>
      </div>

      {error && <ErrorMessage message={error} onClose={() => setError(null)} />}

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
          </button>
        ))}
      </div>

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
    </div>
  );
};

export default ChartPage;
