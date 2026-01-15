package com.poc.data_assessment.domain.port.out;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.De.DE;
import com.poc.jooq.generated.tables.records.DeRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeRepository {
    private final DSLContext dsl;

    public List<DeRecord> findAllDEsByMQId(String mqId) {
        return dsl.selectFrom(DE)
                .where(DE.MQ_ID.eq(mqId))
                .fetchInto(DeRecord.class);
    }

    public String findMQIdByPermanentId(String permanentId) {
        return dsl.selectFrom(DE)
                .where(DE.ID.eq(permanentId))
                .fetchOne(DE.MQ_ID);
    }
}
