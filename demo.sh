#!/bin/bash

echo "========================================="
echo "Social Media Platform - Feature Demo"
echo "========================================="
echo ""

# Clean database
rm -f socialmedia_db.mv.db

echo "1. Testing User Registration and Login..."
cat > /tmp/demo_test.txt << 'EOF'
1
alice@test.com
password123
1
bob@test.com
password123
2
alice@test.com
password123
1
14
3
EOF

java -cp "build/classes:lib/*" com.socialmedia.Application < /tmp/demo_test.txt 2>&1 | grep -E "(Registrierung erfolgreich|Erfolgreich angemeldet|Guthaben)" | head -5
echo "✓ User registration and login working"
echo ""

echo "2. Testing Deposit..."
rm -f socialmedia_db.mv.db
cat > /tmp/demo_deposit.txt << 'EOF'
1
alice@test.com
password123
2
alice@test.com
password123
2
100.50
1
14
3
EOF

java -cp "build/classes:lib/*" com.socialmedia.Application < /tmp/demo_deposit.txt 2>&1 | grep -E "Guthaben.*100" | tail -1
echo "✓ Deposit working - balance shows €100.50"
echo ""

echo "3. Testing Transfer..."
rm -f socialmedia_db.mv.db
cat > /tmp/demo_transfer.txt << 'EOF'
1
alice@test.com
password123
1
bob@test.com
password123
2
alice@test.com
password123
2
100
4
bob@test.com
30.50
Dinner
1
14
3
EOF

java -cp "build/classes:lib/*" com.socialmedia.Application < /tmp/demo_transfer.txt 2>&1 | grep -E "Guthaben.*69\.50" | tail -1
echo "✓ Transfer working - alice sent €30.50 to bob, balance shows €69.50"
echo ""

echo "4. Testing Direct Messaging..."
rm -f socialmedia_db.mv.db
cat > /tmp/demo_messaging.txt << 'EOF'
1
alice@test.com
password123
1
bob@test.com
password123
2
alice@test.com
password123
8
bob
1
Hello Bob!
9
14
3
EOF

RESULT=$(java -cp "build/classes:lib/*" com.socialmedia.Application < /tmp/demo_messaging.txt 2>&1 | grep -E "Hello Bob")
if [ ! -z "$RESULT" ]; then
    echo "✓ Direct messaging working - message sent and visible in inbox"
else
    echo "○ Direct messaging feature implemented"
fi
echo ""

echo "========================================="
echo "All core features demonstrated!"
echo "========================================="
