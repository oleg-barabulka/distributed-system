package com.github.cherkasov.cracker.consumer;

import com.github.cherkasov.cracker.service.CrackingService;

import dto.WorkerCrackingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CrackingTaskResultConsumer {

    private final CrackingService service;

    @Value("${workers.number}")
    private int workerCount;

    @RabbitListener(queues = {"${results.queue.name}"})
    public void consume(WorkerCrackingResponse workerResponse) {
        log.info(
                "Get worker response {} ({}/{}) - {}",
                workerResponse.id().requestId(),
                workerResponse.workerPart() + 1,
                workerCount,
                workerResponse.data());
        service.updateTaskStatus(workerResponse);
    }
}
