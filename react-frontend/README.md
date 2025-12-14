# CoinDCX React Frontend

A modern, responsive React TypeScript frontend for the CoinDCX cryptocurrency trading platform. This application provides a user-friendly interface to interact with the CoinDCX REST API for trading, market data, and account management.

![CoinDCX Trading Platform](https://img.shields.io/badge/React-18.2-blue) ![TypeScript](https://img.shields.io/badge/TypeScript-5.2-blue) ![Vite](https://img.shields.io/badge/Vite-5.0-646CFF)

## 🚀 Features

### 📊 Market Data
- **Real-time Ticker Information** - Live price updates for all trading pairs
- **Market Search** - Quickly find specific trading pairs
- **24h Statistics** - High, low, volume, bid, and ask prices
- **Auto-refresh** - Market data updates every 30 seconds
- **Trend Indicators** - Visual indicators for price movements

### 🔐 Authentication
- **Secure API Key Management** - Store API credentials securely in browser
- **Protected Routes** - Authentication-required pages for trading features
- **Easy Login/Logout** - Simple credential management

### 💼 Trading Features (Authentication Required)
- **Order Management** - Create, view, and cancel orders
- **Futures Trading** - Manage futures positions and leverage
- **Margin Trading** - Access margin trading capabilities
- **Wallet Operations** - Transfer funds and view transactions
- **Account Information** - View balances and account details

### 📈 API Monitoring
- **API Call Logs** - Track all API requests and responses
- **Performance Metrics** - Monitor API response times
- **Error Tracking** - View failed API calls and error messages

### 🎨 User Interface
- **Modern Dark Theme** - Eye-friendly dark color scheme
- **Responsive Design** - Works seamlessly on desktop, tablet, and mobile
- **Smooth Animations** - Polished user experience with transitions
- **Intuitive Navigation** - Easy-to-use menu and routing

## 📋 Prerequisites

Before running this application, ensure you have:

- **Node.js** (v18 or higher) - [Download](https://nodejs.org/)
- **npm** or **yarn** package manager
- **CoinDCX API Credentials** - Get from [CoinDCX API Portal](https://coindcx.com/api)
- **Backend API** - The Spring Boot backend must be running on `http://localhost:8080`

## 🛠️ Installation

### 1. Clone the Repository

```bash
cd react-frontend
```

### 2. Install Dependencies

Using npm:
```bash
npm install
```

Or using yarn:
```bash
yarn install
```

### 3. Configure Environment Variables

Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080
```

For production, create `.env.production`:
```env
VITE_API_BASE_URL=https://your-api-domain.com
```

### 4. Start the Development Server

Using npm:
```bash
npm run dev
```

Or using yarn:
```bash
yarn dev
```

The application will start on `http://localhost:3000`

## 🏗️ Project Structure

```
react-frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── Header.tsx       # Navigation header
│   │   ├── Loading.tsx      # Loading spinner
│   │   └── ErrorMessage.tsx # Error display
│   ├── context/             # React context providers
│   │   └── AuthContext.tsx  # Authentication state management
│   ├── pages/               # Page components
│   │   ├── MarketPage.tsx   # Market data and tickers
│   │   ├── LoginPage.tsx    # API credentials login
│   │   ├── OrdersPage.tsx   # Order management
│   │   ├── FuturesPage.tsx  # Futures trading
│   │   ├── MarginPage.tsx   # Margin trading
│   │   ├── WalletPage.tsx   # Wallet management
│   │   ├── AccountPage.tsx  # Account information
│   │   └── ApiLogsPage.tsx  # API monitoring
│   ├── services/            # API service layer
│   │   └── api.ts           # Axios API client
│   ├── types/               # TypeScript type definitions
│   │   └── index.ts         # All type definitions
│   ├── App.tsx              # Main app component
│   ├── App.css              # Global styles
│   ├── main.tsx             # Application entry point
│   └── index.css            # Base CSS reset
├── public/                  # Static assets
├── .env                     # Environment variables
├── package.json             # Dependencies and scripts
├── tsconfig.json            # TypeScript configuration
├── vite.config.ts           # Vite configuration
└── README.md                # This file
```

## 🎯 Usage Guide

### 1. Start the Backend API

Before using the frontend, ensure the Spring Boot backend is running:

```bash
cd ../spring-client
mvn spring-boot:run
```

The backend should be accessible at `http://localhost:8080`

### 2. Access the Application

Open your browser and navigate to:
```
http://localhost:3000
```

### 3. Login with API Credentials

1. Click the **"Login"** button in the header
2. Enter your CoinDCX API Key
3. Enter your CoinDCX API Secret
4. Click **"Login"**

Your credentials are stored securely in browser local storage.

### 4. Explore Features

#### View Market Data (No Authentication Required)
- View real-time ticker information for all markets
- Use the search box to filter specific trading pairs
- Click the refresh button to manually update data

#### Manage Orders (Authentication Required)
- Navigate to **Orders** page
- Create new orders (limit/market)
- View active orders
- Cancel orders
- Check order history

#### Trade Futures (Authentication Required)
- Navigate to **Futures** page
- Open/close positions
- Manage leverage
- Set take profit and stop loss

#### Margin Trading (Authentication Required)
- Navigate to **Margin** page
- Create margin orders
- Add/remove margin
- Edit prices and stop loss

#### Wallet Management (Authentication Required)
- Navigate to **Wallet** page
- View balances across all currencies
- Transfer funds between accounts
- Check transaction history

#### Account Information (Authentication Required)
- Navigate to **Account** page
- View user profile
- Check total balances
- See verification status

#### API Logs (No Authentication Required)
- Navigate to **API Logs** page
- Monitor all API calls
- View response times
- Track errors and failures

### 5. Logout

Click the **"Logout"** button in the header to:
- Clear stored API credentials
- Return to login screen
- Secure your session

## 🔧 Configuration

### API Proxy Configuration

The Vite development server is configured to proxy API requests to the backend:

```typescript
// vite.config.ts
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

This eliminates CORS issues during development.

### API Base URL

The API base URL is configured via environment variables:

```typescript
// src/services/api.ts
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
```

### Authentication Headers

API credentials are automatically added to requests:

```typescript
// Request interceptor
apiClient.interceptors.request.use((config) => {
  const apiKey = localStorage.getItem('apiKey');
  const apiSecret = localStorage.getItem('apiSecret');
  
  if (apiKey) config.headers['X-API-KEY'] = apiKey;
  if (apiSecret) config.headers['X-API-SECRET'] = apiSecret;
  
  return config;
});
```

## 📦 Building for Production

### Build the Application

Using npm:
```bash
npm run build
```

Or using yarn:
```bash
yarn build
```

This creates an optimized production build in the `dist` folder.

### Preview Production Build

Using npm:
```bash
npm run preview
```

Or using yarn:
```bash
yarn preview
```

### Deploy

The `dist` folder can be deployed to any static hosting service:

- **Vercel**: `vercel deploy`
- **Netlify**: `netlify deploy --prod`
- **GitHub Pages**: Copy `dist` to `gh-pages` branch
- **AWS S3**: Upload `dist` contents to S3 bucket
- **Nginx**: Copy `dist` to web root directory

## 🧪 Development

### Available Scripts

| Script | Description |
|--------|-------------|
| `npm run dev` | Start development server on port 3000 |
| `npm run build` | Build production bundle |
| `npm run preview` | Preview production build locally |
| `npm run lint` | Run ESLint to check code quality |

### Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **React** | 18.2.0 | UI framework |
| **TypeScript** | 5.2.2 | Type safety |
| **Vite** | 5.0.8 | Build tool |
| **React Router** | 6.20.0 | Navigation |
| **Axios** | 1.6.2 | HTTP client |
| **Lucide React** | 0.294.0 | Icons |
| **Recharts** | 2.10.3 | Charts (future use) |

### Code Style

The project uses TypeScript strict mode for type safety:

```json
{
  "compilerOptions": {
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true
  }
}
```

### Adding New Features

1. **Create a new page component** in `src/pages/`
2. **Define TypeScript types** in `src/types/index.ts`
3. **Add API methods** in `src/services/api.ts`
4. **Add route** in `src/App.tsx`
5. **Update navigation** in `src/components/Header.tsx`

## 🔒 Security Considerations

### API Credentials Storage
- Credentials are stored in browser `localStorage`
- Consider implementing encryption for production
- Never commit `.env` files with real credentials

### HTTPS in Production
- Always use HTTPS in production
- Configure proper CORS headers on backend
- Use secure cookies for session management

### Content Security Policy
Add CSP headers in production:
```html
<meta http-equiv="Content-Security-Policy" 
      content="default-src 'self'; connect-src 'self' https://api.coindcx.com">
```

## 🐛 Troubleshooting

### Issue: Cannot connect to backend API

**Solution:**
1. Verify backend is running on `http://localhost:8080`
2. Check console for CORS errors
3. Ensure `.env` has correct `VITE_API_BASE_URL`
4. Try accessing `http://localhost:8080/api/public/ticker` directly

### Issue: API returns 401 Unauthorized

**Solution:**
1. Verify API credentials are correct
2. Check if credentials are being sent in headers (DevTools > Network)
3. Ensure credentials have necessary permissions
4. Try logging out and logging in again

### Issue: Market data not loading

**Solution:**
1. Check browser console for errors
2. Verify backend API is responding
3. Test endpoint directly: `curl http://localhost:8080/api/public/ticker`
4. Check network tab in DevTools

### Issue: Styles not loading properly

**Solution:**
1. Clear browser cache
2. Run `npm run build` and `npm run dev` again
3. Check if CSS files are in `dist` folder
4. Verify no CSS syntax errors in console

### Issue: TypeScript errors

**Solution:**
1. Run `npm install` to ensure all types are installed
2. Delete `node_modules` and `package-lock.json`
3. Run `npm install` again
4. Restart VS Code TypeScript server

## 📝 API Integration

### Available API Endpoints

The frontend integrates with these backend endpoints:

#### Public APIs (No Authentication)
- `GET /api/public/ticker` - Market ticker data
- `GET /api/public/markets` - Available markets
- `GET /api/public/order_book` - Order book for market
- `GET /api/public/trades` - Recent trades

#### Trading APIs (Authentication Required)
- `POST /api/orders/create` - Create new order
- `POST /api/orders/active_orders` - Get active orders
- `DELETE /api/orders/cancel/{id}` - Cancel order
- `POST /api/orders/cancel_all` - Cancel all orders

#### Futures APIs (Authentication Required)
- `POST /api/futures/orders/create` - Create futures order
- `POST /api/futures/positions` - Get positions
- `POST /api/futures/positions/exit` - Close position

#### User APIs (Authentication Required)
- `POST /api/user/balances` - Get account balances
- `POST /api/user/info` - Get user information

#### Monitoring APIs (No Authentication)
- `GET /api/logs` - Get API call logs
- `GET /api/logs/stats` - Get API statistics

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Write TypeScript with strict typing
- Follow React best practices and hooks guidelines
- Add comments for complex logic
- Test thoroughly before submitting PR
- Update README.md if adding new features

## 📄 License

This project is part of the CoinDCX REST API client implementation.

## 🔗 Related Links

- [CoinDCX API Documentation](https://docs.coindcx.com/)
- [React Documentation](https://react.dev/)
- [Vite Documentation](https://vitejs.dev/)
- [TypeScript Documentation](https://www.typescriptlang.org/)

## 💬 Support

For issues and questions:
- Open an issue on GitHub
- Check existing issues for solutions
- Refer to CoinDCX API documentation

## 🎉 Next Steps

After setting up the frontend:

1. ✅ Install dependencies: `npm install`
2. ✅ Configure environment variables in `.env`
3. ✅ Start backend API on port 8080
4. ✅ Start frontend: `npm run dev`
5. ✅ Open browser to `http://localhost:3000`
6. ✅ Login with API credentials
7. ✅ Start trading!

---

**Happy Trading! 🚀📈**
