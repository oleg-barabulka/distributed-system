package com.github.cherkasov.cracker.service;

import com.github.cherkasov.cracker.producer.PercentProducer;
import dto.PercentResponse;
import dto.WorkerCrackingRequest;
import dto.WorkerCrackingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.paukov.combinatorics3.Generator;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCrackingTaskService implements CrackingTaskService {

    private final Executor crackingTaskExecutor;

    private final PercentProducer producer;

    public volatile Long done = 0L;

    public volatile Long all = 0L;

    @Override
    public CompletableFuture<WorkerCrackingResponse> executeCrackingTask(
            WorkerCrackingRequest managerRequest) {
        log.info(
                "Received cracking task from manager for id='{}'", managerRequest.id().requestId());
        return CompletableFuture.supplyAsync(
                () -> executeTask(managerRequest), crackingTaskExecutor);
    }

    private WorkerCrackingResponse executeTask(WorkerCrackingRequest managerRequest) {
        log.info(
                "Start execution task='{}' for part='{}'",
                managerRequest.id().requestId(),
                managerRequest.taskPartId());
        List<String> words = new ArrayList<>();
        done = 0L;
        for (int length = 1; length <= managerRequest.hashLength(); length++)
            all += (int) Math.pow(managerRequest.alphabet().size(), length) / managerRequest.workerCount();

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                producer.produce(new PercentResponse((double) done / all, managerRequest.taskPartId()));
            }
        };

        // Запуск ранбла через 5 секунд (в миллисекундах)
        timer.schedule(task, 500,500);

        for (int length = 1; length <= managerRequest.hashLength(); length++) {
            int allWordCount = (int) Math.pow(managerRequest.alphabet().size(), length);
            int start =
                    start(managerRequest.taskPartId(), managerRequest.workerCount(), allWordCount);
            int partWordCount =
                    currPartCount(
                            managerRequest.taskPartId(),
                            managerRequest.workerCount(),
                            allWordCount);
            words.addAll(
                    Generator.permutation(managerRequest.alphabet())
                            .withRepetitions(length)
                            .stream()
                            .skip(start)
                            .limit(partWordCount)
                            .map(word -> String.join("", word))
                            .filter(
                                    word -> {
                                        done += 1;
                                        String current =
                                                DigestUtils.md5DigestAsHex(
                                                        word.getBytes(StandardCharsets.UTF_8));
                                        if (managerRequest.hash().equals(current)) {
                                            log.info(
                                                    "New result='{}' for hash='{}' with length='{}'",
                                                    word,
                                                    managerRequest.hash(),
                                                    managerRequest.hashLength());
                                            return true;
                                        }
                                        return false;
                                    })
                            .toList());
        }

        timer.cancel();

        done = 0L;
        all = 0L;

        log.info("Finished execution task, result='{}'", words);
        return new WorkerCrackingResponse(managerRequest.id(), managerRequest.taskPartId(), words);
    }

    private int start(int partNumber, int partCount, int words) {
        return (int) Math.ceil((double) words / partCount * partNumber);
    }

    private int currPartCount(int partNumber, int partCount, int words) {
        return (int)
                (Math.ceil((double) words / partCount * (partNumber + 1))
                        - Math.ceil((double) words / partCount * partNumber));
    }

}
