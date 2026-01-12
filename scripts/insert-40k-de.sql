-- Script to insert 40,000 DE records into the database
-- This script creates the DE table if it doesn't exist and inserts 40k records

-- Create DE table if it doesn't exist
CREATE TABLE IF NOT EXISTS de (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create index on id for better performance
CREATE INDEX IF NOT EXISTS idx_de_id ON de(id);

-- Insert 40,000 DE records
-- Using generate_series for efficient bulk insert
INSERT INTO de (id, name)
SELECT 
    generate_series(1, 40000) AS id,
    'DE-' || LPAD(generate_series(1, 40000)::TEXT, 5, '0') AS name
ON CONFLICT (id) DO NOTHING;

-- Verify the count
SELECT COUNT(*) AS total_de_count FROM de;

-- Display sample records
SELECT * FROM de ORDER BY id LIMIT 10;
