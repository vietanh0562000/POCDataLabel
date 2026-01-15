package com.poc.data_assessment.service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.DeSupportPointRepository;
import com.poc.data_assessment.repository.MqSupportPointRepository;
import com.poc.data_assessment.service.domain.MqSupportPointService;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;
import com.poc.jooq.generated.tables.records.MqSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertMQSupportPointStatusUseCase {
    private final MqSupportPointRepository mqSupportPointRepository;
    private final DeSupportPointRepository deSupportPointRepository;
    private final MqSupportPointService mqSupportPointService;

    private enum MetricType {
        Q_KFZ(DeSupportPoint_15mRecord::getQKfzStt, MqSupportPoint_15mRecord::setQKfzStt),
        Q_LKW(DeSupportPoint_15mRecord::getQLkwStt, MqSupportPoint_15mRecord::setQLkwStt),
        Q_PKW(DeSupportPoint_15mRecord::getQPkwStt, MqSupportPoint_15mRecord::setQPkwStt),
        V_KFZ(DeSupportPoint_15mRecord::getVKfzStt, MqSupportPoint_15mRecord::setVKfzStt),
        V_PKW(DeSupportPoint_15mRecord::getVPkwStt, MqSupportPoint_15mRecord::setVPkwStt),
        V_LKW(DeSupportPoint_15mRecord::getVLkwStt, MqSupportPoint_15mRecord::setVLkwStt);

        private final Function<DeSupportPoint_15mRecord, Short> getter;
        private final BiConsumer<MqSupportPoint_15mRecord, Short> setter;

        MetricType(Function<DeSupportPoint_15mRecord, Short> getter,
                BiConsumer<MqSupportPoint_15mRecord, Short> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        Short getStatus(DeSupportPoint_15mRecord record) {
            return getter.apply(record);
        }

        void setStatus(MqSupportPoint_15mRecord record, short status) {
            setter.accept(record, status);
        }
    }

    private static class StatusCounts {
        int completed = 0;
        int implausible = 0;

        void increment(Short status) {
            if (status == null)
                return;
            if (status == 0)
                completed++;
            else if (status == 2)
                implausible++;
        }

        SupportPointStatus determineStatus() {
            if (completed > 0) {
                return SupportPointStatus.COMPLETED;
            } else if (implausible > 0) {
                return SupportPointStatus.IMPLAUSIBLE;
            } else {
                return SupportPointStatus.MISSING;
            }
        }
    }

    public void execute(LocalDateTime timeBucket, String mqId) {
        List<String> permanentIds = deSupportPointRepository.findAllPermanentIdsByMqId(mqId);
        List<DeSupportPoint_15mRecord> deSupportPoint_15mRecords = deSupportPointRepository
                .findAllDeSupportPointsByTime(timeBucket, permanentIds);

        MqSupportPoint_15mRecord mqSupportPoint_15mRecord = mqSupportPointService.findOrCreateMqSupportPoint_15m(mqId,
                timeBucket);

        Map<MetricType, StatusCounts> metricCounts = new EnumMap<>(MetricType.class);

        // Initialize counts for each metric
        for (MetricType metric : MetricType.values()) {
            metricCounts.put(metric, new StatusCounts());
        }

        // Count statuses for each metric
        for (DeSupportPoint_15mRecord record : deSupportPoint_15mRecords) {
            for (MetricType metric : MetricType.values()) {
                metricCounts.get(metric).increment(metric.getStatus(record));
            }
        }

        // Set aggregated status for each metric
        for (MetricType metric : MetricType.values()) {
            SupportPointStatus status = metricCounts.get(metric).determineStatus();
            metric.setStatus(mqSupportPoint_15mRecord, (short) status.ordinal());
        }

        mqSupportPointRepository.save(mqSupportPoint_15mRecord);
    }
}