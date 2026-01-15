package com.poc.data_assessment.service.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.KafkaListener;

import com.poc.data_assessment.common.DataConst;
import com.poc.data_assessment.service.UpsertDESupportPointStatusUseCase;
import com.poc.data_assessment.service.CheckValidDailyDEUseCase;
import com.poc.data_assessment.service.CheckZerosDailyDEUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", matchIfMissing = true)
public class DeConsumer {

    private final UpsertDESupportPointStatusUseCase upsertDESupportPointUseCase;
    private final CheckZerosDailyDEUseCase checkZerosDailyDEUseCase;
    private final CheckValidDailyDEUseCase checkValidDailyDEUseCase;

    @KafkaListener(topics = "gap.traffic-data.short-term-data-ingested", containerFactory = "updateDeEventListenerFactory")
    public void consume(UpdateDeEvent updateDeEvent) {
        System.out.println("Consumed message: " + updateDeEvent.permanentId() + " " + updateDeEvent.timeBucket());
        upsertDESupportPointUseCase.execute(updateDeEvent);
        checkZerosDailyDEUseCase.execute(updateDeEvent.timeBucket().toLocalDate(), updateDeEvent.permanentId(),
                DataConst.CONSECUTIVE_ZERO_THRESHOLD);
        checkValidDailyDEUseCase.execute(updateDeEvent.timeBucket().toLocalDate(), updateDeEvent.permanentId());
    }

}
