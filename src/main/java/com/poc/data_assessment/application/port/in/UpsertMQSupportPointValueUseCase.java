package com.poc.data_assessment.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.adapter.out.persistence.repository.DeRepository;
import com.poc.data_assessment.adapter.out.persistence.repository.MqAggregate15mRepository;
import com.poc.data_assessment.adapter.out.persistence.repository.TrafficAggregateData15mRepository;
import com.poc.data_assessment.application.dto.SpeedMqSupportPointDTO;
import com.poc.data_assessment.application.dto.VolumeMqSupportPointDTO;
import com.poc.jooq.generated.tables.records.DeRecord;
import com.poc.jooq.generated.tables.records.MqAggregate_15mRecord;
import com.poc.jooq.generated.tables.records.TrafficAggregatedData_15mRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertMQSupportPointValueUseCase {
    private final DeRepository deRepository;
    private final TrafficAggregateData15mRepository trafficAggregateData15mRepository;
    private final MqAggregate15mRepository mqAggregate_15mRepository;

    public void execute(LocalDateTime timeBucket, String mqId) {
        List<DeRecord> deRecords = deRepository.findAllDEsByMQId(mqId);
        List<String> deIds = deRecords.stream()
                .map(DeRecord::getId)
                .collect(Collectors.toList());

        List<TrafficAggregatedData_15mRecord> trafficAggregatedData15mRecords = trafficAggregateData15mRepository
                .findAllByDateAndPermanentId(timeBucket,
                        deIds);

        VolumeMqSupportPointDTO volumeMqSupportPointDTO = VolumeMqSupportPointDTO.of();
        trafficAggregatedData15mRecords.stream()
                .filter(Objects::nonNull)
                .forEach(record -> {
                    if (record.getQKfzSum() != null) {
                        volumeMqSupportPointDTO.qKfz = volumeMqSupportPointDTO.qKfz.add(record.getQKfzSum());
                    }
                    if (record.getQLkwSum() != null) {
                        volumeMqSupportPointDTO.qLkw = volumeMqSupportPointDTO.qLkw.add(record.getQLkwSum());
                    }
                    if (record.getQPkwSum() != null) {
                        volumeMqSupportPointDTO.qPkw = volumeMqSupportPointDTO.qPkw.add(record.getQPkwSum());
                    }
                });

        SpeedMqSupportPointDTO speedMqSupportPointDTO = SpeedMqSupportPointDTO.of();
        trafficAggregatedData15mRecords.stream()
                .filter(Objects::nonNull)
                .forEach(record -> {
                    if (record.getVKfzWeightedAvg() != null) {
                        double qKfzSum = record.getQKfzSum() != null ? record.getQKfzSum().doubleValue() : 0.0;
                        speedMqSupportPointDTO.vKfz += record.getVKfzWeightedAvg() * qKfzSum;
                    }
                    if (record.getVLkwWeightedAvg() != null) {
                        double qLkwSum = record.getQLkwSum() != null ? record.getQLkwSum().doubleValue() : 0.0;
                        speedMqSupportPointDTO.vLkw += record.getVLkwWeightedAvg() * qLkwSum;
                    }
                    if (record.getVPkwWeightedAvg() != null) {
                        double qPkwSum = record.getQPkwSum() != null ? record.getQPkwSum().doubleValue() : 0.0;
                        speedMqSupportPointDTO.vPkw += record.getVPkwWeightedAvg() * qPkwSum;
                    }
                });

        if (!volumeMqSupportPointDTO.qKfz.equals(BigDecimal.ZERO)) {
            speedMqSupportPointDTO.vKfz /= volumeMqSupportPointDTO.qKfz.doubleValue();
        } else {
            speedMqSupportPointDTO.vKfz = 0.0;
        }

        if (!volumeMqSupportPointDTO.qLkw.equals(BigDecimal.ZERO)) {
            speedMqSupportPointDTO.vLkw /= volumeMqSupportPointDTO.qLkw.doubleValue();
        } else {
            speedMqSupportPointDTO.vLkw = 0.0;
        }

        if (!volumeMqSupportPointDTO.qPkw.equals(BigDecimal.ZERO)) {
            speedMqSupportPointDTO.vPkw /= volumeMqSupportPointDTO.qPkw.doubleValue();
        } else {
            speedMqSupportPointDTO.vPkw = 0.0;
        }

        MqAggregate_15mRecord mqAggregate_15mRecord = mqAggregate_15mRepository
                .findByMqIdAndTimeBucket(mqId, timeBucket).orElse(new MqAggregate_15mRecord());
        mqAggregate_15mRecord.setMqId(mqId);
        mqAggregate_15mRecord.setTimeBucket(timeBucket);
        mqAggregate_15mRecord.setQKfzSum(volumeMqSupportPointDTO.qKfz.intValue());
        mqAggregate_15mRecord.setQLkwSum(volumeMqSupportPointDTO.qLkw.intValue());
        mqAggregate_15mRecord.setQPkwSum(volumeMqSupportPointDTO.qPkw.intValue());
        mqAggregate_15mRecord.setVKfzWeightedAvg(BigDecimal.valueOf(speedMqSupportPointDTO.vKfz));
        mqAggregate_15mRecord.setVLkwWeightedAvg(BigDecimal.valueOf(speedMqSupportPointDTO.vLkw));
        mqAggregate_15mRecord.setVPkwWeightedAvg(BigDecimal.valueOf(speedMqSupportPointDTO.vPkw));
        mqAggregate_15mRecord.setCreatedAt(LocalDateTime.now());
        mqAggregate_15mRecord.setUpdatedAt(LocalDateTime.now());

        mqAggregate_15mRepository.save(mqAggregate_15mRecord);
    }
}
