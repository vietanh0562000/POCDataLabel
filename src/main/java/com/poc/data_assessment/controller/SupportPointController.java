package com.poc.data_assessment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.data_assessment.dto.SupportPointDTO;
import com.poc.data_assessment.service.GetAllSupportPointUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/support-point")
@RequiredArgsConstructor
public class SupportPointController {
    private final GetAllSupportPointUseCase getAllSupportPointUseCase;

    @GetMapping("/all")
    public ResponseEntity<List<SupportPointDTO>> getAll() {
        List<SupportPointDTO> supportPoints = getAllSupportPointUseCase.execute();
        return ResponseEntity.ok(supportPoints);
    }
}
