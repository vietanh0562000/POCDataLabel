package com.poc.data_assessment.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateMQSupportPointUseCase {
    public void execute(LocalDate date, String mqId) {
        //TODO: Get all DE in the MQ

        //TODO: Update the MQ support point
    }   
}
