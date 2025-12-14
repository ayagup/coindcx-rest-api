import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  Home, 
  TrendingUp, 
  ShoppingCart, 
  BarChart3, 
  Wallet, 
  User, 
  Activity,
  LogOut,
  Key
} from 'lucide-react';

const Header: React.FC = () => {
  const { isAuthenticated, logout } = useAuth();
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="header">
      <div className="header-container">
        <div className="logo">
          <Link to="/">
            <TrendingUp className="logo-icon" />
            <span>CoinDCX Trading</span>
          </Link>
        </div>

        <nav className="nav">
          <Link to="/" className={isActive('/') ? 'nav-link active' : 'nav-link'}>
            <Home size={18} />
            <span>Market</span>
          </Link>
          
          {isAuthenticated && (
            <>
              <Link to="/orders" className={isActive('/orders') ? 'nav-link active' : 'nav-link'}>
                <ShoppingCart size={18} />
                <span>Orders</span>
              </Link>
              
              <Link to="/futures" className={isActive('/futures') ? 'nav-link active' : 'nav-link'}>
                <BarChart3 size={18} />
                <span>Futures</span>
              </Link>
              
              <Link to="/margin" className={isActive('/margin') ? 'nav-link active' : 'nav-link'}>
                <Activity size={18} />
                <span>Margin</span>
              </Link>
              
              <Link to="/wallet" className={isActive('/wallet') ? 'nav-link active' : 'nav-link'}>
                <Wallet size={18} />
                <span>Wallet</span>
              </Link>
              
              <Link to="/account" className={isActive('/account') ? 'nav-link active' : 'nav-link'}>
                <User size={18} />
                <span>Account</span>
              </Link>
            </>
          )}
          
          <Link to="/api-logs" className={isActive('/api-logs') ? 'nav-link active' : 'nav-link'}>
            <Activity size={18} />
            <span>API Logs</span>
          </Link>
        </nav>

        <div className="header-actions">
          {isAuthenticated ? (
            <button onClick={logout} className="btn btn-secondary">
              <LogOut size={18} />
              <span>Logout</span>
            </button>
          ) : (
            <Link to="/login" className="btn btn-primary">
              <Key size={18} />
              <span>Login</span>
            </Link>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
