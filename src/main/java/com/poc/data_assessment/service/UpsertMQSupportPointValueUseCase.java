package com.poc.data_assessment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.dto.SpeedMqSupportPointDTO;
import com.poc.data_assessment.dto.VolumeMqSupportPointDTO;
import com.poc.data_assessment.repository.DeRepository;
import com.poc.data_assessment.repository.MqAggregate15mRepository;
import com.poc.data_assessment.repository.TrafficAggregateData15mRepository;
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
        trafficAggregatedData15mRecords.stream().forEach(record -> {
            volumeMqSupportPointDTO.qKfz.add(record.getQKfzSum());
            volumeMqSupportPointDTO.qLkw.add(record.getQLkwSum());
            volumeMqSupportPointDTO.qPkw.add(record.getQPkwSum());
        });

        SpeedMqSupportPointDTO speedMqSupportPointDTO = SpeedMqSupportPointDTO.of();
        trafficAggregatedData15mRecords.stream().forEach(record -> {
            speedMqSupportPointDTO.vKfz += record.getVKfzWeightedAvg();
            speedMqSupportPointDTO.vLkw += record.getVLkwWeightedAvg();
            speedMqSupportPointDTO.vPkw += record.getVPkwWeightedAvg();
        });

        speedMqSupportPointDTO.vKfz /= volumeMqSupportPointDTO.qKfz.doubleValue();
        speedMqSupportPointDTO.vLkw /= volumeMqSupportPointDTO.qLkw.doubleValue();
        speedMqSupportPointDTO.vPkw /= volumeMqSupportPointDTO.qPkw.doubleValue();

        MqAggregate_15mRecord mqAggregate_15mRecord = new MqAggregate_15mRecord();
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
