# Quick Start Guide - CoinDCX React Frontend

## 🚀 Get Started in 3 Minutes

### Step 1: Install Dependencies (1 min)

```bash
cd react-frontend
npm install
```

### Step 2: Start the Application (30 sec)

```bash
npm run dev
```

The app will start at: **http://localhost:3000**

### Step 3: Explore the App (1 min)

1. **View Market Data** (No login required)
   - Opens automatically on homepage
   - See real-time cryptocurrency prices
   - Use search to filter markets

2. **Login** (Optional - for trading features)
   - Click "Login" button in header
   - Enter your CoinDCX API credentials
   - Access Orders, Futures, Margin, Wallet, Account pages

## 📋 Prerequisites Checklist

- ✅ Node.js 18+ installed
- ✅ Backend API running on `http://localhost:8080`
- ✅ (Optional) CoinDCX API credentials for trading features

## 🎯 Quick Commands

| Command | Purpose |
|---------|---------|
| `npm install` | Install all dependencies |
| `npm run dev` | Start development server |
| `npm run build` | Build for production |
| `npm run preview` | Preview production build |

## 🌐 Important URLs

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## 🔧 Configuration

### .env File
```env
VITE_API_BASE_URL=http://localhost:8080
```

## 📂 Key Files Overview

```
react-frontend/
├── src/
│   ├── App.tsx              # Main application & routing
│   ├── main.tsx             # Entry point
│   ├── services/api.ts      # API client (Axios)
│   ├── context/AuthContext.tsx  # Authentication
│   ├── components/          # Reusable components
│   │   ├── Header.tsx       # Navigation
│   │   ├── Loading.tsx      # Loading state
│   │   └── ErrorMessage.tsx # Error display
│   └── pages/               # Page components
│       ├── MarketPage.tsx   # Market data (default)
│       ├── LoginPage.tsx    # API login
│       └── ...              # Other pages
├── package.json             # Dependencies
├── vite.config.ts           # Vite config
└── README.md                # Full documentation
```

## 🎨 Features at a Glance

### ✅ Implemented
- ✅ Real-time market data display
- ✅ API key authentication
- ✅ Protected routes
- ✅ Responsive design
- ✅ Dark theme UI
- ✅ Auto-refresh market data
- ✅ Search and filter markets
- ✅ Error handling
- ✅ Loading states

### 🚧 Ready to Extend
- Orders page (template ready)
- Futures trading (template ready)
- Margin trading (template ready)
- Wallet management (template ready)
- Account info (template ready)
- API monitoring (template ready)

## 🐛 Troubleshooting

### Port 3000 Already in Use?
```bash
# Kill the process
npx kill-port 3000

# Or use a different port
npm run dev -- --port 3001
```

### Backend Not Running?
```bash
cd ../spring-client
mvn spring-boot:run
```

### Dependencies Not Installing?
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

### CORS Errors?
The Vite proxy should handle this. Check `vite.config.ts`:
```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
}
```

## 💡 Usage Tips

### Viewing Market Data (No Auth)
1. Homepage loads automatically
2. Browse all cryptocurrency tickers
3. Use search bar to find specific pairs
4. Click refresh to update manually
5. Data auto-refreshes every 30 seconds

### Using Trading Features (Requires Auth)
1. Click "Login" in header
2. Enter API Key and Secret
3. Navigate to any protected page:
   - Orders
   - Futures
   - Margin
   - Wallet
   - Account
4. Click "Logout" to clear credentials

### Testing Without Real API Keys
- Use Market Data page (public, no auth required)
- Use API Logs page (public, no auth required)
- Other pages require authentication

## 📚 Learn More

- **Full Documentation**: See [README.md](./README.md)
- **Backend API**: See [../spring-client/SWAGGER_GUIDE.md](../spring-client/SWAGGER_GUIDE.md)
- **API Docs**: http://localhost:8080/swagger-ui.html

## 🎉 Next Steps

After getting started:

1. **Customize** - Modify components and styling
2. **Extend** - Implement full trading features
3. **Test** - Try different API endpoints
4. **Deploy** - Build and deploy to production

## 🤔 Need Help?

- Check the full [README.md](./README.md)
- View backend [Swagger docs](http://localhost:8080/swagger-ui.html)
- Check browser console for errors
- Inspect Network tab in DevTools

---

**You're ready to go! Start the app with `npm run dev` 🚀**
