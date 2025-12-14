import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Key, Lock } from 'lucide-react';

const LoginPage: React.FC = () => {
  const [apiKey, setApiKey] = useState('');
  const [apiSecret, setApiSecret] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!apiKey.trim() || !apiSecret.trim()) {
      setError('Please enter both API key and secret');
      return;
    }

    login(apiKey, apiSecret);
    navigate('/');
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-header">
          <h1>CoinDCX API Login</h1>
          <p>Enter your API credentials to access trading features</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="apiKey">
              <Key size={18} />
              API Key
            </label>
            <input
              id="apiKey"
              type="text"
              value={apiKey}
              onChange={(e) => setApiKey(e.target.value)}
              placeholder="Enter your API key"
              className="input"
            />
          </div>

          <div className="form-group">
            <label htmlFor="apiSecret">
              <Lock size={18} />
              API Secret
            </label>
            <input
              id="apiSecret"
              type="password"
              value={apiSecret}
              onChange={(e) => setApiSecret(e.target.value)}
              placeholder="Enter your API secret"
              className="input"
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="btn btn-primary btn-block">
            Login
          </button>
        </form>

        <div className="login-footer">
          <p>
            Don't have API credentials?{' '}
            <a href="https://coindcx.com/api" target="_blank" rel="noopener noreferrer">
              Get them from CoinDCX
            </a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
