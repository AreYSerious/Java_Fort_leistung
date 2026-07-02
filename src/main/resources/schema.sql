-- Social Media Platform Database Schema

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_balance CHECK (balance >= 0),
    CONSTRAINT chk_email_format CHECK (username LIKE '%_@_%._%')
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sender_username VARCHAR(255),
    recipient_username VARCHAR(255),
    description VARCHAR(500),
    amount DECIMAL(10, 2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    CONSTRAINT chk_amount CHECK (amount > 0),
    CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('EINZAHLUNG', 'AUSZAHLUNG', 'UEBERWEISUNG')),
    CONSTRAINT chk_deposit CHECK (transaction_type != 'EINZAHLUNG' OR (sender_username IS NULL AND recipient_username IS NOT NULL)),
    CONSTRAINT chk_withdrawal CHECK (transaction_type != 'AUSZAHLUNG' OR (sender_username IS NOT NULL AND recipient_username IS NULL)),
    CONSTRAINT chk_transfer CHECK (transaction_type != 'UEBERWEISUNG' OR (sender_username IS NOT NULL AND recipient_username IS NOT NULL AND sender_username != recipient_username)),
    FOREIGN KEY (sender_username) REFERENCES users(username) ON DELETE CASCADE,
    FOREIGN KEY (recipient_username) REFERENCES users(username) ON DELETE CASCADE
);

-- Wall comments table
CREATE TABLE IF NOT EXISTS wall_comments (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    wall_owner_username VARCHAR(255) NOT NULL,
    commenter_username VARCHAR(255) NOT NULL,
    comment_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wall_owner_username) REFERENCES users(username) ON DELETE CASCADE,
    FOREIGN KEY (commenter_username) REFERENCES users(username) ON DELETE CASCADE
);

-- Direct messages table
CREATE TABLE IF NOT EXISTS direct_messages (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sender_username VARCHAR(255) NOT NULL,
    recipient_username VARCHAR(255) NOT NULL,
    message_text TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_no_self_message CHECK (sender_username != recipient_username),
    FOREIGN KEY (sender_username) REFERENCES users(username) ON DELETE CASCADE,
    FOREIGN KEY (recipient_username) REFERENCES users(username) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_transactions_sender ON transactions(sender_username);
CREATE INDEX IF NOT EXISTS idx_transactions_recipient ON transactions(recipient_username);
CREATE INDEX IF NOT EXISTS idx_wall_comments_owner ON wall_comments(wall_owner_username);
CREATE INDEX IF NOT EXISTS idx_direct_messages_sender ON direct_messages(sender_username);
CREATE INDEX IF NOT EXISTS idx_direct_messages_recipient ON direct_messages(recipient_username);
