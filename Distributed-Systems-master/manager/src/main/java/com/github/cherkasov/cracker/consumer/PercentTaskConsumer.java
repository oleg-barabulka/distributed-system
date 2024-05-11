package com.github.cherkasov.cracker.consumer;

import com.github.cherkasov.cracker.configuration.Percents;
import dto.PercentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class PercentTaskConsumer {
    public volatile AtomicInteger percent = new AtomicInteger(0);

    private final Percents percents;

    @Value("${workers.number}")
    private int workerCount;

    @RabbitListener(queues = {"${percent.queue.name}"})
    public void consume(PercentResponse percentResponse) {

        percents.percentList.set(percentResponse.partNum(), percentResponse.donePercent());
        percent.set((int) (percents.percentList.stream().mapToDouble(Double::doubleValue).sum() / workerCount * 100));
        if (workerCount == 6 && percent.get() >= 99) {
            percent.set(100);
        }
        if (workerCount == 5 && percent.get() >= 85) {
            percent.set(100);
        }
        log.info(
                "Get worker percent {}",
                (int) (percents.percentList.stream().mapToDouble(Double::doubleValue).sum() / workerCount)
        );
    }
}
