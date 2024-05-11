package com.github.cherkasov.cracker.consumer;

import com.github.cherkasov.cracker.service.CrackingTaskService;
import com.github.cherkasov.cracker.service.ResultSendingService;
import com.rabbitmq.client.Channel;

import dto.WorkerCrackingRequest;
import dto.WorkerCrackingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CrackingTaskConsumer {

    private final CrackingTaskService service;
    private final ResultSendingService sendingService;

    @RabbitListener(queues = {"${tasks.queue.name}"})
    public void consume(
            WorkerCrackingRequest managerRequest,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        log.info(
                "Get manager request {} ({}/{})",
                managerRequest.id().requestId(),
                managerRequest.taskPartId() + 1,
                managerRequest.workerCount());

        service.executeCrackingTask(managerRequest)
                .thenAccept(response -> sentToManager(managerRequest, channel, tag, response))
                .exceptionally(
                        ex -> {
                            log.error(ex.getMessage(), ex);
                            sendNack(channel, tag);
                            return null;
                        });
    }

    private static void sendNack(Channel channel, long tag) {
        try {
            if (channel.isOpen()) {
                channel.basicNack(tag, false, true);
            }
        } catch (IOException e) {
            log.error("Failed to nack message", e);
        }
    }

    private void sentToManager(
            WorkerCrackingRequest managerRequest,
            Channel channel,
            long tag,
            WorkerCrackingResponse response) {
        try {
            sendingService.sendResultToManager(response);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            String errorMessage = "Error working on task " + managerRequest.id().requestId();
            throw new RuntimeException(errorMessage, e);
        }
    }
}
