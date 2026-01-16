package com.poc.data_assessment.adapter.out.persistence.mapper;

import com.poc.data_assessment.domain.model.DeSupportPoint;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

public class DeSupportPointMapper {
    public static DeSupportPoint mapToDeSupportPoint(DeSupportPoint_15mRecord record) {
        DeSupportPoint deSupportPoint = new DeSupportPoint();
        deSupportPoint.setPermanentId(record.getPermanentId());
        deSupportPoint.setStartTime(record.getStartTime());
        deSupportPoint.setQKfzStt(record.getQKfzStt());
        deSupportPoint.setQLkwStt(record.getQLkwStt());
        deSupportPoint.setQPkwStt(record.getQPkwStt());
        deSupportPoint.setVKfzStt(record.getVKfzStt());
        deSupportPoint.setVPkwStt(record.getVPkwStt());
        deSupportPoint.setVLkwStt(record.getVLkwStt());
        return deSupportPoint;
    }
}
