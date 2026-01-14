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

import com.poc.data_assessment.dto.DeSupportPointDTO;
import com.poc.data_assessment.dto.request.SeedSupportPointRequest;
import com.poc.data_assessment.service.GetAllSupportPointUseCase;

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
    
    @GetMapping("/all")
    public ResponseEntity<List<DeSupportPointDTO>> getAll() {
        List<DeSupportPointDTO> supportPoints = getAllSupportPointUseCase.execute();
        return ResponseEntity.ok(supportPoints);
    }
}
