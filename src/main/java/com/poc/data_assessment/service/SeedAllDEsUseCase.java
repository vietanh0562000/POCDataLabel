package com.poc.data_assessment.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.dto.request.SeedSupportPointRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to seed data for all DEs (40k DEs)
 * Processes DEs in parallel batches for better performance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeedAllDEsUseCase {
    private final SeedFullSupportPointUseCase seedFullSupportPointUseCase;
    
    private static final int BATCH_SIZE = 100; // Process 100 DEs at a time
    private static final int THREAD_POOL_SIZE = 10; // Parallel threads for processing

    /**
     * Seed data for all DEs for a given date
     * If deIds are not provided, it will seed for DE IDs from 1 to 40,000
     * 
     * @param date The date to seed data for
     * @param deIds Optional list of DE IDs. If null, seeds for DEs 1-40000
     */
    public void execute(LocalDate date, List<Long> deIds) {
        List<Long> targetDeIds = deIds != null && !deIds.isEmpty() 
            ? deIds 
            : generateDeIds(1, 40_000);
        
        log.info("Starting to seed data for {} DEs on date {}", targetDeIds.size(), date);
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // Process in batches
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < targetDeIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, targetDeIds.size());
            List<Long> batch = targetDeIds.subList(i, endIndex);
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                processBatch(date, batch, processedCount, successCount, failureCount);
            }, executor);
            
            futures.add(future);
        }
        
        // Wait for all batches to complete
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(2, TimeUnit.HOURS); // 2 hour timeout
        } catch (Exception e) {
            log.error("Error during batch processing", e);
            throw new RuntimeException("Failed to seed all DEs", e);
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        log.info("Completed seeding data for {} DEs. Success: {}, Failed: {}, Total time: {} seconds",
            processedCount.get(), successCount.get(), failureCount.get(), totalTime / 1000.0);
        
        if (failureCount.get() > 0) {
            log.warn("Some DEs failed to seed. Success: {}, Failed: {}", successCount.get(), failureCount.get());
        }
    }

    /**
     * Seed data for all DEs (1-40000) for a given date
     */
    public void execute(LocalDate date) {
        execute(date, null);
    }

    private void processBatch(LocalDate date, List<Long> deIds, 
                             AtomicInteger processedCount, 
                             AtomicInteger successCount, 
                             AtomicInteger failureCount) {
        for (Long deId : deIds) {
            try {
                seedFullSupportPointUseCase.execute(
                    new SeedSupportPointRequest(date, deId)
                );
                successCount.incrementAndGet();
                
                int processed = processedCount.incrementAndGet();
                if (processed % 1000 == 0) {
                    log.info("Progress: {}/{} DEs seeded", processed, deIds.size());
                }
            } catch (Exception e) {
                failureCount.incrementAndGet();
                log.error("Failed to seed DE {}: {}", deId, e.getMessage());
            }
        }
    }

    private List<Long> generateDeIds(long start, long end) {
        List<Long> deIds = new ArrayList<>();
        for (long i = start; i <= end; i++) {
            deIds.add(i);
        }
        return deIds;
    }
}
