package com.github.cherkasov.cracker.producer;

import dto.WorkerCrackingRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrackingTaskProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.manager.request.routing.key}")
    private String routingKey;

    public void produce(WorkerCrackingRequest managerRequest) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, managerRequest);
        log.info(
                "Manager request {} ({}/{}) was sent to queue",
                managerRequest.id().requestId(),
                managerRequest.taskPartId() + 1,
                managerRequest.workerCount());
    }
}
