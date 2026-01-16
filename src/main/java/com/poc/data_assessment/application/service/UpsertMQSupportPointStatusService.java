package com.poc.data_assessment.application.service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.application.port.out.DeSupportPointRepositoryPort;
import com.poc.data_assessment.application.port.out.MqSupportPointRepositoryPort;
import com.poc.data_assessment.domain.model.DeSupportPoint;
import com.poc.data_assessment.domain.model.MqSupportPoint;
import com.poc.data_assessment.domain.model.enums.SupportPointStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertMQSupportPointStatusService {
    private final MqSupportPointRepositoryPort mqSupportPointRepository;
    private final DeSupportPointRepositoryPort deSupportPointRepository;

    private enum MetricType {
        Q_KFZ(DeSupportPoint::getQKfzStt, MqSupportPoint::setQKfzStt),
        Q_LKW(DeSupportPoint::getQLkwStt, MqSupportPoint::setQLkwStt),
        Q_PKW(DeSupportPoint::getQPkwStt, MqSupportPoint::setQPkwStt),
        V_KFZ(DeSupportPoint::getVKfzStt, MqSupportPoint::setVKfzStt),
        V_PKW(DeSupportPoint::getVPkwStt, MqSupportPoint::setVPkwStt),
        V_LKW(DeSupportPoint::getVLkwStt, MqSupportPoint::setVLkwStt);

        private final Function<DeSupportPoint, Short> getter;
        private final BiConsumer<MqSupportPoint, Short> setter;

        MetricType(Function<DeSupportPoint, Short> getter,
                BiConsumer<MqSupportPoint, Short> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        Short getStatus(DeSupportPoint record) {
            return getter.apply(record);
        }

        void setStatus(MqSupportPoint record, short status) {
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
        List<DeSupportPoint> deSupportPoint_15mRecords = deSupportPointRepository
                .findAllDeSupportPointsByTime(timeBucket, permanentIds);

        MqSupportPoint mqSupportPoint = mqSupportPointRepository.findByPermanentIdAndStartTime(mqId, timeBucket);
        if (mqSupportPoint == null) {
            mqSupportPoint = new MqSupportPoint(mqId, timeBucket);
        }

        Map<MetricType, StatusCounts> metricCounts = new EnumMap<>(MetricType.class);

        // Initialize counts for each metric
        for (MetricType metric : MetricType.values()) {
            metricCounts.put(metric, new StatusCounts());
        }

        // Count statuses for each metric
        for (DeSupportPoint record : deSupportPoint_15mRecords) {
            for (MetricType metric : MetricType.values()) {
                metricCounts.get(metric).increment(metric.getStatus(record));
            }
        }

        // Set aggregated status for each metric
        for (MetricType metric : MetricType.values()) {
            SupportPointStatus status = metricCounts.get(metric).determineStatus();
            metric.setStatus(mqSupportPoint, (short) status.ordinal());
        }

        mqSupportPointRepository.save(mqSupportPoint);
    }
}