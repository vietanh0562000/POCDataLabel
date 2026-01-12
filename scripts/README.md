# Database Scripts

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
