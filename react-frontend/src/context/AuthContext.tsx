import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { AuthContextType } from '../types';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [apiKey, setApiKey] = useState<string | null>(null);
  const [apiSecret, setApiSecret] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  // Load credentials from localStorage on mount
  useEffect(() => {
    const storedApiKey = localStorage.getItem('apiKey');
    const storedApiSecret = localStorage.getItem('apiSecret');
    
    if (storedApiKey && storedApiSecret) {
      setApiKey(storedApiKey);
      setApiSecret(storedApiSecret);
      setIsAuthenticated(true);
    }
  }, []);

  const login = (newApiKey: string, newApiSecret: string) => {
    localStorage.setItem('apiKey', newApiKey);
    localStorage.setItem('apiSecret', newApiSecret);
    setApiKey(newApiKey);
    setApiSecret(newApiSecret);
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem('apiKey');
    localStorage.removeItem('apiSecret');
    setApiKey(null);
    setApiSecret(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ apiKey, apiSecret, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
