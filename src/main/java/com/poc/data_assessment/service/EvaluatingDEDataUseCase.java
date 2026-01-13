package com.poc.data_assessment.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.SupportPointRepository;
import com.poc.jooq.generated.tables.records.SupportPointRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluatingDEDataUseCase {
    private final SupportPointRepository supportPointRepository;

    /**
     * Evaluate data for a single DE
     */
    public void execute(LocalDate date, Long deId) {
        List<SupportPointRecord> supportPoints = supportPointRepository.findAllSupportPointsByDateAndDeId(date, deId);
        evaluateSupportPoints(supportPoints);
        supportPointRepository.saveAll(supportPoints);
    }

    /**
     * Evaluate data for multiple DEs in batch using bulk SQL UPDATE
     * This is much faster than fetch-evaluate-save pattern
     */
    public void executeBatch(LocalDate date, List<Long> deIds) {
        if (deIds == null || deIds.isEmpty()) {
            return;
        }
        
        // Use bulk SQL UPDATE - evaluates and updates in database in one operation
        // This is 10-100x faster than fetch-evaluate-save
        supportPointRepository.bulkUpdateStatusByDateAndDeIds(date, deIds);
    }

    /**
     * Evaluate data for multiple DEs in batch (legacy method - uses fetch-evaluate-save)
     * Kept for fallback or when you need to process records in Java
     */
    public void executeBatchLegacy(LocalDate date, List<Long> deIds) {
        if (deIds == null || deIds.isEmpty()) {
            return;
        }
        
        // Fetch all support points for the batch of DEs in one query
        List<SupportPointRecord> supportPoints = supportPointRepository.findAllSupportPointsByDateAndDeIds(date, deIds);
        
        // Evaluate all support points
        evaluateSupportPoints(supportPoints);
        
        // Save all in batch
        supportPointRepository.saveAll(supportPoints);
    }

    /**
     * Evaluate support points based on business rules
     */
    private void evaluateSupportPoints(List<SupportPointRecord> supportPoints) {
        for (SupportPointRecord supportPoint : supportPoints) {
            if (supportPoint.getQlkw() == null || supportPoint.getQkfz() == null || supportPoint.getVlkw() == null || supportPoint.getVkfz() == null) {
                supportPoint.setStatus(SupportPointStatus.IMPLAUSIBLE.name());
            } else {
                if (supportPoint.getQlkw() < 0 || supportPoint.getQkfz() < 0 || supportPoint.getVlkw() < 0 || supportPoint.getVkfz() < 0) {
                    supportPoint.setStatus(SupportPointStatus.IMPLAUSIBLE.name());
                } else if (supportPoint.getQlkw() > 1000 || supportPoint.getQkfz() > 1000 || supportPoint.getVlkw() > 1000 || supportPoint.getVkfz() > 1000) {
                    supportPoint.setStatus(SupportPointStatus.IMPLAUSIBLE.name());
                } else {
                    supportPoint.setStatus(SupportPointStatus.COMPLETED.name());
                }
            }
        }
    }
}
