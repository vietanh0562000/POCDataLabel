package com.poc.data_assessment.application.service.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.KafkaListener;

import com.poc.data_assessment.application.dto.UpdateDeEvent;
import com.poc.data_assessment.application.service.CheckValidDailyDEUseCase;
import com.poc.data_assessment.application.service.CheckValidDailyMQUseCase;
import com.poc.data_assessment.application.service.CheckZerosDailyDEUseCase;
import com.poc.data_assessment.application.service.CheckZerosDailyMQUseCase;
import com.poc.data_assessment.application.service.UpsertDESupportPointStatusUseCase;
import com.poc.data_assessment.application.service.UpsertMQSupportPointStatusUseCase;
import com.poc.data_assessment.application.service.UpsertMQSupportPointValueUseCase;
import com.poc.data_assessment.common.DataConst;
import com.poc.data_assessment.domain.port.out.DeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", matchIfMissing = true)
public class DeConsumer {

    private final UpsertDESupportPointStatusUseCase upsertDESupportPointUseCase;
    private final CheckZerosDailyDEUseCase checkZerosDailyDEUseCase;
    private final CheckValidDailyDEUseCase checkValidDailyDEUseCase;
    private final UpsertMQSupportPointValueUseCase upsertMQSupportPointValueUseCase;
    private final UpsertMQSupportPointStatusUseCase upsertMQSupportPointStatusUseCase;
    private final CheckZerosDailyMQUseCase checkZerosDailyMQUseCase;
    private final CheckValidDailyMQUseCase checkValidDailyMQUseCase;

    private final DeRepository deRepository;

    @KafkaListener(topics = "gap.traffic-data.short-term-data-ingested", containerFactory = "updateDeEventListenerFactory")
    public void consume(UpdateDeEvent updateDeEvent) {
        System.out.println("Consumed message: " + updateDeEvent.permanentId() + " " + updateDeEvent.timeBucket());
        upsertDESupportPointUseCase.execute(updateDeEvent);
        checkZerosDailyDEUseCase.execute(updateDeEvent.timeBucket().toLocalDate(), updateDeEvent.permanentId(),
                DataConst.CONSECUTIVE_ZERO_THRESHOLD);
        checkValidDailyDEUseCase.execute(updateDeEvent.timeBucket().toLocalDate(), updateDeEvent.permanentId());

        String mqId = deRepository.findMQIdByPermanentId(updateDeEvent.permanentId());
        if (mqId != null) {
            upsertMQSupportPointValueUseCase.execute(updateDeEvent.timeBucket(), mqId);
            upsertMQSupportPointStatusUseCase.execute(updateDeEvent.timeBucket(), mqId);
            checkZerosDailyMQUseCase.execute(updateDeEvent.timeBucket().toLocalDate(), mqId,
                    DataConst.CONSECUTIVE_ZERO_THRESHOLD);
            checkValidDailyMQUseCase.execute(updateDeEvent.timeBucket().toLocalDate(), mqId);
        }
    }

}
