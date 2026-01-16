package com.poc.data_assessment.adapter.in.broker;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.KafkaListener;

import com.poc.data_assessment.adapter.out.persistence.repository.DeRepository;
import com.poc.data_assessment.application.service.CheckValidDailyDEService;
import com.poc.data_assessment.application.service.CheckValidDailyMQService;
import com.poc.data_assessment.application.service.CheckZerosDailyDEService;
import com.poc.data_assessment.application.service.CheckZerosDailyMQService;
import com.poc.data_assessment.application.service.UpsertDESupportPointStatusService;
import com.poc.data_assessment.application.service.UpsertMQSupportPointStatusService;
import com.poc.data_assessment.application.service.UpsertMQSupportPointValueService;
import com.poc.data_assessment.common.DataConst;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", matchIfMissing = true)
public class DeConsumer {

    private final UpsertDESupportPointStatusService upsertDESupportPointUseCase;
    private final CheckZerosDailyDEService checkZerosDailyDEUseCase;
    private final CheckValidDailyDEService checkValidDailyDEUseCase;
    private final UpsertMQSupportPointValueService upsertMQSupportPointValueUseCase;
    private final UpsertMQSupportPointStatusService upsertMQSupportPointStatusUseCase;
    private final CheckZerosDailyMQService checkZerosDailyMQUseCase;
    private final CheckValidDailyMQService checkValidDailyMQUseCase;

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
