import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

// ── Types ────────────────────────────────────────────────────────────────────
export interface TradeConfig {
  marginUsdt: number;
  leverage: number;
  tp1Pips: number;
  tp2Pips: number;
  tp3Pips: number;
  slPips: number;
}

interface TradeConfigContextType {
  config: TradeConfig;
  updateConfig: (updates: Partial<TradeConfig>) => void;
  resetConfig: () => void;
}

// ── Defaults ─────────────────────────────────────────────────────────────────
export const DEFAULT_TRADE_CONFIG: TradeConfig = {
  marginUsdt: 50,
  leverage: 10,
  tp1Pips: 50,
  tp2Pips: 100,
  tp3Pips: 200,
  slPips: 30,
};

const STORAGE_KEY = 'tradeConfig';

// ── Context ──────────────────────────────────────────────────────────────────
const TradeConfigContext = createContext<TradeConfigContextType | undefined>(undefined);

export const TradeConfigProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [config, setConfig] = useState<TradeConfig>(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        const parsed = JSON.parse(stored) as Partial<TradeConfig>;
        return { ...DEFAULT_TRADE_CONFIG, ...parsed };
      }
    } catch {
      // ignore parse errors
    }
    return DEFAULT_TRADE_CONFIG;
  });

  // Persist to localStorage whenever config changes
  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(config));
  }, [config]);

  const updateConfig = (updates: Partial<TradeConfig>) => {
    setConfig(prev => ({ ...prev, ...updates }));
  };

  const resetConfig = () => {
    setConfig(DEFAULT_TRADE_CONFIG);
  };

  return (
    <TradeConfigContext.Provider value={{ config, updateConfig, resetConfig }}>
      {children}
    </TradeConfigContext.Provider>
  );
};

export const useTradeConfig = (): TradeConfigContextType => {
  const ctx = useContext(TradeConfigContext);
  if (!ctx) throw new Error('useTradeConfig must be used inside TradeConfigProvider');
  return ctx;
};
