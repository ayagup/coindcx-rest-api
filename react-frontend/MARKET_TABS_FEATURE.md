# Market Tabs Feature

## Overview
Enhanced the Market Data page with separate tabs for **Spot Markets** and **Futures Markets**, allowing users to easily switch between different market types.

## Changes Made

### 1. MarketPage Component (`src/pages/MarketPage.tsx`)

#### Added State Management
```typescript
type MarketType = 'spot' | 'futures';
const [activeTab, setActiveTab] = useState<MarketType>('spot');
```

#### Market Categorization Logic
Markets are automatically categorized based on their naming patterns:

**Futures Markets** are identified by:
- Contains `PERP` (Perpetual contracts)
- Contains `_USDT` (USDT-settled futures)
- Ends with 6 digits (date format like `231215`)

**Spot Markets**: All markets that don't match futures patterns

```typescript
const spotMarkets = tickers.filter(ticker => {
  const isFutures = ticker.market.includes('PERP') || 
                    ticker.market.includes('_USDT') || 
                    /\d{6}$/.test(ticker.market);
  return !isFutures;
});

const futuresMarkets = tickers.filter(ticker => {
  const isFutures = ticker.market.includes('PERP') || 
                    ticker.market.includes('_USDT') || 
                    /\d{6}$/.test(ticker.market);
  return isFutures;
});
```

#### UI Components
- **Tab Navigation**: Two tabs with market counts displayed as badges
- **Search Functionality**: Filters markets within the active tab
- **Empty State**: Context-aware messages based on active tab and search term

### 2. Styling (`src/App.css`)

#### Tab Container
```css
.tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid var(--border-color);
}
```

#### Tab Buttons
- Inactive: Secondary text color with transparent bottom border
- Hover: Highlighted background with rounded top corners
- Active: Primary color with colored bottom border indicator

#### Tab Count Badges
- Show number of markets in each category
- Different styling for active/inactive states
- Responsive to hover states

```css
.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.75rem;
  padding: 0.125rem 0.5rem;
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: 1rem;
  transition: all 0.3s;
}

.tab.active .tab-count {
  background-color: var(--primary-color);
  color: white;
}
```

## Features

### ✅ Tab Switching
- Click to switch between Spot and Futures markets
- Active tab highlighted with primary color and bottom border
- Smooth transitions between states

### ✅ Market Counts
- Real-time count of markets in each category
- Displayed as badges next to tab names
- Updates automatically when data refreshes

### ✅ Filtered Search
- Search box filters markets within the active tab only
- Search across both tabs by switching tabs
- Empty state messages reflect current tab and search term

### ✅ Auto-Refresh
- Continues to refresh every 30 seconds
- Market counts update automatically
- Tab selection persists across refreshes

## User Experience

### Default View
- Opens to "Spot Markets" tab by default
- Shows count of available spot markets
- Search and filter functionality ready

### Switching Tabs
1. Click "Futures Markets" to view futures
2. Count badge shows number of futures available
3. Search box filters only futures markets
4. Click "Spot Markets" to return

### Visual Feedback
- Active tab clearly highlighted
- Hover effects on tabs for better UX
- Count badges change color with tab state
- Smooth transitions between states

## Example Market Types

### Spot Markets
- `BTCINR` - Bitcoin to INR
- `ETHINR` - Ethereum to INR
- `XRPINR` - Ripple to INR
- `LTCINR` - Litecoin to INR

### Futures Markets (if available)
- `BTCPERP` - Bitcoin Perpetual
- `ETHPERP` - Ethereum Perpetual
- `BTC_USDT` - Bitcoin USDT Futures
- `BTC231215` - Bitcoin December 2023 Futures

## Technical Notes

### Market Detection
The futures detection regex `/\d{6}$/` matches markets ending with 6-digit date formats:
- `BTC231215` = BTC futures expiring Dec 15, 2023
- `ETH240130` = ETH futures expiring Jan 30, 2024

### Performance
- Market categorization happens once per data fetch
- Filtering is efficient using array methods
- No unnecessary re-renders

### State Management
- Simple useState for tab selection
- No additional dependencies required
- Clean and maintainable code

## Future Enhancements

### Possible Improvements
1. **Persistent Tab Selection**: Save tab preference to localStorage
2. **URL Query Parameters**: Support direct linking to specific tabs
3. **Additional Filters**: Sort by volume, price change, etc.
4. **Market Statistics**: Show summary stats for each tab
5. **Custom Categories**: Allow users to create custom market groups
6. **Favorites**: Star/favorite specific markets across tabs

### Advanced Features
- **Real-time Updates**: WebSocket integration for live prices
- **Price Alerts**: Set alerts for specific market conditions
- **Chart Integration**: Quick charts within market cards
- **Comparison Mode**: Compare multiple markets side-by-side

## Testing Checklist

- [x] Tab switching works correctly
- [x] Market counts display accurately
- [x] Search filters within active tab
- [x] Empty states show appropriate messages
- [x] Refresh button maintains tab selection
- [x] Auto-refresh (30s) works with tabs
- [x] Hover states work correctly
- [x] Active tab styling is clear
- [x] Count badges update on data refresh
- [x] Responsive on different screen sizes

## Browser Compatibility

Works with all modern browsers:
- ✅ Chrome/Edge (Chromium)
- ✅ Firefox
- ✅ Safari
- ✅ Opera

## Dependencies

No additional dependencies required. Uses existing:
- React hooks (`useState`, `useEffect`)
- Lucide React icons
- Existing CSS styling system

## Notes

- **CoinDCX API**: Ensure the API returns markets with proper naming conventions
- **CORS**: Already configured for localhost:3000 and localhost:5173
- **Logging**: All API calls are logged (HTTP Filter + AOP + External API)

## Support

If you encounter any issues:
1. Check browser console for errors
2. Verify Spring Boot backend is running
3. Confirm CORS is properly configured
4. Check network tab for API responses

## Summary

The Market Tabs feature provides a clean, intuitive way to browse different types of cryptocurrency markets. Users can easily switch between spot and futures markets, with real-time counts and efficient filtering. The implementation is simple, performant, and follows React best practices.
