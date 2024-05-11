package com.github.cherkasov.cracker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Percents {
    @Value("${workers.number}")
    private int workerCount;

    public List<Double> percentList = new ArrayList<>();

    public void init() {
        for (int i = 0; i < workerCount; i++) {
            percentList.add(i, 0.0);
        }
    }
}
