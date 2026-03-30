# import socketio
# import hmac
# import hashlib
# import json
# import time

# socketEndpoint = 'wss://stream.coindcx.com'
# sio = socketio.Client()

# sio.connect(socketEndpoint, transports = 'websocket')

# key = "8fb0800e8afad00a68f3d54fd48d73f82c09718b1b496f4e"
# secret = "21b43bc57a67a4ff05f3b0def38d6b3385d894cac1ae7e089d0c54a4c7f85ee1"

# # python3
# secret_bytes = bytes(secret, encoding='utf-8')
# # python2
# secret_bytes = bytes(secret, encoding='utf-8')

# body = {"channel":"B-BTC_USDT_1m"}
# json_body = json.dumps(body, separators = (',', ':'))
# signature = hmac.new(secret_bytes, json_body.encode(), hashlib.sha256).hexdigest()

# # Join channel
# sio.emit('join', { 'channelName': 'B-BTC_USDT_1m', 'authSignature': signature, 'apiKey' : key })

# ### Listen update on eventName
# ### Replace the <eventName> with the df-position-update, df-order-update, ###balance-update

# @sio.event
# def connect():
#     print("✅ Connected to CoinDCX WebSocket!")
#     print("📊 Subscribing to channel: B-BTC_USDT_1m")
#     # sio.emit('join', {'channelName': "B-BTC_USDT_1m"})


# @sio.event
# def disconnect():
#     print("❌ Disconnected from CoinDCX WebSocket")


# @sio.event
# def connect_error(data):
#     print(f"❌ Connection error: {data}")


# @sio.on('candlestick')
# def on_candlestick(response):
#     print("\n🎉 CANDLESTICK DATA RECEIVED!")
#     print(f"Data: {response}")
#     print("-" * 80)


# @sio.on('*')
# def catch_all(event):
#     print(f"📨 Received event: {event}")
#     print(f"Data: {event['data']}")

    
# @sio.on('candlestick')
# def catch_all(event):
#     print(f"📨 Received event: {event}")
#     print(f"Data: {event['data']}")


# # Keep the connection alive
# while True:
#     time.sleep(1)

# # leave a channel
# sio.emit('leave', { 'channelName' : 'coindcx' })


import socketio
import hmac
import hashlib
import json
import asyncio
from datetime import datetime

socketEndpoint = 'wss://stream.coindcx.com'
sio = socketio.AsyncClient()

key = "8fb0800e8afad00a68f3d54fd48d73f82c09718b1b496f4e"
secret = "21b43bc57a67a4ff05f3b0def38d6b3385d894cac1ae7e089d0c54a4c7f85ee1"

# Create signature for private channel
secret_bytes = bytes(secret, encoding='utf-8')
channelName = "coindcx"
body = {"channel": channelName}
json_body = json.dumps(body, separators=(',', ':'))
signature = hmac.new(secret_bytes, json_body.encode(), hashlib.sha256).hexdigest()


@sio.event
async def connect():
    print("✅ Connected to CoinDCX WebSocket!")
    current_time = datetime.now()
    print(f"   Time: {current_time.strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 80)
    
    # Subscribe to PRIVATE channel (authenticated)
    print("🔐 Subscribing to PRIVATE channel: coindcx")
    channelName = "coindcx"
    body = {"channel": channelName}
    json_body = json.dumps(body, separators=(',', ':'))
    signature = hmac.new(secret_bytes, json_body.encode(), hashlib.sha256).hexdigest()
    
    await sio.emit('join', {
        'channelName': "coindcx",
        'authSignature': signature,
        'apiKey': key
    })
    
    # Subscribe to PUBLIC channels
    print("📊 Subscribing to PUBLIC channels:")
    print("   1. B-BTC_USDT_1m (candlestick)")
    channelName = "B-BTC_USDT_1m"
    body = {"B-BTC_USDT_1m": channelName}
    json_body = json.dumps(body, separators=(',', ':'))
    signature = hmac.new(secret_bytes, json_body.encode(), hashlib.sha256).hexdigest()
    await sio.emit('join', {'channelName': "B-BTC_USDT_1m", 'authSignature': signature, 'apiKey': key})

    print("   2. B-BTC_USDT@trades (new-trade)")
    channelName = "B-BTC_USDT@trades"
    body = {"B-BTC_USDT@trades": channelName}
    json_body = json.dumps(body, separators=(',', ':'))
    signature = hmac.new(secret_bytes, json_body.encode(), hashlib.sha256).hexdigest()
    await sio.emit('join', {'channelName': "B-BTC_USDT@trades", 'authSignature': signature, 'apiKey': key})

    print("\n⏳ Waiting for events...")
    print("=" * 80)


@sio.event
async def disconnect():
    print("\n❌ Disconnected from CoinDCX WebSocket")


@sio.event
async def connect_error(data):
    print(f"❌ Connection error: {data}")


# PRIVATE CHANNEL EVENTS

@sio.on('balance-update')
async def on_balance_update(response):
    current_time = datetime.now()
    print(f"\n💳 BALANCE UPDATE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}")
    print("-" * 80)


@sio.on('order-update')
async def on_order_update(response):
    current_time = datetime.now()
    print(f"\n📝 ORDER UPDATE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}")
    print("-" * 80)


@sio.on('trade-update')
async def on_trade_update(response):
    current_time = datetime.now()
    print(f"\n🔄 TRADE UPDATE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)}")
    print("-" * 80)


# PUBLIC CHANNEL EVENTS

@sio.on('candlestick')
async def on_candlestick(response):
    current_time = datetime.now()
    print(f"\n🕯️  CANDLESTICK - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)[:300]}...")
    print("-" * 80)


@sio.on('new-trade')
async def on_new_trade(response):
    current_time = datetime.now()
    print(f"\n💱 NEW TRADE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)[:300]}...")
    print("-" * 80)


@sio.on('price-change')
async def on_price_change(response):
    current_time = datetime.now()
    print(f"\n💰 PRICE CHANGE - {current_time.strftime('%H:%M:%S')}")
    print(f"Data: {json.dumps(response, indent=2)[:300]}...")
    print("-" * 80)


# Catch-all for debugging
@sio.on('*')
async def catch_all(event, data):
    if event not in ['balance-update', 'order-update', 'trade-update', 
                     'candlestick', 'new-trade', 'price-change']:
        print(f"\n📨 EVENT: {event}")
        print(f"Data: {str(data)[:200]}...")
        print("-" * 80)


async def main():
    try:
        print(f"🔌 Connecting to {socketEndpoint}...")
        await sio.connect(socketEndpoint, transports=['websocket'])
        
        # Keep connection alive - this will wait indefinitely
        await sio.wait()
        
    except KeyboardInterrupt:
        print("\n\n🛑 Stopping...")
        await sio.disconnect()
        
    except Exception as e:
        print(f"\n❌ Error: {e}")
        import traceback
        traceback.print_exc()
        await sio.disconnect()


# Run the main function
if __name__ == '__main__':
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("\n\n✅ Stopped by user")