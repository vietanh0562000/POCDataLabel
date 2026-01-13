package com.poc.data_assessment.service;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to create database indexes for performance optimization
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateIndexesService {
    private final DSLContext dsl;

    /**
     * Create all recommended indexes for optimal query performance
     * Optimized for TimescaleDB
     */
    public void createAllIndexes() {
        log.info("Starting to create database indexes (TimescaleDB optimized)...");

        // Convert to hypertable first (outside transaction, as it may need to migrate data)
        convertToHypertable();

        // Create indexes in separate transactions to avoid connection issues
        try {
            createIndexIfNotExists(
                "idx_support_point_de_id",
                "support_point",
                "de_id"
            );
        } catch (Exception e) {
            log.warn("Failed to create index idx_support_point_de_id: {}", e.getMessage());
        }

        try {
            createIndexIfNotExists(
                "idx_support_point_created_at",
                "support_point",
                "created_at DESC"
            );
        } catch (Exception e) {
            log.warn("Failed to create index idx_support_point_created_at: {}", e.getMessage());
        }

        try {
            createCompositeIndexIfNotExists(
                "idx_support_point_de_id_created_at_desc",
                "support_point",
                "de_id", "created_at DESC"
            );
        } catch (Exception e) {
            log.warn("Failed to create composite index idx_support_point_de_id_created_at_desc: {}", e.getMessage());
        }

        try {
            createCompositeIndexIfNotExists(
                "idx_support_point_created_at_desc_de_id",
                "support_point",
                "created_at DESC", "de_id"
            );
        } catch (Exception e) {
            log.warn("Failed to create composite index idx_support_point_created_at_desc_de_id: {}", e.getMessage());
        }

        try {
            createCoveringIndex();
        } catch (Exception e) {
            log.warn("Failed to create covering index: {}", e.getMessage());
        }

        try {
            createIndexIfNotExists(
                "idx_support_point_status",
                "support_point",
                "status"
            );
        } catch (Exception e) {
            log.warn("Failed to create index idx_support_point_status: {}", e.getMessage());
        }

        // Enable compression (outside transaction)
        try {
            enableCompression();
        } catch (Exception e) {
            log.warn("Failed to enable compression: {}", e.getMessage());
        }

        // Analyze hypertable outside of transaction
        try {
            log.info("Analyzing hypertable to update query planner statistics...");
            analyzeHypertable();
        } catch (Exception e) {
            log.warn("Failed to analyze hypertable (non-critical): {}", e.getMessage());
        }

        log.info("Completed creating TimescaleDB-optimized indexes");
    }

    /**
     * Analyze hypertable outside of transaction
     */
    public void analyzeHypertable() {
        try {
            // Use a separate execution without transaction
            dsl.execute("SELECT analyze_hypertable('support_point')");
            log.info("Successfully analyzed hypertable");
        } catch (Exception e) {
            // If analyze_hypertable fails, try regular ANALYZE
            try {
                dsl.execute("ANALYZE support_point");
                log.info("Used regular ANALYZE instead of analyze_hypertable");
            } catch (Exception ex) {
                log.warn("Failed to analyze table: {}", ex.getMessage());
            }
        }
    }

    /**
     * Convert support_point table to TimescaleDB hypertable
     * Note: This operation should be done outside of a transaction as it may take time
     */
    public void convertToHypertable() {
        try {
            // Check if already a hypertable first
            String checkSql = """
                SELECT COUNT(*) as cnt FROM _timescaledb_catalog.hypertable 
                WHERE table_name = 'support_point'
            """;
            var result = dsl.fetchOne(checkSql);
            Integer count = result != null ? result.get("cnt", Integer.class) : 0;
            
            if (count == null || count == 0) {
                log.info("Converting support_point to TimescaleDB hypertable (this may take time if table has data)...");
                // Use migrate_data => true to handle existing data
                String sql = """
                    SELECT create_hypertable(
                        'support_point',
                        'created_at',
                        chunk_time_interval => INTERVAL '1 day',
                        migrate_data => TRUE,
                        if_not_exists => TRUE
                    )
                """;
                dsl.execute(sql);
                log.info("Successfully converted support_point to TimescaleDB hypertable");
            } else {
                log.info("support_point is already a TimescaleDB hypertable");
            }
        } catch (Exception e) {
            log.warn("Failed to convert to hypertable (may already be one or TimescaleDB not available): {}", e.getMessage());
        }
    }

    /**
     * Create covering index with INCLUDE columns for index-only scans
     */
    @Transactional
    public void createCoveringIndex() {
        try {
            String sql = """
                CREATE INDEX IF NOT EXISTS idx_support_point_de_date_covering 
                ON support_point(de_id, created_at DESC) 
                INCLUDE (qlkw, qkfz, vlkw, vkfz, status)
            """;
            dsl.execute(sql);
            log.info("Created covering index for index-only scans");
        } catch (Exception e) {
            log.warn("Failed to create covering index: {}", e.getMessage());
        }
    }

    /**
     * Enable compression for chunks older than 7 days
     */
    @Transactional
    public void enableCompression() {
        try {
            // Try to add compression policy (if_not_exists will prevent errors if already exists)
            String sql = """
                SELECT add_compression_policy('support_point', INTERVAL '7 days', if_not_exists => TRUE)
            """;
            dsl.execute(sql);
            log.info("Enabled compression policy for chunks older than 7 days");
        } catch (Exception e) {
            log.warn("Failed to enable compression policy (may already exist): {}", e.getMessage());
        }
    }

    /**
     * Create a single column index if it doesn't exist
     */
    @Transactional
    public void createIndexIfNotExists(String indexName, String tableName, String columnName) {
        try {
            String sql = String.format(
                "CREATE INDEX IF NOT EXISTS %s ON %s(%s)",
                indexName, tableName, columnName
            );
            dsl.execute(sql);
            log.info("Created index: {} on {}({})", indexName, tableName, columnName);
        } catch (Exception e) {
            log.warn("Failed to create index {}: {}", indexName, e.getMessage());
        }
    }

    /**
     * Create a composite index if it doesn't exist
     */
    @Transactional
    public void createCompositeIndexIfNotExists(String indexName, String tableName, String... columnNames) {
        try {
            String columns = String.join(", ", columnNames);
            String sql = String.format(
                "CREATE INDEX IF NOT EXISTS %s ON %s(%s)",
                indexName, tableName, columns
            );
            dsl.execute(sql);
            log.info("Created composite index: {} on {}({})", indexName, tableName, columns);
        } catch (Exception e) {
            log.warn("Failed to create composite index {}: {}", indexName, e.getMessage());
        }
    }

    /**
     * Get information about existing indexes
     */
    public void displayIndexInfo() {
        log.info("Current indexes on support_point and de tables:");
        
        String sql = """
            SELECT 
                schemaname,
                tablename,
                indexname,
                indexdef
            FROM pg_indexes
            WHERE tablename IN ('support_point', 'de')
            ORDER BY tablename, indexname
        """;
        
        dsl.fetch(sql).forEach(row -> {
            log.info("  Table: {}, Index: {}, Definition: {}", 
                row.get("tablename"),
                row.get("indexname"),
                row.get("indexdef")
            );
        });

        // Display TimescaleDB hypertable information if available
        try {
            displayHypertableInfo();
        } catch (Exception e) {
            log.debug("TimescaleDB hypertable info not available: {}", e.getMessage());
        }
    }

    /**
     * Display TimescaleDB hypertable information
     */
    public void displayHypertableInfo() {
        log.info("TimescaleDB Hypertable Information:");
        
        String sql = """
            SELECT 
                hypertable_name,
                num_dimensions,
                compression_enabled,
                chunk_time_interval
            FROM timescaledb_information.hypertables
            WHERE hypertable_name = 'support_point'
        """;
        
        dsl.fetch(sql).forEach(row -> {
            log.info("  Hypertable: {}, Dimensions: {}, Compression: {}, Chunk Interval: {}", 
                row.get("hypertable_name"),
                row.get("num_dimensions"),
                row.get("compression_enabled"),
                row.get("chunk_time_interval")
            );
        });

        // Display chunk information
        String chunkSql = """
            SELECT 
                chunk_name,
                range_start,
                range_end,
                is_compressed,
                uncompressed_heap_size,
                compressed_heap_size
            FROM timescaledb_information.chunks
            WHERE hypertable_name = 'support_point'
            ORDER BY range_start DESC
            LIMIT 5
        """;
        
        log.info("Recent chunks:");
        dsl.fetch(chunkSql).forEach(row -> {
            log.info("  Chunk: {}, Range: {} to {}, Compressed: {}, Size: {} -> {}", 
                row.get("chunk_name"),
                row.get("range_start"),
                row.get("range_end"),
                row.get("is_compressed"),
                row.get("uncompressed_heap_size"),
                row.get("compressed_heap_size")
            );
        });
    }
}
