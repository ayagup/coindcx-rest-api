import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { TradeConfigProvider } from './context/TradeConfigContext';
import Header from './components/Header';
import MarketPage from './pages/MarketPage';
import LoginPage from './pages/LoginPage';
import WebSocketPage from './pages/WebSocketPage';
import ChartPage from './pages/ChartPage';
import FuturesTradingPage from './pages/FuturesTradingPage';
import TradeConfigPage from './pages/TradeConfigPage';
import TradeLogPage from './pages/TradeLogPage';
import './App.css';

// Protected Route Component
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

const AppRoutes: React.FC = () => {
  return (
    <>
      <Header />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<MarketPage />} />
          <Route path="/chart/:marketType/:symbol" element={<ChartPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <div className="page"><h1>Orders Page - Coming Soon</h1></div>
              </ProtectedRoute>
            }
          />
          <Route path="/futures" element={<FuturesTradingPage />} />
          <Route path="/trade-config" element={<TradeConfigPage />} />
          <Route path="/trade-log" element={<TradeLogPage />} />
          <Route
            path="/margin"
            element={
              <ProtectedRoute>
                <div className="page"><h1>Margin Page - Coming Soon</h1></div>
              </ProtectedRoute>
            }
          />
          <Route
            path="/wallet"
            element={
              <ProtectedRoute>
                <div className="page"><h1>Wallet Page - Coming Soon</h1></div>
              </ProtectedRoute>
            }
          />
          <Route
            path="/account"
            element={
              <ProtectedRoute>
                <div className="page"><h1>Account Page - Coming Soon</h1></div>
              </ProtectedRoute>
            }
          />
          <Route path="/websocket" element={<WebSocketPage />} />
          <Route path="/api-logs" element={<div className="page"><h1>API Logs - Coming Soon</h1></div>} />
          <Route path="/chart/:marketType/:symbol" element={<ChartPage />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
    </>
  );
};

function App() {
  return (
    <AuthProvider>
      <TradeConfigProvider>
        <Router>
          <AppRoutes />
        </Router>
      </TradeConfigProvider>
    </AuthProvider>
  );
}

export default App;
