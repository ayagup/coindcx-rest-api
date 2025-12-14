import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Header from './components/Header';
import MarketPage from './pages/MarketPage';
import LoginPage from './pages/LoginPage';
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
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <div className="page"><h1>Orders Page - Coming Soon</h1></div>
              </ProtectedRoute>
            }
          />
          <Route
            path="/futures"
            element={
              <ProtectedRoute>
                <div className="page"><h1>Futures Page - Coming Soon</h1></div>
              </ProtectedRoute>
            }
          />
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
          <Route path="/api-logs" element={<div className="page"><h1>API Logs - Coming Soon</h1></div>} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
    </>
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;
