-- TimescaleDB Optimizations for Support Point Table
-- This script optimizes the database for TimescaleDB time-series workloads

-- 1. Convert support_point table to a hypertable if not already
-- This partitions the table by time for better performance
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM _timescaledb_catalog.hypertable 
        WHERE table_name = 'support_point'
    ) THEN
        -- Convert to hypertable partitioned by created_at
        PERFORM create_hypertable(
            'support_point',
            'created_at',
            chunk_time_interval => INTERVAL '1 day',
            if_not_exists => TRUE
        );
        RAISE NOTICE 'Converted support_point to hypertable';
    ELSE
        RAISE NOTICE 'support_point is already a hypertable';
    END IF;
END $$;

-- 2. Create TimescaleDB-optimized indexes
-- These indexes work better with hypertables

-- Index on DE_ID (for filtering by DE)
CREATE INDEX IF NOT EXISTS idx_support_point_de_id 
ON support_point(de_id);

-- Index on CREATED_AT (already optimized by hypertable partitioning, but useful for queries)
CREATE INDEX IF NOT EXISTS idx_support_point_created_at 
ON support_point(created_at DESC);

-- Composite index on (DE_ID, CREATED_AT DESC) - Most important for batch queries
-- DESC ordering helps with recent data queries
CREATE INDEX IF NOT EXISTS idx_support_point_de_id_created_at_desc 
ON support_point(de_id, created_at DESC);

-- Composite index on (CREATED_AT DESC, DE_ID) - For date-first queries
CREATE INDEX IF NOT EXISTS idx_support_point_created_at_desc_de_id 
ON support_point(created_at DESC, de_id);

-- Index on STATUS
CREATE INDEX IF NOT EXISTS idx_support_point_status 
ON support_point(status);

-- 3. Enable compression for older chunks (optional but recommended)
-- Compress chunks older than 7 days
SELECT add_compression_policy('support_point', INTERVAL '7 days', if_not_exists => TRUE);

-- 4. Set chunk time interval to 1 day (if not already set)
-- Smaller chunks = better query performance for recent data
SELECT set_chunk_time_interval('support_point', INTERVAL '1 day', if_not_exists => TRUE);

-- -- 5. Create continuous aggregate for daily statistics (optional)
-- -- This pre-aggregates data for faster queries
-- CREATE MATERIALIZED VIEW IF NOT EXISTS support_point_daily_stats
-- WITH (timescaledb.continuous) AS
-- SELECT 
--     time_bucket('1 day', created_at) AS day,
--     de_id,
--     COUNT(*) AS total_points,
--     COUNT(*) FILTER (WHERE status = 'COMPLETED') AS completed_count,
--     COUNT(*) FILTER (WHERE status = 'IMPLAUSIBLE') AS implausible_count,
--     COUNT(*) FILTER (WHERE status = 'MISSING') AS missing_count,
--     AVG(qlkw) AS avg_qlkw,
--     AVG(qkfz) AS avg_qkfz,
--     AVG(vlkw) AS avg_vlkw,
--     AVG(vkfz) AS avg_vkfz
-- FROM support_point
-- GROUP BY day, de_id
-- WITH NO DATA;

-- -- Refresh policy for continuous aggregate (refresh every hour)
-- SELECT add_continuous_aggregate_policy('support_point_daily_stats',
--     start_offset => INTERVAL '3 days',
--     end_offset => INTERVAL '1 hour',
--     schedule_interval => INTERVAL '1 hour',
--     if_not_exists => TRUE);

-- 6. Optimize for batch queries - create index on (de_id, created_at) with INCLUDE columns
-- This allows index-only scans for common queries
CREATE INDEX IF NOT EXISTS idx_support_point_de_date_covering 
ON support_point(de_id, created_at DESC) 
INCLUDE (qlkw, qkfz, vlkw, vkfz, status);

-- 7. Analyze hypertable to update statistics
SELECT analyze_hypertable('support_point');

-- 8. Display hypertable information
SELECT 
    hypertable_name,
    num_dimensions,
    compression_enabled,
    chunk_time_interval
FROM timescaledb_information.hypertables
WHERE hypertable_name = 'support_point';

-- Display chunk information
SELECT 
    chunk_name,
    range_start,
    range_end,
    is_compressed,
    uncompressed_heap_size,
    uncompressed_toast_size,
    compressed_heap_size,
    compressed_toast_size
FROM timescaledb_information.chunks
WHERE hypertable_name = 'support_point'
ORDER BY range_start DESC
LIMIT 10;
