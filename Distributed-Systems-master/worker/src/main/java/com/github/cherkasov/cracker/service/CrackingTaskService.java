package com.github.cherkasov.cracker.service;

import dto.WorkerCrackingRequest;
import dto.WorkerCrackingResponse;

import java.util.concurrent.CompletableFuture;

public interface CrackingTaskService {

    CompletableFuture<WorkerCrackingResponse> executeCrackingTask(
            WorkerCrackingRequest managerRequest);
}
