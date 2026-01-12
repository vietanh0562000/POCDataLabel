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
import com.poc.data_assessment.service.EvaluatingDEDataUseCase;
import com.poc.data_assessment.service.GetSupportPointUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/de")
@RequiredArgsConstructor
public class DEController {
    private final EvaluatingDEDataUseCase evaluatingDEDataUseCase;
    private final GetSupportPointUseCase getSupportPointUseCase;

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
}
