package com.poc.data_assessment.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poc.data_assessment.dto.DeSupportPointDTO;
import com.poc.data_assessment.service.GetSupportPointUseCase;

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
    private final GetSupportPointUseCase getSupportPointUseCase;


    @GetMapping("{permanentId}/support-points")
    public ResponseEntity<List<DeSupportPointDTO>> getSupportPoints(@PathVariable String permanentId, @RequestParam LocalDate date) {
        List<DeSupportPointDTO> supportPoints = getSupportPointUseCase.execute(date, permanentId);
        return ResponseEntity.ok(supportPoints);
    }
}
