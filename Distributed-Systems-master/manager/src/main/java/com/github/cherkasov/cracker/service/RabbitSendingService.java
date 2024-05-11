package com.github.cherkasov.cracker.service;

import com.github.cherkasov.cracker.dto.CrackingRequest;
import com.github.cherkasov.cracker.producer.CrackingTaskProducer;

import dto.RequestId;
import dto.WorkerCrackingRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RabbitSendingService implements TaskSendingService {

    private final CrackingTaskProducer crackingTaskProducer;

    private final List<String> alphabet;

    @Value("${workers.number}")
    private int workersCount;

    @Override
    public void sendTasksToWorkers(RequestId requestId, CrackingRequest requestDto) {
        for (int partIndex = 0; partIndex < workersCount; partIndex++) {
            final WorkerCrackingRequest submitTaskRequest =
                    new WorkerCrackingRequest(
                            requestId,
                            requestDto.hash(),
                            requestDto.maxLength(),
                            partIndex,
                            workersCount,
                            alphabet);

            crackingTaskProducer.produce(submitTaskRequest);
        }
    }
}
