package com.poc.data_assessment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "DE")
public class DE {
    public String id;
    public String name;
}
