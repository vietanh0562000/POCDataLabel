package com.poc.data_assessment.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poc.data_assessment.dto.SupportPointDTO;
import com.poc.data_assessment.dto.request.SeedSupportPointRequest;
import com.poc.data_assessment.service.CreateIndexesService;
import com.poc.data_assessment.service.GetAllSupportPointUseCase;
import com.poc.data_assessment.service.SeedAllDEsUseCase;
import com.poc.data_assessment.service.SeedFullSupportPointUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for Support Point operations
 */
@Slf4j
@RestController
@RequestMapping("/support-point")
@RequiredArgsConstructor
public class SupportPointController {
    private final GetAllSupportPointUseCase getAllSupportPointUseCase;
    private final SeedFullSupportPointUseCase seedFullSupportPointUseCase;
    private final SeedAllDEsUseCase seedAllDEsUseCase;
    private final CreateIndexesService createIndexesService;
    
    @GetMapping("/all")
    public ResponseEntity<List<SupportPointDTO>> getAll() {
        List<SupportPointDTO> supportPoints = getAllSupportPointUseCase.execute();
        return ResponseEntity.ok(supportPoints);
    }

    @PostMapping("/seed-full")
    public ResponseEntity<Void> seed(@RequestBody SeedSupportPointRequest request) {
        seedFullSupportPointUseCase.execute(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Seed data for all 40k DEs for a given date
     * This endpoint processes all DEs (1-40000) in parallel batches
     * 
     * @param date Optional date parameter (format: yyyy-MM-dd). If not provided, uses today's date
     * @return 202 Accepted - Operation started asynchronously
     */
    @PostMapping("/seed-all")
    public ResponseEntity<Void> seedAll(@RequestParam(required = false) String date) {
        try {
            LocalDate targetDate = date != null 
                ? LocalDate.parse(date) 
                : LocalDate.now();
            
            log.info("Starting to seed data for all DEs on date: {}", targetDate);
            seedAllDEsUseCase.execute(targetDate);
            log.info("Seed operation initiated for all DEs on date: {}", targetDate);
            
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            log.error("Error initiating seed operation for all DEs", e);
            return ResponseEntity.internalServerError().build();
        }
    }

     /**
     * Create database indexes for optimal query performance
     * This endpoint creates indexes on support_point table for:
     * - DE_ID
     * - CREATED_AT
     * - Composite indexes on (DE_ID, CREATED_AT) and (CREATED_AT, DE_ID)
     * - STATUS
     * 
     * @return 200 OK with creation summary
     */
     @PostMapping("/create-indexes")
     public ResponseEntity<String> createIndexes() {
         try {
             log.info("Starting to create database indexes...");
             createIndexesService.createAllIndexes();
             createIndexesService.displayIndexInfo();
             return ResponseEntity.ok("Successfully created all database indexes");
         } catch (Exception e) {
             log.error("Error creating database indexes", e);
             return ResponseEntity.internalServerError()
                 .body("Error creating indexes: " + e.getMessage());
         }
     }
}
