import socketio
import hmac
import hashlib
import json
import time
from datetime import datetime

socketEndpoint = 'wss://stream.coindcx.com'
sio = socketio.Client()

key = "8fb0800e8afad00a68f3d54fd48d73f82c09718b1b496f4e"
secret = "21b43bc57a67a4ff05f3b0def38d6b3385d894cac1ae7e089d0c54a4c7f85ee1"

# Create signature for private channel
secret_bytes = bytes(secret, encoding='utf-8')

def join_auth_channels(channelName):
    body = {"channel": channelName}
    json_body = json.dumps(body, separators=(',', ':'))
    signature = hmac.new(secret_bytes, json_body.encode(), hashlib.sha256).hexdigest()
    sio.emit('join', {
        'channelName': channelName,
        'authSignature': signature,
        'apiKey': key
    })


@sio.event
def connect():
    print("✅ Connected to CoinDCX WebSocket!")
    current_time = datetime.now()
    print(f"   Time: {current_time.strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 80)
    
    # Subscribe to PRIVATE channel (authenticated)
    print("🔐 Subscribing to PRIVATE channel: coindcx")
    # channelName = "coindcx"
    # body = {"channel": channelName}
    # json_body = json.dumps(body, separators=(',', ':'))
    # signature = hmac.new(secret_bytes, json_body.encode(), hashlib.sha256).hexdigest()
    
    # sio.emit('join', {
    #     'channelName': "coindcx",
    #     'authSignature': signature,
    #     'apiKey': key
    # })
    # join_auth_channels("coindcx")
    
    # Subscribe to PUBLIC channels (NO AUTH NEEDED)
    print("📊 Subscribing to PUBLIC channels:")
    print("   1. B-BTC_USDT_1m (candlestick)")
    join_auth_channels("B-BTC_USDT_1m-futures")
    # sio.emit('join', {'channelName': "B-BTC_USDT_1m"})
    
    print("   2. B-BTC_USDT@trades (new-trade)")
    # join_auth_channels("B-BTC_USDT@trades-futures")
    
    print("   3. B-BTC_USDT@orderbook@20 (depth-update)")
    # join_auth_channels("B-BTC_USDT@orderbook@20-futures")
    
    print("   4. currentPrices@spot@10s (current prices)")
    # join_auth_channels("currentPrices@futures@rt")     
    # sio.emit('join', {'channelName': "currentPrices@spot@10s"})
    
    print("\n⏳ Waiting for events...")
    print("=" * 80)


@sio.event
def disconnect():
    print("\n❌ Disconnected from CoinDCX WebSocket")


@sio.event
def connect_error(data):
    print(f"❌ Connection error: {data}")


# PRIVATE CHANNEL EVENTS

@sio.on('balance-update')
def on_balance_update(response):
    current_time = datetime.now()
    print(f"\n💳 BALANCE UPDATE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


@sio.on('order-update')
def on_order_update(response):
    current_time = datetime.now()
    print(f"\n📝 ORDER UPDATE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


@sio.on('trade-update')
def on_trade_update(response):
    current_time = datetime.now()
    print(f"\n🔄 TRADE UPDATE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


# PUBLIC CHANNEL EVENTS

@sio.on('candlestick')
def on_candlestick(response):
    current_time = datetime.now()
    print(f"\n🕯️  CANDLESTICK - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


@sio.on('depth-update')
def on_depth_update(response):
    current_time = datetime.now()
    print(f"\n📊 DEPTH UPDATE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


@sio.on('new-trade')
def on_new_trade(response):
    current_time = datetime.now()
    print(f"\n💱 NEW TRADE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


@sio.on('currentPrices@spot#update')
def on_current_prices(response):
    current_time = datetime.now()
    print(f"\n💰 CURRENT PRICES - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


@sio.on('price-change')
def on_price_change(response):
    current_time = datetime.now()
    print(f"\n💵 PRICE CHANGE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}...")
    print("-" * 80)


# Catch-all for debugging
@sio.on('*')
def catch_all(event, data):
    known_events = ['balance-update', 'order-update', 'trade-update', 
                    'candlestick', 'new-trade', 'price-change', 'depth-update',
                    'currentPrices@spot#update']
    if event not in known_events:
        print(f"\n📨 UNKNOWN EVENT: {event}")
        print(f"Data: {str(data)}...")
        print("-" * 80)


def main():
    try:
        print(f"🔌 Connecting to {socketEndpoint}...")
        sio.connect(socketEndpoint, transports=['websocket'])
        
        # Keep connection alive
        print("\n💡 Press Ctrl+C to stop\n")
        sio.wait()
        
    except KeyboardInterrupt:
        print("\n\n🛑 Stopping...")
        sio.disconnect()
        print("✅ Disconnected successfully")
        
    except Exception as e:
        print(f"\n❌ Error: {e}")
        import traceback
        traceback.print_exc()
        try:
            sio.disconnect()
        except:
            pass


if __name__ == '__main__':
    main()
