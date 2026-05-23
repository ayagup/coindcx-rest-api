import React, { useState } from 'react';
import { useTradeConfig, DEFAULT_TRADE_CONFIG, DEFAULT_MT5_CONFIG, TradeConfig, TradeConfigProfile } from '../context/TradeConfigContext';
import { Save, RotateCcw, Settings2, TrendingUp, DollarSign, Shield } from 'lucide-react';

// ── Styles ────────────────────────────────────────────────────────────────────
const sectionStyle: React.CSSProperties = {
  background: '#1e293b',
  border: '1px solid #334155',
  borderRadius: '0.5rem',
  padding: '1.25rem 1.5rem',
  marginBottom: '1rem',
};

const sectionTitle = (color: string): React.CSSProperties => ({
  display: 'flex', alignItems: 'center', gap: '0.5rem',
  fontSize: '0.95rem', fontWeight: 700, color,
  marginBottom: '1.25rem', paddingBottom: '0.6rem',
  borderBottom: '1px solid #334155',
});

const rowStyle: React.CSSProperties = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
  gap: '1rem',
};

const fieldStyle: React.CSSProperties = {
  display: 'flex', flexDirection: 'column', gap: '0.35rem',
};

const labelStyle: React.CSSProperties = {
  fontSize: '0.78rem', color: '#94a3b8', fontWeight: 500, letterSpacing: '0.02em',
};

const inputStyle: React.CSSProperties = {
  padding: '0.55rem 0.75rem',
  background: '#0f172a',
  border: '1px solid #334155',
  borderRadius: '0.375rem',
  color: '#f1f5f9',
  fontSize: '0.9rem',
  outline: 'none',
  width: '100%',
};

const hintStyle: React.CSSProperties = {
  fontSize: '0.72rem', color: '#475569',
};

// ── Helpers ───────────────────────────────────────────────────────────────────
function NumField({
  label, hint, value, min, max, step = 1, onChange, accentColor,
}: {
  label: string;
  hint?: string;
  value: number;
  min?: number;
  max?: number;
  step?: number;
  onChange: (v: number) => void;
  accentColor?: string;
}) {
  return (
    <div style={fieldStyle}>
      <label style={labelStyle}>{label}</label>
      <input
        type="number"
        min={min}
        max={max}
        step={step}
        value={value}
        onChange={e => {
          const v = parseFloat(e.target.value);
          if (!isNaN(v)) onChange(v);
        }}
        style={{ ...inputStyle, borderColor: accentColor ?? '#334155' }}
      />
      {hint && <span style={hintStyle}>{hint}</span>}
    </div>
  );
}

// ── Main Component ────────────────────────────────────────────────────────────
const PROFILES: { id: TradeConfigProfile; label: string; color: string }[] = [
  { id: 'coindcx', label: 'CoinDCX Futures', color: '#3b82f6' },
  { id: 'mt5',     label: 'MT5 (XAUUSD / Metals)', color: '#f59e0b' },
];

const TradeConfigPage: React.FC = () => {
  const { coindcxConfig, mt5Config, updateConfig, resetConfig } = useTradeConfig();

  const [activeProfile, setActiveProfile] = useState<TradeConfigProfile>('coindcx');
  const [draftDcx,  setDraftDcx]  = useState<TradeConfig>({ ...coindcxConfig });
  const [draftMt5,  setDraftMt5]  = useState<TradeConfig>({ ...mt5Config     });
  const [saved, setSaved] = useState(false);

  const draft  = activeProfile === 'mt5' ? draftMt5  : draftDcx;
  const setDraft = activeProfile === 'mt5'
    ? (fn: (prev: TradeConfig) => TradeConfig) => setDraftMt5(fn)
    : (fn: (prev: TradeConfig) => TradeConfig) => setDraftDcx(fn);

  const set = (field: keyof TradeConfig) => (v: number) =>
    setDraft(prev => ({ ...prev, [field]: v }));

  const handleSave = () => {
    updateConfig(draft, activeProfile);
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  const handleReset = () => {
    if (activeProfile === 'mt5') {
      setDraftMt5({ ...DEFAULT_MT5_CONFIG });
      resetConfig('mt5');
    } else {
      setDraftDcx({ ...DEFAULT_TRADE_CONFIG });
      resetConfig('coindcx');
    }
  };

  const currentSaved = activeProfile === 'mt5' ? mt5Config : coindcxConfig;
  const accentColor  = PROFILES.find(p => p.id === activeProfile)!.color;

  // Estimated TP/SL preview (CoinDCX: margin-based; MT5: just pips)
  const pipValue = 0.0001;
  const tpUsd = (pips: number) =>
    activeProfile === 'mt5'
      ? `${pips}p`
      : `${(pips * pipValue * draft.marginUsdt * draft.leverage).toFixed(2)} USDT`;
  const slUsd = activeProfile === 'mt5'
    ? `${draft.slPips}p`
    : `${(draft.slPips * pipValue * draft.marginUsdt * draft.leverage).toFixed(2)} USDT`;

  return (
    <div className="page" style={{ maxWidth: 860, margin: '0 auto', padding: '1.25rem 1.5rem' }}>

      {/* ── Header ── */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <Settings2 size={22} style={{ color: accentColor }} />
          <h1 style={{ fontSize: '1.35rem', margin: 0 }}>Trade Configuration</h1>
        </div>

        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={handleReset}
            style={{
              display: 'flex', alignItems: 'center', gap: '0.35rem',
              padding: '0.5rem 0.85rem', borderRadius: '0.375rem', border: '1px solid #334155',
              background: 'transparent', color: '#94a3b8', fontSize: '0.85rem', cursor: 'pointer',
            }}
          >
            <RotateCcw size={14} />
            Reset to defaults
          </button>
          <button
            onClick={handleSave}
            style={{
              display: 'flex', alignItems: 'center', gap: '0.35rem',
              padding: '0.5rem 1rem', borderRadius: '0.375rem', border: 'none',
              background: saved ? '#10b981' : accentColor, color: '#fff',
              fontSize: '0.85rem', fontWeight: 700, cursor: 'pointer', transition: 'background 0.2s',
            }}
          >
            <Save size={14} />
            {saved ? 'Saved ✓' : 'Save'}
          </button>
        </div>
      </div>

      {/* ── Profile tabs ── */}
      <div style={{ display: 'flex', gap: '0.25rem', marginBottom: '1.5rem', background: '#1e293b', padding: '0.25rem', borderRadius: '0.5rem', width: 'fit-content' }}>
        {PROFILES.map(p => (
          <button
            key={p.id}
            onClick={() => setActiveProfile(p.id)}
            style={{
              padding: '0.45rem 1.1rem',
              borderRadius: '0.35rem',
              border: 'none',
              cursor: 'pointer',
              fontWeight: 600,
              fontSize: '0.85rem',
              transition: 'all 0.15s',
              background: activeProfile === p.id ? p.color : 'transparent',
              color: activeProfile === p.id ? '#fff' : '#64748b',
            }}
          >
            {p.label}
          </button>
        ))}
      </div>

      <p style={{ color: '#64748b', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
        Configuring: <strong style={{ color: accentColor }}>{PROFILES.find(p => p.id === activeProfile)!.label}</strong>.
        Settings are saved per-platform and pre-filled on each trading page.
        Changes only take effect after clicking <strong style={{ color: '#94a3b8' }}>Save</strong>.
      </p>

      {/* ── Position sizing ── */}
      <div style={sectionStyle}>
        <div style={sectionTitle('#f59e0b')}>
          <DollarSign size={16} />
          Position Sizing
        </div>
        <div style={rowStyle}>
          {activeProfile === 'mt5' ? (
            <>
              <NumField
                label="Quantity per trade (lots)"
                hint="Fixed number of lots/contracts per order"
                value={draft.quantity}
                min={0.001} step={0.001}
                onChange={set('quantity')}
                accentColor="#f59e0b"
              />
              <NumField
                label="Leverage"
                hint="Account leverage (used for margin calculation)"
                value={draft.leverage}
                min={1} max={2000} step={1}
                onChange={set('leverage')}
                accentColor="#f59e0b"
              />
              <div style={{ ...fieldStyle, justifyContent: 'center' }}>
                <div style={labelStyle}>Trade size</div>
                <div style={{ fontSize: '1.3rem', fontWeight: 700, color: '#f59e0b', padding: '0.45rem 0', letterSpacing: '-0.01em' }}>
                  {draft.quantity} lots
                </div>
                <div style={hintStyle}>Fixed quantity per order</div>
              </div>
            </>
          ) : (
            <>
              <NumField
                label="Margin per trade (USDT)"
                hint="Amount of margin committed to each trade"
                value={draft.marginUsdt}
                min={1} step={1}
                onChange={set('marginUsdt')}
                accentColor="#f59e0b"
              />
              <NumField
                label="Leverage"
                hint="Position size = margin × leverage"
                value={draft.leverage}
                min={1} max={500} step={1}
                onChange={set('leverage')}
                accentColor="#f59e0b"
              />
              <div style={{ ...fieldStyle, justifyContent: 'center' }}>
                <div style={labelStyle}>Notional position size</div>
                <div style={{ fontSize: '1.3rem', fontWeight: 700, color: '#f59e0b', padding: '0.45rem 0', letterSpacing: '-0.01em' }}>
                  {(draft.marginUsdt * draft.leverage).toLocaleString(undefined, { minimumFractionDigits: 2 })} USDT
                </div>
                <div style={hintStyle}>{draft.marginUsdt} × {draft.leverage}×</div>
              </div>
            </>
          )}
        </div>
      </div>

      {/* ── Take Profit ── */}
      <div style={sectionStyle}>
        <div style={sectionTitle('#10b981')}>
          <TrendingUp size={16} />
          Take Profit Levels
        </div>
        <div style={rowStyle}>
          <NumField
            label="TP1 (pips)"
            hint={`≈ +${tpUsd(draft.tp1Pips)}`}
            value={draft.tp1Pips}
            min={1} step={1}
            onChange={set('tp1Pips')}
            accentColor="#10b981"
          />
          <NumField
            label="TP2 (pips)"
            hint={`≈ +${tpUsd(draft.tp2Pips)}`}
            value={draft.tp2Pips}
            min={1} step={1}
            onChange={set('tp2Pips')}
            accentColor="#10b981"
          />
          <NumField
            label="TP3 (pips)"
            hint={`≈ +${tpUsd(draft.tp3Pips)}`}
            value={draft.tp3Pips}
            min={1} step={1}
            onChange={set('tp3Pips')}
            accentColor="#10b981"
          />
        </div>

        {/* TP ladder visual */}
        <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem', alignItems: 'flex-end' }}>
          {[
            { label: 'TP1', pips: draft.tp1Pips, h: 48 },
            { label: 'TP2', pips: draft.tp2Pips, h: 72 },
            { label: 'TP3', pips: draft.tp3Pips, h: 96 },
          ].map(({ label, pips, h }) => (
            <div key={label} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '0.25rem' }}>
              <span style={{ fontSize: '0.8rem', color: '#10b981', fontWeight: 700 }}>+{tpUsd(pips)}</span>
              <div style={{
                width: 48, height: h,
                background: 'linear-gradient(180deg, rgba(16,185,129,0.7) 0%, rgba(16,185,129,0.2) 100%)',
                borderRadius: '0.25rem 0.25rem 0 0',
                display: 'flex', alignItems: 'flex-start', justifyContent: 'center',
                paddingTop: '0.3rem',
              }}>
                <span style={{ fontSize: '0.7rem', color: '#fff', fontWeight: 700 }}>{pips}p</span>
              </div>
              <span style={{ fontSize: '0.72rem', color: '#64748b' }}>{label}</span>
            </div>
          ))}

          {/* SL bar */}
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '0.25rem', marginLeft: '1rem' }}>
            <span style={{ fontSize: '0.8rem', color: '#ef4444', fontWeight: 700 }}>−{slUsd}</span>
            <div style={{
              width: 48, height: 40,
              background: 'linear-gradient(0deg, rgba(239,68,68,0.7) 0%, rgba(239,68,68,0.2) 100%)',
              borderRadius: '0 0 0.25rem 0.25rem',
              display: 'flex', alignItems: 'flex-end', justifyContent: 'center',
              paddingBottom: '0.3rem',
            }}>
              <span style={{ fontSize: '0.7rem', color: '#fff', fontWeight: 700 }}>{draft.slPips}p</span>
            </div>
            <span style={{ fontSize: '0.72rem', color: '#64748b' }}>SL</span>
          </div>
        </div>
      </div>

      {/* ── Stop Loss ── */}
      <div style={sectionStyle}>
        <div style={sectionTitle('#ef4444')}>
          <Shield size={16} />
          Stop Loss
        </div>
        <div style={rowStyle}>
          <NumField
            label="SL (pips)"
            hint={`≈ −${slUsd}`}
            value={draft.slPips}
            min={1} step={1}
            onChange={set('slPips')}
            accentColor="#ef4444"
          />
          <div style={fieldStyle}>
            <div style={labelStyle}>Risk / Reward ratio (TP1)</div>
            <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#f1f5f9', padding: '0.45rem 0' }}>
              1 : {draft.slPips > 0 ? (draft.tp1Pips / draft.slPips).toFixed(2) : '—'}
            </div>
            <div style={hintStyle}>TP1 pips ÷ SL pips</div>
          </div>
          <div style={fieldStyle}>
            <div style={labelStyle}>Risk / Reward ratio (TP2)</div>
            <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#f1f5f9', padding: '0.45rem 0' }}>
              1 : {draft.slPips > 0 ? (draft.tp2Pips / draft.slPips).toFixed(2) : '—'}
            </div>
            <div style={hintStyle}>TP2 pips ÷ SL pips</div>
          </div>
        </div>
      </div>

      {/* ── Summary card ── */}
      <div style={{
        ...sectionStyle,
        background: '#0f172a',
        border: '1px solid #1e293b',
        padding: '1rem 1.25rem',
      }}>
        <div style={{ fontSize: '0.78rem', color: '#64748b', marginBottom: '0.75rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          Summary
        </div>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.5rem' }}>
          {(activeProfile === 'mt5' ? [
            { label: 'Quantity', value: `${draft.quantity} lots`, color: '#f59e0b' },
            { label: 'Leverage', value: `${draft.leverage}×`, color: '#f59e0b' },
            { label: 'TP1', value: `+${draft.tp1Pips} pips`, color: '#10b981' },
            { label: 'TP2', value: `+${draft.tp2Pips} pips`, color: '#10b981' },
            { label: 'TP3', value: `+${draft.tp3Pips} pips`, color: '#10b981' },
            { label: 'SL',  value: `−${draft.slPips} pips`, color: '#ef4444' },
          ] : [
            { label: 'Margin', value: `${draft.marginUsdt} USDT`, color: '#f59e0b' },
            { label: 'Leverage', value: `${draft.leverage}×`, color: '#f59e0b' },
            { label: 'Position', value: `${(draft.marginUsdt * draft.leverage).toLocaleString()} USDT`, color: '#f1f5f9' },
            { label: 'TP1', value: `+${draft.tp1Pips} pips / +${tpUsd(draft.tp1Pips)}`, color: '#10b981' },
            { label: 'TP2', value: `+${draft.tp2Pips} pips / +${tpUsd(draft.tp2Pips)}`, color: '#10b981' },
            { label: 'TP3', value: `+${draft.tp3Pips} pips / +${tpUsd(draft.tp3Pips)}`, color: '#10b981' },
            { label: 'SL',  value: `−${draft.slPips} pips / −${slUsd}`, color: '#ef4444' },
          ]).map(({ label, value, color }) => (
            <div key={label}>
              <div style={{ fontSize: '0.72rem', color: '#475569', marginBottom: '0.15rem' }}>{label}</div>
              <div style={{ fontSize: '0.9rem', fontWeight: 700, color }}>{value}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Unsaved changes indicator */}
      {JSON.stringify(draft) !== JSON.stringify(currentSaved) && !saved && (
        <div style={{
          position: 'fixed', bottom: '1.5rem', right: '1.5rem',
          background: '#1e293b', border: `1px solid ${accentColor}`,
          borderRadius: '0.5rem', padding: '0.6rem 1rem',
          display: 'flex', alignItems: 'center', gap: '0.75rem',
          boxShadow: '0 4px 24px rgba(0,0,0,0.4)',
          fontSize: '0.85rem', color: accentColor,
        }}>
          Unsaved changes
          <button
            onClick={handleSave}
            style={{
              padding: '0.3rem 0.7rem', borderRadius: '0.25rem', border: 'none',
              background: accentColor, color: '#0f172a', fontWeight: 700,
              fontSize: '0.8rem', cursor: 'pointer',
            }}
          >
            Save
          </button>
        </div>
      )}
    </div>
  );
};

export default TradeConfigPage;
