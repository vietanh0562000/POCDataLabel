package com.poc.data_assessment.model;

import java.time.Instant;

import com.poc.data_assessment.enums.SupportPointStatus;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;  
import jakarta.persistence.Table;

@Entity
@Table(name = "SupportPoint")
public class SupportPoint {
    public String id;
    private Double qLkw;
    private Double qKfz;
    private Double vLkw;
    private Double vKfz;
    private SupportPointStatus status;
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "de_id")
    private DE de;
}
