package com.github.cherkasov.cracker.producer;

import dto.WorkerCrackingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CrackingTaskResultProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.worker.response.routing.key}")
    private String routingKey;

    public void produce(WorkerCrackingResponse workerResponse) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, workerResponse);
        log.info(
                "Worker response {} part={} was sent to queue: {}",
                workerResponse.id().requestId(),
                workerResponse.workerPart() + 1,
                workerResponse.data());
    }
}
