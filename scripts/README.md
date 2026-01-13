# Database Scripts

## TimescaleDB Optimizations (Recommended for TimescaleDB)

### Option 1: SQL Script (Recommended)

Run the TimescaleDB optimization script:

```bash
# Using psql
psql -h localhost -U postgres -d postgres -f create-timescaledb-optimizations.sql

# Or using environment variables
psql -h $DB_HOST -U $DB_USER -d $DB_NAME -f create-timescaledb-optimizations.sql
```

### Option 2: REST API Endpoint

Use the REST API endpoint (now optimized for TimescaleDB):

```bash
curl -X POST "http://localhost:8080/support-point/create-indexes"
```

## Create Database Indexes (Standard PostgreSQL)

### Option 1: SQL Script

Run the SQL script directly against your PostgreSQL database:

```bash
# Using psql
psql -h localhost -U postgres -d postgres -f create-indexes.sql

# Or using environment variables
psql -h $DB_HOST -U $DB_USER -d $DB_NAME -f create-indexes.sql
```

### Option 2: REST API Endpoint

Use the REST API endpoint to create indexes programmatically:

```bash
curl -X POST "http://localhost:8080/support-point/create-indexes"
```

### TimescaleDB Optimizations

The TimescaleDB script performs the following optimizations:

1. **Convert to Hypertable** - Converts `support_point` table to a TimescaleDB hypertable
   - Partitions data by time (1 day chunks)
   - Enables automatic chunk management
   - Significantly improves query performance for time-series data

2. **Optimized Indexes**:
   - **idx_support_point_de_id** - Index on `de_id`
   - **idx_support_point_created_at** - Index on `created_at DESC` (optimized for recent data)
   - **idx_support_point_de_id_created_at_desc** - Composite index `(de_id, created_at DESC)`
     - **Most important** for batch queries filtering by DE and date
   - **idx_support_point_created_at_desc_de_id** - Composite index `(created_at DESC, de_id)`
   - **idx_support_point_de_date_covering** - Covering index with INCLUDE columns
     - Enables index-only scans (no table access needed)
     - Includes: `qlkw, qkfz, vlkw, vkfz, status`
   - **idx_support_point_status** - Index on `status`

3. **Compression Policy** - Automatically compresses chunks older than 7 days
   - Reduces storage by 90%+ for historical data
   - Improves query performance for recent data

4. **Continuous Aggregates** (Optional) - Pre-aggregated daily statistics
   - Materialized view: `support_point_daily_stats`
   - Auto-refreshes every hour
   - Useful for reporting and analytics

### Standard PostgreSQL Indexes

The standard script creates the following indexes for optimal performance:

1. **idx_support_point_de_id** - Index on `de_id` column
   - Used for filtering by DE ID

2. **idx_support_point_created_at** - Index on `created_at` column
   - Used for date range queries

3. **idx_support_point_de_id_created_at** - Composite index on `(de_id, created_at)`
   - **Most important** - Optimizes queries filtering by both DE ID and date
   - Used in: `findAllSupportPointsByDateAndDeId`, `findAllSupportPointsByDateAndDeIds`

4. **idx_support_point_created_at_de_id** - Composite index on `(created_at, de_id)`
   - Alternative ordering for queries primarily filtering by date

5. **idx_support_point_status** - Index on `status` column
   - Used for filtering by status

### Performance Impact

These indexes significantly improve query performance:
- **Before indexes**: Full table scans, slow queries
- **After indexes**: Index scans, 10-100x faster queries
- **Batch queries**: Especially beneficial for batch evaluation of 40k DEs

### Verification

After creating indexes, verify they exist:

```sql
-- List all indexes
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename IN ('support_point', 'de')
ORDER BY tablename, indexname;

-- Check index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes
WHERE tablename = 'support_point'
ORDER BY idx_scan DESC;
```

## Insert 40k DE Records

### Option 1: SQL Script (Recommended - Fastest)

Run the SQL script directly against your PostgreSQL database:

```bash
# Using psql
psql -h localhost -U postgres -d postgres -f insert-40k-de.sql

# Or using environment variables
psql -h $DB_HOST -U $DB_USER -d $DB_NAME -f insert-40k-de.sql
```

**Note:** This method uses PostgreSQL's `generate_series` function which is the most efficient way to insert 40,000 records. It typically completes in a few seconds.

### Option 2: REST API Endpoint

Use the REST API endpoint to insert DEs programmatically:

```bash
# Using generate_series (fastest - default)
curl -X POST "http://localhost:8080/de/insert-40k?useGenerateSeries=true"

# Using batch insert (slower but more control)
curl -X POST "http://localhost:8080/de/insert-40k?useGenerateSeries=false"
```

### Option 3: Java Service

You can also call the service directly from your application:

```java
@Autowired
private InsertDEsService insertDEsService;

// Fast method using generate_series
insertDEsService.insert40kDEsUsingGenerateSeries();

// Or batch insert method
insertDEsService.insert40kDEs();
```

## What the Script Does

1. **Creates the DE table** if it doesn't exist with:
   - `id` (BIGSERIAL PRIMARY KEY)
   - `name` (VARCHAR(255) NOT NULL)

2. **Creates an index** on the `id` column for better performance

3. **Inserts 40,000 DE records** with:
   - IDs from 1 to 40,000
   - Names in format: "DE-00001", "DE-00002", ..., "DE-40000"

4. **Uses ON CONFLICT DO NOTHING** to prevent errors if records already exist

## Performance

- **SQL Script (generate_series)**: ~1-5 seconds
- **REST API (generate_series)**: ~1-5 seconds  
- **REST API (batch insert)**: ~30-60 seconds

## Verification

After running the script, verify the insertion:

```sql
-- Check total count
SELECT COUNT(*) FROM de;

-- View sample records
SELECT * FROM de ORDER BY id LIMIT 10;

-- Check specific range
SELECT * FROM de WHERE id BETWEEN 1 AND 100;
```
