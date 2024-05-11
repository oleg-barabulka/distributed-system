package com.github.cherkasov.cracker.service;

import dto.WorkerCrackingResponse;

public interface ResultSendingService {

    void sendResultToManager(WorkerCrackingResponse crackingDto);
}
