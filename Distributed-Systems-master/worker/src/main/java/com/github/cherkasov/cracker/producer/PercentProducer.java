package com.github.cherkasov.cracker.producer;

import dto.PercentResponse;
import dto.WorkerCrackingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PercentProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.worker.percent.routing.key}")
    private String routingKey;

    public void produce(PercentResponse response) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, response);
        log.info(
                "Worker percent response {}",
                response.donePercent()
        );
    }
}
