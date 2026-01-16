package com.poc.data_assessment.adapter.out.persistence.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.De.DE;

import com.poc.data_assessment.application.port.out.DeRepositoryPort;
import com.poc.data_assessment.domain.model.De;
import com.poc.jooq.generated.tables.records.DeRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeRepository implements DeRepositoryPort {
    private final DSLContext dsl;

    @Override
    public List<De> findAllDEsByMQId(String mqId) {
        var records = dsl.selectFrom(DE)
                .where(DE.MQ_ID.eq(mqId))
                .fetchInto(DeRecord.class);
        return records.stream().map(record -> {
            De de = new De();
            de.setId(record.getId());
            de.setName(record.getName());
            de.setMqId(record.getMqId());
            return de;
        }).collect(Collectors.toList());
    }

    @Override
    public String findMQIdByPermanentId(String permanentId) {
        return dsl.selectFrom(DE)
                .where(DE.ID.eq(permanentId))
                .fetchOne(DE.MQ_ID);
    }
}
