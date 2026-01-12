package com.poc.data_assessment.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poc.data_assessment.dto.SupportPointDTO;
import com.poc.data_assessment.service.EvaluateAllDEsUseCase;
import com.poc.data_assessment.service.EvaluatingDEDataUseCase;
import com.poc.data_assessment.service.GetSupportPointUseCase;
import com.poc.data_assessment.service.InsertDEsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for DE (Data Entity) operations
 */
@Slf4j
@RestController
@RequestMapping("/de")
@RequiredArgsConstructor
public class DEController {
    private final EvaluatingDEDataUseCase evaluatingDEDataUseCase;
    private final EvaluateAllDEsUseCase evaluateAllDEsUseCase;
    private final GetSupportPointUseCase getSupportPointUseCase;
    private final InsertDEsService insertDEsService;

    @PatchMapping("/{id}/evaluate")
    public ResponseEntity<Void> evaluate(@PathVariable Long id, @RequestParam LocalDate date) {
        evaluatingDEDataUseCase.execute(date, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}/support-points")
    public ResponseEntity<List<SupportPointDTO>> getSupportPoints(@PathVariable Long id, @RequestParam LocalDate date) {
        List<SupportPointDTO> supportPoints = getSupportPointUseCase.execute(date, id);
        return ResponseEntity.ok(supportPoints);
    }

    /**
     * Evaluate data for all 40k DEs for a given date
     * This endpoint processes all DEs (1-40000) in parallel batches
     * 
     * @param date Optional date parameter (format: yyyy-MM-dd). If not provided, uses today's date
     * @return 202 Accepted - Operation started asynchronously
     */
    @PatchMapping("/evaluate-all")
    public ResponseEntity<Void> evaluateAll(@RequestParam(required = false) String date) {
        try {
            LocalDate targetDate = date != null 
                ? LocalDate.parse(date) 
                : LocalDate.now();
            
            log.info("Starting to evaluate data for all DEs on date: {}", targetDate);
            evaluateAllDEsUseCase.execute(targetDate);
            log.info("Evaluate operation initiated for all DEs on date: {}", targetDate);
            
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            log.error("Error initiating evaluate operation for all DEs", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Insert 40,000 DE records into the database
     * This endpoint creates the DE table if it doesn't exist and inserts all records
     * 
     * @param useGenerateSeries If true, uses PostgreSQL generate_series (faster). Default: true
     * @return 200 OK with insertion summary
     */
    @PostMapping("/insert-40k")
    public ResponseEntity<String> insert40kDEs(@RequestParam(defaultValue = "true") boolean useGenerateSeries) {
        try {
            log.info("Starting to insert 40k DE records. Using generate_series: {}", useGenerateSeries);
            
            if (useGenerateSeries) {
                insertDEsService.insert40kDEsUsingGenerateSeries();
            } else {
                insertDEsService.insert40kDEs();
            }
            
            return ResponseEntity.ok("Successfully inserted 40,000 DE records");
        } catch (Exception e) {
            log.error("Error inserting 40k DE records", e);
            return ResponseEntity.internalServerError()
                .body("Error inserting DE records: " + e.getMessage());
        }
    }
}
