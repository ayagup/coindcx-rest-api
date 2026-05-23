import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

// ── Types ────────────────────────────────────────────────────────────────────
export interface TradeConfig {
  marginUsdt: number;
  leverage: number;
  tp1Pips: number;
  tp2Pips: number;
  tp3Pips: number;
  slPips: number;
  /** MT5 only — fixed quantity (lots/contracts) per trade */
  quantity: number;
}

export type TradeConfigProfile = 'coindcx' | 'mt5';

interface TradeConfigContextType {
  /** CoinDCX config (legacy alias — equals coindcxConfig) */
  config: TradeConfig;
  coindcxConfig: TradeConfig;
  mt5Config: TradeConfig;
  updateConfig: (updates: Partial<TradeConfig>, profile?: TradeConfigProfile) => void;
  resetConfig: (profile?: TradeConfigProfile) => void;
}

// ── Defaults ─────────────────────────────────────────────────────────────────
export const DEFAULT_TRADE_CONFIG: TradeConfig = {
  marginUsdt: 3,
  leverage: 20,
  tp1Pips: 30,
  tp2Pips: 50,
  tp3Pips: 80,
  slPips: 30,
  quantity: 0, // not used by CoinDCX
};

export const DEFAULT_MT5_CONFIG: TradeConfig = {
  marginUsdt: 0,  // not used — MT5 uses fixed quantity
  leverage: 100,
  tp1Pips: 30,
  tp2Pips: 50,
  tp3Pips: 80,
  slPips: 30,
  quantity: 0.01, // lots per trade
};

const STORAGE_KEY_COINDCX = 'tradeConfig';        // keep legacy key
const STORAGE_KEY_MT5     = 'tradeConfig_mt5';

function loadConfig(key: string, defaults: TradeConfig): TradeConfig {
  try {
    const stored = localStorage.getItem(key);
    if (stored) return { ...defaults, ...(JSON.parse(stored) as Partial<TradeConfig>) };
  } catch { /* ignore */ }
  return defaults;
}

// ── Context ──────────────────────────────────────────────────────────────────
const TradeConfigContext = createContext<TradeConfigContextType | undefined>(undefined);

export const TradeConfigProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [coindcxConfig, setCoindcxConfig] = useState<TradeConfig>(() =>
    loadConfig(STORAGE_KEY_COINDCX, DEFAULT_TRADE_CONFIG),
  );
  const [mt5Config, setMt5Config] = useState<TradeConfig>(() =>
    loadConfig(STORAGE_KEY_MT5, DEFAULT_MT5_CONFIG),
  );

  useEffect(() => { localStorage.setItem(STORAGE_KEY_COINDCX, JSON.stringify(coindcxConfig)); }, [coindcxConfig]);
  useEffect(() => { localStorage.setItem(STORAGE_KEY_MT5,     JSON.stringify(mt5Config));     }, [mt5Config]);

  const updateConfig = (updates: Partial<TradeConfig>, profile: TradeConfigProfile = 'coindcx') => {
    if (profile === 'mt5') setMt5Config(prev => ({ ...prev, ...updates }));
    else setCoindcxConfig(prev => ({ ...prev, ...updates }));
  };

  const resetConfig = (profile: TradeConfigProfile = 'coindcx') => {
    if (profile === 'mt5') setMt5Config(DEFAULT_MT5_CONFIG);
    else setCoindcxConfig(DEFAULT_TRADE_CONFIG);
  };

  return (
    <TradeConfigContext.Provider value={{
      config: coindcxConfig,   // legacy alias
      coindcxConfig,
      mt5Config,
      updateConfig,
      resetConfig,
    }}>
      {children}
    </TradeConfigContext.Provider>
  );
};

export const useTradeConfig = (): TradeConfigContextType => {
  const ctx = useContext(TradeConfigContext);
  if (!ctx) throw new Error('useTradeConfig must be used inside TradeConfigProvider');
  return ctx;
};
