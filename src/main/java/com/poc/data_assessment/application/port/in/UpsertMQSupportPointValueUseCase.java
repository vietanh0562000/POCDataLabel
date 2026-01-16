package com.poc.data_assessment.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.application.dto.SpeedMqSupportPointDTO;
import com.poc.data_assessment.application.dto.VolumeMqSupportPointDTO;
import com.poc.data_assessment.application.port.out.DeRepositoryPort;
import com.poc.data_assessment.application.port.out.MqAggregate15mRepositoryPort;
import com.poc.data_assessment.application.port.out.TrafficAggregateData15mRepositoryPort;
import com.poc.data_assessment.domain.model.De;
import com.poc.data_assessment.domain.model.MqAggregate15m;
import com.poc.data_assessment.domain.model.TrafficAggregatedData15m;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertMQSupportPointValueUseCase {
    private final DeRepositoryPort deRepository;
    private final TrafficAggregateData15mRepositoryPort trafficAggregateData15mRepository;
    private final MqAggregate15mRepositoryPort mqAggregate_15mRepository;

    public void execute(LocalDateTime timeBucket, String mqId) {
        List<De> deRecords = deRepository.findAllDEsByMQId(mqId);
        List<String> deIds = deRecords.stream()
                .map(De::getId)
                .collect(Collectors.toList());

        List<TrafficAggregatedData15m> trafficAggregatedData15mRecords = trafficAggregateData15mRepository
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

        MqAggregate15m mqAggregate_15mRecord = mqAggregate_15mRepository
                .findByMqIdAndTimeBucket(mqId, timeBucket).orElse(new MqAggregate15m(mqId, timeBucket));
        mqAggregate_15mRecord.setQKfzSum(volumeMqSupportPointDTO.qKfz);
        mqAggregate_15mRecord.setQLkwSum(volumeMqSupportPointDTO.qLkw);
        mqAggregate_15mRecord.setQPkwSum(volumeMqSupportPointDTO.qPkw);
        mqAggregate_15mRecord.setVKfzWeightedAvg(speedMqSupportPointDTO.vKfz);
        mqAggregate_15mRecord.setVLkwWeightedAvg(speedMqSupportPointDTO.vLkw);
        mqAggregate_15mRecord.setVPkwWeightedAvg(speedMqSupportPointDTO.vPkw);
        mqAggregate_15mRecord.setCreatedAt(LocalDateTime.now());
        mqAggregate_15mRecord.setUpdatedAt(LocalDateTime.now());

        mqAggregate_15mRepository.save(mqAggregate_15mRecord);
    }
}
