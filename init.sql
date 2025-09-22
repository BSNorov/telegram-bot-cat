-- Database initialization script for Telegram Cat Bot
-- This script ensures the database and tables are created properly

-- Create database if it doesn't exist
-- CREATE DATABASE telegram_cat_bot;

-- Use the database
-- \c telegram_cat_bot;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    telegram_id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    cat_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Create cats table
CREATE TABLE IF NOT EXISTS cats (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    photo_url VARCHAR(500),
    file_id VARCHAR(255),
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    likes_count INTEGER NOT NULL DEFAULT 0,
    dislikes_count INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (owner_id) REFERENCES users(telegram_id)
);

-- Create likes table
CREATE TABLE IF NOT EXISTS likes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    cat_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('LIKE', 'DISLIKE')),
    created_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, cat_id),
    FOREIGN KEY (user_id) REFERENCES users(telegram_id),
    FOREIGN KEY (cat_id) REFERENCES cats(id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_cats_owner_id ON cats(owner_id);
CREATE INDEX IF NOT EXISTS idx_cats_created_at ON cats(created_at);
CREATE INDEX IF NOT EXISTS idx_likes_user_id ON likes(user_id);
CREATE INDEX IF NOT EXISTS idx_likes_cat_id ON likes(cat_id);

-- Insert some test data (optional)
-- INSERT INTO users (telegram_id, username, cat_name, created_at, updated_at) 
-- VALUES (123456789, 'testuser', 'Мурзик', NOW(), NOW()) 
-- ON CONFLICT (telegram_id) DO NOTHING;
