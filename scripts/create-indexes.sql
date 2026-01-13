-- Database Indexes for Performance Optimization
-- This script creates indexes to optimize query performance for support points

-- Index on DE_ID for filtering by DE
-- Used in: findAllSupportPointsByDateAndDeId, findAllSupportPointsByDateAndDeIds
CREATE INDEX IF NOT EXISTS idx_support_point_de_id 
ON support_point(de_id);

-- Index on CREATED_AT for date filtering
-- Used in: findAllSupportPointsToday, findAllSupportPointsByDateAndDeId, findAllSupportPointsByDateAndDeIds
CREATE INDEX IF NOT EXISTS idx_support_point_created_at 
ON support_point(created_at);

-- Composite index on (DE_ID, CREATED_AT) for queries filtering by both
-- This is the most efficient index for the common query pattern
-- Used in: findAllSupportPointsByDateAndDeId, findAllSupportPointsByDateAndDeIds
CREATE INDEX IF NOT EXISTS idx_support_point_de_id_created_at 
ON support_point(de_id, created_at);

-- Composite index on (CREATED_AT, DE_ID) as alternative ordering
-- Useful for queries that primarily filter by date then by DE
CREATE INDEX IF NOT EXISTS idx_support_point_created_at_de_id 
ON support_point(created_at, de_id);

-- Index on STATUS for filtering by status (if needed in future)
CREATE INDEX IF NOT EXISTS idx_support_point_status 
ON support_point(status);

-- Index on DE table ID (if not already exists as primary key)
-- This is typically already indexed as PRIMARY KEY, but ensuring it exists
-- CREATE INDEX IF NOT EXISTS idx_de_id ON de(id); -- Usually not needed as PK

-- Analyze tables to update statistics for query planner
ANALYZE support_point;
ANALYZE de;

-- Display index information
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename IN ('support_point', 'de')
ORDER BY tablename, indexname;
