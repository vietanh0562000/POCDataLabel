package com.poc.data_assessment.service;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.jooq.impl.DSL.*;

/**
 * Service to insert 40,000 DE records into the database
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InsertDEsService {
    private final DSLContext dsl;
    
    private static final int TOTAL_DE_COUNT = 40_000;
    private static final int BATCH_SIZE = 1000; // Insert in batches of 1000

    /**
     * Create DE table if it doesn't exist
     */
    @Transactional
    public void createTableIfNotExists() {
        log.info("Creating DE table if it doesn't exist...");
        
        dsl.execute("""
            CREATE TABLE IF NOT EXISTS de (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL
            )
        """);
        
        dsl.execute("CREATE INDEX IF NOT EXISTS idx_de_id ON de(id)");
        
        log.info("DE table created/verified successfully");
    }

    /**
     * Insert 40,000 DE records into the database
     * Uses batch insert for better performance
     */
    @Transactional
    public void insert40kDEs() {
        log.info("Starting to insert {} DE records...", TOTAL_DE_COUNT);
        
        createTableIfNotExists();
        
        long startTime = System.currentTimeMillis();
        int insertedCount = 0;
        
        // Process in batches
        for (int startId = 1; startId <= TOTAL_DE_COUNT; startId += BATCH_SIZE) {
            int endId = Math.min(startId + BATCH_SIZE - 1, TOTAL_DE_COUNT);
            
            // Direct batch insert using VALUES
            String sql = "INSERT INTO de (id, name) VALUES ";
            List<String> values = new ArrayList<>();
            for (long id = startId; id <= endId; id++) {
                String name = "DE-" + String.format("%05d", id);
                values.add("(" + id + ", '" + name.replace("'", "''") + "')");
            }
            sql += String.join(", ", values);
            sql += " ON CONFLICT (id) DO NOTHING";
            
            dsl.execute(sql);
            insertedCount += (endId - startId + 1);
            
            if (startId % 5000 == 1 || endId == TOTAL_DE_COUNT) {
                log.info("Progress: Processed DEs {}-{} (Total processed: {}/{})", 
                    startId, endId, insertedCount, TOTAL_DE_COUNT);
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        log.info("Completed inserting DE records. Total inserted: {}, Time: {} seconds", 
            insertedCount, totalTime / 1000.0);
        
        // Verify count
        Integer count = dsl.selectCount()
            .from(table("de"))
            .fetchOne(0, Integer.class);
        
        log.info("Total DE records in database: {}", count);
    }

    /**
     * Insert DEs using PostgreSQL generate_series (most efficient)
     */
    @Transactional
    public void insert40kDEsUsingGenerateSeries() {
        log.info("Starting to insert {} DE records using generate_series...", TOTAL_DE_COUNT);
        
        createTableIfNotExists();
        
        long startTime = System.currentTimeMillis();
        
        String sql = """
            INSERT INTO de (id, name)
            SELECT 
                generate_series(1, ?) AS id,
                'DE-' || LPAD(generate_series(1, ?)::TEXT, 5, '0') AS name
            ON CONFLICT (id) DO NOTHING
        """;
        
        int inserted = dsl.execute(sql, TOTAL_DE_COUNT, TOTAL_DE_COUNT);
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        log.info("Completed inserting DE records using generate_series. Inserted: {}, Time: {} seconds", 
            inserted, totalTime / 1000.0);
        
        // Verify count
        Integer count = dsl.selectCount()
            .from(table("de"))
            .fetchOne(0, Integer.class);
        
        log.info("Total DE records in database: {}", count);
    }
}
