#!/bin/sh
set -e

echo "🔍 Waiting for ngrok to be ready..."

# Wait for ngrok API to be available
NGROK_API="http://ngrok:4040/api/tunnels"
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s -f "$NGROK_API" >/dev/null 2>&1; then
        echo "✓ Ngrok API is ready"
        break
    fi
    
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "  Waiting for ngrok... ($RETRY_COUNT/$MAX_RETRIES)"
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "⚠️  Warning: Could not connect to ngrok after $MAX_RETRIES attempts"
    echo "   Proceeding with static PAYU_NOTIFY_BASE_URL from environment"
else
    # Get the public URL from ngrok
    echo "🔗 Fetching ngrok public URL..."
    
    NGROK_URL=$(curl -s "$NGROK_API" | jq -r '.tunnels[] | select(.proto=="https") | .public_url' | head -n 1)
    
    if [ -n "$NGROK_URL" ] && [ "$NGROK_URL" != "null" ]; then
        export PAYU_NOTIFY_BASE_URL="$NGROK_URL"
        echo "✓ Ngrok URL retrieved: $PAYU_NOTIFY_BASE_URL"
        echo "✓ PayU notifications will be sent to: $PAYU_NOTIFY_BASE_URL"
    else
        echo "⚠️  Warning: Could not retrieve ngrok URL"
        echo "   Using static PAYU_NOTIFY_BASE_URL: $PAYU_NOTIFY_BASE_URL"
    fi
fi

echo "🚀 Starting Monolith application..."
echo ""

# Execute the Java application
exec java -jar app.jar
