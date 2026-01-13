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

import com.poc.data_assessment.repository.SupportPointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to evaluate data for all DEs (40k DEs)
 * Processes DEs in parallel batches for better performance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluateAllDEsUseCase {
    private final SupportPointRepository supportPointRepository;
    private final EvaluatingDEDataUseCase evaluatingDEDataUseCase;
    
    private static final int BATCH_SIZE = 2000; // Process 2000 DEs at a time (larger batches = fewer queries)
    private static final int THREAD_POOL_SIZE = 10; // Reduced threads since we're doing larger batches

    /**
     * Evaluate data for all DEs for a given date
     * If deIds are not provided, it will evaluate for DE IDs from 1 to 40,000
     * 
     * @param date The date to evaluate data for
     * @param deIds Optional list of DE IDs. If null, evaluates for DEs 1-40000
     */
    public void execute(LocalDate date, List<Long> deIds) {
        List<Long> targetDeIds = deIds != null && !deIds.isEmpty() 
            ? deIds 
            : generateDeIds(1, 40_000);
        
        log.info("Starting to evaluate data for {} DEs on date {}", targetDeIds.size(), date);
        
        long startTime = System.currentTimeMillis();
        
        // Try single bulk update for ALL support points on the date (fastest - no DE filtering)
        try {
            log.info("Attempting single bulk update for all support points on date {}...", date);
            int updated = supportPointRepository.bulkUpdateStatusByDate(date);
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Completed evaluating data for all support points on date {}. Updated {} records. Total time: {} seconds",
                date, updated, totalTime / 1000.0);
            return;
        } catch (Exception e) {
            log.warn("Single bulk update failed, trying with DE list: {}", e.getMessage());
        }
        
        // Try single bulk update with DE list
        try {
            log.info("Attempting single bulk update for {} DEs...", targetDeIds.size());
            int updated = supportPointRepository.bulkUpdateStatusByDateAndDeIds(date, targetDeIds);
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Completed evaluating data for {} DEs in single bulk update. Updated {} records. Total time: {} seconds",
                targetDeIds.size(), updated, totalTime / 1000.0);
            return;
        } catch (Exception e) {
            log.warn("Single bulk update with DE list failed, falling back to batched processing: {}", e.getMessage());
        }
        
        // Fallback to batched processing if single update fails (e.g., too many DEs)
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        AtomicInteger processedCount = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
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
            throw new RuntimeException("Failed to evaluate all DEs", e);
        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        log.info("Completed evaluating data for {} DEs. Success: {}, Failed: {}, Total time: {} seconds",
            processedCount.get(), successCount.get(), failureCount.get(), totalTime / 1000.0);
        
        if (failureCount.get() > 0) {
            log.warn("Some DEs failed to evaluate. Success: {}, Failed: {}", successCount.get(), failureCount.get());
        }
    }

    /**
     * Evaluate data for all DEs (1-40000) for a given date
     */
    public void execute(LocalDate date) {
        execute(date, null);
    }

    /**
     * Evaluate data for all DEs found in the database for a given date
     */
    public void executeForAllDEsInDatabase(LocalDate date) {
        List<Long> deIds = supportPointRepository.findAllDistinctDeIds();
        if (deIds.isEmpty()) {
            log.warn("No DE IDs found in database. Using default range 1-40000");
            execute(date, null);
        } else {
            log.info("Found {} DEs in database", deIds.size());
            execute(date, deIds);
        }
    }

    private void processBatch(LocalDate date, List<Long> deIds, 
                             AtomicInteger processedCount, 
                             AtomicInteger successCount, 
                             AtomicInteger failureCount) {
        try {
            // Process entire batch at once instead of one DE at a time
            evaluatingDEDataUseCase.executeBatch(date, deIds);
            
            int batchSize = deIds.size();
            successCount.addAndGet(batchSize);
            int processed = processedCount.addAndGet(batchSize);
            
            if (processed % 1000 == 0 || processed % 5000 == 0) {
                log.info("Progress: {} DEs evaluated (last batch: {} DEs)", processed, batchSize);
            }
        } catch (Exception e) {
            failureCount.addAndGet(deIds.size());
            log.error("Failed to evaluate batch of {} DEs: {}", deIds.size(), e.getMessage());
            // Fallback: try individual DEs if batch fails
            log.warn("Falling back to individual DE evaluation for batch");
            for (Long deId : deIds) {
                try {
                    evaluatingDEDataUseCase.execute(date, deId);
                    successCount.incrementAndGet();
                    failureCount.decrementAndGet();
                } catch (Exception ex) {
                    log.error("Failed to evaluate DE {}: {}", deId, ex.getMessage());
                }
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
