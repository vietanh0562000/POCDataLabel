package com.poc.data_assessment.model;

import java.time.Instant;

import com.poc.data_assessment.enums.SupportPointStatus;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "SupportPoint")
public class SupportPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    private Double qLkw;
    private Double qKfz;
    private Double vLkw;
    private Double vKfz;

    @Enumerated(EnumType.STRING)
    private SupportPointStatus status;
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "de_id")
    private DE de;
}
